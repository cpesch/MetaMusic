/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import java.io.IOException;
import java.io.InputStream;

/**
 * My instances represent the properties an Ogg or OggVorbis audio file,
 * which is parsed. Then, information about the Ogg meta data may
 * be queried.
 *
 * @author Christian Pesch
 * @version $Id: OggProperties.java 203 2004-03-18 14:33:32Z cpesch $
 */

public class OggProperties extends AbstractAudioProperties {
    public static final String OGG_HEADER = "OggS";
    private static final int OGG_PAGE_SIZE = 58;

    /**
     * Construct new (empty) Ogg properties.
     */
    public OggProperties() {
    }

    // --- read/write object -----------------------------------

    public boolean read(InputStream in) throws NoMP3FrameException, IOException {
        valid = false;
        readSize = 0;

        if (!findString(in, OGG_HEADER, ENCODING))
            return valid;

        // skip first ogg page
        int skip = OGG_PAGE_SIZE - OGG_HEADER.length();
        in.skip(skip);
        readSize += skip;

        if (!findString(in, OGG_HEADER, ENCODING))
            return valid;

        // TODO add more ogg features here

        valid = true;

        return valid;
    }

    // --- get object ------------------------------------------

    public boolean isMP3() {
        return false;
    }

    public boolean isWAV() {
        return false;
    }

    public boolean isOgg() {
        return isValid();
    }

    public int getMode() {
        return -1; // TODO add more ogg features here
    }

    public long getBitRate() {
        return -1; // TODO add more ogg features here
    }

    public int getSeconds() {
        return -1; // TODO add more ogg features here
    }

    // --- member variables ------------------------------------

    /** Ogg properties */
}
