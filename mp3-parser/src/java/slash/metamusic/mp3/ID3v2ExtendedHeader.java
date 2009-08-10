/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.util.BitConversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * My instances represent a ID3v2 extended header of the MP3 frames as
 * described in http://www.id3.org/id3v2.3.0.html.
 *
 * @author Christian Pesch
 * @version $Id: ID3v2ExtendedHeader.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class ID3v2ExtendedHeader {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v2ExtendedHeader.class.getName());

    public static final int MINIMUM_EXTENDED_HEADER_SIZE = 6;
    public static final int CRC_CHECKSUM_SIZE = 5;

    public static final int UPDATE_FLAG = 0x0040;
    public static final int CRC_FLAG = 0x0020;
    public static final int MORE_FLAG = 0x0010;

    public static final int[] MAXIMUM_TAG_FRAMES = {128, 64, 32, 32};
    public static final int[] MAXIMUM_TAG_SIZES = {8000000, 1024000, 320000, 32000};
    public static final int[] MAXIMUM_TEXT_SIZES = {-1, 1024, 128, 30};

    /**
     * Create a new (empty) extended header
     */
    public ID3v2ExtendedHeader() {
        this.size = 0;
        this.flagSize = 0;
        this.update = false;
        this.crced = false;
        this.crc = new byte[CRC_CHECKSUM_SIZE];
        this.maximumTagSize = -1;
        this.textEncoded = false;
        this.maximumTextSize = -1;
        this.imageEncoded = false;
        this.imageRestriction = -1;
    }


    /**
     * Reads the ID3v2 extended header from the input stream.
     *
     * @param in the InputStream to read from
     * @return if the read header is valid
     * @throws IOException if an error occurs
     */
    public boolean read(InputStream in) throws IOException {
        byte[] buffer = new byte[4];
        if (in.read(buffer) != buffer.length)
            throw new IOException("Read invalid extended header");

        size = BitConversion.extract4BigEndian(buffer);
        if (size < MINIMUM_EXTENDED_HEADER_SIZE) {
            log.severe("The extended header size data is less than the minimum required size.");
            return false;
        }

        buffer = new byte[1];
        if (in.read(buffer) != buffer.length)
            throw new IOException("Read invalid extended header flag count");

        flagSize = (int) buffer[0];
        buffer = new byte[flagSize + 1];

        if (in.read(buffer) != buffer.length)
            throw new IOException("Read invalid extended header flags");

        return readHeader(buffer);
    }

    protected boolean readHeader(byte[] data) {
        int bytesRead = 1;

        int flags = data[0];
        if ((flags & UPDATE_FLAG) != 0) {
            update = true;
            bytesRead += 1;
        }
        if ((flags & CRC_FLAG) != 0) {
            crced = true;
            bytesRead += 1;
            for (int i = 0; i < crc.length; i++) {
                crc[i] = data[bytesRead++];
            }
        }
        if ((flags & MORE_FLAG) != 0) {
            bytesRead += 1;
            maximumTagSize = BitConversion.shiftAnd(data[bytesRead], 6, 1);
            textEncoded = BitConversion.getBit(data[bytesRead], 5) == 1;
            maximumTextSize = BitConversion.shiftAnd(data[bytesRead], 3, 1);
            imageEncoded = BitConversion.getBit(data[bytesRead], 2) == 1;
            imageRestriction = BitConversion.shiftAnd(data[bytesRead], 0, 1);
            bytesRead += 1;
        }

        if (bytesRead != flagSize) {
            log.severe("The number of found flag bytes " +
                    "in the extended header is not " +
                    "equals to the number specified " +
                    "in the extended header.");
            return false;
        }

        return true;
    }


    /**
     * Writes the ID3v2 extended header to the OutputStream.
     *
     * @throws IOException if an error occurs
     */
    public void write(OutputStream out) throws IOException {
        out.write(getBytes());
    }

    /**
     * Return an array of bytes representing this extended header in the
     * standard format to be written to a file.
     *
     * @return a binary represenation of this extended header
     */
    public byte[] getBytes() {
        byte[] result = new byte[size];
        int bytesCopied = 0;

        System.arraycopy(BitConversion.create4BigEndian(size), 0, result, bytesCopied, 4);
        bytesCopied += 4;
        result[bytesCopied++] = (byte) flagSize;
        System.arraycopy(getFlagBytes(), 0, result, bytesCopied, flagSize);
        bytesCopied += flagSize;

        return result;
    }

    /**
     * A helper function for the getBytes method that returns a byte array
     * representing the extended flags field of the extended header.
     *
     * @return the extended flags field of the extended header
     */
    protected byte[] getFlagBytes() {
        byte[] b = new byte[flagSize];
        int bytesCopied = 1;
        b[0] = 0;

        if (update) {
            b[0] = BitConversion.setBit(b[0], 7);
            b[bytesCopied++] = 0;
        }
        if (crced) {
            b[0] = BitConversion.setBit(b[0], 6);
            b[bytesCopied++] = (byte) crc.length;
            System.arraycopy(crc, 0, b, bytesCopied, crc.length);
            bytesCopied += crc.length;
        }
        if ((maximumTagSize != -1) || textEncoded || (maximumTextSize != -1) ||
                imageEncoded || (imageRestriction != -1)) {

            b[0] = BitConversion.setBit(b[0], 5);
            b[bytesCopied++] = 0x01;
            byte restrict = 0;
            if (maximumTagSize != -1) {
                if (BitConversion.getBit((byte) maximumTagSize, 0) == 1) {
                    restrict = BitConversion.setBit(restrict, 6);
                }
                if (BitConversion.getBit((byte) maximumTagSize, 1) == 1) {
                    restrict = BitConversion.setBit(restrict, 7);
                }
            }
            if (textEncoded) {
                restrict = BitConversion.setBit(restrict, 5);
            }
            if (maximumTextSize != -1) {
                if (BitConversion.getBit((byte) maximumTextSize, 0) == 1) {
                    restrict = BitConversion.setBit(restrict, 3);
                }
                if (BitConversion.getBit((byte) maximumTextSize, 1) == 1) {
                    restrict = BitConversion.setBit(restrict, 4);
                }
            }
            if (imageEncoded) {
                restrict = BitConversion.setBit(restrict, 2);
            }
            if (imageRestriction != -1) {
                if (BitConversion.getBit((byte) imageRestriction, 0) == 1) {
                    restrict = BitConversion.setBit(restrict, 0);
                }
                if (BitConversion.getBit((byte) imageRestriction, 1) == 1) {
                    restrict = BitConversion.setBit(restrict, 1);
                }
            }

            b[bytesCopied++] = restrict;
        }

        return b;
    }

    /**
     * Returns the size of the extended header
     *
     * @return the size of the extended header
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the number of extended flag bytes
     *
     * @return the number of extended flag bytes
     */
    public int getFlagSize() {
        return flagSize;
    }

    /**
     * Returns the maximum number of frames if set.  If unset, returns -1
     *
     * @return the maximum number of frames or -1 if unset
     */
    public int getMaximumFrames() {
        int retval = -1;

        if ((maximumTagSize >= 0) && (maximumTagSize < MAXIMUM_TAG_FRAMES.length)) {
            retval = MAXIMUM_TAG_FRAMES[maximumTagSize];
        }

        return retval;
    }

    /**
     * Returns the maximum tag size or -1 if unset
     *
     * @return the maximum tag size or -1 if unset
     */
    public int getMaximumTagSize() {
        int retval = -1;

        if ((maximumTagSize >= 0) && (maximumTagSize < MAXIMUM_TAG_SIZES.length)) {
            retval = MAXIMUM_TAG_SIZES[maximumTagSize];
        }

        return retval;
    }

    /**
     * Returns true if the text encode flag is set
     *
     * @return true if the text encode flag is set
     */
    public boolean getTextEncoded() {
        return textEncoded;
    }

    /**
     * Returns the maximum length of a string if set or -1
     *
     * @return the maximum length of a string if set or -1
     */
    public int getMaximumTextSize() {
        int retval = -1;

        if ((maximumTextSize >= 0) && (maximumTextSize < MAXIMUM_TEXT_SIZES.length)) {
            retval = MAXIMUM_TEXT_SIZES[maximumTextSize];
        }

        return retval;
    }

    /**
     * Returns true if the image encode flag is set
     *
     * @return true if the image encode flag is set
     */
    public boolean getImageEncoded() {
        return imageEncoded;
    }

    /**
     * Returns the value of the image restriction field or -1 if not set
     *
     * @return the value of the image restriction field or -1 if not set
     */
    public int getImageRestriction() {
        return imageRestriction;
    }

    /**
     * Returns true if this tag is an update of a previous tag
     *
     * @return true if this tag is an update of a previous tag
     */
    public boolean getUpdate() {
        return update;
    }

    /**
     * Returns true if CRC information is provided for this tag
     *
     * @return true if CRC information is provided for this tag
     */
    public boolean getCRCed() {
        return crced;
    }

    /**
     * If there is crc data in the extended header, then the attached 5 byte
     * crc will be returned.  An empty array will be returned if this has
     * not been set.
     *
     * @return the attached crc data if there is any
     */
    public byte[] getCRC() {
        return crc;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "ID3v2ExtendedHeader[size=" + getSize() + ", " +
                "flagSize=" + getFlagSize() + ", " +
                "update=" + getUpdate() + ", " +
                "crced=" + getCRCed() + ", " +
                "maximumFrames=" + getMaximumFrames() + ", " +
                "maximumTagSize=" + getMaximumTagSize() + ", " +
                "textEncoded=" + getTextEncoded() + ", " +
                "maximumTextSize=" + getMaximumTextSize() + ", " +
                "imageEncoded=" + getImageEncoded() + ", " +
                "imageRestriction=" + getImageRestriction() + "]";
    }

    // --- member variables ------------------------------------

    private int size;
    private int flagSize;
    private boolean update;
    private boolean crced;
    private byte[] crc;
    private int maximumTagSize;
    private boolean textEncoded;
    private int maximumTextSize;
    private boolean imageEncoded;
    private int imageRestriction;
}
