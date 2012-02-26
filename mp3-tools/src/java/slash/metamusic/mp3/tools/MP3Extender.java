/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2006 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.tools;

import slash.metamusic.coverdb.*;
import slash.metamusic.lyricsdb.LyricsDBClient;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.trm.TRM;
import slash.metamusic.util.DiscIndexHelper;
import slash.metamusic.util.ImageResizer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A class to add
 * <ul>
 * <li>MusicBrainz id,</li>
 * <li>cover image,</li>
 * <li>lyrics,</li>
 * <li>publisher information and</li>
 * <li>compilation name</li>
 * </ul>
 * to files.
 *
 * @author Christian Pesch
 * @version $Id: MP3Extender.java 475 2005-01-14 17:31:47Z cpesch $
 */

public class MP3Extender extends BaseMP3Modifier {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3Extender.class.getName());

    private CoverDBClient coverClient = new CoverDBClient();
    private LyricsDBClient lyricsClient = new LyricsDBClient();
    private boolean addCoverToFolder = false, addCover = true, addLyrics = true, addMetaData = true;

    public boolean isAddCoverToFolder() {
        return addCoverToFolder;
    }

    public void setAddCoverToFolder(boolean addCoverToFolder) {
        this.addCoverToFolder = addCoverToFolder;
    }

    public boolean isAddCover() {
        return addCover;
    }

    public void setAddCover(boolean addCover) {
        this.addCover = addCover;
    }

    public boolean isAddLyrics() {
        return addLyrics;
    }

    public void setAddLyrics(boolean addLyrics) {
        this.addLyrics = addLyrics;
    }

    public boolean isAddMetaData() {
        return addMetaData;
    }

    public void setAddMetaData(boolean addMetaData) {
        this.addMetaData = addMetaData;
    }

    public void setCoverDirectoryName(String coverDirectoryName) {
        coverClient.setCoverDirectoryName(coverDirectoryName);
    }

    public void setLyricsDirectoryName(String lyricsDirectoryName) {
        lyricsClient.setLyricsDirectoryName(lyricsDirectoryName);
    }

    /**
     * Extend the given file, i.e. add
     * <ul>
     * <li>MusicBrainz id,</li>
     * <li>cover image,</li>
     * <li>lyrics,</li>
     * <li>publisher information and</li>
     * <li>compilation name</li>
     * </ul>
     * to the file.
     *
     * @param file the {@link File} to operate on
     * @throws IOException if the file cannot be read
     */
    public void extend(File file) throws IOException {
        MP3File mp3 = MP3File.readValidFile(file);
        if (mp3 == null) {
            throw new IOException("Invalid MP3 file " + file.getAbsolutePath());
        }
        extend(mp3);
    }

    /**
     * Extend the given MP3 file, i.e. add
     * <ul>
     * <li>MusicBrainz id,</li>
     * <li>cover image,</li>
     * <li>lyrics,</li>
     * <li>publisher information and</li>
     * <li>compilation name</li>
     * </ul>
     * to the MP3 file.
     *
     * @param file the {@link MP3File} to operate on
     * @throws IOException if the file cannot be read
     */
    public void extend(MP3File file) throws IOException {
        boolean haveToWrite = extendTags(file);

        if (haveToWrite) {
            write(file);
            log.info("Extended " + file.getFile().getAbsolutePath());
        }
    }

    public boolean extendTags(MP3File file) throws IOException {
        boolean addedMusicBrainzId = isAddMetaData() && addMusicBrainzId(file);
        boolean addedAttachedPicture = isAddCover() && addCover(file);
        boolean addedLyrics = isAddLyrics() && addLyrics(file);
        boolean addedPublisher = isAddMetaData() && addPublisher(file);
        boolean addedCompilationName = isAddMetaData() && addCompilationName(file);
        boolean addedIndexCount = isAddMetaData() && addIndexCount(file);

        if (isAddCoverToFolder())
            addCoverToFolder(file);

        boolean extended = addedMusicBrainzId || addedAttachedPicture || addedLyrics || addedPublisher ||
                addedCompilationName || addedIndexCount;

        log.info("extended: " + extended + " MusicBrainzId: " + addedMusicBrainzId +
                " cover: " + addedAttachedPicture + " lyrics: " + addedLyrics +
                " publisher: " + addedPublisher + " compilation name: " + addedCompilationName +
                " index count: " + addedIndexCount);
        return extended;
    }

    private void addCoverToFolder(MP3File file) {
        byte[] cover = file.getHead().getAttachedPicture();
        if (cover == null)
            return;

        WindowsMediaPlayerCoverClient wmpClient = new WindowsMediaPlayerCoverClient();
        wmpClient.storeCover(file.getFile(), cover);
    }

    protected boolean addMusicBrainzId(MP3File file) throws IOException {
        String musicBrainzId = file.getHead() != null ? file.getHead().getMusicBrainzId() : null;
        if (musicBrainzId == null) {
            if (TRM.isSupported()) {
                TRM trm = new TRM();
                try {
                    trm.read(file);
                    if (trm.isValid()) {
                        log.fine("Calculated MusicBrainzId '" + trm.getSignature() + "' for " + file.getFile().getAbsolutePath());
                        file.getHead().setMusicBrainzId(trm.getSignature());
                        return true;
                    } else
                        log.severe("Cannot calculate MusicBrainzId for " + file.getFile().getAbsolutePath());
                } catch (Throwable t) {
                    log.severe("Cannot calculate MusicBrainzId for " + file.getFile().getAbsolutePath() + ": " + t.getMessage());
                }
            }
        } else
            log.info("Found MusicBrainzId '" + musicBrainzId + "' for " + file.getFile().getAbsolutePath());
        return false;
    }

    protected boolean addCover(MP3File file) {
        byte[] fileCover = file.getHead() != null ? file.getHead().getAttachedPicture() : null;
        try {
            byte[] fetchCover = file.getHead().isCompilation() ?
                    coverClient.fetchCompilationCover(file.getAlbum()) :
                    coverClient.fetchAlbumCover(file.getArtist(), file.getAlbum());
            File cachedFile = coverClient.getCachedFile(file.getArtist(), file.getTrack());
            byte[] wmpCover = findWindowsMediaPlayerCover(file);
            byte[] lfCover = findLastFmCover(file);

            // in case the cached file has been modified but may be shorter
            if (isFirstNewerThanSecond(cachedFile, file.getFile()))
                fileCover = null;

            // find best source
            byte[] foundCover = fetchCover;
            if (isFirstBetterThanSecond(wmpCover, foundCover))
                foundCover = wmpCover;
            if (isFirstBetterThanSecond(lfCover, foundCover))
                foundCover = lfCover;
            if (isFirstBetterThanSecond(fileCover, foundCover))
                foundCover = fileCover;

            // store found in coverdb cache if better than peek
            if (foundCover != null && isFirstBetterThanSecond(foundCover, fetchCover)) {
                log.fine("Storing found cover (" + foundCover.length + " bytes) in CoverDB cache");
                if (file.getHead().isCompilation())
                    coverClient.storeCompilationCover(file.getAlbum(), foundCover);
                else
                    coverClient.storeAlbumCover(file.getArtist(), file.getAlbum(), foundCover);
            }

            // store in file if better
            byte[] transformedCover = foundCover != null ? new ImageResizer().resize(foundCover, "jpg", 200, 200) : null;
            if (transformedCover != null && fileCover != null && isFirstBetterThanSecond(transformedCover, fileCover)) {
                log.fine("Adding cover (" + transformedCover.length + " bytes) to " + file.getFile().getAbsolutePath());
                file.getHead().setCover(transformedCover);
                return true;
            }
        } catch (IOException e) {
            log.severe("Cannot add cover to " + file.getFile().getAbsolutePath() + ": " + e.getMessage());
        }
        return false;
    }

    private boolean isFirstBetterThanSecond(byte[] first, byte[] second) {
        if (first == null)
            return false;
        if (second == null)
            return true;
        return first.length > second.length;
    }

    private byte[] findWindowsMediaPlayerCover(MP3File file) throws IOException {
        WindowsMediaPlayerCoverClient wmpClient = new WindowsMediaPlayerCoverClient();
        return wmpClient.findCover(file);
    }

    private byte[] findLastFmCover(MP3File file) throws IOException {
        LastFmCoverClient lfClient = new LastFmCoverClient();
        return lfClient.findCover(file);
    }

    protected boolean addLyrics(MP3File file) {
        String fileLyrics = file.getHead() != null ? file.getHead().getLyrics() : null;
        fileLyrics = lyricsClient.cleanLyrics(fileLyrics);
        String fetchLyrics = lyricsClient.fetchLyrics(file.getArtist(), file.getTrack());
        fetchLyrics = lyricsClient.cleanLyrics(fetchLyrics);
        File cachedFile = lyricsClient.getCachedFile(file.getArtist(), file.getTrack());

        // in case the cached file has been modified but may be shorter
        if (isFirstNewerThanSecond(cachedFile, file.getFile()))
            fileLyrics = null;

        // find best source
        String foundLyrics = fetchLyrics;
        if (isFirstBetterThanSecond(fileLyrics, foundLyrics))
            foundLyrics = fileLyrics;

        // store found in lyricsdb cache if better than peek
        if (foundLyrics != null && isFirstBetterThanSecond(foundLyrics, fetchLyrics)) {
            log.fine("Storing found lyrics (" + foundLyrics.length() + " bytes) in LyricsDB cache");
            lyricsClient.storeLyrics(file.getArtist(), file.getTrack(), foundLyrics);
        }

        // store in file if better
        if (foundLyrics != null && isFirstBetterThanSecond(foundLyrics, fileLyrics)) {
            log.fine("Adding lyrics (" + foundLyrics.length() + " bytes) to " + file.getFile().getAbsolutePath());
            file.getHead().setLyrics(foundLyrics);
            return true;
        }
        return false;
    }

    private boolean isFirstNewerThanSecond(File first, File second) {
        if (first == null)
            return false;
        if (second == null)
            return true;
        return (first.lastModified() / 1000) > (second.lastModified() / 1000);
    }

    private boolean isFirstBetterThanSecond(String first, String second) {
        if (first == null)
            return false;
        if (second == null)
            return true;
        return first.length() > second.length();
    }

    protected boolean addPublisher(MP3File file) {
        if (file.getHead().getPublisher() == null) {
            AmazonMusicClient client = new AmazonMusicClient();
            String publisher = client.searchPublisher(file.getArtist(), file.getAlbum());
            if (publisher != null) {
                log.info("Adding publisher '" + publisher + "' for " + file.getFile().getAbsolutePath());
                file.getHead().setPublisher(publisher);
                return true;
            }
        }
        return false;
    }

    protected boolean addCompilationName(MP3File file) {
        if (file.getHead().isCompilation() && file.getHead().getBand() == null) {
            String compilationName = file.getFile().getParentFile().getName();
            log.info("Adding compilation name '" + compilationName + "' for " + file.getFile().getAbsolutePath());
            file.getHead().setBand(compilationName);
            return true;
        }
        return false;
    }

    private Maxima determineMaximumIndices(File directory) {
        Maxima maxima = new Maxima();

        // search on same directory level for albums 
        File[] files = directory.listFiles();
        for (File file : files) {
            MP3File mp3 = MP3File.readValidFile(file);
            if (mp3 == null)
                continue;

            int discIndex = DiscIndexHelper.parseDiscIndex(mp3.getAlbum());
            if (discIndex == -1)
                discIndex = mp3.getPartOfSetIndex();
            if (discIndex > 0)
                maxima.checkIfMaximumDiscIndex(mp3.getArtist(), mp3.getAlbum(), discIndex);

            int albumIndex = mp3.getIndex();
            if (albumIndex > 0)
                maxima.checkIfMaximumAlbumIndex(mp3.getArtist(), mp3.getAlbum(), discIndex, albumIndex);
        }
        return maxima;
    }

    private static class Maxima {
        private Map<Album, Integer> maximumAlbumIndex = new HashMap<Album, Integer>();
        private Map<Album, Integer> maximumDiscIndex = new HashMap<Album, Integer>();

        public Integer getMaximumAlbumIndex(String artist, String album, int discIndex) {
            return maximumAlbumIndex.get(new Album(artist, DiscIndexHelper.formatDiscIndex(album, discIndex)));
        }

        public void checkIfMaximumAlbumIndex(String artist, String album, int discIndex, int albumIndex) {
            Album key = new Album(artist, DiscIndexHelper.formatDiscIndex(DiscIndexHelper.removeDiscIndexPostfix(album), discIndex));
            Integer maximum = maximumAlbumIndex.get(key);
            if (maximum == null || maximum < albumIndex)
                maximumAlbumIndex.put(key, albumIndex);
        }

        public Integer getMaximumDiscIndex(String artist, String album) {
            return maximumDiscIndex.get(new Album(artist, DiscIndexHelper.removeDiscIndexPostfix(album)));
        }

        public void checkIfMaximumDiscIndex(String artist, String album, int discIndex) {
            Album key = new Album(artist, DiscIndexHelper.removeDiscIndexPostfix(album));
            Integer maximum = maximumDiscIndex.get(key);
            if (maximum == null || maximum < discIndex)
                maximumDiscIndex.put(key, discIndex);
        }
    }

    private Map<File, Maxima> indexCountCache = new HashMap<File, Maxima>();

    protected boolean addIndexCount(MP3File file) {
        boolean result = false;

        int discIndex = DiscIndexHelper.parseDiscIndex(file.getAlbum());
        if (discIndex > 0) {
            file.setPartOfSetIndex(discIndex);
            result = true;
        }

        if (file.getIndex() > 0 || file.getPartOfSetIndex() > 0) {
            // cache for the running of this JVM, locality of the cache should be really good
            File directory = file.getFile().getParentFile();
            Maxima maxima = indexCountCache.get(directory);
            if (maxima == null) {
                maxima = determineMaximumIndices(directory);
                indexCountCache.put(directory, maxima);
            }

            Integer maximumAlbumIndex = maxima.getMaximumAlbumIndex(file.getArtist(), file.getAlbum(), file.getPartOfSetIndex());
            if (maximumAlbumIndex != null && maximumAlbumIndex != file.getCount()) {
                file.setCount(maximumAlbumIndex);
                result = true;
            }

            Integer maximumDiscIndex = maxima.getMaximumDiscIndex(file.getArtist(), file.getAlbum());
            if (maximumDiscIndex != null && maximumDiscIndex != file.getPartOfSetCount()) {
                file.setPartOfSetCount(maximumDiscIndex);
                result = true;
            }
        }

        if (discIndex > 0) {
            file.setAlbum(DiscIndexHelper.removeDiscIndexPostfix(file.getAlbum()));
        }

        return result;
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.mp3.tools.MP3Extender <file>");
            System.exit(1);
        }

        File file = new File(args[0]);
        MP3Extender extender = new MP3Extender();
        extender.extend(file);
        System.exit(0);
    }
}
