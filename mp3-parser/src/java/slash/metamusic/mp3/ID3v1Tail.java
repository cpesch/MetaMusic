/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

import slash.metamusic.mp3.util.BitConversion;
import slash.metamusic.util.StringHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 * My instances represent a ID3v1 tail of the MP3 frames as
 * described in http://www.id3.org/id3v1.html.
 *
 * @author Christian Pesch
 * @version $Id: ID3v1Tail.java 953 2007-01-21 15:31:50Z cpesch $
 */

public class ID3v1Tail implements ID3MetaData {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(ID3v1Tail.class.getName());

    public static final String ID3V1TAG = "TAG";
    public static final int TAG_SIZE = 3;
    public static final int ID3V1_SIZE = 128;

    public static final int TRACK_SIZE = 30;
    public static final int ARTIST_SIZE = 30;
    public static final int ALBUM_SIZE = 30;
    public static final int YEAR_SIZE = 4;
    public static final int INDEX_SIZE = 1;
    public static final int GENRE_SIZE = 1;
    public static final int COMMENT_SIZE = 28;

    /**
     * Encoding to use when converting from bytes to Unicode (String).
     */
    protected static final String ENCODING = "ISO8859_1";

    /**
     * Char to fill up strings with
     */
    protected static final char EMPTY_CHAR = ' ';

    /**
     * Create a new (empty) tail.
     */
    public ID3v1Tail() {
        this("", "", "", 0, new ID3Genre(""), 0, "");
    }

    public ID3v1Tail(String artist, String album, String track,
                     int index, ID3Genre genre, int year, String comment) {
        this.artist = artist;
        this.album = album;
        this.track = track;
        this.index = index;
        this.genre = genre;
        this.year = year;
        this.comment = comment;

        valid = true;
        id3v1dot1 = true;
    }

    public ID3v2Header toID3v2Header() {
        return new ID3v2Header(getArtist(), getAlbum(), getTrack(),
                getIndex(), getGenre(), getYear(), getComment()
        );
    }

    // --- read/write object -----------------------------------

    /**
     * Reads the ID3v1 tail from the InputStream.
     *
     * @param in the InputStream to read
     * @return if the read tail is valid
     * @throws NoID3v1TailException if no tail can be found or exists
     * @throws IOException          if an error occurs
     */
    public boolean read(InputStream in) throws NoID3v1TailException, IOException {
        valid = false;
        readSize = 0;

        log.fine("Reading ID3v1 tail");

        // search for a valid tail
        boolean tailFound = false;
        while (checkForID3v1Tail(in)) {
            tailFound = parse(in);
            if (tailFound) {
                valid = true;
                readSize = ID3V1_SIZE;
                break;
            }
        }

        if (!tailFound)
            throw new NoID3v1TailException();

        return valid;
    }

    /**
     * Check if ID3v1 tail is present
     *
     * @param in the InputStream to read
     * @return true if tail is present
     * @throws IOException if an error occurs
     */
    protected boolean checkForID3v1Tail(InputStream in) throws IOException {
        byte tag[] = ID3V1TAG.getBytes(ENCODING);

        while (true) {
            // read through stream until T is read
            int t = in.read();
            while (t != tag[0] && t != -1) {
                t = in.read();
            }

            if (t == -1) {
                return false;
            }

            // now next byte must be A
            int a = in.read();
            if (a == tag[1]) {

                // now next byte must be G
                int g = in.read();
                if (g == tag[2]) {
                    // synchronized
                    return true;
                }

                if (g == -1)
                    return false;
            }

            if (a == -1) {
                return false;
            } else {
                // continue search
            }
        }
    }

