/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.coverdb;

import slash.metamusic.util.DiscIndexHelper;
import slash.metamusic.util.InputOutput;
import slash.metamusic.util.StringHelper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 * A client that queries cover databases.
 *
 * @author Christian Pesch
 */

public class CoverDBClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(CoverDBClient.class.getName());

    private static final String VARIOUS_ARTISTS = "various artists";

    private CoverDBCache coverDBCache = new CoverDBCache();

    public void setCoverDirectoryName(String coverDirectoryName) {
        coverDBCache.setCacheDirectoryName(coverDirectoryName);
    }

    public File getCachedFile(String artist, String track) {
        try {
            return coverDBCache.getCachedFile(artist, track);
        } catch (IOException e) {
            log.severe("Cannot get cached cover for artist '" + artist + "' and track '" + track + "': " + e.getMessage());
        }
        return null;
    }

    protected byte[] fetchPortrait(String artist, boolean download) {
        if (artist == null)
            return null;
        artist = artist.trim();

        byte[] portrait = null;
        try {
            portrait = coverDBCache.peekPortrait(artist);
            if (portrait != null) {
                log.info("Portrait for artist '" + artist + "' is cached");
            } else if (download) {
                portrait = downloadPortrait(artist);
                if (portrait != null)
                    storePortrait(artist, portrait);
            }
        } catch (IOException e) {
            log.severe("Cannot fetch portrait for '" + artist + "': " + e.getMessage());
        }
        return portrait;
    }

    public byte[] fetchPortrait(String artist) {
        return fetchPortrait(artist, true);
    }

    public void storePortrait(String artist, byte[] portrait) {
        log.fine("Storing portrait (" + portrait.length + " bytes) for artist '" + artist + "'");
        try {
            coverDBCache.storePortrait(artist, portrait);
        } catch (IOException e) {
            log.severe("Cannot store portrait for artist '" + artist + "': " + e.getMessage());
        }
    }


    protected byte[] fetchCover(String artist, String album, boolean download) {
        if (artist == null || album == null)
            return null;
        artist = artist.trim();
        album = DiscIndexHelper.removeDiscIndexPostfix(album);
        album = album.trim();

        byte[] cover = null;
        try {
            cover = coverDBCache.peekCover(artist, album);
            if (cover != null)
                log.info("Cover for artist '" + artist + "' and album '" + album + "' (" + cover.length + " bytes) is cached");

            else if (download) {
                cover = downloadCover(artist, album);
                if (cover != null)
                    storeAlbumCover(artist, album, cover);
            }
        } catch (IOException e) {
            log.severe("Cannot fetch cover for artist '" + artist + "' and album '" + album + "': " + e.getMessage());
        }
        return cover;
    }

    protected void storeCover(String artist, String album, byte[] cover) {
        album = DiscIndexHelper.removeDiscIndexPostfix(album);
        log.fine("Storing cover (" + cover.length + " bytes) for artist '" + artist + "' and album '" + album + "'");
        try {
            coverDBCache.storeCover(artist, album, cover);
        } catch (IOException e) {
            log.severe("Cannot store cover for artist '" + artist + "' and album '" + album + "': " + e.getMessage());
        }
    }


    public byte[] fetchAlbumCover(String artist, String album) {
        return fetchCover(artist, album, true);
    }

    public void storeAlbumCover(String artist, String album, byte[] cover) {
        storeCover(artist, album, cover);
    }


    public byte[] fetchCompilationCover(String compilation) {
        return fetchCover(VARIOUS_ARTISTS, compilation, true);
    }

    public void storeCompilationCover(String compilation, byte[] cover) {
        storeCover(VARIOUS_ARTISTS, compilation, cover);
    }


    protected byte[] downloadPortrait(String artist) {
        // TODO this is not very successful for portraits, try google image search instead
        return downloadCover(artist, artist);
    }

    protected byte[] downloadCover(String artist, String album) {
        if (artist == null || artist.length() == 0 || artist.toLowerCase().contains("unknown") ||
                album == null || album.length() == 0 || album.toLowerCase().contains("unknown")) {
            log.severe("Cannot download cover for unknown artist '" + artist + "' or unknown album '" + album + "'");
            return null;
        }

        if (coverDBCache.hasDownloadAlreadyFailed(artist, album)) {
            log.fine("Cover download already failed for artist '" + artist + "' and album '" + album + "'");
            return null;
        }

        byte[] cover = null;
        try {
            AmazonMusicClient client = new AmazonMusicClient();
            cover = client.downloadCover(artist, album);

            /*
            http://img.darktown.com/cover/audio/download/f/fury_in_the_slaughterhouse_-_jau_a.jpg
            http://vince8290.free.fr/pochettes/audio/santana_supernatural_front.jpg
            http://covers.coverkingdom.net/covers_audio/N/N_Sync_-_No_Strings_Attached_(Cd).jpg
            hrtp://www.cdpochettes.net/audio/d/Dire_staits_Alchemy_1.jpg
            http://www.cdpochettes.net/audio/d/Depeche_mode_singles_86_98_1.jpg
            http://www.cdpochettes.net/audio/e/Ema_burton_a_girl_like_me_1.jpg
            http://www.babilomnia.it/Cover/S/Santana-Supernatural-Front.jpg
            http://www.babilomnia.it/Cover/B/BackStreet_Boys_Millenium_front.jpg
            http://cdcovers.iespana.es/cdcovers/CoversA/AC_DC-HighWay_To_Hell-Front.jpg
            http://www.fredo-covers.com/jaquettes/audio/cher_heart_of_stone_avant.jpg
            does not work anymore, service stopped
            */

            // http://www.coveralia.com/audio/a/Atomic_Kitten-Feels_So_Good-Frontal.jpg
            if (cover == null) {
                char artistChar = Character.toLowerCase(artist.charAt(0));
                cover = downloadCover(new URL("http://www.coveralia.com/audio/" + artistChar +
                        "/" + StringHelper.toMixedCase(StringHelper.replaceForURI(artist)) +
                        "-" + StringHelper.toMixedCase(StringHelper.replaceForURI(album)) + "-Frontal.jpg"), artist, album);
            }

            // http://coverlandia.altervista.org/Copertine%20cd/D/Depeche_Mode_Ultra.jpg
            if (cover == null) {
                char artistChar = Character.toUpperCase(artist.charAt(0));
                cover = downloadCover(new URL("http://coverlandia.altervista.org/Copertine%20cd/" + artistChar +
                        "/" + StringHelper.toMixedCase(StringHelper.replaceForURI(artist)) +
                        "_" + StringHelper.toMixedCase(StringHelper.replaceForURI(album)) + ".jpg"), artist, album);
            }

            // http://coverlandia.altervista.org/Copertine%20cd/D/Doors_-_Greatest_hits.jpg
            if (cover == null) {
                char artistChar = Character.toUpperCase(artist.charAt(0));
                cover = downloadCover(new URL("http://coverlandia.altervista.org/Copertine%20cd/" + artistChar +
                        "/" + StringHelper.toMixedCase(StringHelper.replaceForURI(artist)) +
                        "_-_" + StringHelper.toMixedCase(StringHelper.replaceForURI(album)) + ".jpg"), artist, album);
            }

            // http://coverlandia.altervista.org/Copertine%20cd/D/def_leppard_-_euphoria.JPG
            if (cover == null) {
                char artistChar = Character.toUpperCase(artist.charAt(0));
                cover = downloadCover(new URL("http://coverlandia.altervista.org/Copertine%20cd/" + artistChar +
                        "/" + StringHelper.replaceForURI(artist).toLowerCase() +
                        "_-_" + StringHelper.replaceForURI(album).toLowerCase() + ".JPG"), artist, album);
            }
        } catch (MalformedURLException e) {
            log.severe("Could not create url for artist " + artist + " and album " + album + ":" + e.getMessage());
        }

        if (cover == null) {
            log.fine("Cover download failed for artist '" + artist + "' and album '" + album + "'");
            coverDBCache.addFailedDownload(artist, album);
        }
        return cover;
    }

    protected byte[] downloadCover(URL url, String artist, String album) {
        log.info("Trying url " + url + " for artist '" + artist + "' and album '" + album + "'");
        try {
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream in = connection.getInputStream();
            byte[] result = InputOutput.readBytes(in);
            if (result == null || result.length == 0 || isHtmlErrorPage(result)) {
                log.warning("Download from url " + url + " failed");
            } else {
                log.info("Download from url " + url + " successfull (" + result.length + " bytes)");
                return result;
            }
        } catch (IOException e) {
            log.severe("Could not load cover for artist '" + artist + "' and album '" + album + "': " + e.getMessage());
        }
        return null;
    }

    private boolean isHtmlErrorPage(byte[] bytes) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new StringReader(new String(bytes)));
            String data;
            while ((data = reader.readLine()) != null) {
                if (data.toLowerCase().contains("html>"))
                    return true;
                if (data.toLowerCase().contains("head>"))
                    return true;
                if (data.toLowerCase().contains("meta>"))
                    return true;
                if (data.toLowerCase().contains("title>"))
                    return true;
                if (data.toLowerCase().contains("body>"))
                    return true;
                if (data.toLowerCase().contains("HTTP"))
                    return true;
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    // do nothing...
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("slash.metamusic.coverdb.CoverDBClient <artist> <album>");
            System.exit(1);
        }

        CoverDBClient client = new CoverDBClient();
        client.fetchAlbumCover(args[0], args[1]);
        System.exit(0);
    }
}
