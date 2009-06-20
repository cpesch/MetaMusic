/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.freedb;

import slash.metamusic.discid.DiscId;
import slash.metamusic.util.FileCache;
import slash.metamusic.util.StringHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Caches queries to FreeDB.
 *
 * @author Christian Pesch
 * @version $Id: FreeDBCache.java 926 2006-12-29 15:02:22Z cpesch $
 */

public class FreeDBCache {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(FreeDBCache.class.getName());

    /**
     * The name of the cache directory
     */
    public static final String CACHE_DIRECTORY_NAME = ".freedb";

    private FileCache fileCache = new FileCache();


    public void setCacheDirectoryName(String cacheDirectoryName) {
        fileCache.setCacheDirectoryName(cacheDirectoryName);
    }


    protected String createCacheKey(String freeDBQueryString) {
        return StringHelper.replaceWhitespaces(freeDBQueryString);
    }


    protected String peekResult(String key) throws IOException {
        return fileCache.getFileAsString(createCacheKey(key));
    }

    public String peekResult(DiscId discId) throws IOException {
        return peekResult(discId.getFreeDBQueryString());
    }

    public String peekResult(CDDBRecord record) throws IOException {
        return peekResult(record.getDiscId());
    }


    protected void storeResult(String key, String result) throws IOException {
        fileCache.putAsString(createCacheKey(key), result);
    }

    public void storeResult(DiscId discId, String result) throws IOException {
        storeResult(discId.getFreeDBQueryString(), result);
    }

    public void storeResult(CDDBRecord record, String result) throws IOException {
        storeResult(record.getDiscId(), result);
    }


    protected List<File> collectCacheFiles() {
        log.fine("Collecting files from FreeDB cache");
        File cacheDirectory = fileCache.getCacheDirectory();
        File[] files = cacheDirectory.listFiles();
        log.fine("Found " + files.length + " files in FreeDB cache");
        return Arrays.asList(files);
    }

    protected DiscId createDiscIdFor(String name) {
        StringTokenizer tokenizer = new StringTokenizer(name, "_");
        if (!tokenizer.hasMoreTokens())
            return null;
        String discId = tokenizer.nextToken();
        if (!tokenizer.hasMoreTokens())
            return null;

        List<Integer> numbers = new ArrayList<Integer>();
        while (tokenizer.hasMoreTokens()) {
            try {
                numbers.add(new Integer(tokenizer.nextToken()));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (numbers.size() < 3)
            return null;

        int trackCount = numbers.get(0);
        int[] trackOffsets = new int[trackCount];
        for (int i = 0; i < trackOffsets.length; i++) {
            trackOffsets[i] = numbers.get(i + 1);
        }
        int discLength = numbers.get(numbers.size() - 1);
        return new DiscId(discId, trackCount, trackOffsets, discLength, true);
    }

    public Collection<DiscId> getCachedDiscIds() {
        List<File> files = collectCacheFiles();
        Set<DiscId> discIds = new HashSet<DiscId>(1);
        for (File file : files) {
            DiscId discId = createDiscIdFor(file.getName());
            if (discId != null)
                discIds.add(discId);
        }
        log.info("Found " + discIds.size() + " disc ids in FreeDB cache");
        return discIds;
    }
}
