/**
 * LyricWikiPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package slash.metamusic.lyricwiki;

public interface LyricWikiPortType extends java.rmi.Remote {

    /**
     * Check if a song exists in the LyricWiki database yet
     */
    public boolean checkSongExists(java.lang.String artist, java.lang.String song) throws java.rmi.RemoteException;

    /**
     * Search for an artist by name and return up to 10 close matches
     */
    public java.lang.String[] searchArtists(java.lang.String searchString) throws java.rmi.RemoteException;

    /**
     * Search for an album on LyricWiki and return up to 10 close
     * matches (year optional)
     */
    public slash.metamusic.lyricwiki.AlbumResult[] searchAlbums(java.lang.String artist, java.lang.String album, int year) throws java.rmi.RemoteException;

    /**
     * Search for a song on LyricWiki and get up to 10 close matches
     */
    public slash.metamusic.lyricwiki.SongResult searchSongs(java.lang.String artist, java.lang.String song) throws java.rmi.RemoteException;

    /**
     * Get the lyrics for a the current Song of the Day on LyricWiki
     */
    public slash.metamusic.lyricwiki.SOTDResult getSOTD() throws java.rmi.RemoteException;

    /**
     * Get the lyrics for a LyricWiki song with the exact artist and
     * song match
     */
    public slash.metamusic.lyricwiki.LyricsResult getSong(java.lang.String artist, java.lang.String song) throws java.rmi.RemoteException;

    /**
     * Get the lyrics for a LyricWiki song with the exact artist and
     * song match
     */
    public slash.metamusic.lyricwiki.LyricsResult getSongResult(java.lang.String artist, java.lang.String song) throws java.rmi.RemoteException;

    /**
     * Gets the entire discography for an artist
     */
    public slash.metamusic.lyricwiki.AlbumData[] getArtist(javax.xml.rpc.holders.StringHolder artist) throws java.rmi.RemoteException;

    /**
     * Gets the track listing, album cover, and amazon link for an
     * album
     */
    public void getAlbum(javax.xml.rpc.holders.StringHolder artist, javax.xml.rpc.holders.StringHolder album, javax.xml.rpc.holders.IntHolder year, javax.xml.rpc.holders.StringHolder amazonLink, javax.xml.rpc.holders.StringHolder imgUrl, javax.xml.rpc.holders.StringHolder url, slash.metamusic.lyricwiki.holders.ArrayOfstringHolder songs) throws java.rmi.RemoteException;

    /**
     * Gets the hometown for an artist
     */
    public void getHometown(java.lang.String artist, javax.xml.rpc.holders.StringHolder country, javax.xml.rpc.holders.StringHolder state, javax.xml.rpc.holders.StringHolder hometown) throws java.rmi.RemoteException;

    /**
     * Gets the most popular songs. Currently, this data comes from
     * the iTunes Top 100 feed, so the largest possible value of "limit"
     * is 100.  Limit defaults to 10.
     */
    public slash.metamusic.lyricwiki.TopSong[] getTopSongs(java.lang.String limit) throws java.rmi.RemoteException;

    /**
     * Posts data of an artist and their discography.  Will create
     * any missing album pages based on the data passed in.
     */
    public void postArtist(boolean overwriteIfExists, javax.xml.rpc.holders.StringHolder artist, slash.metamusic.lyricwiki.AlbumData[] albums, javax.xml.rpc.holders.BooleanHolder dataUsed, javax.xml.rpc.holders.StringHolder message) throws java.rmi.RemoteException;

    /**
     * Posts data for a single album including its track-list and
     * optionally the amazon ASIN
     */
    public void postAlbum(boolean overwriteIfExists, javax.xml.rpc.holders.StringHolder artist, javax.xml.rpc.holders.StringHolder album, javax.xml.rpc.holders.IntHolder year, java.lang.String asin, java.lang.String[] songs, javax.xml.rpc.holders.BooleanHolder dataUsed, javax.xml.rpc.holders.StringHolder message) throws java.rmi.RemoteException;

    /**
     * Posts data for a single song.  If correcting exiting lyrics,
     * make sure overwriteIfExists is set to true.  In the onAlbums array,
     * if artist is left blank, it will default to the artist of the song.
     */
    public void postSong(boolean overwriteIfExists, javax.xml.rpc.holders.StringHolder artist, javax.xml.rpc.holders.StringHolder song, java.lang.String lyrics, slash.metamusic.lyricwiki.AlbumResult[] onAlbums, javax.xml.rpc.holders.BooleanHolder dataUsed, javax.xml.rpc.holders.StringHolder message) throws java.rmi.RemoteException;

    /**
     * Posts data for a single song.  If correcting exiting lyrics,
     * make sure overwriteIfExists is set to true.  In the onAlbums array,
     * if artist is left blank, it will default to the artist of the song.For
     * the flags parameter, this is a comma-separated list of flags. For
     * example, pass 'LW_SANDBOX' in to use the sandbox for testing and not
     * actually update the site.
     */
    public void postSong_flags(boolean overwriteIfExists, javax.xml.rpc.holders.StringHolder artist, javax.xml.rpc.holders.StringHolder song, java.lang.String lyrics, slash.metamusic.lyricwiki.AlbumResult[] onAlbums, java.lang.String flags, javax.xml.rpc.holders.BooleanHolder dataUsed, javax.xml.rpc.holders.StringHolder message) throws java.rmi.RemoteException;
}
