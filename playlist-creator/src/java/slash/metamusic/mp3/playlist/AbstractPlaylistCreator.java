/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.playlist;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import slash.metamusic.client.CommandLineClient;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.mp3.MP3Files;
import slash.metamusic.util.Files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * A abstract class to create play lists from file system trees.
 *
 * @author Christian Pesch
 * @version $Id: AbstractPlaylistCreator.java 749 2006-03-30 09:24:19Z cpesch $
 */

public abstract class AbstractPlaylistCreator extends CommandLineClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(AbstractPlaylistCreator.class.getName());

    protected static final String DIRECTORY_PARAMETER = "d";
    protected static final String DIRECTORY_PARAMETER_LONG = "directory";
    protected static final String PLAYLIST_PARAMETER = "p";
    protected static final String PLAYLIST_PARAMETER_LONG = "playlist";

    private File directory, playlist;

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getPlaylist() {
        return playlist;
    }

    public void setPlaylist(File playlist) {
        this.playlist = playlist;
    }

    @SuppressWarnings({"ACCESS_STATIC_VIA_INSTANCE"})
    protected void fillInOptions(Options options) {
        options.addOption(OptionBuilder.withArgName(DIRECTORY_PARAMETER_LONG)
                .hasArg()
                .isRequired()
                .withDescription("directory to scan for mp3s (required)")
                .withLongOpt(DIRECTORY_PARAMETER_LONG)
                .create(DIRECTORY_PARAMETER));
        options.addOption(OptionBuilder.withArgName(PLAYLIST_PARAMETER_LONG)
                .hasArg()
                .isRequired()
                .withDescription("playlist to create (required)")
                .withLongOpt(PLAYLIST_PARAMETER_LONG)
                .create(PLAYLIST_PARAMETER));
    }

    protected boolean parseCommandLine(CommandLine commandLine) {
        String directory = commandLine.getOptionValue(DIRECTORY_PARAMETER);
        if (directory != null) {
            setDirectory(new File(directory));
            if (!getDirectory().exists()) {
                log.severe("The directory '" + getDirectory() + "' does not exist.");
                return false;
            }
        }
        String playlist = commandLine.getOptionValue(PLAYLIST_PARAMETER);
        if (playlist == null)
            playlist = Files.replaceSeparators(getDirectory().getAbsolutePath() + "/" + getDirectory().getName() + "." + getExtension());
        setPlaylist(new File(playlist));
        return true;
    }

    protected String getUsage() {
        return getClass().getName() + ".jar -d <directory> -p <playlist> [-v]";
    }

    public void beforeRunning() {
    }

    public void afterRunning() {
    }

    public void run() {
        try {
            process(getDirectory(), "mp3", getPlaylist());
        } catch (IOException e) {
            log.severe("Error while creating playlist '" + getPlaylist() + "' from '" + getDirectory() + "': " + e.getMessage());
        }
    }

    /**
     * Create a M3U playlist file from the given directory.
     *
     * @param directory the directory to create the playlist from
     * @param extension the file extension of the files to create the playlist from
     * @param playlist  the playlist file to create
     * @throws IOException if an access error occurs and the playlist cannot be processed
     */
    public void process(File directory, String extension,
                        File playlist) throws IOException {
        log.info("Creating play list for " + directory.getAbsolutePath());

        List<File> files = Files.collectFiles(directory, extension);
        if (files.size() == 0) {
            log.warning("No files found below " + directory.getAbsolutePath());
            return;
        }

        log.info("Parsing " + files.size() + " files for play list");

        List<MP3File> mp3s = MP3Files.parseMP3Files(files);
        if (mp3s.size() == 0) {
            log.warning("No mp3 files found below " + directory.getAbsolutePath());
            return;
        }

        log.info("Creating play list from " + mp3s.size() + " mp3 files");

        MP3File[] mp3Array = mp3s.toArray(new MP3File[mp3s.size()]);
        Arrays.sort(mp3Array, new AlbumIndexFileComparator());
        FileWriter writer = new FileWriter(playlist);
        String pathName = playlist.getParentFile() != null ? playlist.getParentFile().getAbsolutePath() : null;
        try {
            writeHeader(writer);

            for (int i = 0, c = mp3Array.length; i < c; i++) {
                MP3File mp3 = mp3Array[i];
                String fileName = Files.relativize(pathName, mp3.getFile().getAbsolutePath());
                writeMP3(writer, mp3, fileName, i);
            }

            writeFooter(writer, mp3Array.length);
        } finally {
            writer.close();
        }

        log.info("Created play list " + playlist.getAbsolutePath());
    }

    protected abstract String getExtension();

    protected abstract void writeHeader(FileWriter writer) throws IOException;

    protected abstract void writeMP3(FileWriter writer, MP3File mp3, String fileName, int index) throws IOException;

    protected abstract void writeFooter(FileWriter writer, int count) throws IOException;
}
