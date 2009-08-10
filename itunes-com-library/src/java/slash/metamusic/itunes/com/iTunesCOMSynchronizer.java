/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.com;

import slash.metamusic.itunes.com.binding.IITFileOrCDTrack;
import slash.metamusic.itunes.com.binding.IITTrackCollection;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.mp3.tools.BaseMP3Modifier;

import java.io.File;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Syncs the iTunes library rating, play count and play time with the tags
 * of the MP3 files contained in the library.
 *
 * @author Christian Pesch
 * @version $Id: iTunesCOMSynchronizer.java 792 2006-04-22 10:09:35 +0200 (Sa, 22 Apr 2006) cpesch $
 */

public class iTunesCOMSynchronizer extends BaseMP3Modifier {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(iTunesCOMSynchronizer.class.getName());

    private static DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private iTunesCOMLibrary library = new iTunesCOMLibrary();
    private List<Notifier> notifiers = new ArrayList<Notifier>();
    private boolean addPlayCount = false;
    private IITTrackCollection tracks;
    private int trackCount, processedTrackCount, failedTrackCount, modifiedFileCount, modifiedTrackCount, removedTrackCount;

    public iTunesCOMSynchronizer() {
        addNotifier(new LogNotifier());
    }


    public boolean isAddPlayCount() {
        return addPlayCount;
    }

    public void setAddPlayCount(boolean addPlayCount) {
        this.addPlayCount = addPlayCount;
    }

    public boolean addNotifier(Notifier notifier) {
        return notifiers.add(notifier);
    }

    public boolean removeNotifier(Notifier notifier) {
        return notifiers.remove(notifier);
    }


    public boolean isiTunesSupported() {
        return iTunesCOMLibrary.isSupported();
    }

    public void open() {
        library.open();
        for (Notifier notifier : notifiers)
            notifier.opened(library.getVersion(), library.getLibraryPath(), library.getTrackCount(), library.getPlaylistCount());
    }

    public void close() {
        library.close();
    }

    public void start() {
        for (Notifier notifier : notifiers)
            notifier.started(library.getTrackCount(), library.getPlaylistCount());
        processedTrackCount = 1;
        modifiedFileCount = 0;
        modifiedTrackCount = 0;
        removedTrackCount = 0;
        tracks = library.getTracks();
        trackCount = tracks.getCount();
    }

    public boolean next() {
        if (processedTrackCount > trackCount) {
            for (Notifier notifier : notifiers)
                notifier.finished(modifiedFileCount, modifiedTrackCount, removedTrackCount);
            return false;

        } else {
            String location = null;
            try {
                for (Notifier notifier : notifiers)
                    notifier.processing(processedTrackCount);

                location = processNext(true);
            }
            catch (Throwable t) {
                t.printStackTrace();
                log.severe("Error while processing " + location + ": " + t.getMessage());
                failedTrackCount++;
                for (Notifier notifier : notifiers)
                    notifier.failed(failedTrackCount, location);
            }

            return true;
        }
    }

