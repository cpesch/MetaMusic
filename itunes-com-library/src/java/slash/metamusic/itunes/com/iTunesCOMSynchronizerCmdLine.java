/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.com;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import slash.metamusic.client.CommandLineClient;

/**
 * Tool that allows to use the {@link iTunesCOMSynchronizer} from the command line.
 *
 * @author Christian Pesch
 * @version $Id: iTunesCOMSynchronizerCmdLine.java 792 2006-04-22 10:09:35 +0200 (Sa, 22 Apr 2006) cpesch $
 */

public class iTunesCOMSynchronizerCmdLine extends CommandLineClient {
    protected static final String ADD_PLAY_COUNT_PARAMETER = "a";
    protected static final String ADD_PLAY_COUNT_PARAMETER_LONG = "add-play-count";

    private iTunesCOMSynchronizer synchronizer = new iTunesCOMSynchronizer();


    public boolean isAddPlayCount() {
        return synchronizer.isAddPlayCount();
    }

    public void setAddPlayCount(boolean addPlayCount) {
        synchronizer.setAddPlayCount(addPlayCount);
    }


    @SuppressWarnings({"ACCESS_STATIC_VIA_INSTANCE"})
    protected void fillInOptions(Options options) {
        options.addOption(OptionBuilder.withArgName(iTunesCOMSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER_LONG)
                .withDescription("add play count of iTunes library and MP3 files")
                .withLongOpt(iTunesCOMSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER_LONG)
                .create(iTunesCOMSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER));
    }

    protected boolean parseCommandLine(CommandLine commandLine) {
        setAddPlayCount(commandLine.hasOption(iTunesCOMSynchronizerCmdLine.ADD_PLAY_COUNT_PARAMETER));
        return true;
    }

    protected String getUsage() {
        return getClass().getName() + ".jar [-a] [-v]";
    }

    public void beforeRunning() {
    }

    public void afterRunning() {
    }

    public void run() {
        if (!synchronizer.isiTunesSupported()) {
            log.severe("iTunes is not supported. Maybe you're not running Windows?");
            return;
        }
        synchronizer.open();
        synchronizer.start();
        while (synchronizer.next()) {
        }
        synchronizer.close();
    }

    public static void main(String[] args) {
        main(new iTunesCOMSynchronizerCmdLine(), args);
    }
}
