/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.util.BitConversion;
import slash.metamusic.mp3.util.CRC16;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * My instances represent the properties of a MP3 File,
 * which is parsed. Then, information about the MP3 meta data
 * may be queried.
 *
 * @author Christian Pesch
 * @version $Id: MP3Properties.java 958 2007-02-28 14:44:37Z cpesch $
 */

public class MP3Properties extends AbstractAudioProperties {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v2Frame.class.getName());

    // (MPEG-1,   layer 1; MPEG-1,   layer 2; MPEG-1,   layer3;
    //  MPEG-2,   layer 1; MPEG-2,   layer 2; MPEG-2,   layer3;
    //  MPEG-2.5, layer 1; MPEG-2.5, layer 2; MPEG-2.5, layer3)
    public static final int[][] BIT_RATES = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {32, 32, 32, 32, 32, 8, 32, 32, 8},
            {64, 48, 40, 64, 48, 16, 40, 48, 16},
            {96, 56, 48, 96, 56, 24, 48, 56, 24},
            {128, 64, 56, 128, 64, 32, 56, 64, 32},
            {160, 80, 64, 160, 80, 64, 64, 80, 40},
            {192, 96, 80, 192, 96, 80, 80, 96, 48},
            {224, 112, 96, 224, 112, 56, 96, 112, 56},
            {256, 128, 112, 256, 128, 64, 112, 128, 64},
            {288, 160, 128, 288, 160, 128, 128, 144, 80},
            {320, 192, 160, 320, 192, 160, 160, 160, 96},
            {352, 224, 192, 352, 224, 112, 192, 176, 112},
            {384, 256, 224, 384, 256, 128, 224, 192, 128},
            {416, 320, 256, 416, 320, 256, 256, 224, 144},
            {448, 384, 320, 448, 384, 320, 320, 256, 160},
            {0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    // MPEG-1; MPEG-2; MPEG-2.5
    public static final int[][] SAMPLE_FREQUENCIES = {
            {44100, 22050, 11025},
            {48000, 24000, 12000},
            {32000, 16000, 8000}
    };

    public static final String[] VERSION_STRINGS = {
            "MPEG-1", "MPEG-2", "MPEG-2.5"
    };

    public static final String[] LAYER_STRINGS = {
            "I", "II", "III"
    };

    /**
     * Constants for emphasis
     */
    public final static int EMPHASIS_NONE = 0;
    public final static int EMPHASIS_5015MS = 1;
    public final static int EMPHASIS_RESERVED = 2;
    public final static int EMPHASIS_CCITT_J17 = 3;

    public static final int[] EMPHASISES = {
            EMPHASIS_NONE, EMPHASIS_RESERVED, EMPHASIS_5015MS, EMPHASIS_CCITT_J17
    };

    public static final String[] EMPHASIS_STRINGS = {
            "none", "reserved", "50/15ms", "CCIT J.17"
    };

    public static final String VBR_FLAG = "Xing";
    public static final int FRAMES_FLAG = 0x0001;
    public static final int BYTES_FLAG = 0x0002;
    public static final int TOC_FLAG = 0x0004;
    public static final int VBR_SCALE_FLAG = 0x0008;

    public static final int CHECKSUM_SIZE = 2;
    private static final int PADDING_SEARCH_SIZE = 256 + 16;

    public static final int MILLISECONDS_PER_FRAME = 26;

    /**
     * Construct new MP3 properties.
     */
    public MP3Properties() {
    }

    // --- read/write object -----------------------------------

    public boolean read(InputStream in) throws NoMP3FrameException, IOException {
        valid = false;
        readSize = 0;

        // synchronize to next MP3 frame
        // usually, this should not be necessary
        int second = synchronize(in);
        int third = in.read();
        int fourth = in.read();

        if (readSize > 0) {
            log.fine("First FFFB after " + readSize + " bytes");
        }

        // second, third and fourth now contain the second, third and fourth byte of
        // MP3 frame header, respectively
        valid = parse(second, third, fourth);

        // header checksum
        if (valid && protection) {
            byte[] buffer = new byte[CHECKSUM_SIZE];
            if (in.read(buffer, 0, CHECKSUM_SIZE) != CHECKSUM_SIZE)
                throw new IOException("Read invalid checksum");

            checksum = (short) BitConversion.extract2Endian(buffer);

            CRC16 crc = new CRC16();
            crc.update(0xFF, 8);
            crc.update(second, 8);
            crc.update(third, 8);
            crc.update(fourth, 8);
            short compare = crc.getValue();

            if (checksum != compare)
                log.severe("Invalid header checksum found: " + checksum + ", calculated: " + compare);
        }

        vbr = searchVBR(in);

        return valid;
    }

    protected boolean parse(int second, int third, int fourth) throws UnsupportedEncodingException {
        valid = false;

        version = convertMPEGVersion(BitConversion.getBit(second, 4), BitConversion.getBit(second, 3));
        layer = convertLayer(BitConversion.getBit(second, 2), BitConversion.getBit(second, 1));
        // protection bit in invers (1: no crc)
        protection = ((BitConversion.getBit(second, 0)) == 0);

        bitrate = convertBitrate(BitConversion.getBit(third, 7), BitConversion.getBit(third, 6),
                BitConversion.getBit(third, 5), BitConversion.getBit(third, 4)) * 1000;
        sampleFrequency = convertSampleFrequency(BitConversion.getBit(third, 3), BitConversion.getBit(third, 2));
        padding = (BitConversion.getBit(third, 1) == 1);
        privated = (BitConversion.getBit(third, 0) == 1);

        mode = convertMode(BitConversion.getBit(fourth, 7), BitConversion.getBit(fourth, 6));
        modeExtension = convertModeExtension(BitConversion.getBit(fourth, 5), BitConversion.getBit(fourth, 4));

        copyrighted = (BitConversion.getBit(fourth, 3) == 1);
        original = (BitConversion.getBit(fourth, 2) == 1);
        emphasis = convertEmphasis(BitConversion.getBit(fourth, 1), BitConversion.getBit(fourth, 0));

        valid = true;

        return valid;
    }

    /**
     * Converts bit to MPEG version.
     * <p/>
     * Bit  Version
     * 1 1  MPEG-1
     * 1 0  MPEG-2
     * 0 0  MPEG-2.5
     * <p/>
     * Note: Uses an int to represent a bit
     */
    protected int convertMPEGVersion(int in1, int in2) {
        if (in1 == 1) {
            if (in2 == 1) {
                // 1 1 = MPEG-1
                return 1;

            } else {
                // 1 0 = MPEG-2
                return 2;
            }
        } else {
            if (in2 == 0) {
                // 0 0 = MPEG-2.5
                return 3;

            } else {
                // Illegal combination
                return 0;
            }
        }
    }

    /**
     * Convert 2 bits to layer:
     * <p/>
     * Bit  Layer
     * 0 0  Not defined
     * 0 1  Layer III
     * 1 0  Layer II
     * 1 1  Layer I
     * <p/>
     * Note: Uses an int to represent a bit
     */
    protected int convertLayer(int in1, int in2) {
        if (in1 == 0 && in2 == 0) {
            // Illegal combination
            return 0;

        } else {
            // Layer is 4-in value
            return (4 - ((in1 << 1) + in2));
        }
    }

    /**
     * Convert 4 bits to bitrate
     *
     * @param in1 eighth bit of the third byte of the MPEG header represented by an int
     * @param in2 seventh bit of the third byte of the MPEG header represented by an int
     * @param in3 sixth bit of the third byte of the MPEG header represented by an int
     * @param in4 fifth bit of the third byte of the MPEG header represented by an int
     * @return the bitrate in Kbit/s
     */
    protected int convertBitrate(int in1, int in2, int in3, int in4) {
        // first index is the input (combined to one byte)
        int index1 = (in1 << 3) | (in2 << 2) | (in3 << 1) | in4;

        // second index is MPEG version and layer
        int index2 = (version - 1) * 3 + layer - 1;

        if (index1 < 0 || index1 > BIT_RATES.length) {
            log.severe("Unknown bitrate index: " + index1);
            return 0;
        }
        if (index2 < 0 || index2 > BIT_RATES[index1].length) {
            log.severe("Unknown MPEG version and layer index: " + index2);
            return 0;
        }

        return BIT_RATES[index1][index2];
    }

    /**
     * Convert 2 bits to sample frequency
     *
     * @param in1 fourth bit of the third byte of the MPEG header represented by an int
     * @param in2 third bit of the third byte of the MPEG header represented by an int
     * @return the sample frequency in Hz
     */
    protected int convertSampleFrequency(int in1, int in2) {
        // first index is input (combined to one byte)
        int index1 = (in1 << 1) | in2;

        // second index is MPEG version
        int index2 = version - 1;

        if (index1 < 0 || index1 >= SAMPLE_FREQUENCIES.length) {
            log.severe("Unknown sample frequency index: " + index1);
            return 0;
        }
        if (index2 < 0 || index2 >= SAMPLE_FREQUENCIES[index1].length) {
            log.severe("Unknown MPEG version: " + index2);
            return 0;
        }

        return SAMPLE_FREQUENCIES[index1][index2];
    }

    /**
     * Convert 2 bits to mode
     * <p/>
     * Note: Uses an int to represent a bit
     */
    protected int convertMode(int in1, int in2) {
        int index = (in1 << 1) | in2;

        // illegal values
        if (index < 0 || index > modes.length)
            return 0;

        return modes[index];
    }

    /**
     * Convert 2 bits to mode extension
     * <p/>
     * Note: Uses an int to represent a bit
     */
    protected int convertModeExtension(int in1, int in2) {
        //noinspection UnnecessaryLocalVariable
        int index = (in1 << 1) | in2;

        // The purpose of the mode extension field is different for
        // different layers, but I really don't know exactly what it's
        // for.

        return index;
    }

    /**
     * Convert 2 bits to emphasis
     * <p/>
     * Note: Uses an int to represent a bit
     */
    protected int convertEmphasis(int in1, int in2) {
        int index = (in1 << 1) | in2;

        // illegal values
        if (index < 0 || index > EMPHASISES.length)
            return 0;

        return EMPHASISES[index];
    }

    /**
     * Sets input stream to third byte of MP3 frame header (first byte
     * is 0xff, second is consumed in synchronizing) and returns the
     * byte already consumed.
     *
     * @param in Stream to read from
     * @return Second byte of MP3 frame header
     * @throws IOException         If an I/O error occurs
     * @throws NoMP3FrameException If file does not contain at least one mp3 frame
     */
    protected int synchronize(InputStream in) throws IOException, NoMP3FrameException {
        // skip until start of header (at least 11 bits in a row set to 1)
        boolean finished = false;

        int store = 0;
        while (!finished) {
            // read through stream until 0xFF is read
            int skip = readByte(in);

            while (skip != 0xFF && skip != -1) {
                skip = readByte(in);
            }

            if (skip == -1) {
                // End of stream reached without finding a frame
                throw new NoMP3FrameException();
            }

            // now next byte must to >= 0xE0
            store = readByte(in);

            if (store >= 0xE0) {
                // synchronized
                finished = true;

            } else if (store == -1) {
                // End of stream reached without finding a frame
                throw new NoMP3FrameException();

            } else {
                // continue search
            }
        }

        // reduce the read size by the 0xFFFB header already read
        readSize -= 2;

        // if we reach this point, an MP3 frame has been found. If
        // file does not contain one, method has already thrown an
        // NoMP3FrameException
        return store;
    }

    /**
     * Searches for a VBR header.
     * <p/>
     * <p>Xing VBR Headers explained:
     * <p/>
     * Each frame represents exactly .026 seconds of playback time.
     * But frames in a VBR mp3 vary in size, according to the bit rate
     * and encoding methods used, on a frame by frame basis.
     * <p/>
     * This makes random seeks to percentage points within a VBR file
     * rather difficult without reading/decoding the file to count
     * frames up to the seek point -- which also requires knowing the
     * file size and frame count in advance.
     * <p/>
     * The Xing VBR header eases this problem, using a header which
     * (optionally) provides a framecount (Xframes), byte count (Xbytes),
     * and a 100-position TOC for seeking to percentages with the file.
     * The seeks are not exactly dead on, so the software using them
     * must then scan for the next Sync pattern to find the next frame
     * after seeking to the byte offset indicated.
     * <p/>
     * The seek table itself does not store byte offsets, but rather
     * scale factors from 0 to 255 representing the seek points as the
     * number of 1/256's of the filesize.
     * <p/>
     * For example, to do a seek to the P% point one would do something
     * like this to calculate the corresponding byte offset:
     * <p/>
     * if (P <  0) P =  0;
     * if (P > 99) P = 99;
     * byteoffset = Xbytes * Xtoc[P] / 256;
     * <p/>
     * If the percentage is a fractional (floating point) value,
     * then we must interpolate between two table entries for
     * more accurate positioning:
     * <p/>
     * int Pi; float Tp, Tr;
     * if (P <   0.0) P =   0.0;
     * if (P > 100.0) P = 100.0;
     * Pi = (int)P;
     * if (Pi >= 99) {
     * Tp = Xtoc[Pi = 99];
     * Tr = 256.0;
     * } else {
     * Tp = Xtoc[Pi];
     * Tr = Xtoc[Pi+1];
     * }
     * P = ( (P - Pi) * (Tr - Tp) + Tp ) * (1.0 / 256.0);
     * byteoffset = (int)(P * Xbytes);
     * <p/>
     * The Xing header also includes an "Xscale" value, the use for which
     * appears to be a complete mystery.  If anyone can explain to me what
     * this field is, and how to use it, then I will add code here to validate
     * and repair it as well.  For now, we mostly just leave it alone. </p>
     *
     * @param in Stream to read from
     * @return whether the MP3 file is a variable bitrate file
     * @throws IOException If an I/O error occurs
     */
    protected boolean searchVBR(InputStream in) throws IOException {
        boolean vbr = false;
        int vbrSkip;

        // MPEG-1
        if (version == 1) {
            vbrSkip = (mode != 3 ? 32 : 17);

            // MPEG-2 & 2.5
        } else {
            vbrSkip = (mode != 3 ? 17 : 9);
        }

        // search for vbr
        if (in.skip(vbrSkip) != vbrSkip)
            throw new IOException("Cannot read " + vbrSkip + "bytes");

        if (BitConversion.findString(in, VBR_FLAG, ENCODING)) {
            byte[] buffer = new byte[4];
            if (in.read(buffer, 0, buffer.length) != buffer.length)
                throw new IOException("Cannot read " + buffer.length + "bytes");
            int headFlags = BitConversion.extract4BigEndian(buffer);

            if (in.read(buffer, 0, buffer.length) != buffer.length)
                throw new IOException("Cannot read " + buffer.length + "bytes");
            frames = 0;
            if ((headFlags & FRAMES_FLAG) != 0) {
                frames = BitConversion.extract4BigEndian(buffer);
                if (frames <= 0)
                    log.severe("No VBR frame count found");

                if (in.read(buffer, 0, buffer.length) != buffer.length)
                    throw new IOException("Cannot read " + buffer.length + "bytes");
            }

            if ((headFlags & BYTES_FLAG) != 0) {
                setFileSize(BitConversion.extract4BigEndian(buffer));
                if (in.read(buffer, 0, buffer.length) != buffer.length)
                    throw new IOException("Cannot read " + buffer.length + "bytes");
            }

            if ((headFlags & TOC_FLAG) != 0) {
                byte[] toc = new byte[100];
                if (in.read(toc, 0, toc.length) != toc.length)
                    throw new IOException("Cannot read " + toc.length + "bytes");

                // for(int i=0; i <= 100; i += 5)
                //   log.fine(i+"% at byte: "+seekPoint(toc, fileSizeWithoutHeader, i));
            }

            if (in.read(buffer, 0, buffer.length) != buffer.length)
                throw new IOException("Cannot read " + buffer.length + "bytes");
            if ((headFlags & VBR_SCALE_FLAG) != 0) {
                /*int vbrScale =*/
                BitConversion.extract4BigEndian(buffer);
                encoder = new String(buffer, ENCODING);
            }

            byte[] large = new byte[PADDING_SEARCH_SIZE];
            if (in.read(large, 0, large.length) != large.length)
                throw new IOException("Cannot read " + large.length + "bytes");

            int count = 0;
            // TODO 070105: removed space condition as iTunes writes things like "iTunes v7.0.2"
            while (count < large.length && large[count] != 0 && /*large[count] != 32 &&*/ large[count] < 127)
                count++;

            encoder = encoder + new String(large, 0, count, ENCODING);

            // TODO EXPERIMENTAL: Search for beginning of next frame
            int ff = count;
            while (ff < large.length && large[ff] != -1)
                ff++;
            if (ff != 257)
                log.warning("Next FF frame after " + ff + " bytes");

            vbr = true;
        }

        return vbr;
    }

    /**
     * Interpolate in TOC to get file seek point in bytes
     */
    protected int seekPoint(byte[] toc, long fileSize, double percent) {
        if (percent < 0.0)
            percent = 0.0;
        if (percent > 100.0)
            percent = 100.0;

        int a = (int) percent;
        if (a > 99)
            a = 99;

        double fa = BitConversion.unsignedByteToInt(toc[a]);
        double fb = 0.0;
        if (a < 99) {
            fb = BitConversion.unsignedByteToInt(toc[a + 1]);
        } else {
            fb = 256.0;
        }
        double fx = fa + (fb - fa) * (percent - a);

        return (int) ((1.0 / 256.0) * fx * fileSize);
    }

    // --- get object ------------------------------------------

    public boolean isMP3() {
        return isValid();
    }

    public boolean isWAV() {
        return false;
    }

    public boolean isOgg() {
        return false;
    }

    public int getFrames() {
        if (vbr)
            return frames;

        // for cbr, every frame has the same size
        return (int) (getFrameSize() > 0 ? (getDataSize() / getFrameSize()) : 0);
    }

    public int getFrameSize() {
        if (!vbr) {
            // avoid division by zero error
            if (getSampleFrequency() == 0)
                return 0;

            // MPEG-2.5 has other constant
            if (version == 3)
                return (int) (12 * getBitRate() / (getSampleFrequency() + (getPadding() ? 1 : 0)));
            else
                return (int) (144 * getBitRate() / (getSampleFrequency() + (getPadding() ? 1 : 0)));
        }

        // for vbr just average the frame size about the known frame count
        return (int) (getDataSize() / frames);
    }

    public long getBitRate() {
        if (bitrate == 0) {
            log.severe("Undefined bit rate in MP3 properties");
            return 0;
        }

        // for vbr just average the bitrate about the known frame count
        if (vbr)
            return getDataSize() * 8 / (frames * MILLISECONDS_PER_FRAME) * 1000;
        else
            return bitrate;
    }

    public boolean isVBR() {
        return vbr;
    }

    public int getSeconds() {
        long bitrate = getBitRate();
        if (bitrate == 0)
            return 0;

        return (int) Math.ceil(getDataSize() * 8.0 / bitrate);
    }

    public int getMPEGVersion() {
        return version;
    }

    public String getMPEGVersionString() {
        if (version < 1 || version > VERSION_STRINGS.length) {
            log.severe("Unknown MPEG version: " + version);
            return Integer.toString(version);
        }

        return VERSION_STRINGS[version - 1];
    }

    public int getMPEGLayer() {
        return layer;
    }

    public String getMPEGLayerString() {
        if (layer < 1 || layer > LAYER_STRINGS.length) {
            log.severe("Unknown MPEG layer: " + layer);
            return Integer.toString(layer);
        }

        return LAYER_STRINGS[layer - 1];
    }

    public boolean getPadding() {
        return padding;
    }

    public int getCRC() {
        if (!protection)
            return 0;

        return checksum;
    }

    public int getMode() {
        return mode;
    }

    public int getModeExtension() {
        return modeExtension;
    }

    public boolean isProtected() {
        return protection;
    }

    public boolean isPrivate() {
        return privated;
    }

    public boolean isCopyrighted() {
        return copyrighted;
    }

    public boolean isOriginal() {
        return original;
    }

    public int getEmphasis() {
        return emphasis;
    }

    public String getEmphasisString() {
        return EMPHASIS_STRINGS[emphasis];
    }

    public String getEncoder() {
        return encoder;
    }

    // --- member variables ------------------------------------

    /**
     * MPEG properties
     */
    protected int version = 0;
    protected int layer = 0;
    protected int bitrate = 0;
    protected int frames = 0;
    protected boolean protection = false;
    protected short checksum = 0;
    protected boolean padding = false;
    protected int mode = 0;
    protected int modeExtension = 0;
    protected boolean privated = false;
    protected boolean copyrighted = false;
    protected boolean original = false;
    protected int emphasis = 0;

    protected String encoder = "";
}
