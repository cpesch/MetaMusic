/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2004 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.musicbrainz;

import com.hp.hpl.jena.rdf.model.Model;
import com.ldodds.musicbrainz.*;
import slash.metamusic.util.FileCache;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * MusicBrainzClient implements the connection to the musicbrainz.org
 * server.
 *
 * @author Christian Pesch
 * @version $Id: MusicBrainzClient.java 914 2006-12-26 20:44:49Z cpesch $
 */

public class MusicBrainzClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MusicBrainzClient.class.getName());

    protected static int QUERY_DEPTH = 4;
    protected static int QUERY_MAXIMUM_ITEMS = 1;

    private FileCache fileCache = new FileCache();
    private boolean cache = true;

    private MusicBrainz musicBrainz;
    private String server;


    public MusicBrainzClient(String server) {
        setServer(server);
        initializeMusicBrainz();
    }

    public MusicBrainzClient() {
        this(MusicBrainzImpl.DEFAULT_SERVER);
    }


    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public void setCacheDirectoryName(String cacheDirectoryName) {
        fileCache.setCacheDirectoryName(cacheDirectoryName);
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    protected void initializeMusicBrainz() {
        this.musicBrainz = new MusicBrainzImpl(getServer());
    }

    protected String replaceWhitespaces(String string) {
        StringBuffer buffer = new StringBuffer(string);
        for (int i = 0; i < buffer.length(); i++) {
            char c = buffer.charAt(i);
            if (Character.isWhitespace(c))
                buffer.setCharAt(i, '_');
        }
        return buffer.toString();
    }

    protected String createCacheKey(String category, String name) {
        return replaceWhitespaces(category) + "_" + replaceWhitespaces(name);
    }

    protected interface Query {
        public Object query(MusicBrainz musicBrainz, String name) throws IOException;
    }

    protected Object performQuery(String category, String name,
                                  Query query, boolean download) {
        try {
            String cacheKey = createCacheKey(category, name);
            Object result = fileCache.getFileAsObject(cacheKey);
            if (result != null && cache) {
                log.info("Query for " + category + " '" + name + "' is cached");
            } else if (download) {
                result = query.query(musicBrainz, name);
                fileCache.putAsObject(cacheKey, result);
            }
            return result;
        } catch (IOException e) {
            log.severe("Could not query music brainz for " + category + " '" + name + "': " + e.getMessage());
        }
        return null;
    }

    public String peekArtistId(String artistName) {
        return fetchArtistId(artistName, false);
    }

    public String fetchArtistId(String artistName) {
        return fetchArtistId(artistName, true);
    }

    protected String fetchArtistId(String artistName, boolean download) {
        if (artistName.equals("Various"))
            return MusicBrainz.VARIOUS_ARTISTS;

        List artists = (List) performQuery("Artist", artistName, new Query() {
            public Object query(MusicBrainz musicBrainz, String name) throws IOException {
                Model model = musicBrainz.findArtistByName(name, QUERY_DEPTH, QUERY_MAXIMUM_ITEMS);
                List artists = BeanPopulator.getArtists(model);
                log.finer("Fetching artist " + name + " found: " + artists);
                return artists;
            }
        }, download);
        return artists != null && artists.size() > 0 ? ((Artist) artists.get(0)).getId() : null;
    }

    public String peekAlbumId(String albumName) {
        return fetchAlbumId(albumName, false);
    }

    public String fetchAlbumId(String albumName) {
        return fetchAlbumId(albumName, true);
    }

    protected String fetchAlbumId(String albumName, boolean download) {
        List albums = (List) performQuery("Album", albumName, new Query() {
            public Object query(MusicBrainz musicBrainz, String name) throws IOException {
                Model model = musicBrainz.findAlbumByName(name, QUERY_DEPTH, QUERY_MAXIMUM_ITEMS);
                List albums = BeanPopulator.getAlbums(model);
                log.finer("Fetching albums " + name + " found: " + albums);
                return albums;
            }
        }, download);
        return albums != null && albums.size() > 0 ? ((Album) albums.get(0)).getId() : null;
    }

    public String peekTrackId(String trackName) {
        return fetchTrackId(trackName, false);
    }

    public String fetchTrackId(String trackName) {
        return fetchTrackId(trackName, true);
    }

    protected String fetchTrackId(String trackName, boolean download) {
        List tracks = (List) performQuery("Track", trackName, new Query() {
            public Object query(MusicBrainz musicBrainz, String name) throws IOException {
                Model model = musicBrainz.findTrackByName(name, QUERY_DEPTH, QUERY_MAXIMUM_ITEMS);
                List tracks = BeanPopulator.getTracks(model);
                log.finer("Fetching tracks " + name + " found: " + tracks);
                return tracks;
            }
        }, download);
        return tracks != null && tracks.size() > 0 ? ((Track) tracks.get(0)).getId() : null;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("slash.metamusic.musicbrainz.MusicBrainzClient <name>");
            System.exit(1);
        }
        String name = args[0];

        MusicBrainzClient client = new MusicBrainzClient();
        System.out.println("Server: " + client.getServer());

        System.out.println("Name:" + name);
        System.out.println("ArtistId:" + client.fetchArtistId(name));
        System.out.println("AlbumId:" + client.fetchAlbumId(name));
        System.out.println("TrackId:" + client.fetchTrackId(name));
        System.exit(0);
    }
}
