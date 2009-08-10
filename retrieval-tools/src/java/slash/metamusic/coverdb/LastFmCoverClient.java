/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.coverdb;

import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.URLLoader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A client that looks in the filesystem for Last.fm cover files,
 * which are put separately next to the media files.
 * <p/>
 * That is
 * <ul>
 * <li>cover.jpg</li>
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: LastFmCoverClient.java 797 2008-04-03 06:28:05Z cpesch $
 */

public class LastFmCoverClient extends FileSystemCoverClient {
    private static final String FOLDER_JPG_FILE_NAME = "cover" + ALBUM_ART_FILE_NAME_EXTENSION;

    static {
        log = Logger.getLogger(LastFmCoverClient.class.getName());
    }

    public byte[] findCover(MP3File file) throws IOException {
        File parent = file.getFile().getParentFile();

        File folderJpg = new File(parent, FOLDER_JPG_FILE_NAME);
        if (folderJpg.exists())
            return URLLoader.getContents(folderJpg);

        return null;
    }

    public void storeCover(File file, byte[] cover) {
        storeCover(file.getParentFile(), FOLDER_JPG_FILE_NAME, cover);
    }

    public void removeCover(File file) {
        File parent = file;
        if (file.isFile())
            parent = file.getParentFile();
        removeFile(new File(parent, FOLDER_JPG_FILE_NAME));
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.coverdb.LastFmCoverClient <file>");
            System.exit(1);
        }

        MP3File file = MP3File.readValidFile(new File(args[0]));
        LastFmCoverClient client = new LastFmCoverClient();
        client.findCover(file);
        System.exit(0);
    }
}