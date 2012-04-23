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
import java.util.logging.Logger;

/**
 * My instances represent the properties an WAV audio file,
 * which is parsed. Then, information about the WAV meta data may
 * be queried.
 *
 * @author Christian Pesch
 * @version $Id: WAVProperties.java 242 2004-04-19 14:34:59Z cpesch $
 */

public class WAVProperties extends AbstractAudioProperties {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(WAVProperties.class.getName());

    public static final String RIFF_HEADER = "RIFF";
    public static final String WAVE_HEADER = "WAVE";
    public static final String FMT_CHUNK = "fmt ";
    public static final String DATA_CHUNK = "data";

    /**
     * Construct new (empty) WAV properties.
     */
    public WAVProperties() {
    }

    // --- read/write object -----------------------------------

    public boolean read(InputStream in) throws NoMP3FrameException, IOException {
        valid = false;
        readSize = 0;

        if (!findString(in, RIFF_HEADER, ENCODING))
            return valid;

        long fileSize = BitConversion.extract4LittleEndian(in);
        setFileSize(fileSize + RIFF_HEADER.length() + 4);
        readSize += 4;

        if (!findString(in, WAVE_HEADER, ENCODING))
            return valid;

        if (!findString(in, FMT_CHUNK, ENCODING))
            return valid;

        long fmtDataSize = BitConversion.extract4LittleEndian(in);
        readSize += 4;
        if (16 != fmtDataSize) {
            log.severe("fmt size differs: 16 bytes expected, but " + fmtDataSize + " bytes in header");
            return valid;
        }

        format = BitConversion.extract2LittleEndian(in);
        channels = BitConversion.extract2LittleEndian(in);
        sampleFrequency = BitConversion.extract4LittleEndian(in);
        bytesPerSecond = BitConversion.extract4LittleEndian(in);
        blockAlign = BitConversion.extract2LittleEndian(in);
        bitsPerSample = BitConversion.extract2LittleEndian(in);
        readSize += 16;

        // according to the spec, a DATA_CHUNK should follow, but I've
        // found chunks, so this is disabled
        // if(!findString(in, DATA_CHUNK))
        //   return valid;

        valid = true;

        return valid;
    }

    // --- get object ------------------------------------------

    public boolean isMP3() {
        return false;
    }

    public boolean isWAV() {
        return isValid();
    }

    public boolean isOgg() {
        return false;
    }

    public int getFormat() {
        return format;
    }

    public int getChannels() {
        return channels;
    }

    public long getBytesPerSecond() {
        return bytesPerSecond;
    }

    public int getBlockAlign() {
        return blockAlign;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getMode() {
        switch (getChannels()) {
            case 1:
                return MODE_SINGLE_CHANNEL;
            case 2:
                return MODE_DUAL_CHANNEL;
            default:
                return MODE_UNKNOWN;
        }
    }

    public long getBitRate() {
        return getBytesPerSecond() * 8;
    }

    public int getSeconds() {
        if (getBytesPerSecond() != getSampleFrequency() * getBlockAlign())
            log.severe("Inconsistent bytes per second, header=" + getBytesPerSecond() +
                    " properties=" + (getSampleFrequency() * getBlockAlign()));
        if (getBlockAlign() != (getChannels() * getBitsPerSample() / 8))
            log.severe("Inconsistent block align, header=" + getBlockAlign() +
                    " properties=" + (getChannels() * getBitsPerSample() / 8));

        return (int) (getDataSize() / getBytesPerSecond());
    }

    // --- member variables ------------------------------------

    /**
     * WAV properties
     */
    protected int format = 0;
    protected int channels = 0;
    protected long bytesPerSecond = 0;
    protected int blockAlign = 0;
    protected int bitsPerSample = 0;
}
