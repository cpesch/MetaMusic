/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.coverdb;

import slash.metamusic.hex.HexEncoder;
import slash.metamusic.mp3.ID3v2Frame;
import slash.metamusic.mp3.ID3v2Header;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.URLLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A client that looks in the filesystem for Windows Media Player cover files,
 * which are put separately next to the media files.
 * <p/>
 * That is
 * <ul>
 * <li>AlbumArt_{ALBUM_ID}_Large.jpg</li>
 * <li>AlbumArt_{ALBUM_ID}_Small.jpg</li>
 * <li>Folder.jpg</li>
 * <li>AlbumArtSmall.jpg</li>
 * <li>desktop.ini</li>
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: WindowsMediaPlayerCoverClient.java 797 2006-04-23 20:28:05Z cpesch $
 */

public class WindowsMediaPlayerCoverClient extends FileSystemCoverClient {
    private static final String FOLDER_JPG_FILE_NAME = "Folder" + ALBUM_ART_FILE_NAME_EXTENSION;
    private static final String FOLDER_SMALL_JPG_FILE_NAME = "AlbumArtSmall" + ALBUM_ART_FILE_NAME_EXTENSION;
    private static final String DESKTOP_INI_FILE_NAME = "desktop.ini";
    private static final String ALBUM_ART_FILE_NAME_PREFIX = "AlbumArt";
    private static final String WM_COLLECTION_ID_PREFIX = "WM/WMCollectionID";

    static {
        log = Logger.getLogger(WindowsMediaPlayerCoverClient.class.getName());
    }


    private String asAlbumArtId(byte[] bytes) {
        String hexId = HexEncoder.encodeBytes(bytes);
        // 3rd and 4th char wrong: Backstreet Boys                           3F instead of 81
        // 3rd and 4th char after first dash wrong: The Psychedelic Furs     3F            8D
        //                                          The Smashing Pumpkins    3F            8F
        // 3rd and 4th char after second dash wrong: The Judds               3F            90
        if (hexId.length() < 22)
            return null;
        return hexId.substring(8, 10) + hexId.substring(6, 8) +
                hexId.substring(4, 6) + hexId.substring(2, 4) + "-" +
                hexId.substring(12, 14) + hexId.substring(10, 12) + "-" +
                hexId.substring(16, 18) + hexId.substring(14, 16) + "-" + hexId.substring(18, 22) + "-" +
                hexId.substring(22, hexId.length());
    }

    public String findWindowsMediaAlbumId(MP3File file) {
        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String content = f.getStringContent();
            if (f.getTagName().equals("PRIV")) {
                if (content.startsWith(WM_COLLECTION_ID_PREFIX)) {
                    content = content.substring(WM_COLLECTION_ID_PREFIX.length());
                    content = content.substring(0, content.indexOf('<'));
                    return asAlbumArtId(content.getBytes());
                }
            }
        }
        return null;
    }

    private File findMatchingAlbumArtFile(File directory, final String albumId) {
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (!name.startsWith(ALBUM_ART_FILE_NAME_PREFIX + "_{"))
                    return false;
                if (!name.endsWith("}_Large" + ALBUM_ART_FILE_NAME_EXTENSION))
                    return false;
                int beginIndex = name.indexOf('{');
                int endIndex = name.indexOf('}');
                if (beginIndex == -1 || endIndex == -1)
                    return false;
                String idFromName = name.substring(beginIndex + 1, endIndex);
                return idFromName.substring(0, 2).equals(albumId.substring(0, 2)) &&
                        idFromName.substring(4, 11).equals(albumId.substring(4, 11)) &&
                        idFromName.substring(13, 16).equals(albumId.substring(13, 16)) &&
                        idFromName.substring(18).endsWith(albumId.substring(18));
            }
        });
        return files.length > 0 ? files[0] : null;
    }

    private int countAlbumArtFiles(File directory) {
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(ALBUM_ART_FILE_NAME_PREFIX + "_{") && name.endsWith(ALBUM_ART_FILE_NAME_EXTENSION);
            }
        });
        return files.length;
    }

    private byte[] load(File file) throws IOException {
        byte[] result = URLLoader.getContents(file);
        if (result.length > 1) {
            // sometimes !%#&§! WMP does not add FFD8 prefix
            if (result[0] == (byte) 0xFF && result[1] == (byte) 0xE0) {
                byte[] copy = new byte[result.length + 2];
                System.arraycopy(result, 0, copy, 2, result.length);
                copy[0] = (byte) 0xFF;
                copy[1] = (byte) 0xD8;
                result = copy;
            }
        }
        return result;
    }

    public byte[] findCover(MP3File file) throws IOException {
        File parent = file.getFile().getParentFile();

        // first fetch from Windows Media tags
        String albumId = findWindowsMediaAlbumId(file);
        if (albumId != null) {
            File albumArtLargeJpg = new File(parent, ALBUM_ART_FILE_NAME_PREFIX + "_{" + albumId + "}_Large" + ALBUM_ART_FILE_NAME_EXTENSION);
            if (albumArtLargeJpg.exists())
                return load(albumArtLargeJpg);
            File albumArtSmallJpg = new File(parent, ALBUM_ART_FILE_NAME_PREFIX + "_{" + albumId + "}_Small" + ALBUM_ART_FILE_NAME_EXTENSION);
            if (albumArtSmallJpg.exists())
                return load(albumArtSmallJpg);

            // some bytes seem to be not understood, be a little more lenient
            File albumArtMatchingJpg = findMatchingAlbumArtFile(parent, albumId);
            if (albumArtMatchingJpg != null)
                return load(albumArtMatchingJpg);
        }

        // if not yet successful fetch from folder but only if there is no AlbumArt_{ID} file
        if (countAlbumArtFiles(parent) > 0)
            return null;

        File folderJpg = new File(parent, FOLDER_JPG_FILE_NAME);
        if (folderJpg.exists())
            return load(folderJpg);
        File folderSmallJpg = new File(parent, FOLDER_SMALL_JPG_FILE_NAME);
        if (folderSmallJpg.exists())
            return load(folderSmallJpg);

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
        removeFile(new File(parent, DESKTOP_INI_FILE_NAME));
        File[] files = parent.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(ALBUM_ART_FILE_NAME_PREFIX) && name.endsWith(ALBUM_ART_FILE_NAME_EXTENSION);
            }
        });
        if (files != null) {
            for (File f : files) {
                removeFile(f);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.coverdb.WindowsMediaPlayerCoverClient <file>");
            System.exit(1);
        }

        MP3File file = MP3File.readValidFile(new File(args[0]));
        WindowsMediaPlayerCoverClient client = new WindowsMediaPlayerCoverClient();
        client.findCover(file);
        System.exit(0);
    }
}
