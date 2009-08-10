/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.util.Files;
import slash.metamusic.util.StringHelper;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * My instances represent an ID3 compatible file name.
 *
 * @author Christian Pesch
 * @version $Id: ID3FileName.java 960 2007-03-24 21:07:08Z cpesch $
 */

public class ID3FileName implements ID3MetaData {
    public static final int WINDOWS_PATH_LENGTH_LIMIT = 255;

    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3FileName.class.getName());

    private String delimiter = "-.";


    public ID3FileName() {
    }

    public ID3FileName(File file) throws IOException {
        if (!read(file))
            throw new IOException("Cannot read file name of " + file);
    }

    public ID3FileName(String track, String artist, String album, int index, boolean various) {
        initialize(track, artist, album, index, various, true);
    }

    private void initialize(String track, String artist, String album, int index, boolean various, boolean valid) {
        setTrack(track);
        setArtist(artist);
        setAlbum(album);
        setIndex(index);
        setVarious(various);
        this.valid = valid;
    }

    private void initialize() {
        initialize(null, null, null, -1, false, false);
    }

    // --- read/write object -----------------------------------

    private boolean isInvalidChar(char c) {
        return c == '.' || c == '-' || c == '/' || c == '\\';
    }

    private String trimWhileReading(String str) {
        str = str.trim();

        if (str.length() > 0) {
            if (isInvalidChar(str.charAt(0))) {
                if (str.length() > 1) {
                    str = str.substring(1, str.length() - 1);
                } else {
                    str = "";
                }
            }
        }

        return str;
    }

    private String trimForWriting(String str) {
        if (str == null)
            return null;
        str = str.trim();
        str = str.replace("\\", "");
        str = str.replace("/", "");
        str = str.replace(File.separator, "");
        str = str.replace("\"", "");
        str = str.replace("?", "");
        str = str.replace("*", "");
        str = str.replace(":", "");
        str = str.replace(" - ", "-");
        str = str.replace("`", "'");
        str = str.replace("!", "");
        return str;
    }

    public void setDelimiter(String delimiter) { // TODO should take regex or some other configurable parse string
        this.delimiter = delimiter;
    }

    /**
     * Read MP3 file name from file
     *
     * @param file the File to read from
     * @throws IOException if an error occurs
     */
    public boolean read(File file) throws IOException {
        initialize();
        this.file = file;

        log.fine("Analysing file name of " + file.getAbsolutePath());

        // TODO make parsing configurable like juk does
        StringTokenizer tokenizer = new StringTokenizer(file.getName(), delimiter, true);
        if (tokenizer.hasMoreTokens()) {
            artist = trimWhileReading(tokenizer.nextToken());
        }

        String temp = null;
        while (tokenizer.hasMoreTokens() && (temp == null || temp.length() == 0)) {
            temp = trimWhileReading(tokenizer.nextToken());
        }
        album = temp;

        valid = true;

        temp = null;
        while (tokenizer.hasMoreTokens() && (temp == null || temp.length() == 0)) {
            temp = trimWhileReading(tokenizer.nextToken());

            try {
                index = Integer.parseInt(temp);
                // track number found
                temp = null;
                break;
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        while (tokenizer.hasMoreTokens() && (temp == null || temp.length() == 0)) {
            temp = trimWhileReading(tokenizer.nextToken());
        }
        track = temp;

        // no album found: track is album
        if (track == null || track.toLowerCase().equals("mp3")) {
            track = album;
            album = null;
        }

        // not even track found
        if (track != null && track.toLowerCase().equals("mp3")) {
            track = null;
        }

        return valid;
    }

    /**
     * Write MP3 file name to this file, i.e. rename
     * and move it.
     *
     * @throws IOException if an error occurs
     */
    public void write() throws IOException {
        write(getFile());
    }

    /**
     * Write MP3 file name to the file, i.e. rename
     * and move it.
     *
     * @param file the File to write to
     * @return the file after the write operation
     * @throws IOException if an error occurs
     */
    public File write(File file) throws IOException {
        file = rename(file);
        file = move(file);
        return file;
    }

    private int getOversize(File file, String fileName) {
        return file.getParent().length() + 1 + fileName.length() - WINDOWS_PATH_LENGTH_LIMIT;
    }

    /**
     * Rename the file, i.e. rename the file to a MP3 file name
     * compliant name
     *
     * @param file the File to rename
     * @return the file after the rename operation
     * @throws IOException if an error occurs
     */
    public File rename(File file) throws IOException {
        String fileName = getFileName(file);

        // work around Windows path length limitations
        int oversize = getOversize(file, fileName);
        if (oversize > 0) {
            log.warning("Path " + fileName + " is " + oversize + " chars too long");
            fileName = createFileName(file, getArtist(), getAlbum(),
                    StringHelper.shortenString(getTrack(), 3, oversize), getIndex(), getPartOfSetIndex());
        }
        oversize = getOversize(file, fileName);
        if (oversize > 0) {
            log.warning("Path " + fileName + " is " + oversize + " chars too long");
            fileName = createFileName(file, getArtist(),
                    StringHelper.shortenString(getAlbum(), 3, oversize),
                    StringHelper.shortenString(getTrack(), 3, getTrack().length()), getIndex(), getPartOfSetIndex());
        }
        oversize = getOversize(file, fileName);
        if (oversize > 0) {
            log.warning("Path " + fileName + " is " + oversize + " chars too long");
            fileName = createFileName(file, StringHelper.shortenString(getArtist(), 3, oversize),
                    StringHelper.shortenString(getAlbum(), 3, getAlbum().length()),
                    StringHelper.shortenString(getTrack(), 3, getTrack().length()), getIndex(), getPartOfSetIndex());
        }

        oversize = getOversize(file, fileName);
        if (oversize > 0) {
            log.severe("Path " + fileName + " is " + oversize + " chars too long");
            throw new IOException("Cannot shorten file name of " + file + "; " + fileName + " still " + oversize + " chars too long");
        }

        if (!file.getName().equals(fileName)) {
            File newFile = new File(file.getParentFile(), fileName);

            // work around Windows case insensitivity for file names by renaming source first
            if (newFile.getAbsolutePath().toLowerCase().equals(file.getAbsolutePath().toLowerCase())) {
                File renameFile = new File(file.getParentFile(), "cAsE" + file.getName());
                if (file.renameTo(renameFile))
                    file = renameFile;
            }

            boolean exists = newFile.exists();
            if (!exists && file.renameTo(newFile)) {
                log.fine("Renamed file " + file + " to " + newFile);
                file = newFile;
            } else {
                log.severe("Cannot rename file " + file + " to " + newFile);
                throw new IOException("Cannot rename file " + file + " to " + newFile + ", target exists");
            }
        }
        return file;
    }

    /**
     * Move the file, i.e. move the file to a MP3 file name
     * compliant path
     *
     * @param file the File to move
     * @return the file after the move operation
     * @throws IOException if an error occurs
     */
    public File move(File file) throws IOException {
        String currentPath = file.getParentFile().getAbsolutePath();
        String filePath = getFilePath();
        if (!currentPath.endsWith(filePath)) {
            File newPath = new File(file.getParentFile(), filePath);
            if (!newPath.exists())
                if (!newPath.mkdirs())
                    log.severe("Cannot create path to " + newPath);
            File newFile = new File(newPath, file.getName());
            boolean exists = newFile.exists();
            if (!exists && file.renameTo(newFile)) {
                log.fine("Moved file " + file + " to " + newPath);
                file = newFile;
            } else {
                log.severe("Cannot move file " + file + " to " + newFile);
                throw new IOException("Cannot move file " + file + " to " + newFile + ", target exists");
            }
        }
        return file;
    }

    // --- get object ------------------------------------------


    public File getFile() {
        return file;
    }

    public String getFileName() {
        return createFileName(getFile(), getArtist(), getAlbum(), getTrack(), getIndex(), getPartOfSetIndex());
    }

    private String getFileName(File file) {
        return createFileName(file, getArtist(), getAlbum(), getTrack(), getIndex(), getPartOfSetIndex());
    }

    private String createFileName(File file, String artist, String album, String track, int index, int partOfSetIndex) {
        artist = trimForWriting(artist);
        if (artist == null)
            return trimForWriting(file.getName());

        track = trimForWriting(track);
        if (track == null)
            return trimForWriting(file.getName());

        album = trimForWriting(album);
        String indexStr = index != -1 ? StringHelper.formatNumber(index, 2) : null;
        String partOfSetIndexStr = partOfSetIndex != -1 ? StringHelper.formatNumber(partOfSetIndex, 1) : null;

        // TODO make this pattern configurable
        return artist + " - " +
                (album != null ? album : "") +
                (partOfSetIndexStr != null ? " (disc " + partOfSetIndexStr + ")" : "") +
                (album != null ? " - " : "") +
                (indexStr != null ? indexStr + " - " : "") +
                track + ".mp3";
    }

    public String getFilePath() {
        String artist = getArtist();
        if (artist == null)
            return file.getAbsolutePath();

        String album = getAlbum();
        if (album == null)
            return file.getAbsolutePath();

        if (isVarious())
            return Files.replaceSeparators(trimForWriting(album));
        else
            return Files.replaceSeparators(trimForWriting(artist) + "/" + trimForWriting(album));
    }

    public String getAbsolutePath() {
        return Files.replaceSeparators(getFilePath() + "/" + getFileName());
    }

    // --- MetaData get ----------------------------------------

    public boolean isValid() {
        return valid;
    }

    public long getReadSize() {
        return -1;
    }

    public String getTrack() {
        return track;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getYear() {
        return -1;
    }

    public ID3Genre getGenre() {
        return null;
    }

    public String getComment() {
        return null;
    }

    public int getIndex() {
        return index;
    }

    public int getPartOfSetIndex() {
        return partOfSetIndex;
    }

    public boolean isVarious() {
        return various;
    }

    // --- MetaData set ----------------------------------------

    public void setTrack(String newTrack) {
        this.track = newTrack;
    }

    public void setArtist(String newArtist) {
        this.artist = newArtist;
    }

    public void setAlbum(String newAlbum) {
        this.album = newAlbum;
    }

    public void setYear(int newYear) {
    }

    public void setGenre(ID3Genre newGenre) {
    }

    public void setIndex(int newIndex) {
        this.index = newIndex;
    }

    public void setPartOfSetIndex(int newIndex) {
        this.partOfSetIndex = newIndex;
    }

    public void setComment(String newComment) {
    }

    public void setVarious(boolean various) {
        this.various = various;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("slash.metamusic.mp3.ID3FileName <file1> <file2> ... <fileN>");
            System.exit(1);
        }

        for (String arg : args) {
            ID3FileName fileName = new ID3FileName();
            fileName.read(new File(arg));
            log.info(fileName.toString());
        }
        System.exit(0);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ID3FileName[" + "file=").append(file != null ? file.getAbsolutePath() : "<null>").append(",").
                append("valid=").append(isValid());
        buffer.append(", " + "track=").append(getTrack()).
                append(", " + "artist=").append(getArtist()).
                append(", " + "album=").append(getAlbum()).
                append(", " + "index=").append(getIndex()).
                append(", " + "partOfSet=").append(getPartOfSetIndex());
        buffer.append("]");
        return buffer.toString();
    }

    // --- member variables ------------------------------------

    protected File file;

    protected boolean valid;

    protected String track, artist, album;
    protected int index = -1, partOfSetIndex = -1;
    protected boolean various;
}
