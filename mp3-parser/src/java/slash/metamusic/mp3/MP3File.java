/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2004 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.util.TimeConversion;
import slash.metamusic.util.Files;
import slash.metamusic.util.InputOutput;

import java.io.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * My instances represent an MP3 File, which is parsed. Then,
 * information about the MP3 Header and the ID3 Tags may be
 * queried.
 *
 * @author Christian Pesch
 * @version $Id: MP3File.java 956 2007-02-03 10:39:39Z cpesch $
 */

public class MP3File implements ID3MetaData {

    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3File.class.getName());

    protected static final String FILE_PARAMETER = "f";
    protected static final String FILE_PARAMETER_LONG = "file";

    private static final int READ_BUFFER_SIZE = 64 * 1024;

    private File file;
    private boolean valid = false;
    private AbstractAudioProperties properties = new MP3Properties();
    private ID3v1Tail tail = new ID3v1Tail();
    private APETail ape = new APETail();
    private ID3v2Header head = new ID3v2Header();
    private ID3FileName fileName = new ID3FileName();

    /**
     * Returns the data file or null if we've read from a stream
     *
     * @return the data file or null if we've read from a stream
     */
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Read given File and return an MP3File. If an error
     * occurs or the file is invalid, null is returned.
     *
     * @param file the file to read
     * @return an MP3File for the given File or null, if
     *         the file is invalid or an error occured
     */
    public static MP3File readValidFile(File file) {
        MP3File mp3 = new MP3File();
        try {
            if (mp3.read(file)) {
                if (mp3.isValid()) {
                    return mp3;
                }
            }
        } catch (IOException e) {
            log.severe("Cannot process invalid MP3 file " + file.getAbsolutePath() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Read the file for the given file name.
     *
     * @param fileName the fileName to read a file from
     * @return true, if the was could be read
     * @throws IOException if an error occured reading the fine
     */
    public boolean read(String fileName) throws IOException {
        return read(new File(Files.replaceSeparators(fileName)));
    }

    /**
     * Read the given file.
     *
     * @param file the file to read
     * @return true, if the was could be read
     * @throws IOException if an error occured reading the fine
     */
    public boolean read(File file) throws IOException {
        if (!file.exists())
            throw new IOException("File " + file.getAbsolutePath() + " does not exist");
        if (!file.isFile())
            throw new IOException("File " + file.getAbsolutePath() + " is not a file");

        this.file = file;
        valid = true;

        log.info("Analyzing " + getFile().getAbsolutePath() + " (" + file.length() + " bytes)");

        fileName = new ID3FileName();
        valid = valid & fileName.read(file);

        if (file.getName().toLowerCase().endsWith(".mp3"))
            properties = new MP3Properties();
        else if (file.getName().toLowerCase().endsWith(".wav"))
            properties = new WAVProperties();
        else if (file.getName().toLowerCase().endsWith(".ogg"))
            properties = new OggProperties();

        FileInputStream in = new FileInputStream(file);
        try {
            valid = valid & read(in);
        } finally {
            in.close();
        }

        if (properties.getFileSize() != 0 && getFileSize() != properties.getFileSize()) {
            log.severe("File size differs: " + getFileSize() + " bytes calculated, but " +
                    properties.getFileSize() + " bytes in header (difference " +
                    (getFileSize() - properties.getFileSize()) + ")");
        }
        properties.setFileSize(getFileSize());

        return valid;
    }

    public boolean read(InputStream in) throws IOException {
        valid = true;

        InputStream buffer;
        if (!in.markSupported()) {
            buffer = new BufferedInputStream(in, READ_BUFFER_SIZE);
        } else
            buffer = in;

        try {
            buffer.mark(READ_BUFFER_SIZE);
            readHead(buffer);
            buffer.mark(READ_BUFFER_SIZE);
            readProperties(buffer);

            // fast forward for ID3v1Tail searching
            int available = in.available();
            if (available > ID3v1Tail.ID3V1_SIZE * 3) {
                int toSkip = available - ID3v1Tail.ID3V1_SIZE * 3;
                long skipped = in.skip(toSkip);
                if (skipped < toSkip)
                    log.fine("Skipped " + skipped + " but wanted to skip " + toSkip + " bytes");
            }

            buffer.mark(READ_BUFFER_SIZE);
            readTail(buffer, READ_BUFFER_SIZE);

            properties.setMetaDataSize(getReadSize());

        } finally {
            buffer.close();
        }
        return valid;
    }

    private void readHead(InputStream buffer) throws IOException {
        head = new ID3v2Header();
        try {
            head.read(buffer);
        } catch (NoID3v2HeaderException e) {
            log.info("No ID3v2 head found");
            buffer.reset();
        }
    }

    private void readProperties(InputStream buffer) throws IOException {
        try {
            valid = properties.read(buffer);
        } catch (NoMP3FrameException e) {
            log.severe("No valid properties found");
            try {
                buffer.reset();
            }
            catch (IOException e2) {
                // means that a valid MP3 frame was not found within READ_BUFFER_SIZE
            }
            valid = false;
        }
    }

    private void readTail(InputStream buffer, int bufferSize) throws IOException {
        ape = new APETail();
        try {
            ape.read(buffer, bufferSize);
        } catch (NoAPEv2TailException e) {
            log.fine("No APEv2 tail found");
            buffer.reset();
        }

        tail = new ID3v1Tail();
        try {
            tail.read(buffer);
        } catch (NoID3v1TailException e) {
            log.info("No ID3v1 tail found");
        }
    }

    public void write(File file) throws IOException {
        if (!file.isFile())
            throw new IOException("File " + file.getAbsolutePath() + " is not a file");
        if (!file.canWrite())
            throw new IOException("No permission to write on file " + file.getAbsolutePath());

        this.file = file;

        File tmp = File.createTempFile("mp3file", ".mp3");
        OutputStream out = new FileOutputStream(tmp);
        try {
            write(out);
        } finally {
            out.close();
        }

        // not required on Unix, but on Windows we need to delete the target before
        // renaming another file to it or copying bytes to it
        if (!file.delete())
            throw new IOException("Cannote delete target file " + file.getAbsolutePath());

        // temp file: rename file to original filename
        // if temp file and file are in the same directory, we can rename
        File tmp1 = new File(tmp.getAbsolutePath());
        File tmp2 = new File(file.getAbsolutePath());
        if (tmp1.getParent().equals(tmp2.getParent())) {
            if (!tmp.renameTo(file))
                throw new IOException("Cannote rename " + tmp.getAbsolutePath() + " to " + file.getAbsolutePath());

        } else {
            // else, we must copy
            InputStream in = new FileInputStream(tmp);
            out = new FileOutputStream(file);
            InputOutput inout = new InputOutput(in, out);
            inout.start();
            inout.close();

            if (file.length() != tmp.length())
                throw new IOException("Failed to copy " + tmp.getAbsolutePath() + " to " + file.getAbsolutePath() + " source: " + tmp.length() + " destination: " + file.length() + " bytes");

            if (!tmp.delete())
                throw new IOException("Cannote delete temporary file " + tmp.getAbsolutePath());
        }
        log.info("Wrote " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
    }

    public void rename(File file) throws IOException {
        copyID3MetaData(this, getFileName());
        this.file = fileName.rename(file);
    }

    public void move(File file) throws IOException {
        copyID3MetaData(this, getFileName());
        this.file = fileName.move(file);
    }

    public void removeID3(File file) throws IOException {
        setID3v1(false);
        setID3v2(false);
        write(file);
    }

    public void write(OutputStream out) throws IOException {
        if (isID3v2())
            head.write(out);

        if (getFile() != null) {
            FileInputStream in = new FileInputStream(getFile());

            // skip header
            if (in.skip(head.getReadSize()) != head.getReadSize())
                log.warning("Could skip " + properties.getReadSize() + " bytes of header");

            // skip padding
            if (in.skip(properties.getReadSize()) != properties.getReadSize())
                log.warning("Could skip " + properties.getReadSize() + " bytes of padding");

            // copy body
            InputOutput inout = new InputOutput(in, out);
            long bytes = getFileSize() - head.getReadSize() - properties.getReadSize() - tail.getReadSize();
            log.fine("Writing MP3 data (" + bytes + " bytes)");
            inout.copy(bytes);
            in.close();
        }

        if (isID3v1())
            tail.write(out);
    }

    public void write() throws IOException {
        write(getFile());
    }

    private void copyID3MetaData(ID3MetaData from, ID3MetaData to) {
        to.setArtist(from.getArtist());
        to.setComment(from.getComment());
        to.setGenre(from.getGenre());
        to.setAlbum(from.getAlbum());
        to.setTrack(from.getTrack());
        try {
            to.setIndex(from.getIndex());
        }
        catch (IllegalArgumentException e) {
            // happens if a IDv1 but not 1.1 tail is asked for an index
        }
        to.setYear(from.getYear());
    }

    // --- get object ------------------------------------------

    /**
     * Returns the data file size or -1 if we've read from a stream
     *
     * @return the data file size or -1 if we've read from a stream
     */
    public long getFileSize() {
        return getFile() != null ? getFile().length() : -1;
    }


    public AbstractAudioProperties getProperties() {
        return properties;
    }

    public APETail getApe() {
        return ape;
    }

    public ID3v1Tail getTail() {
        return tail;
    }

    public ID3v2Header getHead() {
        return head;
    }

    public ID3FileName getFileName() {
        return fileName;
    }

    public boolean isMP3() {
        return properties.isMP3();
    }

    public boolean isWAV() {
        return properties.isWAV();
    }

    public boolean isOgg() {
        return properties.isOgg();
    }

    public boolean isAPE() {
        return ape.isValid();
    }

    public boolean isID3v1() {
        return tail.isValid();
    }

    public boolean isID3v1dot1() {
        return isID3v1() && tail.isID3v1dot1();
    }

    public boolean isID3v2() {
        return head.isValid();
    }

    public long getBitRate() {
        return properties.getBitRate();
    }

    public long getSampleFrequency() {
        return properties.getSampleFrequency();
    }

    public boolean isVBR() {
        return properties.isVBR();
    }

    public int getMode() {
        return properties.getMode();
    }

    public String getModeAsString() {
        return properties.getModeAsString();
    }

    public int getSeconds() {
        if (isID3v2()) {
            if (properties.getSeconds() != -1 && head.getSeconds() != -1 &&
                    properties.getSeconds() != head.getSeconds()) {
                log.warning("Inconsistent seconds, head=" + head.getSeconds() + " properties=" + properties.getSeconds());
            }
        }
        return properties.getSeconds();
    }

    public String getSecondsAsTimeString() {
        return TimeConversion.getTimeFromSeconds(getSeconds());
    }

    // --- MetaData get ----------------------------------------

    /**
     * Returns if the MP3 meta data has been read successfully.
     *
     * @return true if the MP3 meta data has been read successfully
     */
    public boolean isValid() {
        return valid;
    }

    public long getReadSize() {
        return tail.getReadSize() + properties.getReadSize() + head.getReadSize();
    }


    private String calculateValueFromHeadOrTail(String headValue, String tailValue) {
        if (headValue != null && tailValue != null &&
                (!headValue.startsWith(tailValue))) {
            log.severe("Inconsistent value, head=" + headValue + " tail=" + tailValue);
        }

        String currentValue = tailValue;

        // if the tail defines a substring of the head, take the head
        if (currentValue == null || currentValue.length() == 0 || currentValue.indexOf('\u0000') != -1 ||
                (headValue != null && headValue.startsWith(currentValue)))
            currentValue = headValue;

        return currentValue;
    }

    public String getTrack() {
        String trackName = null;
        if (isID3v1())
            trackName = tail.getTrack();

        if (isID3v2())
            trackName = calculateValueFromHeadOrTail(head.getTrack(), trackName);

        if (trackName /*still*/ == null)
            trackName = fileName.getTrack();

        return trackName;
    }

    public String getArtist() {
        String artistName = null;
        if (isID3v1())
            artistName = tail.getArtist();

        if (isID3v2())
            artistName = calculateValueFromHeadOrTail(head.getArtist(), artistName);

        if (artistName /*still*/ == null)
            artistName = fileName.getArtist();

        return artistName;
    }

    public String getAlbum() {
        String albumName = null;
        if (isID3v1())
            albumName = tail.getAlbum();

        if (isID3v2())
            albumName = calculateValueFromHeadOrTail(head.getAlbum(), albumName);

        if (albumName /*still*/ == null)
            albumName = fileName.getAlbum();

        return albumName;
    }

    public ID3Genre getGenre() {
        ID3Genre genre = null;
        if (isID3v1()) {
            genre = tail.getGenre();
            if (genre != null && genre.getId() == -1)
                genre = null;
        }

        if (isID3v2()) {
            if (head.getGenre() != null && genre != null &&
                    !head.getGenre().equals(genre)) {
                log.severe("Inconsistent genre, head=" + head.getGenre() +
                        " tail=" + tail.getGenre());
            }

            if (genre == null)
                genre = head.getGenre();
        }
        return genre;
    }

    public int getYear() {
        int year = -1;
        if (isID3v1())
            year = tail.getYear();

        if (isID3v2()) {
            if (head.getYear() != -1 && year != -1 &&
                    head.getYear() != year) {
                log.severe("Inconsistent year, head=" + head.getYear() +
                        " tail=" + tail.getYear());
            }

            if (year == -1)
                year = head.getYear();
        }

        return year;
    }

    public String getComment() {
        String comment = null;
        if (isID3v1())
            comment = tail.getComment();

        if (isID3v2())
            comment = calculateValueFromHeadOrTail(head.getComment(), comment);

        return comment;
    }

    public int getIndex() {
        int track = -1;
        if (isID3v1dot1())
            track = tail.getIndex();

        if (isID3v2()) {
            if (head.getIndex() != -1 && isID3v1dot1() && tail.getIndex() != -1 &&
                    head.getIndex() != tail.getIndex()) {
                log.severe("Inconsistent track, head=" + head.getIndex() +
                        " tail=" + tail.getIndex());
            }

            if (track == -1)
                track = head.getIndex();
        }

        if (track == -1)
            track /*still*/ = fileName.getIndex();

        return track;
    }

    public int getCount() {
        return head.getCount();
    }

    public int getPartOfSetIndex() {
        return head.getPartOfSetIndex();
    }

    public int getPartOfSetCount() {
        return head.getPartOfSetCount();
    }

    public String getEncoder() {
        String encoder = null;
        if (isMP3())
            encoder = ((MP3Properties) properties).getEncoder();

        if (isID3v2())
            encoder = calculateValueFromHeadOrTail(head.getEncoder(), encoder);

        return encoder;
    }

    // --- MetaData set ----------------------------------------

    public void setID3v1(boolean isID3v1) {
        if (isID3v1 && !tail.isValid()) {
            if (head.isValid())
                copyID3MetaData(head, tail);
            else if (fileName.isValid())
                copyID3MetaData(fileName, tail);
        }
        tail.setValid(isID3v1);
    }

    public void setID3v2(boolean isID3v2) {
        if (isID3v2 && !head.isValid()) {
            if (tail.isValid())
                copyID3MetaData(tail, head);
            else if (fileName.isValid())
                copyID3MetaData(fileName, head);
        }
        head.setValid(isID3v2);
    }

    public ID3v2Frame addID3v2Frame(String tagName) {
        return head.addID3v2Frame(tagName);
    }

    public void setTrack(String newTrack) {
        head.setTrack(newTrack);
        tail.setTrack(newTrack);
    }

    public void setArtist(String newArtist) {
        head.setArtist(newArtist);
        tail.setArtist(newArtist);
    }

    public void setAlbum(String newAlbum) {
        head.setAlbum(newAlbum);
        tail.setAlbum(newAlbum);
    }

    public void setYear(int newYear) {
        head.setYear(newYear);
        tail.setYear(newYear);
    }

    public void setGenre(ID3Genre newGenre) {
        head.setGenre(newGenre);
        tail.setGenre(newGenre);
    }

    public void setIndex(int newIndex) {
        head.setIndex(newIndex);
        tail.setIndex(newIndex);
    }

    public void setCount(int newCount) {
        head.setCount(newCount);
    }

    public void setPartOfSetIndex(int newIndex) {
        head.setPartOfSetIndex(newIndex);
    }

    public void setPartOfSetCount(int newCount) {
        head.setPartOfSetCount(newCount);
    }

    public void setComment(String newComment) {
        head.setComment(newComment);
        tail.setComment(newComment);
    }

    public void setSeconds(int newSeconds) {
        head.setSeconds(newSeconds);
    }

    public void setMetaMusicComment() {
        Calendar date = Calendar.getInstance();

        DateFormat fullFormat = DateFormat.getDateTimeInstance();
        getHead().setComment("Written by MetaMusic on " + fullFormat.format(date.getTime()), "Written", "English");
        getHead().setTaggingTime(date);

        DateFormat shortFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        getTail().setComment("MetaMusic on " + shortFormat.format(date.getTime()));

        // normalizes genre tag to "name(id)" if it is not "Unknown(-1)"
        ID3Genre genre = getGenre();
        if (genre != null && genre.getName() != null && !genre.getName().equals(ID3Genre.UNKNOWN)) {
            getHead().setGenre(genre);
            getTail().setGenre(genre);
        }

        // sets track count if track index and count exist
        int count = getHead().getCount();
        if (count != -1)
            getHead().setCount(count);

        int seconds = getProperties().getSeconds();
        if (seconds != -1)
            getHead().setSeconds(seconds);
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (getFile() != null) {
            buffer.append("file:        ").append(getFile().getAbsolutePath()).append("\n").
                    append("size:        ").append(getFileSize()).append(" bytes\n").
                    append("valid:       ").append(isValid());
        }

        if (!fileName.isValid())
            buffer.append("valid name:  ").append(fileName.isValid()).append("\n");

        if (isValid()) {
            buffer.append("vbr:         ").append(isVBR()).append("\n").
                    append("bitrate:     ").append(getBitRate()).append(" bit\\s\n").
                    append("sample freq: ").append(getSampleFrequency()).append(" Hz\n").
                    append("mode:        ").append(getModeAsString()).append(" (").append(getMode()).append(")\n").
                    append("time:        ").append(getSecondsAsTimeString()).append(" (").append(getSeconds()).append(" secs)\n");

            if (properties instanceof MP3Properties) {
                MP3Properties mp3 = (MP3Properties) properties;
                buffer.append("frames:      ").append(mp3.getFrames()).append("\n").
                        append("frame size:  ").append(mp3.getFrameSize()).append("\n").
                        append("version:     ").append(mp3.getMPEGVersionString()).append(" (").append(mp3.getMPEGVersion()).append(")\n").
                        append("layer:       ").append(mp3.getMPEGLayerString()).append("\n").
                        append("padding:     ").append(mp3.getPadding()).append("\n").
                        append("protection:  ").append(mp3.isProtected()).append("\n");
                if (mp3.isProtected())
                    buffer.append("crc:         ").append(mp3.getCRC()).append("\n");
                buffer.append("mode ext:    ").append(mp3.getModeExtension()).append("\n").
                        append("private:     ").append(mp3.isPrivate()).append("\n").
                        append("copyrighted: ").append(mp3.isCopyrighted()).append("\n").
                        append("original:    ").append(mp3.isOriginal()).append("\n").
                        append("emphasis:    ").append(mp3.getEmphasisString()).append(" (").append(mp3.getEmphasis()).append(")\n");
                if (mp3.getEncoder().length() > 0) {
                    buffer.append("encoder:     ").append(getEncoder()).append("\n");
                }
                buffer.append("valid mp3:   ").append(mp3.isValid()).append("\n");
            }

            if (properties instanceof WAVProperties) {
                WAVProperties wav = (WAVProperties) properties;
                buffer.append("bits/sample: ").append(wav.getBitsPerSample()).append("\n");
                buffer.append("valid wav:   ").append(wav.isValid()).append("\n");
            }

            if (properties instanceof OggProperties) {
                OggProperties ogg = (OggProperties) properties;
                ogg.getBitRate(); // TODO OggProperties should be extended
                buffer.append("valid ogg:   ").append(ogg.isValid()).append("\n");
            }

            buffer.append("APE:         ").append(isAPE()).append("\n");
            if (isAPE()) {
                buffer.append("APE rel:     ").append(ape.getVersion()).append("\n");
            }
            buffer.append("ID3v1:       ").append(isID3v1()).append("\n");
            if (isID3v1())
                buffer.append("ID3v1.1:     ").append(isID3v1dot1()).append("\n");
            buffer.append("ID3v2:       ").append(isID3v2()).append("\n");
            if (isID3v2()) {
                buffer.append("ID3v2 rel:   ").append(head.getVersion().getVersionString()).append("\n");
                for (ID3v2Frame f : head.getFrames()) {
                    buffer.append(f.getTagName());
                    String description = f.getTagDescription();
                    if (description != null) {
                        buffer.append(" [").append(description).append("]");
                    }
                    String stringContent = f.getStringContent();
                    if (stringContent == null || stringContent.length() < 1000)
                        buffer.append(": ").append(stringContent).append("\n");
                    else {
                        buffer.append(": [").append(stringContent.length()).append(" bytes]\n");
                    }
                }
            }
            buffer.append("track:       ").append(getTrack()).append("\n").
                    append("artist:      ").append(getArtist()).append("\n").
                    append("album:       ").append(getAlbum()).append("\n");
            if (isID3v1() || isID3v2()) {
                buffer.append("year:        ").append(getYear()).append("\n").
                        append("comment:     ").append(getComment()).append("\n");
                if (isID3v1dot1() || isID3v2() || fileName.getIndex() != -1)
                    buffer.append("index:       ").append(getIndex()).append("\n");
                if (isID3v2() && getCount() != -1)
                    buffer.append("count:       ").append(getCount()).append("\n");
                buffer.append("genre:       ").append(getGenre()).append("\n");
                if (isID3v2()) {
                    int rating = head.getRating();
                    if (rating > 0)
                        buffer.append("rating:      ").append(rating).append("\n");
                    int playCount = head.getPlayCount();
                    if (playCount > 0)
                        buffer.append("play count:  ").append(playCount).append("\n");
                    Calendar playTime = head.getPlayTime();
                    if (playTime != null) {
                        String playTimeStr = DateFormat.getDateTimeInstance().format(playTime.getTime());
                        buffer.append("play time:   ").append(playTimeStr).append("\n");
                    }
                }
            }
        }
        return buffer.toString();
    }
}