    protected boolean parse(InputStream in) throws NoID3v1TailException, IOException {
        int sizeToRead = ID3V1_SIZE;
        int sizeWhenTagRead = ID3V1_SIZE - ID3V1TAG.length();

        // ID3V1TAG has been read, using modulo operation if
        // there is falsely more than one tail
        if (sizeWhenTagRead == in.available() % 128)
            sizeToRead = sizeWhenTagRead;

        byte[] buffer = new byte[ID3V1_SIZE];
        if (in.read(buffer, ID3V1_SIZE - sizeToRead, sizeToRead) != sizeToRead)
            throw new NoID3v1TailException();

        // ID3V1TAG has not been read yet, so check it
        if (sizeToRead == ID3V1_SIZE) {
            String id3v1Tag = new String(buffer, 0, 3, ENCODING);
            if (!id3v1Tag.equals(ID3V1TAG))
                return false;
        }

        track = StringHelper.trim(new String(buffer, 3, ALBUM_SIZE, ENCODING));
        artist = StringHelper.trim(new String(buffer, 33, ARTIST_SIZE, ENCODING));
        album = StringHelper.trim(new String(buffer, 63, TRACK_SIZE, ENCODING));
        year = BitConversion.stringToInt(new String(buffer, 93, YEAR_SIZE, ENCODING));
        id3v1dot1 = (BitConversion.unsignedByteToInt(buffer[125]) == 0 &&
                BitConversion.unsignedByteToInt(buffer[126]) > 0);
        index = id3v1dot1 ? BitConversion.unsignedByteToInt(buffer[126]) : -1;
        comment = StringHelper.trim(new String(buffer, 97, id3v1dot1 ? 28 : 30, ENCODING));
        genre = new ID3Genre(BitConversion.unsignedByteToInt(buffer[127]));

        // last bytes read - this has been the tag
        return in.available() == 0;
    }

    /**
     * Writes the ID3v1 tail to the OutputStream.
     *
     * @param out the OutputStream to write to
     * @throws IOException if an error occurs
     */
    public void write(OutputStream out) throws IOException {
        byte[] bytes = getBytes();
        log.fine("Writing ID3v1 tail (" + bytes.length + " bytes)");
        out.write(bytes);
    }

    protected byte[] getBytes() throws UnsupportedEncodingException {
        byte[] data = new byte[(int) getWriteSize()];

        byte[] tag = ID3V1TAG.getBytes(ENCODING);
        System.arraycopy(tag, 0, data, 0, TAG_SIZE);

        byte[] trackArray = trimString(track, ALBUM_SIZE).getBytes(ENCODING);
        System.arraycopy(trackArray, 0, data, 3, ALBUM_SIZE);

        byte[] artistArray = trimString(artist, ARTIST_SIZE).getBytes(ENCODING);
        System.arraycopy(artistArray, 0, data, 33, ARTIST_SIZE);

        byte[] albumArray = trimString(album, TRACK_SIZE).getBytes(ENCODING);
        System.arraycopy(albumArray, 0, data, 63, TRACK_SIZE);

        byte[] yearArray = trimInt(year, YEAR_SIZE).getBytes(ENCODING);
        System.arraycopy(yearArray, 0, data, 93, YEAR_SIZE);

        byte[] commentArray = trimString(comment, getCommentSize()).getBytes(ENCODING);
        System.arraycopy(commentArray, 0, data, id3v1dot1 ? 97 : 95, getCommentSize());

        if (id3v1dot1) {
            byte[] indexArray = new byte[]{0x0, (byte) index};
            System.arraycopy(indexArray, 0, data, 125, INDEX_SIZE + 1);
        }

        byte[] genreArray = new byte[]{(byte) genre.getId()};
        System.arraycopy(genreArray, 0, data, 127, GENRE_SIZE);

        return data;
    }

    private int getCommentSize() {
        return COMMENT_SIZE + (id3v1dot1 ? 0 : INDEX_SIZE);
    }

    private String trimString(String str, int len) {
        if (str == null)
            str = "";
        if (str.length() > len)
            return str.substring(0, len - 1);

        StringBuffer buffer = new StringBuffer(str);
        while (buffer.length() < len)
            buffer.append(EMPTY_CHAR);

        return buffer.toString();
    }

