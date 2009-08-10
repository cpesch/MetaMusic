/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.sections.PictureType;
import slash.metamusic.mp3.util.BitConversion;
import slash.metamusic.mp3.util.ISO8601;
import slash.metamusic.util.ImageResizer;
import slash.metamusic.util.MimeTypeGuesser;

import javax.activation.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * My instances represent a ID3v2 header of the MP3 frames as
 * described in http://www.id3.org/id3v2.3.0.html.
 *
 * @author Christian Pesch
 * @version $Id: ID3v2Header.java 952 2007-01-17 20:14:15Z cpesch $
 */

public class ID3v2Header implements ID3MetaData {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v2Header.class.getName());

    public static final String ID3V2TAG = "ID3";

    public static final int ID3V2_HEADER_SIZE = 10;
    public static final int ID3_SIZE = 3;
    public static final int SIZE_SIZE = 4;
    public static final int FLAG_SIZE = 1;

    public final static int UNSYNCHRONIZATION_FLAG = 0x0080;
    public final static int EXTENDED_HEADER_FLAG = 0x0040;
    public final static int EXPERIMENTAL_FLAG = 0x0020;
    public final static int FOOTER_FLAG = 0x0010;

    /**
     * Encoding to use when converting from bytes to Unicode (String).
     */
    public static final String ISO_8859_1_ENCODING = "ISO8859_1";

    private static final String TRACK_TAG_NAME = "TIT2";
    private static final String ARTIST_TAG_NAME = "TPE1";
    private static final String BAND_TAG_NAME = "TPE2";
    private static final String COMPILATION_TAG_NAME = "TCMP";
    private static final String ALBUM_TAG_NAME = "TALB";
    private static final String COMMENT_TAG_NAME = "COMM";
    private static final String INDEX_TAG_NAME = "TRCK";
    private static final String PART_OF_SET_INDEX_TAG_NAME = "TPOS";
    private static final String GENRE_TAG_NAME = "TCON";
    private static final String YEAR_TAG_NAME = "TYER";
    private static final String SECONDS_TAG_NAME = "TLEN";
    private static final String MUSICBRAINZ_ID_TAG_NAME = "UFID";
    private static final String PUBLISHER_TAG_NAME = "TPUB";
    private static final String ATTACHED_PICTURE_TAG_NAME = "APIC";
    private static final String LYRICS_TAG_NAME = "USLT";
    private static final String RATING_TAG_NAME = "RATG";
    private static final String PLAY_COUNTER_TAG_NAME = "PCNT";
    private static final String PLAY_TIME_TAG_NAME = "TDPL";
    private static final String TAGGING_TIME_TAG_NAME = "TDTG";
    private static final String ENCODER_TAG_NAME = "TENC";

    private static final String COVER_FORMAT = "jpg";
    private static final int COVER_SIZE_LIMIT = 200;
    private static final String LYRICS_DESCRIPTION = "Lyrics from http://www.lyrc.com.ar";
    private static final String LYRICS_LANGUAGE = "English";

    /**
     * Create a new (empty) header
     */
    public ID3v2Header() {
        this.version = new ID3v2Version();
        this.unsynchronized = false;
        this.extended = false;
        this.experimental = false;
        this.footer = false;
        this.frames = new ArrayList<ID3v2Frame>(1);

        valid = true;
    }

    public ID3v2Header(String artist, String album, String track,
                       int index, ID3Genre genre, int year, String comment) {
        this();

        setArtist(artist);
        setAlbum(album);
        setTrack(track);
        setIndex(index);
        setGenre(genre);
        setYear(year);
        setComment(comment);
    }

    public ID3v1Tail toID3v1Tail() {
        return new ID3v1Tail(getArtist(), getAlbum(), getTrack(),
                getIndex(), getGenre(), getYear(), getComment()
        );
    }

    public void migrateToVersion(ID3v2Version current) {
        if (!getVersion().equals(current)) {
            for (ID3v2Frame f : getFrames()) {
                f.migrateToVersion(current);
            }
            setVersion(current);
        }
    }

    // --- read object -----------------------------------------

    /**
     * Reads the ID3v2 header from the input stream.
     *
     * @param in the InputStream to read from
     * @return if the read header is valid
     * @throws NoID3v2HeaderException if no header can be found or exists
     * @throws IOException            if an error occurs
     */
    public boolean read(InputStream in) throws NoID3v2HeaderException, IOException {
        valid = false;
        readSize = 0;

        log.fine("Reading ID3v2 header");

        if (!checkForID3v2Header(in)) {
            throw new NoID3v2HeaderException();

        } else {
            valid = readHeader(header);

            if (valid && extended)
                valid = valid & readExtendedHeader(in);

            if (valid)
                valid = valid & readFrames(in);
        }

        return valid;
    }

    /**
     * Check if ID3v2 header is present
     *
     * @return true if tag present
     * @throws IOException if an error occurs
     */
    protected boolean checkForID3v2Header(InputStream in) throws IOException {
        header = new byte[ID3V2_HEADER_SIZE];
        if (in.read(header, 0, ID3V2_HEADER_SIZE) != ID3V2_HEADER_SIZE)
            return false;

        String id3v2Tag = new String(header, 0, ID3_SIZE, ISO_8859_1_ENCODING);
        if (!id3v2Tag.equals(ID3V2TAG))
            return false;

        // next two bytes must be smaller than OxFF
        if (header[3] == (byte) 0xFF || header[4] == (byte) 0xFF)
            return false;

        // for safety's sake (who knows what future versions will bring),
        // the flags are not checked

        // last 4 bytes must be smaller than 0x80 (first bit set to 0)
        //noinspection RedundantIfStatement
        if ((header[6] & 0xFF) >= 0x80 || (header[7] & 0xFF) >= 0x80 ||
                (header[8] & 0xFF) >= 0x80 || (header[9] & 0xFF) >= 0x80)
            return false;

        return true;
    }

    protected boolean readHeader(byte[] data) throws IOException {
        String id3v2Tag = new String(data, 0, ID3_SIZE, ISO_8859_1_ENCODING);
        if (id3v2Tag.equals(ID3V2TAG)) {
            int major = data[3] & 0xFF;
            int minor = data[4] & 0xFF;

            version = new ID3v2Version(major, minor);

            // read & read flags
            int flags = data[5] & 0xFF;
            unsynchronized = (flags & UNSYNCHRONIZATION_FLAG) != 0;
            extended = (flags & EXTENDED_HEADER_FLAG) != 0;
            experimental = (flags & EXPERIMENTAL_FLAG) != 0;
            footer = (flags & FOOTER_FLAG) != 0; // TODO read footer

            // size of the complete header is stored in 4 bytes,
            // which all have their highest bit set to 0 (unsynchronization)
            readSize = (data[9] & 0xFF) +
                    ((data[8] & 0xFF) << 7) +
                    ((data[7] & 0xFF) << 14) +
                    ((data[6] & 0xFF) << 21);

            return true;
        }

        return false;
    }

    protected boolean readExtendedHeader(InputStream in) throws IOException {
        extendedHeader = new ID3v2ExtendedHeader();
        return extendedHeader.read(in);
    }

    protected boolean readFrames(InputStream in) throws IOException {
        byte[] buffer = new byte[(int) readSize];
        if (in.read(buffer, 0, buffer.length) != buffer.length)
            throw new IOException("Read invalid header");

        // collect frames
        int frameBytes = 0;
        while (frameBytes < readSize) {
            ID3v2Frame frame = new ID3v2Frame(version);

            int parsedBytes = frame.parse(buffer, frameBytes);

            // found valid frame?
            if (parsedBytes > 0 && frame.isValid()) {
                add(frame);
                frameBytes += parsedBytes;
            } else
                frameBytes++;
        }

        return true;
    }

    // --- write object ----------------------------------------

    /**
     * Writes the ID3v2 header to the OutputStream.
     *
     * @throws IOException if an error occurs
     */
    public void write(OutputStream out) throws IOException {
        byte[] bytes = getBytes();
        log.fine("Writing ID3v2 header (" + bytes.length + " bytes)");
        out.write(bytes);

        if (extended)
            extendedHeader.write(out);
    }

    private long pad(long size) {
        long paddedSize = size + (64 - size % 64) + 64;
        assert paddedSize % 64 == 0;
        return paddedSize;
    }

    protected byte[] getBytes() throws UnsupportedEncodingException {
        int writeSize = (int) getWriteSize();
        byte[] data = new byte[writeSize];

        byte[] headerID3 = ID3V2TAG.getBytes();
        System.arraycopy(headerID3, 0, data, 0, ID3_SIZE);

        byte[] headerVersion = version.getBytes();
        System.arraycopy(headerVersion, 0, data, ID3_SIZE, ID3v2Version.VERSION_SIZE);

        byte flags = 0;
        if (unsynchronized)
            flags += UNSYNCHRONIZATION_FLAG;

        if (extended)
            flags += EXTENDED_HEADER_FLAG;

        if (experimental)
            flags += EXPERIMENTAL_FLAG;

        if (footer)
            flags += FOOTER_FLAG;

        byte[] headerFlags = new byte[]{flags};
        System.arraycopy(headerFlags, 0, data, ID3_SIZE + ID3v2Version.VERSION_SIZE, FLAG_SIZE);

        // write frame size here (excluding header bytes) as LAME 3.93 does, too
        long frameSize = getFrameSize();
        byte[] frameSizeData = new byte[SIZE_SIZE];
        for (int i = 0; i < frameSizeData.length; i++) {
            frameSizeData[i] = (byte) ((frameSize >> ((3 - i) * 7)) & 0x7f);
        }
        // was: frameSizeData = BitConversion.create4BigEndian(contentSize);
        System.arraycopy(frameSizeData, 0, data, ID3_SIZE + ID3v2Version.VERSION_SIZE + FLAG_SIZE, SIZE_SIZE);

        int count = ID3_SIZE + ID3v2Version.VERSION_SIZE + SIZE_SIZE + FLAG_SIZE;
        for (ID3v2Frame f : frames) {
            byte[] frameData = f.getBytes();
            // TODO don't want to write empty tags, think of a way to determine whether a tag is empty
            System.arraycopy(frameData, 0, data, count, frameData.length);
            count += frameData.length;
        }

        return data;
    }

    // --- get uobject ------------------------------------------

    public boolean isValid() {
        return valid;
    }

    public ID3v2Version getVersion() {
        return version;
    }

    public long getReadSize() {
        return readSize > 0 ? readSize + ID3V2_HEADER_SIZE : 0;
    }

    /**
     * Return the padded size of this header including its frames.
     *
     * @return the padded size of this header including its frames
     */
    public long getWriteSize() {
        long headerSize = getHeaderSize();
        return headerSize > 0 ? pad(getHeaderSize()) : 0;
    }

    /**
     * Return the size of this header including its frames.
     *
     * @return the size of this header including its frames
     */
    public long getHeaderSize() {
        long frameSize = getFrameSize();
        return frameSize > 0 ? frameSize + ID3V2_HEADER_SIZE : 0;
    }

    /**
     * Return the summed size of the content + metadata of the
     * frames this header consists of.
     *
     * @return the summed size of the content of the frames
     *         this header consists of
     */
    public long getFrameSize() {
        long size = 0;
        for (ID3v2Frame f : frames) {
            size += f.getFrameSize();
        }
        return size;
    }

    /**
     * Return the summed size of the content of the frames
     * this header consists of.
     *
     * @return the summed size of the content of the frames
     *         this header consists of
     */
    public long getContentSize() {
        long size = 0;
        for (ID3v2Frame f : frames) {
            size += f.getContentSize();
        }
        return size;
    }

    /**
     * Returns true if the unsynchronization bit is set in this header.
     *
     * @return true if the unsynchronization bit is set in this header.
     */
    public boolean isUnsynchronized() {
        return unsynchronized;
    }

    /**
     * Returns true if this tag has an extended header.
     *
     * @return true if this tag has an extended header
     */
    public boolean isExtendedHeader() {
        return extended;
    }

    /**
     * Returns true if the experimental bit of this header is set.
     *
     * @return true if the experimental bit of this header is set
     */
    public boolean isExperimental() {
        return experimental;
    }

    /**
     * Returns true if this tag has a footer.
     *
     * @return true if this tag has a footer
     */
    public boolean isFooter() {
        return footer;
    }


    public List<ID3v2Frame> getFrames() {
        return frames;
    }

    public ID3v2Frame getFrame(String tagName) {
        for (ID3v2Frame f : frames) {
            if (f.isTagWithName(tagName)) {
                return f;
            }
        }
        return null;
    }

    public List<ID3v2Frame> getFrames(String tagName) {
        List<ID3v2Frame> result = new ArrayList<ID3v2Frame>();

        for (ID3v2Frame f : frames) {
            if (f.isTagWithName(tagName))
                result.add(f);
        }

        return result;
    }

    public ID3v2Frame getFrame(String tagName, String description, String language) {
        for (ID3v2Frame f : frames) {
            if (f.isTagWithName(tagName) && description.equals(f.getDescription()) && language.equals(f.getLanguage())) {
                return f;
            }
        }
        return null;
    }


    public String getStringContent(String tagName) {
        ID3v2Frame frame = getFrame(tagName);
        return frame != null ? frame.getStringContent() : null;
    }

    protected String getTextContent(String tagName, String description, String language) {
        ID3v2Frame frame = getFrame(tagName, description, language);
        return frame != null ? frame.getTextContent() : null;
    }

    protected String getTextContent(String tagName) {
        ID3v2Frame frame = getFrame(tagName);
        return frame != null ? frame.getTextContent() : null;
    }

    protected int getIntContent(String tagName) {
        String string = getStringContent(tagName);
        return parseInt(string, tagName);
    }

    protected Calendar getDateContent(String tagName) {
        String string = getStringContent(tagName);
        return string != null ? ISO8601.parse(string) : null;
    }

    private int parseInt(String string, String tagName) {
        try {
            if (string != null)
                return Integer.valueOf(string.trim());
        } catch (NumberFormatException e) {
            log.severe("Invalid int value in tag " + tagName + ": '" + string + "' (" + string.length() + " chars)");
        }
        return -1;
    }

    private int parseIndex(String tagName) {
        String indexString = getStringContent(tagName);
        if (indexString == null)
            return -1;

        // iTunes encodes trackindex and part of set index as index/count
        if (indexString.indexOf('/') != -1)
            indexString = indexString.substring(0, indexString.indexOf('/'));
        return parseInt(indexString, tagName);
    }

    private int parseCount(String tagName) {
        String indexString = getStringContent(tagName);
        if (indexString == null)
            return -1;

        if (indexString.indexOf('/') != -1) {
            indexString = indexString.substring(indexString.indexOf('/') + 1, indexString.length());
            return parseInt(indexString, tagName);
        }

        // no slash found, no count found
        return -1;
    }

    protected byte[] getByteContent(String tagName) {
        ID3v2Frame frame = getFrame(tagName);
        return frame != null ? frame.getByteContent() : null;
    }


    private ID3v2Frame addID3v2Frame(String tagName, String description, String language) {
        tagName = getTagNameForAdd(tagName);
        ID3v2Frame frame = getFrame(tagName, description, language);
        if (frame == null) {
            frame = new ID3v2Frame(tagName, version);
            frame.setDescription(description);
            frame.setLanguage(language);
            add(frame);
        }
        return frame;
    }

    private String getTagNameForAdd(String tagName) {
        if (version.isObsolete()) {
            String obsoleteTagName = ID3v2Tag.findObsoleteTagName(tagName);
            if (obsoleteTagName != null)
                tagName = obsoleteTagName;
        }
        return tagName;
    }

    public ID3v2Frame addID3v2Frame(String tagName) {
        tagName = getTagNameForAdd(tagName);
        ID3v2Frame frame = getFrame(tagName);
        if (frame == null) {
            frame = new ID3v2Frame(tagName, version);
            add(frame);
        }
        return frame;
    }

    public void removeID3v2Frame(String tagName) {
        List<ID3v2Frame> frames = getFrames(tagName);
        for (ID3v2Frame f : frames) {
            remove(f);
        }
    }


    protected void setContent(String tagName, String value) {
        ID3v2Frame frame = addID3v2Frame(tagName);
        frame.setText(value);
    }

    protected void setContent(String tagName, int value) {
        setContent(tagName, Integer.toString(value));
    }

    protected void setContent(String tagName, Calendar value) {
        setContent(tagName, ISO8601.format(value));
    }

    protected void setContent(String tagName, byte[] value) {
        ID3v2Frame frame = addID3v2Frame(tagName);
        frame.setBytes(value);
    }

    // --- MetaData get ----------------------------------------

    public String getTrack() {
        return getStringContent(TRACK_TAG_NAME);
    }

    public String getArtist() {
        return getStringContent(ARTIST_TAG_NAME);
    }

    public String getBand() {
        return getStringContent(BAND_TAG_NAME);
    }

    public boolean isCompilation() {
        String compilation = getStringContent(COMPILATION_TAG_NAME);
        return compilation != null && compilation.equals("1");
    }

    public String getAlbum() {
        return getStringContent(ALBUM_TAG_NAME);
    }

    public int getYear() {
        return getIntContent(YEAR_TAG_NAME);
    }

    public ID3Genre getGenre() {
        String genreStr = getStringContent(GENRE_TAG_NAME);
        return ID3Genre.findWellknown(genreStr);
    }

    public int getIndex() {
        return parseIndex(INDEX_TAG_NAME);
    }

    public int getCount() {
        return parseCount(INDEX_TAG_NAME);
    }

    public int getPartOfSetIndex() {
        return parseIndex(PART_OF_SET_INDEX_TAG_NAME);
    }

    public int getPartOfSetCount() {
        return parseCount(PART_OF_SET_INDEX_TAG_NAME);
    }

    public String getComment() {
        return getComment("", "");
    }

    public String getComment(String description, String language) {
        return getTextContent(COMMENT_TAG_NAME, description, language);
    }

    public int getSeconds() {
        int seconds = getIntContent(SECONDS_TAG_NAME);
        return seconds > 0 ? seconds / 1000 : seconds;
    }

    public String getMusicBrainzId() {
        byte[] musicBrainzId = getByteContent(MUSICBRAINZ_ID_TAG_NAME);
        if (musicBrainzId == null || musicBrainzId.length == 0)
            return null;
        try {
            return new String(musicBrainzId, ISO_8859_1_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.severe("Cannot create string for music brainz id: " + e.getMessage() + ": " + e.getMessage());
            return new String(musicBrainzId);
        }
    }

    public String getPublisher() {
        return getTextContent(PUBLISHER_TAG_NAME);
    }

    public byte[] getAttachedPicture() {
        return getByteContent(ATTACHED_PICTURE_TAG_NAME);
    }

    public String getLyrics(String description, String language) {
        return getTextContent(LYRICS_TAG_NAME, description, language);
    }

    public String getLyrics() {
        return getLyrics(LYRICS_DESCRIPTION, LYRICS_LANGUAGE);
    }

    public int getRating() {
        byte[] bytes = getByteContent(RATING_TAG_NAME);
        return bytes != null ? BitConversion.extract4BigEndian(bytes) : -1;
    }

    public int getPlayCount() {
        byte[] bytes = getByteContent(PLAY_COUNTER_TAG_NAME);
        return bytes != null ? BitConversion.extract4BigEndian(bytes) : -1;
    }

    public Calendar getPlayTime() {
        return getDateContent(ID3v2Header.PLAY_TIME_TAG_NAME);
    }

    public Calendar getTaggingTime() {
        return getDateContent(ID3v2Header.TAGGING_TIME_TAG_NAME);
    }

    public String getEncoder() {
        return getTextContent(ENCODER_TAG_NAME);
    }

    // --- set object ------------------------------------------

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    // --- MetaData set ----------------------------------------

    public void setArtist(String newArtist) {
        setContent(ARTIST_TAG_NAME, newArtist);
    }

    public void setBand(String newBand) {
        setContent(BAND_TAG_NAME, newBand);
    }

    public void setCompilation(boolean isCompilation) {
        setContent(COMPILATION_TAG_NAME, isCompilation ? "1" : "0");
    }

    public void setAlbum(String newAlbum) {
        setContent(ALBUM_TAG_NAME, newAlbum);
    }

    public void setTrack(String newTrack) {
        setContent(TRACK_TAG_NAME, newTrack);
    }

    public void setYear(int newYear) {
        setContent(YEAR_TAG_NAME, newYear);
    }

    public void setGenre(ID3Genre newGenre) {
        String genreName = (newGenre != null) ? newGenre.getFormattedName() : ID3Genre.UNKNOWN;
        setContent(GENRE_TAG_NAME, genreName);
    }

    private void setIndexCount(String tagName, int newIndex, int newCount) {
        String indexString = "";
        if (newIndex != -1) {
            indexString = Integer.toString(newIndex);
            if (newCount != -1)
                indexString += "/" + Integer.toString(newCount);
        }
        setContent(tagName, indexString);
    }

    public void setIndex(int newIndex) {
        setIndexCount(INDEX_TAG_NAME, newIndex, getCount());
    }

    public void setCount(int newCount) {
        if (newCount < getIndex())
            throw new IllegalArgumentException("Count " + newCount + " is smaller than index " + getIndex());
        setIndexCount(INDEX_TAG_NAME, getIndex(), newCount);
    }

    public void setPartOfSetIndex(int newIndex) {
        setIndexCount(PART_OF_SET_INDEX_TAG_NAME, newIndex, getPartOfSetCount());
    }

    public void setPartOfSetCount(int newCount) {
        if (newCount < getPartOfSetIndex())
            throw new IllegalArgumentException("Count " + newCount + " is smaller than index " + getPartOfSetIndex());
        setIndexCount(PART_OF_SET_INDEX_TAG_NAME, getPartOfSetIndex(), newCount);
    }

    public void setComment(String newComment) {
        setComment(newComment, "", "");
    }

    public void setComment(String newComment, String description, String language) {
        ID3v2Frame frame = addID3v2Frame(COMMENT_TAG_NAME, description, language);
        frame.setText(newComment);
    }

    public void setSeconds(int newSeconds) {
        setContent(SECONDS_TAG_NAME, newSeconds * 1000);
    }

    public void setMusicBrainzId(String newMusicBrainzId) {
        ID3v2Frame frame = addID3v2Frame(MUSICBRAINZ_ID_TAG_NAME);
        frame.setDescription("http://musicbrainz.org");
        try {
            frame.setBytes(newMusicBrainzId.getBytes(ISO_8859_1_ENCODING));
        } catch (UnsupportedEncodingException e) {
            log.severe("Cannot get bytes from music brainz id: " + e.getMessage() + ": " + e.getMessage());
            frame.setBytes(newMusicBrainzId.getBytes());
        }
    }

    public void setPublisher(String newPublisher) {
        setContent(PUBLISHER_TAG_NAME, newPublisher);
    }

    public void setAttachedPicture(byte[] newAttachedPicture, MimeType mimeType, String description) {
        ID3v2Frame frame = addID3v2Frame(ATTACHED_PICTURE_TAG_NAME);
        frame.setDescription(description);
        frame.setPictureType(PictureType.getPictureType(0x03));
        frame.setMimeType(mimeType);
        frame.setBytes(newAttachedPicture);
    }

    public void setCover(byte[] newCover) {
        byte[] resizedCover = new ImageResizer().resize(newCover, COVER_FORMAT, COVER_SIZE_LIMIT, COVER_SIZE_LIMIT);
        MimeType mimeType = new MimeTypeGuesser().guess(resizedCover);
        setAttachedPicture(resizedCover, mimeType, "cover");
    }

    public void setLyrics(String newLyrics, String description, String language) {
        ID3v2Frame frame = addID3v2Frame(LYRICS_TAG_NAME, description, language);
        frame.setText(newLyrics);
    }

    public void setLyrics(String newLyrics) {
        setLyrics(newLyrics, LYRICS_DESCRIPTION, LYRICS_LANGUAGE);
    }

    public void setRating(int newRating) {
        ID3v2Frame frame = addID3v2Frame(RATING_TAG_NAME);
        frame.setBytes(BitConversion.create4BigEndian(newRating));
    }

    public void setPlayCount(int newPlayCount) {
        ID3v2Frame frame = addID3v2Frame(PLAY_COUNTER_TAG_NAME);
        frame.setBytes(BitConversion.create4BigEndian(newPlayCount));
    }

    public void setPlayTime(Calendar newPlayTime) {
        ID3v2Frame frame = addID3v2Frame(PLAY_TIME_TAG_NAME);
        frame.setText(ISO8601.format(newPlayTime));
    }

    public void setTaggingTime(Calendar newTaggingTime) {
        ID3v2Frame frame = addID3v2Frame(TAGGING_TIME_TAG_NAME);
        frame.setText(ISO8601.format(newTaggingTime));
    }

    // --- set object ------------------------------------------

    public void setVersion(ID3v2Version version) {
        this.version = version;
    }

    /**
     * Set the unsynchronization flag for this header.
     *
     * @param unsynchronized the new value of the unsynchronization flag
     */
    public void setUnsynchronized(boolean unsynchronized) {
        this.unsynchronized = unsynchronized;
    }

    /**
     * Set the value of the extended header bit of this header.
     *
     * @param extended the new value of the extended header bit
     */
    public void setExtendedHeader(boolean extended) {
        this.extended = extended;
    }

    /**
     * Set the value of the experimental bit of this header.
     *
     * @param experimental the new value of the experimental bit
     */
    public void setExperimental(boolean experimental) {
        this.experimental = experimental;
    }

    /**
     * Sets the value of the footer bit for this header.
     *
     * @param footer the new value of the footer bit for this header
     */
    public void setFooter(boolean footer) {
        this.footer = footer;
    }


    /**
     * Add a ID3v2 frame
     *
     * @param frame the ID3v2 Frame to add
     */
    public void add(ID3v2Frame frame) {
        frames.add(frame);
    }

    /**
     * Remove a ID3v2Frame
     *
     * @param frame the ID3v2Frame to remove
     */
    public void remove(ID3v2Frame frame) {
        frames.remove(frame);
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ID3v2Header[isValid=").append(isValid());
        if (isValid()) {
            buffer.append(", ").
                    append("release=").append(getVersion().getVersionString()).append(", ").
                    append("unsynchronized=").append(isUnsynchronized()).append(", ").
                    append("extended=").append(isExtendedHeader()).append(", ");
            if (isExtendedHeader())
                buffer.append("extendHeader=").append(extendedHeader).append(", ");
            buffer.append("experimental=").append(isExperimental()).append(", ").
                    append("footer=").append(isFooter()).append(", ").
                    append("readSize=").append(getReadSize()).append(", ").
                    append("contentSize=").append(getContentSize()).append(", ").
                    append("track=").append(getTrack()).append(", ").
                    append("artist=").append(getArtist()).append(", ").
                    append("album=").append(getAlbum()).append(", ").
                    append("year=").append(getYear()).append(", ").
                    append("comment=").append(getComment()).append(", ").
                    append("index=").append(getIndex()).append(", ").
                    append("count=").append(getCount()).append(", ").
                    append("genre=").append(getGenre()).append(", ").
                    append("\nframes=[");
            for (ID3v2Frame f : getFrames()) {
                buffer.append(f).append(", ");
            }
            buffer.append("]");
        }
        buffer.append("]");

        return buffer.toString();
    }

    // --- member variables ------------------------------------

    /**
     * valid
     */
    protected boolean valid;

    /**
     * header if read
     */
    protected byte[] header;

    /**
     * ID3v2 data
     */
    protected ID3v2Version version;
    protected ID3v2ExtendedHeader extendedHeader;
    protected long readSize;

    /**
     * flags
     */
    protected boolean unsynchronized;
    protected boolean extended;
    protected boolean experimental;
    protected boolean footer;

    /**
     * frames
     */
    protected List<ID3v2Frame> frames;
}
