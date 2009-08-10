/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2006 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.tools;

import slash.metamusic.mp3.ID3v2Version;
import slash.metamusic.mp3.MP3File;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A base class to modify MP3s.
 *
 * @author Christian Pesch
 * @version $Id: BaseMP3Modifier.java 475 2005-01-14 17:31:47Z cpesch $
 */

public abstract class BaseMP3Modifier {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(BaseMP3Modifier.class.getName());

    private boolean writeID3v1 = false, writeID3v2 = true;

    public boolean isWriteID3v1() {
        return writeID3v1;
    }

    public void setWriteID3v1(boolean writeID3v1) {
        this.writeID3v1 = writeID3v1;
    }

    public boolean isWriteID3v2() {
        return writeID3v2;
    }

    public void setWriteID3v2(boolean writeID3v2) {
        this.writeID3v2 = writeID3v2;
    }

    /**
     * Extend the given MP3 file, i.e.
     * <ul>
     * <li>add MusicBrainz id and</li>
     * <li>add cover information to MP3 file.</li>
     * </ul>
     *
     * @param file the {@link MP3File} to operate on
     */
    public void write(MP3File file) {
        file.setID3v1(isWriteID3v1());
        file.setID3v2(isWriteID3v2());
        if (isWriteID3v2())
            file.getHead().migrateToVersion(new ID3v2Version());
        file.setMetaMusicComment();

        try {
            file.write();
        } catch (IOException e) {
            log.severe("Cannot write " + file.getFile().getAbsolutePath() + ": " + e.getMessage());
        }
    }
}
