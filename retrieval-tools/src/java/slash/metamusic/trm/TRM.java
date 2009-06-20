/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.trm;

import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.LibraryLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Logger;

/**
 * A client that calculates TRM ids.
 *
 * @author Christian Pesch
 * @version $Id: TRM.java 914 2006-12-26 20:44:49Z cpesch $
 */

public class TRM {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(TRM.class.getName());

    private static boolean libraryLoaded = false;

    static {
        try {
            LibraryLoader.loadLibrary(TRM.class.getClassLoader(), "trm");
            libraryLoaded = true;
        } catch (IOException e) {
            log.severe("Cannot load native library 'trm': " + e.getMessage());
        }
    }

    static class TRMStruct {
        long duration = 0;    // milliseconds of the sample
        String signature = null;  // trm signature
    }

    private static final int TRM_OK = 0;
    private static final int TRM_FILENOTFOUND = -1;
    private static final int TRM_DECODEERR = -2;
    private static final int TRM_CANNOTCONNECT = -3;
    private static final int TRM_OTHERERROR = -4;

    private static native int generateTRMforMP3(String fileName, long duration, String proxyServer, int proxyPort,
                                                TRMStruct result);

    private static native int generateTRMforWAV(String fileName, long duration, String proxyServer, int proxyPort,
                                                TRMStruct result);

    private static native int generateTRMforOggVorbis(String fileName, long duration, String proxyServer, int proxyPort,
                                                      TRMStruct result);

    private TRMStruct trmStruct = new TRMStruct();
    private boolean valid = false;

    /**
     * Return whether the TRM calculation is supported on this plattform.
     *
     * @return true, if the TRM calculation is supported on this plattform
     */
    public static boolean isSupported() {
        return libraryLoaded;
    }

    /**
     * Returns if the TRM has been calculated successfully.
     *
     * @return true if the TRM has been calculated successfully
     */
    public boolean isValid() {
        return valid;
    }

    public long getDuration() {
        return trmStruct.duration;
    }

    public String getSignature() {
        String signature = trmStruct.signature;
        if (signature != null && signature.equals("00000000-0000-0000-0000-000000000000"))
            signature = null;
        return signature;
    }

    public void read(MP3File file) throws FileNotFoundException, ConnectException {
        if (!file.getFile().exists())
            throw new FileNotFoundException();

        if (!libraryLoaded)
            throw new UnsupportedOperationException("Native 'trm' library not loaded");

        //noinspection UnusedAssignment
        int ret = TRM_OK;
        if (file.isMP3())
            ret = generateTRMforMP3(file.getFile().getAbsolutePath(), file.getSeconds() * 1000, "", 0, trmStruct);
        else if (file.isWAV())
            ret = generateTRMforWAV(file.getFile().getAbsolutePath(), file.getSeconds() * 1000, "", 0, trmStruct);
        else if (file.isOgg())
            ret = generateTRMforOggVorbis(file.getFile().getAbsolutePath(), file.getSeconds() * 1000, "", 0, trmStruct);
        else
            throw new UnsupportedOperationException("File " + file + " is not supported");

        switch (ret) {
            case TRM_FILENOTFOUND:
                throw new FileNotFoundException();
            case TRM_DECODEERR:
                throw new Error("Decoder error");
            case TRM_CANNOTCONNECT:
                throw new ConnectException();
            case TRM_OTHERERROR:
                throw new Error("Other error");
        }

        valid = true;
    }

    /**
     * Calculate TRM from given MP3File and return it. If an error
     * occurs or the file is invalid, null is returned.
     *
     * @param file the file to read
     * @return a signature for the given MP3File or null, if
     *         the file is invalid or an error occured
     */
    public static String readValidSignature(MP3File file) {
        try {
            if (TRM.isSupported()) {
                TRM trm = new TRM();
                trm.read(file);
                if (trm.isValid())
                    return trm.getSignature();
            }
        } catch (IOException e) {
            log.severe("Cannot calculate TRM for " + file.getFile().getAbsolutePath() + ": " + e.getMessage());
        }
        return null;
    }

    public String toString() {
        return super.toString() + "[duration=" + getDuration() +
                ", signature=" + getSignature() + "]";
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("slash.metamusic.trm.TRM <file1> <file2> ... <fileN>");
            System.exit(1);
        }

        for (String arg : args) {
            TRM trm = new TRM();
            MP3File mp3 = new MP3File();
            File file = new File(arg);
            if (mp3.read(file)) {
                trm.read(mp3);
                System.out.println(trm);
            } else
                System.err.println("Cannot read invalid MP3 file: " + file.getAbsolutePath());
        }
        System.exit(0);
    }
}