    private String trimInt(int value, int len) {
        StringBuffer buffer = new StringBuffer(Integer.toString(value));
        while (buffer.length() < len)
            buffer.insert(0, EMPTY_CHAR);

        return buffer.toString();
    }

    // --- get object ------------------------------------------

    public boolean isValid() {
        return valid;
    }

    public boolean isID3v1dot1() {
        return id3v1dot1;
    }

    public long getReadSize() {
        return readSize;
    }

    private int length(String string) {
        return string != null ? string.length() : 0;
    }

    public long getContentSize() {
        return length(track) + length(artist) + length(album) + length(comment);
    }

    public long getWriteSize() {
        return getContentSize() > 0 ? ID3V1_SIZE : 0;
    }

    // --- MetaData get ----------------------------------------

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
        return year > 0 ? year : -1;
    }

    public ID3Genre getGenre() {
        return genre;
    }

    public String getComment() {
        return comment;
    }

    public int getIndex() {
        if (!isID3v1dot1())
            throw new IllegalArgumentException("No track information exists in ID3v1");
        return index;
    }

    // --- set object ------------------------------------------

    public void setID3v1dot1(boolean isID3v1dot1) {
        this.id3v1dot1 = isID3v1dot1;

        // reduce comment size
        if (isID3v1dot1)
            setComment(getComment());
    }

    public void setValid(boolean valid) {
        this.valid = valid;

        if (valid)
            setID3v1dot1(true);
    }

    // --- MetaData set ----------------------------------------

    public void setTrack(String newTrack) {
        if (newTrack != null && newTrack.length() > ALBUM_SIZE)
            newTrack = newTrack.substring(0, ALBUM_SIZE);

        this.track = newTrack;
    }

    public void setArtist(String newArtist) {
        if (newArtist != null && newArtist.length() > ARTIST_SIZE)
            newArtist = newArtist.substring(0, ARTIST_SIZE);

        this.artist = newArtist;
    }

    public void setAlbum(String newAlbum) {
        if (newAlbum != null && newAlbum.length() > TRACK_SIZE)
            newAlbum = newAlbum.substring(0, TRACK_SIZE);

        this.album = newAlbum;
    }

    public void setYear(int newYear) {
        if (newYear < 0)
            newYear = 0;
        else if (newYear > 9999)
            newYear = 9999;

        this.year = newYear;
    }

    public void setGenre(ID3Genre newGenre) {
        this.genre = newGenre;
    }

    public void setIndex(int newIndex) {
        setID3v1dot1(true);

        if (newIndex < 0)
            newIndex = 0;
        else if (newIndex > 9999)
            newIndex = 9999;

        this.index = newIndex;
    }

    public void setComment(String newComment) {
        if (newComment != null && newComment.length() > getCommentSize())
            newComment = newComment.substring(0, getCommentSize());

        this.comment = newComment;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("ID3v1Tail[isValid=").append(isValid());
        if (isValid()) {
            buffer.append(", isID3v1.1=").append(isID3v1dot1()).
                    append(", track=").append(getTrack()).
                    append(", artist=").append(getArtist()).
                    append(", album=").append(getAlbum()).
                    append(", year=").append(getYear()).
                    append(", comment=").append(getComment());
            if (isID3v1dot1())
                buffer.append(", index=").append(getIndex());
            buffer.append(", genre=").append(getGenre());
        }
        buffer.append("]");

        return buffer.toString();
    }

    // --- member variables ------------------------------------

    /**
     * file data
     */
    protected boolean valid;
    protected long readSize;

    /**
     * ID3v1 data
     */
    protected boolean id3v1dot1;
    protected String track;
    protected String artist;
    protected String album;
    protected String comment;
    protected ID3Genre genre;
    protected int year;
    protected int index;
}
