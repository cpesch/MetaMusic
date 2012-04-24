/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.com;

import com.jacob.com.ComThread;
import slash.metamusic.itunes.com.binding.IITLibraryPlaylist;
import slash.metamusic.itunes.com.binding.IITPlaylistCollection;
import slash.metamusic.itunes.com.binding.IITTrackCollection;
import slash.metamusic.itunes.com.binding.IiTunes;
import slash.metamusic.util.LibraryLoader;

import java.io.IOException;
import java.util.logging.Logger;

public class iTunesCOMLibrary {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(iTunesCOMLibrary.class.getName());

    private static boolean libraryLoaded = false;

    static {
        try {
          String jacobDllPath = LibraryLoader.loadLibrary(iTunesCOMLibrary.class.getClassLoader(),
                  "jacob-1.14.3-" + (LibraryLoader.getBits() == 64 ? "x64" : "x86"));
          System.setProperty(com.jacob.com.LibraryLoader.JACOB_DLL_PATH, jacobDllPath);
          libraryLoaded = true;
        } catch (IOException e) {
            log.severe("Cannot load native library 'jacob': " + e.getMessage());
        }
    }

    /**
     * Return whether iTunes COM interface is supported on this plattform.
     *
     * @return true, if the iTunes COM interface is supported on this plattform
     */
    public static boolean isSupported() {
        return libraryLoaded;
    }


    private IiTunes iTunes;

    public void open() {
        log.info("Opening iTunes COM object");
        try {
            ComThread.InitMTA(true);
            iTunes = new IiTunes("iTunes.Application");
            log.info("Opened iTunes " + getVersion());
        }
        catch (Exception e) {
            log.severe("Error opening iTunes COM object: " + e.getMessage());
        }
    }

    public void close() {
        log.info("Closing iTunes COM object");
        try {
            if (iTunes != null) {
                iTunes.safeRelease();
                iTunes = null;
                ComThread.Release();
                ComThread.quitMainSTA();
                log.info("Closed iTunes");
            }
        }
        catch (Exception e) {
            log.severe("Error closing iTunes COM object: " + e.getMessage());
        }
    }

    public String getVersion() {
        return iTunes.getVersion();
    }

    public String getLibraryPath() {
        return iTunes.getLibraryXMLPath();
    }

    private IITLibraryPlaylist getLibrary() {
        return iTunes.getLibraryPlaylist();
    }

    public int getTrackCount() {
        return getTracks().getCount();
    }

    public IITTrackCollection getTracks() {
        return getLibrary().getTracks();
    }

    public int getPlaylistCount() {
        return getPlaylists().getCount();
    }

    public IITPlaylistCollection getPlaylists() {
        return iTunes.getLibrarySource().getPlaylists();
    }
}
