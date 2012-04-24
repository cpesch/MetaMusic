/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2006 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.tools;

import slash.metamusic.mp3.ID3FileName;
import slash.metamusic.mp3.MP3File;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class to
 * <ul>
 * <li>rename a file according to its tags</li>
 * <li>convert the file name to filesystem conventions</li>
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: MP3Mover.java 944 2007-01-10 17:12:53Z cpesch $
 */

public class MP3Mover extends BaseMP3Modifier {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3Mover.class.getName());

    /**
     * Move the given file, i.e.
     * <ul>
     * <li>rename a file according to its tags and</li>
     * <li>convert the file name to filesystem conventions.</li>
     * </ul>
     *
     * @param file the {@link File} to operate on
     */
    public void move(File file) throws IOException {
        MP3File mp3 = MP3File.readValidFile(file);
        if (mp3 == null) {
            throw new IOException("Invalid MP3 file " + file.getAbsolutePath());
        }
        move(mp3);
    }

    /**
     * Clean the given MP3 file, i.e.
     * <ul>
     * <li>rename a file according to its tags and</li>
     * <li>convert the file name to filesystem conventions.</li>
     * </ul>
     *
     * @param file the {@link MP3File} to operate on
     */
    public void move(MP3File file) throws IOException {
        // TODO when moveFileToTags(file) ?
        renameFileToTags(file);
    }

    public boolean renameFileToTags(MP3File mp3) {
        File before = mp3.getFile();
        File after = renameFile(before, mp3.getArtist(), mp3.getAlbum(), mp3.getTrack(), mp3.getIndex(), mp3.getPartOfSetIndex(), false);
        return after != null && !after.equals(before);
    }

    public boolean moveFileToTags(MP3File mp3) {
        File before = mp3.getFile();
        File after = moveFile(before, mp3.getArtist(), mp3.getAlbum(), mp3.getTrack(), mp3.getIndex(), mp3.getPartOfSetIndex(), false);
        return after != null && !after.equals(before);
    }

    private ID3FileName prepareID3FileName(String artist, String album, String track, int index, int partOfSetIndex, boolean various) {
        ID3FileName fileName = new ID3FileName();
        fileName.setArtist(artist);
        fileName.setAlbum(album);
        fileName.setTrack(track);
        fileName.setIndex(index);
        fileName.setPartOfSetIndex(partOfSetIndex);
        fileName.setVarious(various);
        return fileName;
    }

    public File renameFile(File file, String artist, String album, String track, int index, int partOfSetIndex, boolean various) {
        ID3FileName fileName = prepareID3FileName(artist, album, track, index, partOfSetIndex, various);
        try {
            file = fileName.rename(file);
        } catch (IOException e) {
            MP3Mover.log.severe("Cannot rename " + file + ": " + e.getMessage());
            return null;
        }
        return file;
    }

    public File moveFile(File file, String artist, String album, String track, int index, int partOfSetIndex, boolean various) {
        ID3FileName fileName = prepareID3FileName(artist, album, track, index, partOfSetIndex, various);
        try {
            file = fileName.rename(file);
        } catch (IOException e) {
            MP3Mover.log.severe("Cannot rename " + file + ": " + e.getMessage());
            return null;
        }
        try {
            // TODO does not keep correct directory name stable
            file = fileName.move(file);
        } catch (IOException e) {
            MP3Mover.log.severe("Cannot move " + file + ": " + e.getMessage());
        }
        return file;
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.mp3.tools.MP3Mover <file>");
            System.exit(1);
        }

        File file = new File(args[0]);
        MP3Mover mover = new MP3Mover();
        mover.move(file);
        System.exit(0);
    }
}
