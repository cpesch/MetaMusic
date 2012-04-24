/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import slash.metamusic.mp3.ID3v2Frame;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * A class to count the occurrences of ID3v2 tags for all MP3s in a file system tree.
 *
 * @author Christian Pesch
 * @version $Id: ID3v2TagCounter.java 910 2006-12-23 12:18:38Z cpesch $
 */

public class ID3v2TagCounter {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v2TagCounter.class.getName());

    private CounterMap tags = new CounterMap();

    /**
     * Count the occurences of ID3v2 tags for all MP3s from the given path.
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

        int count = 1;
        for (File file : files) {
            MP3File mp3 = MP3File.readValidFile(file);
            log.info("Processing " + (count++) + ". from " + files.size() + " files: " + mp3);
            if (mp3 != null && mp3.isID3v2()) {
                process(mp3);
            }
        }

        log.info("Result: " + tags);

        // place break point here
        tags.toString();
    }

    protected void process(MP3File mp3) {
        for (ID3v2Frame frame : mp3.getHead().getFrames()) {
            String tagName = frame.getTagName();
            String content = frame.getStringContent();
            tags.add(tagName, content);
        }
    }


    private static class CounterMap {
        private Map<String, Counter> map = new HashMap<String, Counter>();

        public void add(String name, String value) {
            Counter counter = map.get(name);
            if (counter == null) {
                counter = new Counter(value);
                map.put(name, counter);
            } else
                counter.increase(value);
        }

        public String toString() {
            return map.toString();
        }
    }

    private static class Counter {
        private int count = 0;
        private Set<String> values = new HashSet<String>();

        public Counter(String value) {
            increase(value);
        }

        public int getCount() {
            return count;
        }

        public Set<String> getValues() {
            return values;
        }

        public void increase(String value) {
            count += 1;
            values.add(value);
        }

        public String toString() {
            return "[count=" + count + ", values=" + values + "]";
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.mp3.tools.ID3v2TagCounter <path>");
            System.exit(1);
        }

        String pathName = args[0];
        File path = new File(pathName);
        ID3v2TagCounter counter = new ID3v2TagCounter();
        counter.process(path, "mp3");
        System.exit(0);
    }
}
