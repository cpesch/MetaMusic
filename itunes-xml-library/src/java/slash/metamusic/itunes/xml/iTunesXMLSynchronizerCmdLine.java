/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.xml;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import slash.metamusic.client.CommandLineClient;

import java.io.File;

/**
 * Tool that allows to use the {@link iTunesXMLSynchronizer} from the command line.
 *
 * @author Christian Pesch
 * @version $Id: iTunesXMLSynchronizerCmdLine.java 792 2006-04-22 10:09:35 +0200 (Sa, 22 Apr 2006) cpesch $
 */

public class iTunesXMLSynchronizerCmdLine extends CommandLineClient {
    protected static final String LIBRARY_PARAMETER = "l";
    protected static final String LIBRARY_PARAMETER_LONG = "library";
    protected static final String ADD_PLAY_COUNT_PARAMETER = "a";
    protected static final String ADD_PLAY_COUNT_PARAMETER_LONG = "add-play-count";

    private iTunesXMLSynchronizer synchronizer = new iTunesXMLSynchronizer();


    public File getLibrary() {
        return synchronizer.getLibrary();
    }

    public void setLibrary(File library) {
        synchronizer.setLibrary(library);
    }

    public boolean isAddPlayCount() {
        return synchronizer.isAddPlayCount();
    }

    public void setAddPlayCount(boolean addPlayCount) {
        synchronizer.setAddPlayCount(addPlayCount);
    }


    @SuppressWarnings({"ACCESS_STATIC_VIA_INSTANCE"})
    protected void fillInOptions(Options options) {
        options.addOption(OptionBuilder.withArgName(iTunesXMLSynchronizerCmdLine.LIBRARY_PARAMETER_LONG)
                .hasArg()
                .isRequired()
                .withDescription("iTunes library file (required)")
                .withLongOpt(iTunesXMLSynchronizerCmdLine.LIBRARY_PARAMETER_LONG)
                .create(iTunesXMLSynchronizerCmdLine.LIBRARY_PARAMETER));
        options.addOption(OptionBuilder.withArgName(iTunesXMLSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER_LONG)
                .withDescription("add play count of iTunes library and MP3 files")
                .withLongOpt(iTunesXMLSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER_LONG)
                .create(iTunesXMLSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER));
    }

    protected boolean parseCommandLine(CommandLine commandLine) {
        String library = commandLine.getOptionValue(iTunesXMLSynchronizerCmdLine.LIBRARY_PARAMETER);
        if (library != null) {
            setLibrary(new File(library));
            if (!getLibrary().exists()) {
                log.severe("The iTunes library '" + getLibrary() + "' does not exist.");
                return false;
            }
        }
        setAddPlayCount(commandLine.hasOption(iTunesXMLSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER));
        return true;
    }

    protected String getUsage() {
        return getClass().getName() + ".jar -l <library> [-a] [-f] [-v]";
    }

    public void beforeRunning() {
    }

    public void afterRunning() {
    }

    public void run() {
        synchronizer.start();
    }

    public static void main(String[] args) {
        main(new iTunesXMLSynchronizerCmdLine(), args);
    }
}
