/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import slash.metamusic.client.CommandLineClient;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Cleans and extends the meta data for all MP3s in a file system tree.
 *
 * @author Christian Pesch
 * @version $Id: MP3TidyCmdLine.java 956 2007-02-03 10:39:39Z cpesch $
 */

public class MP3TidyCmdLine extends CommandLineClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3TidyCmdLine.class.getName());

    private MP3Tidy tidyer = new MP3Tidy();

    protected static final String DIRECTORY_PARAMETER = "d";
    protected static final String DIRECTORY_PARAMETER_LONG = "directory";

    @SuppressWarnings({"ACCESS_STATIC_VIA_INSTANCE"})
    protected void fillInOptions(Options options) {
        options.addOption(OptionBuilder.withArgName(DIRECTORY_PARAMETER_LONG)
                .hasArg()
                .isRequired()
                .withDescription("directory to scan for mp3s (required)")
                .withLongOpt(DIRECTORY_PARAMETER_LONG)
                .create(DIRECTORY_PARAMETER));
    }

    protected boolean parseCommandLine(CommandLine commandLine) {
        String directory = commandLine.getOptionValue(DIRECTORY_PARAMETER);
        if (directory != null) {
            File path = new File(directory);
            tidyer.prepare(Arrays.asList(path));
            if (!path.exists()) {
                log.severe("The directory '" + path + "' does not exist.");
                return false;
            }
        }
        return true;
    }

    protected String getUsage() {
        return getClass().getName() + ".jar -d <directory> [-v]";
    }

    public void beforeRunning() {
    }

    public void afterRunning() {
    }

    public void run() {
        tidyer.start();
        while (!tidyer.next()) {
        }
    }

    public static void main(String[] args) throws Exception {
        main(new MP3TidyCmdLine(), args);
    }
}
