/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2006 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Some useful methods to create and manipulate MP3File objects
 *
 * @author Christian Pesch
 * @version $Id: MP3Files.java 794 2006-04-22 16:31:40 +0200 (Sa, 22 Apr 2006) cpesch $
 */

public class MP3Files {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3Files.class.getName());

    /**
     * Parse the given list of files and returns mp3 file objects.
     *
     * @param files the list of files to parse
     * @return parsed mp3 file objects
     */
    public static List<MP3File> parseMP3Files(List<File> files) {
        List<MP3File> mp3s = new ArrayList<MP3File>(files.size());
        for (File file : files) {
            MP3File mp3 = MP3File.readValidFile(file);
            if (mp3 != null)
                mp3s.add(mp3);
            else
                log.fine("Skipping invalid mp3 file " + file.getAbsolutePath());
        }
        log.fine("Parsed " + mp3s.size() + " mp3 files from " + files.size() + " files");
        return mp3s;
    }
}
