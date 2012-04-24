/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import slash.metamusic.util.Files;
import slash.metamusic.util.StringHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * A class to calculate the path length of files in a file system tree.
 *
 * @author Christian Pesch
 * @version $Id$
 */

public class PathLength {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(PathLength.class.getName());

    public void process(File path) throws IOException {
        log.info("Calculating path length below " + path.getAbsolutePath());

        List<File> files = Files.collectFiles(path);
        int fileCount = files.size();
        if (fileCount == 0) {
            log.warning("No files found below " + path.getAbsolutePath());
            return;
        }

        int count = 0;
        try {
            for (File file : files) {
                count++;
                String filePath = file.getPath();
                int pathLength = filePath.length();
                // log.info(StringHelper.formatNumber(pathLength, 3) + " " + filePath);
                System.out.println(StringHelper.formatNumber(pathLength, 3) + " " + filePath);
            }
        } finally {
            log.info("Calculated path length from " + count + " out of " + fileCount + " files");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.mp3.tools.PathLength <path>");
            System.exit(1);
        }

        String pathName = args[0];
        File path = new File(pathName);
        PathLength pathLength = new PathLength();
        pathLength.process(path);
        System.exit(0);
    }
}
