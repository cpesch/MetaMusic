/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.client;

import org.apache.commons.cli.*;
import slash.metamusic.util.Files;
import slash.metamusic.util.LoggingWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;

/**
 * Abstract base class for command line clients
 *
 * @author Christian Pesch
 * @version $Id: CommandLineClient.java 731 2006-02-24 08:44:04Z cpesch $
 */

public abstract class CommandLineClient {
    /**
     * Logging output
     */
    protected static Logger log = Logger.getLogger(CommandLineClient.class.getName());

    protected static final String VERBOSE_PARAMETER = "v";
    protected static final String VERBOSE_PARAMETER_LONG = "verbose";

    /**
     * Indicates wether output should be done with more verbosity.
     * Will pass debug outputs to user.
     */
    protected boolean verbose;


    public CommandLineClient() {
        log = Logger.getLogger(getClass().getName());
    }


    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }


    protected Properties loadProperties(String name) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Files.replaceSeparators("properties/" + name + ".properties")));
            log.fine("Loading properties " + properties);
        } catch (IOException e) {
            log.severe("Cannot load properties from '" + name + "': " + e.getMessage());
        }
        return properties;
    }


    /**
     * Parse the given command line arguments.
     *
     * @param args the command line arguments to parse
     */
    @SuppressWarnings({"ACCESS_STATIC_VIA_INSTANCE"})
    protected void parseCommandLine(String[] args) {
        Options options = new Options();
        options.addOption(OptionBuilder.withArgName(VERBOSE_PARAMETER_LONG)
                .withDescription("enables verbose output")
                .withLongOpt(VERBOSE_PARAMETER_LONG)
                .create(VERBOSE_PARAMETER));
        fillInOptions(options);

        List<String> argList = new ArrayList<String>();
        Properties properties = loadProperties("client");
        argList.addAll(parseProperties(properties, "client."));
        argList.addAll(Arrays.asList(args));

        CommandLineParser parser = new GnuParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, argList.toArray(new String[argList.size()]));
        } catch (ParseException e) {
            log.severe("Cannot parse command line: " + e.getMessage());
            printUsage(options);
            System.exit(10);
        }

        setVerbose(commandLine.hasOption(VERBOSE_PARAMETER));

        boolean success = parseCommandLine(commandLine);
        if (!success) {
            printUsage(options);
            System.exit(5);
        }
    }

    private List<String> parseProperties(Properties properties, String prefix) {
        List<String> argList = new ArrayList<String>();
        //noinspection unchecked
        Enumeration<String> enumeration = (Enumeration<String>) properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String propertyName = enumeration.nextElement();
            if (propertyName.startsWith(prefix)) {
                String propertyValue = properties.getProperty(propertyName);
                propertyName = propertyName.substring(prefix.length());
                argList.add("--" + propertyName);
                argList.add(propertyValue);
            }
        }
        return argList;
    }

    /**
     * Hook for subclasses to add additional command line options.
     *
     * @param options the options to add additional options to
     */
    protected abstract void fillInOptions(Options options);

    /**
     * Hook for subclasses to parse additional command line options.
     *
     * @param commandLine the command line to use for parsing
     * @return true, if the parsing was successful
     */
    protected abstract boolean parseCommandLine(CommandLine commandLine);

    /**
     * Print the given command line options
     *
     * @param options the command line options to print
     */
    protected void printUsage(Options options) {
        new HelpFormatter().printHelp(
                new PrintWriter(new LoggingWriter(log)),
                HelpFormatter.DEFAULT_WIDTH,
                getUsage(),
                "available options:",
                options,
                HelpFormatter.DEFAULT_LEFT_PAD,
                HelpFormatter.DEFAULT_DESC_PAD,
                "");
    }

    /**
     * Hook for subclasses to print the usage of this client.
     *
     * @return the usage of this client
     */
    protected abstract String getUsage();

    /**
     * Run this client with the given command line arguments
     *
     * @param args the command line arguments to parse
     */
    public void run(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        try {
            parseCommandLine(args);
            try {
                beforeRunning();
                run();
            }
            catch (Exception e) {
                log.severe("An exception occurred while running: " + e.getMessage());
            }
            finally {
                afterRunning();
            }
        } finally {
            long end = System.currentTimeMillis();
            log.info("Overall run time was " + (end - start) / 1000 + " seconds.");
        }
    }

    /**
     * Hook for subclasses before running.
     */
    public abstract void beforeRunning();

    /**
     * Hook for subclasses to run.
     */
    public abstract void run();

    /**
     * Hook for subclasses after running.
     */
    public abstract void afterRunning();

    /**
     * A convenience method to implement the method main(String[]).
     *
     * @param client the client to run
     * @param args   the command line arguments
     */
    protected static void main(CommandLineClient client, String[] args) {
        try {
            client.run(args);
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(20);
        }
    }
}
