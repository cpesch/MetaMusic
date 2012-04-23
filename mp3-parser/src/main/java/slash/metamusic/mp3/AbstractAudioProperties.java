/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.util.BitConversion;

import java.io.IOException;
import java.io.InputStream;

/**
 * My instances represent the properties an audio file,
 * which is parsed. Then, information about the meta data
 * may be queried.
 *
 * @author Christian Pesch
 * @version $Id: AbstractAudioProperties.java 819 2006-10-21 14:43:36 +0200 (Sa, 21 Okt 2006) cpesch $
 */

public abstract class AbstractAudioProperties {

    /**
     * Constants for mode
     */
    public final static int MODE_STEREO = 0;
    public final static int MODE_JOINT_STEREO = 1;
    public final static int MODE_DUAL_CHANNEL = 2;
    public final static int MODE_SINGLE_CHANNEL = 3;
    public final static int MODE_UNKNOWN = 4;

    public static final int[] modes = {
            MODE_STEREO, MODE_JOINT_STEREO, MODE_DUAL_CHANNEL, MODE_SINGLE_CHANNEL, MODE_UNKNOWN
    };

    public static final String[] modeStrings = {
            "stereo", "joint stereo", "dual channel", "single channel", "unknown"
    };

    /**
     * Encoding to use when converting from bytes to Unicode (String).
     */
    protected static final String ENCODING = "ISO8859_1";

    // --- read/write object -----------------------------------

    /**
     * Read properties from audio stream
     *
     * @param in the InputStram to read from
     * @return true if the read properties are valid
     * @throws IOException if an error occurs
     */
    public abstract boolean read(InputStream in) throws NoMP3FrameException, IOException;

    // --- get object ------------------------------------------

    public boolean isValid() {
        return valid;
    }

    public abstract boolean isMP3();

    public abstract boolean isWAV();

    public abstract boolean isOgg();


    public long getReadSize() {
        return readSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    long getMetaDataSize() {
        return metaDataSize;
    }

    void setMetaDataSize(long metaDataSize) {
        this.metaDataSize = metaDataSize;
    }

    protected long getDataSize() {
        return getFileSize() - getMetaDataSize();
    }

    public boolean isVBR() {
        return vbr;
    }

    public long getSampleFrequency() {
        return sampleFrequency;
    }

    public abstract int getMode();

    public String getModeAsString() {
        int mode = getMode();
        if (mode < 0 || mode > modeStrings.length - 1)
            mode = modeStrings.length - 1;
        return modeStrings[mode];
    }

    public abstract long getBitRate();

    public abstract int getSeconds();

    public String getTimeString() {
        long total = getSeconds();
        long minutes = total / 60;
        long seconds = total - minutes * 60;
        return Long.toString(minutes) + ":" + (seconds < 10 ? "0" : "") + Long.toString(seconds);
    }

    // --- read/write helpers ----------------------------------

    protected boolean findString(InputStream in, String find, String encoding) throws IOException {
        readSize += find.length();
        return BitConversion.findString(in, find, encoding);
    }

    protected int readByte(InputStream in) throws IOException {
        int read = in.read();
        readSize++;
        return read;
    }

    // --- member variables ------------------------------------

    /**
     * file data
     */
    protected boolean valid = false;
    protected long fileSize = 0;
    protected long readSize;
    protected long metaDataSize = 0;

    /**
     * meta data properties
     */
    protected boolean vbr = false;
    protected long sampleFrequency = 0;
}
