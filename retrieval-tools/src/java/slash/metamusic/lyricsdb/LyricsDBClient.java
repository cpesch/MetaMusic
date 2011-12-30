/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.lyricsdb;

import slash.metamusic.distance.Levenshtein;
import slash.metamusic.util.StringHelper;
import slash.metamusic.util.URLLoader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A client that queries lyrics databases.
 *
 * @author Christian Pesch
 */

public class LyricsDBClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(LyricsDBClient.class.getName());

    private LyricsDBCache lyricsDBCache = new LyricsDBCache();

    public void setLyricsDirectoryName(String lyricsDirectoryName) {
        lyricsDBCache.setCacheDirectoryName(lyricsDirectoryName);
    }

    private static String encode(String request) throws UnsupportedEncodingException {
        return URLEncoder.encode(request, "UTF-8");
    }

    public File getCachedFile(String artist, String track) {
        try {
            return lyricsDBCache.getCachedFile(artist, track);
        } catch (IOException e) {
            log.severe("Cannot get cached lyrics for artist '" + artist + "' and track '" + track + "': " + e.getMessage());
        }
        return null;
    }

    protected String fetchLyrics(String artist, String track, boolean download) {
        if (artist == null || artist.length() == 0 || artist.toLowerCase().contains("unknown") ||
                track == null || track.length() == 0 || track.toLowerCase().contains("unknown")) {
            log.severe("Cannot download lyrics for unknown artist '" + artist + "' or unknown track '" + track + "'");
            return null;
        }

        // TODO strip of (feat. Bla) from track
        // TODO strip of (radio version) from track
        String lyrics = null;
        try {
            lyrics = lyricsDBCache.peekLyrics(artist, track);
            if (lyrics != null) {
                log.info("Lyrics for artist '" + artist + "' and track '" + track + "' (" + lyrics.length() + " bytes) is cached");
            } else if (download) {
                lyrics = downloadLyrics(artist, track);
                if (lyrics != null)
                    storeLyrics(artist, track, lyrics);
            }
        } catch (IOException e) {
            log.severe("Cannot fetch lyrics for artist '" + artist + "' and track '" + track + "': " + e.getMessage());
        }
        return StringHelper.trimButKeepLineFeeds(lyrics);
    }

    public String peekLyrics(String artist, String track) {
        return fetchLyrics(artist, track, false);
    }

    public String fetchLyrics(String artist, String track) {
        return fetchLyrics(artist, track, true);
    }

    public void storeLyrics(String artist, String track, String lyrics) {
        log.fine("Storing lyrics (" + lyrics.length() + " bytes) for artist '" + artist + "' and track '" + track + "'");
        try {
            lyricsDBCache.storeLyrics(artist, track, lyrics);
        } catch (IOException e) {
            log.severe("Cannot store lyrics for artist '" + artist + "' and track '" + track + "': " + e.getMessage());
        }
    }


    protected String downloadLyrics(String artist, String track) {
        if (lyricsDBCache.hasDownloadAlreadyFailed(artist, track)) {
            log.fine("Lyrics download already failed for artist '" + artist + "' and track '" + track + "'");
            return null;
        }

        try {
            URL url = new URL("http://lyrc.com.ar/en/tema1en.php?artist=" + encode(artist) + "&songname=" + encode(track));
            String html = URLLoader.getContents(url, false);
            String[] resultList = html.split("</font><br>");
            if (resultList.length != 1) {
                String lyrics = parseHtml(html);
                log.fine("Lyrics search for '" + artist + "' and '" + track + "' has exact match: " + lyrics);
                return lyrics;
            } else {
                String[] suggestions = html.split("Suggestions : <br><a href=\"");
                if (suggestions.length != 1) {
                    String[] backslash = suggestions[suggestions.length - 1].split("\"");
                    Map<String, URL> suggestedLyricsMap = new LinkedHashMap<String, URL>();
                    for (int i = 1; i < backslash.length; i = i + 2) {
                        String[] font = backslash[i].split("><font color='white'>");
                        String[] artistAndTitle;
                        if (font.length != 1) {
                            artistAndTitle = font[1].split("</font>");
                            suggestedLyricsMap.put(artistAndTitle[0], new URL("http://lyrc.com.ar/en/" + backslash[i - 1]));
                        }
                    }
                    log.fine("Lyrics search for '" + artist + "' and '" + track + "' has several matches: " + suggestedLyricsMap.keySet());

                    for (String artistAndTitle : suggestedLyricsMap.keySet()) {
                        if (Levenshtein.distance(artist + " - " + track, artistAndTitle) < 5) {
                            html = URLLoader.getContents(suggestedLyricsMap.get(artistAndTitle), false);
                            String lyrics = parseHtml(html);
                            log.fine("Lyrics search for '" + artist + "' and '" + track + "' choosing match: " + lyrics);
                            return lyrics;
                        } else
                            log.fine("Skipping result for '" + artist + "' and '" + track + "': " + artistAndTitle);
                    }
                }

            }
        } catch (IOException e) {
            log.severe("Cannot download lyrics: " + e.getMessage());
        }

        log.fine("Lyrics download failed for artist '" + artist + "' and track '" + track + "'");
        lyricsDBCache.addFailedDownload(artist, track);
        return null;
    }

    String parseHtml(String htmlCode) {
        String lyrics = "";
        String[] table = htmlCode.split("</script></td></tr></table>");
        String[] br = table[1].split("<br />");
        String[] smaller = br[br.length - 1].split("<");
        for (int i = 0; i < br.length - 1; i++) {
            lyrics = lyrics + br[i] + "\n";
        }
        br[br.length - 1] = smaller[0];
        lyrics = lyrics + smaller[0];
        lyrics = StringHelper.trimButKeepLineFeeds(lyrics);
        lyrics = StringHelper.decodeEntities(lyrics);
        return lyrics;
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("slash.metamusic.lyricsdb.LyricsDBClient <artist> <track>");
            System.exit(1);
        }

        LyricsDBClient client = new LyricsDBClient();
        String lyrics = client.downloadLyrics(args[0], args[1]);
        System.out.println(lyrics);
        System.exit(0);
    }
}
