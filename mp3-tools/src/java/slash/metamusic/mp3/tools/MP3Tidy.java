/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Cleans and extends the meta data for all MP3s in a file system tree.
 *
 * @author Christian Pesch
 * @version $Id: MP3Tidy.java 956 2007-02-03 10:39:39Z cpesch $
 */

public class MP3Tidy {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3Tidy.class.getName());

    private String extension = "mp3";
    private boolean renamePathToTags = false, renameFileToTags = true;
    private String newRoot = null;

    private MP3Cleaner cleaner = new MP3Cleaner();
    private MP3Extender extender = new MP3Extender();
    private MP3Mover mover = new MP3Mover();

    private List<Notifier> notifiers = new ArrayList<Notifier>();
    private List<File> files = new ArrayList<File>();
    private int fileCount, processedFileCount, failedFileCount,
            modifiedFileCount, movedFileCount, renamedFileCount, extendedTagCount, cleanedTagCount;


    public MP3Tidy() {
        addNotifier(new LogNotifier());
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean addNotifier(Notifier notifier) {
        return notifiers.add(notifier);
    }

    public boolean removeNotifier(Notifier notifier) {
        return notifiers.remove(notifier);
    }

    public void setAddCover(boolean addCover) {
        extender.setAddCover(addCover);
    }

    public void setCoverDirectoryName(String coverDirectoryName) {
        extender.setCoverDirectoryName(coverDirectoryName);
    }

    public void setAddLyrics(boolean addLyrics) {
        extender.setAddLyrics(addLyrics);
    }

    public void setLyricsDirectoryName(String lyricsDirectoryName) {
        extender.setLyricsDirectoryName(lyricsDirectoryName);
    }

    public void setAddMetaData(boolean addMetaData) {
        extender.setAddMetaData(addMetaData);
    }

    public void setRemoveiTunesTags(boolean removeiTunesTags) {
        cleaner.setRemoveiTunesTags(removeiTunesTags);
    }

    public void setRemoveMusicBrainzTags(boolean removeMusicBrainzTags) {
        cleaner.setRemoveMusicBrainzTags(removeMusicBrainzTags);
    }

    public void setRemoveMusicMatchTags(boolean removeMusicMatchTags) {
        cleaner.setRemoveMusicMatchTags(removeMusicMatchTags);
    }

    public boolean isRemoveWindowsMediaPlayerTags() {
        return cleaner.isRemoveWindowsMediaPlayerTags();
    }

    public void setRemoveWindowsMediaPlayerTags(boolean removeWindowsMediaPlayerTags) {
        cleaner.setRemoveWindowsMediaPlayerTags(removeWindowsMediaPlayerTags);
    }

    public void setUnifyTags(boolean unifyTags) {
        cleaner.setUnifyTags(unifyTags);
    }

    public boolean isRenameFileToTags() {
        return renameFileToTags;
    }

    public void setRenameFileToTags(boolean renameFileToTags) {
        this.renameFileToTags = renameFileToTags;
    }

    public boolean isWriteID3v1() {
        return cleaner.isWriteID3v1() && extender.isWriteID3v1();
    }

    public void setWriteID3v1(boolean writeID3v1) {
        cleaner.setWriteID3v1(writeID3v1);
        extender.setWriteID3v1(writeID3v1);
    }

    public boolean isWriteID3v2() {
        return cleaner.isWriteID3v2() || extender.isWriteID3v2();
    }

    public void setWriteID3v2(boolean writeID3v2) {
        cleaner.setWriteID3v2(writeID3v2);
        extender.setWriteID3v2(writeID3v2);
    }


    public void prepare(List<File> paths) {
        files.clear();
        for (Notifier notifier : notifiers)
            notifier.startedToPrepare();

        for (File path : paths) {
            List<File> collected = Files.collectFiles(path, getExtension());
            files.addAll(collected);

            for (Notifier notifier : notifiers)
                notifier.preparing(collected.size(), path);
        }

        fileCount = files.size();
        for (Notifier notifier : notifiers)
            notifier.finishedToPrepare(fileCount);
    }

    public void start() {
        processedFileCount = 0;
        failedFileCount = 0;
        modifiedFileCount = 0;
        movedFileCount = 0;
        renamedFileCount = 0;
        cleanedTagCount = 0;
        extendedTagCount = 0;
        for (Notifier notifier : notifiers)
            notifier.started();
    }

    public boolean next() {
        if (processedFileCount >= fileCount) {
            for (Notifier notifier : notifiers)
                notifier.finished(modifiedFileCount, renamedFileCount, cleanedTagCount, extendedTagCount);
            return false;

        } else {
            File file = null;
            try {
                file = files.get(processedFileCount++);
                if (newRoot != null) {
                    File newFile = new File(newRoot, file.getName());
                    if (file.renameTo(newFile))
                        file = newFile;
                }

                for (Notifier notifier : notifiers)
                    notifier.processing(processedFileCount);

                process(file);
            }
            catch (Throwable t) {
                t.printStackTrace();
                log.severe("Error while processing " + file + ": " + t.getMessage());
                failedFileCount++;
                for (Notifier notifier : notifiers)
                    notifier.failed(failedFileCount, file);
            }

            return true;
        }
    }

    protected void process(File file) throws IOException {
        MP3File mp3 = MP3File.readValidFile(file);
        if (mp3 == null)
            throw new IllegalArgumentException("No valid mp3: " + file.getAbsolutePath());

        boolean extendedTags = extender.extendTags(mp3);
        boolean cleanedTags = cleaner.cleanTags(mp3);

        if (extendedTags)
            extendedTagCount++;
        if (cleanedTags)
            cleanedTagCount++;

        boolean changeToID3v2 = !isWriteID3v1() && mp3.isID3v1() && mp3.isID3v2();
        boolean changeToID3v1 = !isWriteID3v2() && mp3.isID3v1() && mp3.isID3v2();
        boolean modifiedFile = extendedTags || cleanedTags || changeToID3v2 || changeToID3v1;
        if (modifiedFile) {
            modifiedFileCount++;
            extender.write(mp3);
        }

        boolean movedFile = false, renamedFile = false;
        if (isRenameFileToTags()) {
            if (renamePathToTags) {
                if (mover.moveFileToTags(mp3)) {
                    movedFile = true;
                    movedFileCount++;
                }
            } else {
                if (mover.renameFileToTags(mp3)) {
                    renamedFile = true;
                    renamedFileCount++;
                }
            }
        }

        if (isRemoveWindowsMediaPlayerTags())
            cleaner.removeCovers(file);

        for (Notifier notifier : notifiers)
            notifier.processed(movedFileCount, renamedFileCount, modifiedFileCount,
                    cleanedTagCount, extendedTagCount,
                    file,
                    movedFile, renamedFile, modifiedFile,
                    cleanedTags, extendedTags);
    }

    public interface Notifier {
        void startedToPrepare();

        void preparing(int fileCount, File path);

        void finishedToPrepare(int fileCount);

        void started();

        void processing(int processedFiles);

        void failed(int failedFileCount, File file);

        void processed(int movedFileCount, int renamedFileCount, int modifiedFileCount, int cleanedTagCount, int extendedTagCount,
                       File file, boolean movedFile, boolean renamedFile, boolean modifiedFile, boolean cleanedTags, boolean extendedTags);

        void finished(int modifiedFileCount, int renamedFileCount, int cleanedTagCount, int extendedTagCount);
    }

    private static class LogNotifier implements Notifier {
        private int fileCount = 0, processedFiles = 0;

        public void startedToPrepare() {
        }

        public void preparing(int fileCount, File path) {
            log.info("Preparing " + fileCount + " files from " + path + " to process");
        }

        public void finishedToPrepare(int fileCount) {
            this.fileCount = fileCount;
            log.info("Prepared " + fileCount + " files");
        }

        public void started() {
            log.info("Started with " + fileCount + " files");
        }

        public void processing(int processedFiles) {
            this.processedFiles = processedFiles;
            log.info("Processing " + processedFiles + ". from " + fileCount + " files");
        }

        public void failed(int failedFileCount, File file) {
            log.info("Failed to process " + failedFileCount + ". file: " + file);
        }

        public void processed(int movedFileCount, int renamedFileCount, int modifiedFileCount, int cleanedTagCount, int extendedTagCount, File file,
                              boolean movedFile, boolean renamedFile, boolean modifiedFile, boolean cleanedTags, boolean extendedTags) {
            if (movedFile)
                log.info("Moved " + movedFileCount + ". file: " + file);
            if (renamedFile)
                log.info("Renamed " + renamedFileCount + ". file: " + file);
            if (modifiedFile)
                log.info("Modified " + modifiedFileCount + ". file: " + file);
            if (cleanedTags)
                log.info("Cleaned tags of " + cleanedTagCount + ". file: " + file);
            if (extendedTags)
                log.info("Extended tags of " + extendedTagCount + ". file: " + file);
        }

        public void finished(int modifiedFileCount, int renamedFileCount, int cleanedTagCount, int extendedTagCount) {
            log.info("Processed " + processedFiles + " out of " + fileCount + " files");
            log.info("Modified " + modifiedFileCount + " out of " + processedFiles + " processed files");
            log.info("Renamed " + renamedFileCount + " out of " + processedFiles + " processed files");
            log.info("Cleaned tags from " + cleanedTagCount + " out of " + fileCount + " files");
            log.info("Extended tags from " + extendedTagCount + " out of " + fileCount + " files");
        }
    }
}
