/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.freedb;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import slash.metamusic.client.CommandLineClient;
import slash.metamusic.coverdb.Album;
import slash.metamusic.coverdb.CoverDBClient;
import slash.metamusic.discid.DiscId;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A client to use the FreeDB cache for Cover downloads.
 *
 * @author Christian Pesch
 * @version $Id: CoverDownloader.java 959 2007-03-11 08:21:11Z cpesch $
 */

public class CoverDownloader extends CommandLineClient {
    private FreeDBClient freedb;
    private CoverDBClient cover = new CoverDBClient();

    public CoverDownloader() {
        freedb = new FreeDBClient();
    }

    protected void fillInOptions(Options options) {
    }

    protected boolean parseCommandLine(CommandLine commandLine) {
        return true;
    }

    protected String getUsage() {
        return getClass() + ".jar";
    }

    public void beforeRunning() {
    }

    public void afterRunning() {
    }

    public void run() {
        FreeDBCache freeDBCache = new FreeDBCache();
        Collection<DiscId> cachedDiscIds = freeDBCache.getCachedDiscIds();

        Set<String> artists = new HashSet<String>();
        Set<Album> albums = new HashSet<Album>();
        int count = 1;
        for (Iterator<DiscId> iterator = cachedDiscIds.iterator(); iterator.hasNext(); count++) {
            DiscId discId = iterator.next();
            if (discId != null) {
                long start = System.currentTimeMillis();
                try {
                    log.info("Collecting " + count + ". from " + cachedDiscIds.size() + " discids: " + discId);
                    CDDBRecord[] records = freedb.queryDiscId(discId);
                    collect(records, artists, albums);
                } catch (Exception e) {
                    log.severe("Exception while collecting " + discId + ": " + e.getMessage());
                } finally {
                    long end = System.currentTimeMillis();
                    log.fine("Collecting from " + count + " discids took " + (end - start) + " milliseconds");
                }
            }
        }

        downloadArtists(artists);
        downloadAlbums(albums);
    }

    private void collect(CDDBRecord[] records, Set<String> artists, Set<Album> albums) throws IOException {
        for (CDDBRecord record : records) {
            String recordAlbum = record.getAlbum();
            CDDBEntry entry = freedb.readCDInfo(record);
            String album = entry.getAlbum();
            String artist = entry.getArtist();
            artists.add(artist);
            albums.add(new Album(artist, album));
            albums.add(new Album(artist, recordAlbum));
        }
    }

    private void downloadArtists(Set<String> artists) {
        long start = System.currentTimeMillis();
        int count = 1;
        try {
            for (Iterator<String> iterator = artists.iterator(); iterator.hasNext(); count++) {
                String artist = iterator.next();
                log.info("Downloading portrait for " + count + ". of " + artists.size() + " artists: " + artist);
                try {
                    cover.fetchPortrait(artist);
                } catch (Exception e) {
                    log.severe("Error while fetching portrait for " + artist + ": " + e.getMessage());
                }
            }
        } finally {
            long end = System.currentTimeMillis();
            log.fine("Downloading portraits for " + count + " artists took " + (end - start) + " milliseconds");
        }
    }

    private void downloadAlbums(Set<Album> albums) {
        long start = System.currentTimeMillis();
        int count = 1;
        try {
            for (Iterator<Album> iterator = albums.iterator(); iterator.hasNext(); count++) {
                Album album = iterator.next();
                log.info("Downloading album for " + count + ". of " + albums.size() + " albums: " + album);
                try {
                    cover.fetchPortrait(album.artist);
                } catch (Exception e) {
                    log.severe("Error while fetching portrait for " + album.artist + ": " + e.getMessage());
                }
                try {
                    cover.fetchCompilationCover(album.title);
                } catch (Exception e) {
                    log.severe("Error while fetching cover for " + album.title + ": " + e.getMessage());
                }
                try {
                    cover.fetchAlbumCover(album.artist, album.title);
                } catch (Exception e) {
                    log.severe("Error while fetching cover for " + album + ": " + e.getMessage());
                }
            }
        } finally {
            long end = System.currentTimeMillis();
            log.fine("Downloading cover for " + count + " albums took " + (end - start) + " milliseconds");
        }
    }

    public static void main(String[] args) {
        main(new CoverDownloader(), args);
    }
}
