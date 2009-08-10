/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.lyricsdb;

import slash.metamusic.util.FileCache;
import slash.metamusic.util.StringHelper;
import slash.metamusic.util.ZipCache;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Caches queries to lyrics databases.
 *
 * @author Christian Pesch
 */

public class LyricsDBCache {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(LyricsDBCache.class.getName());

    private FileCache fileCache = new FileCache();
    private ZipCache zipCache = new ZipCache();

    private static final String LYRICS_DOWNLOAD_FAILURES = "lyrics.downloadfailures";
    private Set<String> downloadFailures = new HashSet<String>();


    public synchronized void setCacheDirectoryName(String cacheDirectoryName) {
        fileCache.setCacheDirectoryName(cacheDirectoryName);
        zipCache.setCacheFileName(cacheDirectoryName + ".zip");

        try {
            //noinspection unchecked
            downloadFailures = (Set<String>) fileCache.getFileAsObject(LYRICS_DOWNLOAD_FAILURES);
        } catch (IOException e) {
            log.warning("Could not deserialize dowload failures: " + e.getMessage());
        }
        if (downloadFailures == null)
            downloadFailures = new HashSet<String>();
    }

    synchronized boolean hasDownloadAlreadyFailed(String artist, String track) {
        return downloadFailures.contains(createCacheKey(artist, track));
    }

    synchronized void addFailedDownload(String artist, String track) {
        downloadFailures.add(createCacheKey(artist, track));

        try {
            fileCache.putAsObject(LYRICS_DOWNLOAD_FAILURES, downloadFailures);
        } catch (IOException e) {
            log.warning("Could not serialize dowload failures: " + e.getMessage());
        }
    }

    public List<File> getCachedFiles() {
        return fileCache.values();
    }

    public File getCachedFile(String artist, String track) throws IOException {
        return fileCache.get(createCacheKey(artist, track));
    }

    protected String createCacheKey(String artist, String track) {
        return (StringHelper.replaceForURI(artist) + "-" + StringHelper.replaceForURI(track) + ".txt").toLowerCase();
    }

    public String peekLyrics(String artist, String track) throws IOException {
        String key = createCacheKey(artist, track);
        String result = zipCache.getFileAsString(key);
        if (result == null)
            result = fileCache.getFileAsString(key);
        return result;
    }

    public void storeLyrics(String artist, String track, String lyrics) throws IOException {
        fileCache.put(createCacheKey(artist, track), lyrics.getBytes());
    }

    public void removeLyrics(String artist, String track) {
        fileCache.remove(createCacheKey(artist, track));
    }
}
