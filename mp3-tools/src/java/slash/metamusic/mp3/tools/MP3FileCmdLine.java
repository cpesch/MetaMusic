/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2004 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import slash.metamusic.client.CommandLineClient;
import slash.metamusic.mp3.MP3File;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Shows information about the MP3 Header and the ID3 Tags of an MP3 file.
 *
 * @author Christian Pesch
 * @version $Id: MP3FileCmdLine.java 956 2007-02-03 10:39:39Z cpesch $
 */

public class MP3FileCmdLine extends CommandLineClient {

    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3FileCmdLine.class.getName());

    protected static final String FILE_PARAMETER = "f";
    protected static final String FILE_PARAMETER_LONG = "file";

    private File file;

    /**
     * Returns the data file or null if we've read from a stream
     *
     * @return the data file or null if we've read from a stream
     */
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @SuppressWarnings({"ACCESS_STATIC_VIA_INSTANCE"})
    protected void fillInOptions(Options options) {
        options.addOption(OptionBuilder.withArgName(FILE_PARAMETER_LONG)
                .hasArg()
                .isRequired()
                .withDescription("file to parse (required)")
                .withLongOpt(FILE_PARAMETER_LONG)
                .create(FILE_PARAMETER));
    }

    protected boolean parseCommandLine(CommandLine commandLine) {
        String file = commandLine.getOptionValue(FILE_PARAMETER);
        if (file != null) {
            setFile(new File(file));
            if (!getFile().exists()) {
                log.severe("The file '" + getFile() + "' does not exist.");
                return false;
            }
        }
        return true;
    }

    protected String getUsage() {
        return getClass().getName() + ".jar -f <file> [-v]";
    }

    public void beforeRunning() {
    }

    public void afterRunning() {
    }

    public void run() {
        try {
            log.info("Parsing file '" + getFile().getAbsolutePath() + "'");
            MP3File mp3 = new MP3File();
            if (mp3.read(getFile())) {
                log.info(mp3.toString());
                if (mp3.isID3v1())
                    log.info(mp3.getTail().toString());
                if (mp3.isID3v2())
                    log.info(mp3.getHead().toString());
            } else
                log.severe("Cannot read invalid MP3 file '" + getFile().getAbsolutePath() + "'");
        } catch (IOException e) {
            log.severe("Cannot read '" + getFile() + "': " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        main(new MP3FileCmdLine(), args);
    }
}
