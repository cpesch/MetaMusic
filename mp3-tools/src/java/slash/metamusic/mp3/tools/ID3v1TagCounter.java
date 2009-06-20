/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class to count the occurrences of ID3v1 tags for all MP3s in a file system tree.
 *
 * @author Christian Pesch
 * @version $Id: ID3v1TagCounter.java 910 2006-12-23 12:18:38Z cpesch $
 */

public class ID3v1TagCounter {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v1TagCounter.class.getName());

    /**
     * Count the occurences of ID3v1 tags for all MP3s from the given path.
     *
     * @param path      the path to process
     * @param extension the file extension of the files to process
     * @throws IOException if an access error occurs and the processing stops
     */
    public void process(File path, String extension) throws IOException {
        log.info("Counting meta data below " + path.getAbsolutePath());

        List<File> files = Files.collectFiles(path, extension);
        if (files.size() == 0) {
            log.warning("No files found below " + path.getAbsolutePath());
            return;
        }

        log.info("Parsing " + files.size() + " files for meta data extension");

        int count = 0, notMP3Count = 0, invalidCount = 0, id3v1Count = 0, id3v2Count = 0;
        for (File file : files) {
            MP3File mp3 = MP3File.readValidFile(file);
            log.info("Processing " + (++count) + ". from " + files.size() + " files: " + mp3);
            if (mp3 != null) {
                if (!mp3.isValid()) {
                    invalidCount++;
                    log.warning(file.getAbsolutePath() + " is invalid");
                }
                if (mp3.isID3v1())
                    id3v1Count++;
                if (mp3.isID3v2())
                    id3v2Count++;
            } else {
                notMP3Count++;
                log.warning(file.getAbsolutePath() + " is not an MP3");
            }
        }

        log.info("From " + count + " files " + notMP3Count + " are not MP3s " + invalidCount + " are invalid MP3s");
        log.info(id3v1Count + " carry an ID3v1 tag and " + id3v2Count + " an ID3v2 tag");
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.mp3.tools.ID3v1TagCounter <path>");
            System.exit(1);
        }

        String pathName = args[0];
        File path = new File(pathName);
        ID3v1TagCounter counter = new ID3v1TagCounter();
        counter.process(path, "mp3");
        System.exit(0);
    }
}
