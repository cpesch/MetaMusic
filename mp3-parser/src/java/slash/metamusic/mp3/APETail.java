/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.util.BitConversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * My instances represent a APEv2 tail of the MP3 frames as
 * described in http://wiki.hydrogenaudio.org/index.php?title=APEv2.
 *
 * @author Christian Pesch
 * @version $Id: APEv2Tail.java 954 2009-02-2( 15:31:50Z cpesch $
 */

public class APETail implements ID3MetaData {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(APETail.class.getName());

    public static final String APEV2TAG = "APETAGEX";
    public static final int VERSION_NUMBER_SIZE = 4;
    public static final int TAG_SIZE_SIZE = 4;
    public static final int ITEM_COUNT_SIZE = 4;
    public static final int TAGS_FLAGS_SIZE = 4;
    public static final int RESERVED_SIZE = 8;

    /**
     * Encoding to use when converting from bytes to Unicode (String).
     */
    protected static final String ENCODING = "ISO8859_1";

    /**
     * Create a new (empty) tail.
     */
    public APETail() {
        valid = true;
    }

    // --- read/write object -----------------------------------

    /**
     * Reads the APEv2Tail tail from the InputStream.
     *
     * @param in the InputStream to read
     * @param bufferSize the buffer on the tail
     * @return if the read tail is valid
     * @throws NoAPEv2TailException if no tail can be found or exists
     * @throws IOException          if an error occurs
     */
    public boolean read(InputStream in, int bufferSize) throws NoAPEv2TailException, IOException {
        valid = false;
        readSize = 0;

        log.fine("Reading APEv2 tail");

        // search for a valid tail
        boolean tailFound = false;
        while (checkForAPEv2Tail(in, bufferSize)) {
            tailFound = parse(in);
            if (tailFound) {
                valid = true;
                break;
            }
        }

        if (!tailFound)
            throw new NoAPEv2TailException();

        return valid;
    }

    /**
     * Check if APEv2 tail is present
     *
     * @param in the InputStream to read
     * @param bufferSize the buffer on the tail
     * @return true if tail is present
     * @throws IOException if an error occurs
     */
    protected boolean checkForAPEv2Tail(InputStream in, int bufferSize) throws IOException {
        byte tag[] = APEV2TAG.getBytes(ENCODING);
        int readSize = 0;

        while (true) {
            // read through stream until first byte of the tag is read
            int read = in.read(); readSize++;
            while (read != tag[0] && read != -1 && readSize < bufferSize) {
                read = in.read(); readSize++;
            }

            if (read == -1 || readSize >= bufferSize) {
                // first tag byte not found but stream finished
                return false;
            }

            // check if rest of the tag matches
            int count = 1;
            while (count < tag.length && readSize < bufferSize) {
                read = in.read(); readSize++;
                if (read == -1 || readSize >= bufferSize) {
                    // tag byte not found but stream finished
                    return false;
                }

                if (read == tag[count]) {
                    count++;
                } else
                    break;
            }

            if (count == tag.length) {
                // synchronized
                return true;
            }
        }
    }

    protected boolean parse(InputStream in) throws NoAPEv2TailException, IOException {
        byte[] buffer = new byte[VERSION_NUMBER_SIZE];
        if (in.read(buffer, 0, VERSION_NUMBER_SIZE) != VERSION_NUMBER_SIZE)
            return false;
        int versionCode = BitConversion.extract4LittleEndian(buffer);
        version = versionCode == 2000 ? "2" : (versionCode == 1000) ? "1" : "?";

        buffer = new byte[TAG_SIZE_SIZE];
        if (in.read(buffer, 0, TAG_SIZE_SIZE) != TAG_SIZE_SIZE)
            return false;
        tagSize = BitConversion.extract4LittleEndian(buffer);

        buffer = new byte[ITEM_COUNT_SIZE];
        if (in.read(buffer, 0, ITEM_COUNT_SIZE) != ITEM_COUNT_SIZE)
            return false;
        itemCount = BitConversion.extract4LittleEndian(buffer);

        buffer = new byte[TAGS_FLAGS_SIZE];
        if (in.read(buffer, 0, TAGS_FLAGS_SIZE) != TAGS_FLAGS_SIZE)
            return false;
        tagsFlags = BitConversion.extract4LittleEndian(buffer);

        buffer = new byte[RESERVED_SIZE];
        if (in.read(buffer, 0, RESERVED_SIZE) != RESERVED_SIZE)
            return false;

        byte[] tail = new byte[tagSize];
        //noinspection RedundantIfStatement
        if (in.read(tail, 0, tagSize) != tagSize)
            return false;

        return true;
    }

    // --- get object ------------------------------------------

    public boolean isValid() {
        return valid;
    }

    public long getReadSize() {
        return readSize;
    }

    // --- MetaData get ----------------------------------------

    public String getTrack() {
        throw new UnsupportedOperationException();
    }

    public String getArtist() {
        throw new UnsupportedOperationException();
    }

    public String getAlbum() {
        throw new UnsupportedOperationException();
    }

    public int getYear() {
        throw new UnsupportedOperationException();
    }

    public ID3Genre getGenre() {
        throw new UnsupportedOperationException();
    }

    public String getComment() {
        throw new UnsupportedOperationException();
    }

    public int getIndex() {
        throw new UnsupportedOperationException();
    }

    public String getVersion() {
        return version;
    }

    // --- set object ------------------------------------------

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    // --- MetaData set ----------------------------------------

    public void setTrack(String newTrack) {
        throw new UnsupportedOperationException();
    }

    public void setArtist(String newArtist) {
        throw new UnsupportedOperationException();
    }

    public void setAlbum(String newAlbum) {
        throw new UnsupportedOperationException();
    }

    public void setYear(int newYear) {
        throw new UnsupportedOperationException();
    }

    public void setGenre(ID3Genre newGenre) {
        throw new UnsupportedOperationException();
    }

    public void setIndex(int newIndex) {
        throw new UnsupportedOperationException();
    }

    public void setComment(String newComment) {
        throw new UnsupportedOperationException();
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("APETail[isValid=").append(isValid());
        if (isValid()) {
            buffer.append(", version=").append(getVersion());
            buffer.append(", size=").append(tagSize);
            buffer.append(", items=").append(itemCount);
        }
        buffer.append("]");

        return buffer.toString();
    }

    // --- member variables ------------------------------------

    /**
     * file data
     */
    protected boolean valid;
    protected long readSize;

    protected String version;
    protected int tagSize, itemCount, tagsFlags;
}