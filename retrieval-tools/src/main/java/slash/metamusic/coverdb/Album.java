/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.coverdb;

/**
 * An album that is identified by the name of its artist and its title.
 *
 * @author Christian Pesch
 */

public class Album {
    public String artist;
    public String title;

    public Album(String artist, String album) {
        this.artist = artist;
        this.title = album;
    }

    public String toString() {
        return artist + " - " + title;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;

        final Album album = (Album) o;

        return !(title != null ? !title.equals(album.title) : album.title != null) &&
                !(artist != null ? !artist.equals(album.artist) : album.artist != null);
    }

    public int hashCode() {
        int result;
        result = (artist != null ? artist.hashCode() : 0);
        result = 29 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
