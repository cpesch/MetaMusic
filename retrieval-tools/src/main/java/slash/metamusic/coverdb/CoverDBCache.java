/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.coverdb;

import slash.metamusic.util.FileCache;
import slash.metamusic.util.StringHelper;
import slash.metamusic.util.ZipCache;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static slash.metamusic.util.StringHelper.toMixedCase;

/**
 * Caches queries to cover databases.
 *
 * @author Christian Pesch
 */

public class CoverDBCache {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(CoverDBCache.class.getName());

    private FileCache fileCache = new FileCache();
    private ZipCache zipCache = new ZipCache();

    private static final String COVER_DOWNLOAD_FAILURES = "cover.downloadfailures";
    private Set<String> downloadFailures = new HashSet<String>();


    @SuppressWarnings("unchecked")
    public synchronized void setCacheDirectoryName(String cacheDirectoryName) {
        fileCache.setCacheDirectoryName(cacheDirectoryName);
        zipCache.setCacheFileName(cacheDirectoryName + ".zip");

        try {
            //noinspection unchecked
            downloadFailures = (Set<String>) fileCache.getFileAsObject(COVER_DOWNLOAD_FAILURES);
        } catch (IOException e) {
            log.warning("Could not deserialize dowload failures: " + e.getMessage());
        }
        if (downloadFailures == null)
            downloadFailures = new HashSet<String>();
    }

    synchronized boolean hasDownloadAlreadyFailed(String artist, String album) {
        return downloadFailures.contains(createCacheKey(artist, album));
    }

    synchronized void addFailedDownload(String artist, String album) {
        downloadFailures.add(createCacheKey(artist, album));

        try {
            fileCache.putAsObject(COVER_DOWNLOAD_FAILURES, downloadFailures);
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


    protected String createCacheKey(String artist, String album) {
        return (StringHelper.replaceForURI(artist) + "-" + StringHelper.replaceForURI(album) + ".jpg").toLowerCase();
    }

    protected String createCacheKey(String artist) {
        return (StringHelper.replaceForURI(artist) + ".jpg").toLowerCase();
    }


    private byte[] peekCache(String key) throws IOException {
        byte[] result = zipCache.getFileAsBytes(key);
        if (result == null)
            result = fileCache.getFileAsBytes(key);
        return result;
    }

    public byte[] peekPortrait(String artist) throws IOException {
        return peekCache(createCacheKey(artist));
    }

    public byte[] peekCover(String artist, String album) throws IOException {
        return peekCache(createCacheKey(artist, album));
    }


    public void storePortrait(String artist, byte[] portrait) throws IOException {
        fileCache.put(createCacheKey(artist), portrait);
    }

    public void storeCover(String artist, String album, byte[] cover) throws IOException {
        fileCache.put(createCacheKey(artist, album), cover);
    }


    public void removePortrait(String artist) {
        fileCache.remove(createCacheKey(artist));
    }

    public void removeCover(String artist, String album) {
        fileCache.remove(createCacheKey(artist, album));
    }


    protected List<File> collectCacheFiles() {
        log.fine("Collecting files from CoverDB cache");
        File cacheDirectory = fileCache.getCacheDirectory();
        File[] files = cacheDirectory.listFiles();
        log.fine("Found " + files.length + " files in CoverDB cache");
        return Arrays.asList(files);
    }

    private String toImportName(String name) {
        name = name.replaceAll("_", " ");
        return toMixedCase(name);
    }

    protected String createArtistFor(String name) {
        int extensionIndex = name.indexOf(".jpg");
        if (extensionIndex == -1)
            return null;
        int dashIndex = name.indexOf('-');
        if (dashIndex == -1)
            return null;
        return toImportName(name.substring(0, dashIndex));
    }

    public Collection<String> getArtistsForCachedPortraits() {
        List<File> files = collectCacheFiles();
        Set<String> result = new HashSet<String>(1);
        for (File file : files) {
            String artist = createArtistFor(file.getName());
            if (artist != null)
                result.add(artist);
        }
        log.info("Found " + result.size() + " artist portraits in CoverDB cache");
        return result;
    }

    protected Album createAlbumFor(String name) {
        int extensionIndex = name.indexOf(".jpg");
        if (extensionIndex == -1)
            return null;
        int dashIndex = name.indexOf('-');
        if (dashIndex == -1)
            return null;
        return new Album(toImportName(name.substring(0, dashIndex)),
                toImportName(name.substring(dashIndex + 1, extensionIndex)));
    }

    public Collection<Album> getAlbumsForCachedCovers() {
        List<File> files = collectCacheFiles();
        Set<Album> result = new HashSet<Album>(1);
        for (File file : files) {
            Album album = createAlbumFor(file.getName());
            if (album != null)
                result.add(album);
        }
        log.info("Found " + result.size() + " album covers in CoverDB cache");
        return result;
    }
}