    private String processNext(boolean firstTime) {
        String location = null;
        try {
            IITFileOrCDTrack track = tracks.getFileOrCDTrack(processedTrackCount);
            location = track.getLocation();
            if (!canProcess(track)) {
                remove(track);
            } else {
                try {
                    process(track);
                }
                finally {
                    // increase count for IITFileOrCDTrack access only if file existed
                    // TODO split processedTrackCount and getFileOrCDTrackCount
                    processedTrackCount++;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            log.severe("Error while processing track " + processedTrackCount + ": " + e.getMessage());

            // wait and have one more chance
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                // intentionally left empty
            }

            // one more chance
            if (firstTime)
                return processNext(false);
            else
                // avoids endless loops I've seen accessing the same track again and again
                processedTrackCount++;
        }
        return location;
    }

    protected boolean canProcess(IITFileOrCDTrack track) {
        String location = track.getLocation();
        if (location == null || location.length() == 0) {
            log.warning("Cannot process location-less track " + track);
            return false;
        }

        File file = new File(location);
        if (!file.exists()) {
            log.warning("Cannot process not-existing file " + location);
            return false;
        }
        return true;
    }

    protected void remove(IITFileOrCDTrack track) {
        String location = track.getLocation();
        if (location == null || location.length() == 0)
            location = track.getName() + ", " + track.getAlbum() + ", " + track.getArtist() + ", " + track.gettrackID();
        track.delete();
        removedTrackCount++;
        for (Notifier notifier : notifiers)
            notifier.removed(removedTrackCount, location);
    }

    protected void process(IITFileOrCDTrack track) {
        File file = new File(track.getLocation());

        Date iTunesModificationDate = track.getModificationDate();
        long iTunesModificationTime = iTunesModificationDate.getTime() / 1000;
        long fileModificationTime = file.lastModified() / 1000;
        if (iTunesModificationTime < fileModificationTime) {
            log.info("File modified after library modification " + iTunesModificationDate +
                    " diff: " + (iTunesModificationTime - fileModificationTime) + " seconds");
        } else if (iTunesModificationTime == fileModificationTime) {
            log.info("File modification matches library modification " + iTunesModificationDate);
        } else {
            log.info("Library modified after file modification " + iTunesModificationDate +
                    " diff: " + (fileModificationTime - iTunesModificationTime) + " seconds");
        }

        MP3File mp3 = MP3File.readValidFile(file);
        if (mp3 != null)
            process(track, mp3);
        else {
            failedTrackCount++;
            for (Notifier notifier : notifiers)
                notifier.failed(failedTrackCount, file.getAbsolutePath());
        }
    }

    protected void process(IITFileOrCDTrack track, MP3File mp3) {
        boolean fileModified = false, trackModified = false;

        int mp3Rating = mp3.getHead().getRating();
        if (mp3Rating > 0 && track.getRating() == 0) {
            log.info("Modifying library rating to " + mp3Rating);
            track.setRating(mp3Rating);
            trackModified = true;
        }
        int iTunesRating = track.getRating();
        if (track.getRating() > 0 && iTunesRating != mp3Rating) {
            log.info("Modifying rating from '" + mp3.getFile().getAbsolutePath() + "' to " + iTunesRating + " from " + mp3Rating);
            mp3.getHead().setRating(iTunesRating);
            fileModified = true;
        }

        if (isAddPlayCount()) {
            int mp3PlayCount = mp3.getHead().getPlayCount();
            if (mp3PlayCount < 0)
                mp3PlayCount = 0;
            int iTunesPlayCount = track.getPlayedCount();

            int playCount = mp3PlayCount + iTunesPlayCount;
            if (playCount > 0) {
                log.info("Adding library play count to " + playCount + " from " + iTunesPlayCount);
                track.setPlayedCount(playCount);
                trackModified = true;
                log.info("Adding play count of '" + mp3.getFile().getAbsolutePath() + "' to " + playCount + " from " + mp3PlayCount);
                mp3.getHead().setPlayCount(playCount);
                fileModified = true;
            }

        } else {
            int mp3PlayCount = mp3.getHead().getPlayCount();
            int iTunesPlayCount = track.getPlayedCount();
            if (mp3PlayCount > 0 && mp3PlayCount > iTunesPlayCount) {
                log.info("Modifying library play count to " + mp3PlayCount + " from " + iTunesPlayCount);
                track.setPlayedCount(mp3PlayCount);
                trackModified = true;
            }

            if (iTunesPlayCount > 0 && iTunesPlayCount > mp3PlayCount) {
                log.info("Modifying play count of '" + mp3.getFile().getAbsolutePath() + "' to " + iTunesPlayCount + " from " + mp3PlayCount);
                mp3.getHead().setPlayCount(iTunesPlayCount);
                fileModified = true;
            }
        }

        Date mp3PlayTime = mp3.getHead().getPlayTime() != null ? mp3.getHead().getPlayTime().getTime() : null;
        if (mp3PlayTime != null && (track.getPlayedDate() == null || mp3PlayTime.after(track.getPlayedDate()))) {
            log.info("Modifying library play time to " + DATE_FORMAT.format(mp3PlayTime.getTime()));
            track.setPlayedDate(mp3PlayTime);
            trackModified = true;
        }

        Date iTunesPlayTime = track.getPlayedDate();
        if (iTunesPlayTime != null && (mp3PlayTime == null || iTunesPlayTime.after(mp3PlayTime))) {
            log.info("Modifying play time from '" + mp3.getFile().getAbsolutePath() + "' to " + DATE_FORMAT.format(iTunesPlayTime.getTime()) +
                    " from " + (mp3PlayTime != null ? DATE_FORMAT.format(mp3PlayTime.getTime()) : "<null>"));
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(iTunesPlayTime);
            mp3.getHead().setPlayTime(calendar);
            fileModified = true;
        }

        if (fileModified) {
            modifiedFileCount++;

            write(mp3);
        }

        if (trackModified)
            modifiedTrackCount++;

        for (Notifier notifier : notifiers)
            notifier.processed(modifiedFileCount, modifiedTrackCount, mp3.getFile().getAbsolutePath(), fileModified, trackModified);
    }

    public interface Notifier {
        void opened(String version, String libraryPath, int trackCount, int playlistCount);

        void started(int trackCount, int playlistCount);

        void processing(int processedTracks);

        void failed(int failedTrackCount, String location);

        void removed(int removedTrackCount, String location);

        void processed(int modifiedFileCount, int modifiedTrackCount, String location, boolean fileModified, boolean trackModified);

        void finished(int modifiedFileCount, int modifiedTrackCount, int removedTrackCount);
    }

    private static class LogNotifier implements Notifier {
        private int trackCount = 0, processedTracks = 0;

        public void opened(String version, String libraryPath, int trackCount, int playlistCount) {
            log.info("Connected to iTunes " + version + " with library from '" + libraryPath + "'");
        }

        public void started(int trackCount, int playlistCount) {
            this.trackCount = trackCount;
            log.info("Library contains " + trackCount + "  tracks and " + playlistCount + " playlists");
        }

        public void processing(int processedTracks) {
            this.processedTracks = processedTracks;
            log.info("Processing " + processedTracks + ". from " + trackCount + " tracks");
        }

        public void failed(int failedTrackCount, String location) {
            log.info("Failed to process " + failedTrackCount + ". track: " + location);
        }

        public void removed(int removedTrackCount, String location) {
            log.info("Removed " + removedTrackCount + ". track for not-existing file: " + location);
        }

        public void processed(int modifiedFileCount, int modifiedTrackCount, String location, boolean fileModified, boolean trackModified) {
            if (fileModified)
                log.info("Modified " + modifiedFileCount + ". file: " + location);
            if (trackModified)
                log.info("Modified " + modifiedTrackCount + ". track: " + location);
        }

        public void finished(int modifiedFileCount, int modifiedTrackCount, int removedTrackCount) {
            log.info("Processed " + processedTracks + " out of " + trackCount + " tracks");
            log.info("Modified " + modifiedFileCount + " out of " + processedTracks + " processed files");
            log.info("Modified " + modifiedTrackCount + " out of " + trackCount + " tracks");
            log.info("Removed " + removedTrackCount + " out of " + trackCount + " tracks");
        }
    }
}
