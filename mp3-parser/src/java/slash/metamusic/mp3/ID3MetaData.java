/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

/**
 * My instances represent ID3 meta data common to both
 * ID3v1Tail and ID3v2Header.
 *
 * @author Christian Pesch
 */

public interface ID3MetaData {

    public boolean isValid();

    public long getReadSize();

    // --- get object ------------------------------------------

    public String getTrack();

    public String getArtist();

    public String getAlbum();

    public int getYear();

    public ID3Genre getGenre();

    public String getComment();

    public int getIndex();

    // --- set object ------------------------------------------

    public void setTrack(String newTrack);

    public void setArtist(String newArtist);

    public void setAlbum(String newAlbum);

    public void setYear(int newYear);

    public void setGenre(ID3Genre newGenre);

    public void setIndex(int newIndex);

    public void setComment(String newComment);

}
