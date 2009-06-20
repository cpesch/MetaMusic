/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.coverdb;

import slash.metamusic.util.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Abstract super class for all filesystem based cover clients.
 *
 * @author Christian Pesch
 * @version $Id: FileSystemCoverClient.java 797 2008-04-03 06:28:05Z cpesch $
 */

abstract class FileSystemCoverClient {
    /**
     * Logging output
     */
    protected static Logger log = Logger.getLogger(FileSystemCoverClient.class.getName());

    protected static final String ALBUM_ART_FILE_NAME_EXTENSION = ".jpg";

    protected void storeCover(File parent, String fileName, byte[] cover) {
        File folderJpg = new File(parent, fileName);
        if (folderJpg.exists() && folderJpg.length() >= cover.length)
            return;

        if (folderJpg.exists()) {
            try {
                File tempFile = File.createTempFile(Files.removeExtension(folderJpg.getName()),
                        "." + Files.getExtension(folderJpg.getName()));
                log.warning("The file '" + folderJpg + "' exists, moving it to tmp '" + tempFile + "'");
                folderJpg.renameTo(tempFile);
            } catch (IOException e) {
                log.severe("Cannot delete " + folderJpg.getAbsolutePath() + ": " + e.getMessage());
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(folderJpg);
            fos.write(cover);
            fos.close();
        } catch (IOException e) {
            log.severe("Cannot write " + folderJpg.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    protected void removeFile(File remove) {
        if (!remove.exists())
            return;
        if (!remove.delete())
            LastFmCoverClient.log.severe("Failed to remove " + remove);
        else
            LastFmCoverClient.log.info("Removed " + remove.getAbsolutePath());
    }
}
