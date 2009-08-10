/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.itunes.xml;

import slash.metamusic.itunes.xml.binding.Array;
import slash.metamusic.itunes.xml.binding.Dict;
import slash.metamusic.itunes.xml.binding.Plist;
import slash.metamusic.itunes.xml.iTunesXMLLibrary.Playlist;
import slash.metamusic.itunes.xml.iTunesXMLLibrary.Track;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.Files;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Syncs the iTunes library rating, play count and play time with the tags
 * of the MP3 files contained in the library.
 *
 * @author Christian Pesch
 * @version $Id: iTunesXMLSynchronizer.java 792 2006-04-22 10:09:35 +0200 (Sa, 22 Apr 2006) cpesch $
 */

public class iTunesXMLSynchronizer {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(iTunesXMLSynchronizer.class.getName());

    private static DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private File library;
    private boolean addPlayCount;
    private int fileCount, libraryEntryCount, modifiedFileCount, modifiedLibraryEntryCount, removedLibraryEntryCount;


    public File getLibrary() {
        return library;
    }

    public void setLibrary(File library) {
        this.library = library;
    }

    public boolean isAddPlayCount() {
        return addPlayCount;
    }

    public void setAddPlayCount(boolean addPlayCount) {
        this.addPlayCount = addPlayCount;
    }


    private List<Track> mapToTracks(Dict dict) {
        List<Track> result = new ArrayList<Track>();
        List<Object> objects = dict.getKeyAndArrayOrData();
        for (int i = 0; i < objects.size(); i += 2) {
            JAXBElement key = (JAXBElement) objects.get(i);
            Dict value = (Dict) objects.get(i + 1);
            Track track = new Track(dict, key, value);
            result.add(track);
        }
        return result;
    }

    private List<Playlist> mapToPlaylists(Array array) {
        List<Playlist> result = new ArrayList<Playlist>();
        List<Object> objects = array.getArrayOrDataOrDate();
        for (Object object : objects) {
            Dict value = (Dict) object;
            Playlist playlist = new Playlist(value);
            result.add(playlist);
        }
        return result;
    }

    public void start() {
        try {
            log.info("Parsing iTunes library from '" + getLibrary().getAbsolutePath() + "'");
            Plist plist = (Plist) iTunesXMLLibrary.unmarshal(Plist.class, new StreamSource(new FileReader(getLibrary())));

            log.fine("Plist version: " + plist.getVersion());
            Dict propertyDict = plist.getDict();
            Map<String, Object> properties = iTunesXMLLibrary.dictToMap(propertyDict);
            log.fine("Properties (" + properties.size() + "): " + properties);
            List<Track> tracks = mapToTracks((Dict) properties.get("Tracks"));
            libraryEntryCount = tracks.size();
            log.fine("Tracks (" + libraryEntryCount + "): " + tracks);
            List<Playlist> playlists = mapToPlaylists((Array) properties.get("Playlists"));
            log.fine("Playlists (" + playlists.size() + "): " + playlists);

            process(tracks);
            // since playlists are removed anyway...
            // Set<Track> accessibleTracks = process(tracks);
            // process(playlists, accessibleTracks);
            removePlaylists(plist);

            File modifiedLibrary = new File(Files.replaceExtension(getLibrary().getAbsolutePath(), "import.xml"));
            log.info("Writing iTunes library to '" + modifiedLibrary.getAbsolutePath() + "'");
            iTunesXMLLibrary.marshal("", "plist", Plist.class, plist, new FileOutputStream(modifiedLibrary));
        } catch (JAXBException e) {
            log.severe("Cannot parse iTunes library '" + getLibrary() + "': " + e.getMessage());
        } catch (FileNotFoundException e) {
            log.severe("iTunes library '" + getLibrary() + "' not found: " + e.getMessage());
        } finally {
            log.info("Modified " + modifiedFileCount + " out of " + fileCount + " files");
            log.info("Modified " + modifiedLibraryEntryCount + " iTunes library entries out of " + libraryEntryCount);
            log.info("Removed " + removedLibraryEntryCount + " iTunes library entries out of " + libraryEntryCount);
        }
    }

    protected void process(List<Playlist> playlists, Set<Track> accessibleTracks) {
        for (Playlist playlist : playlists) {
            remove(playlist, accessibleTracks);
        }
    }

    protected Set<Track> process(List<Track> tracks) {
        Set<Track> accessibleTracks = new HashSet<Track>();
        for (Track track : tracks) {
            if (!canProcess(track)) {
                removedLibraryEntryCount++;
                remove(track);
            } else {
                log.info("Processed " + (++fileCount) + ". from " + libraryEntryCount + " tracks with id: " + track.getId());
                accessibleTracks.add(track);
                process(track);
            }
        }
        return accessibleTracks;
    }

    protected boolean canProcess(Track track) {
        URL location = track.getLocation();
        if (location == null) {
            log.warning("Cannot process location-less track " + track);
            return false;
        }

        if (!"file".equals(location.getProtocol())) {
            log.warning("Cannot process non-file track " + location);
            return false;
        }

        File file = new File(location.getFile());
        if (!file.exists()) {
            log.warning("Cannot process not-existing file " + file);
            return false;
        }
        return true;
    }

    protected boolean remove(Track track) {
        boolean result = track.delete();
        if (result) {
            log.info("Removed track " + track.getId() + " for not-existing file " + track.getLocation());
        }
        return result;
    }

    protected boolean remove(Playlist playlist, Set<Track> accessibleTracks) {
        int result = playlist.delete(accessibleTracks);
        if (result > 0)
            log.info("Removed " + result + " unaccessible tracks from play list " + playlist.getName());
        return result > 0;
    }

    protected void process(Track track) {
        URL location = track.getLocation();
        File file = new File(location.getFile());

        // TODO avoid parsing the MP3 by comparing file and library modification timestamps
        if (track.getSynchronizationTime() != null) {
            if (track.getSynchronizationTime().getTimeInMillis() < file.lastModified()) {
                log.info("File modified after library synchronization " + track.getSynchronizationTime() +
                        " diff:" + (track.getSynchronizationTime().getTimeInMillis() - file.lastModified()));
            }
            if (track.getModificationTime() != null && track.getSynchronizationTime().getTimeInMillis() < track.getModificationTime().getTimeInMillis()) {
                log.info("Library synchronization after file modified " + track.getSynchronizationTime() +
                        " diff:" + (track.getSynchronizationTime().getTimeInMillis() - track.getModificationTime().getTimeInMillis()));
            }
        }

        MP3File mp3 = MP3File.readValidFile(file);
        if (mp3 != null)
            process(track, mp3);
        else
            log.warning("Could not read " + file.getAbsolutePath());
    }

    protected void process(Track track, MP3File mp3) {
        boolean fileModified = false, libraryEntryModified = false;

        int mp3Rating = mp3.getHead().getRating();
        if (mp3Rating > 0 && track.getRating() == null) {
            log.info("Modifying library rating to " + mp3Rating);
            track.setRating(mp3Rating);
            libraryEntryModified = true;
        }
        int iTunesRating = track.getRating() != null ? track.getRating() : 0;
        if (iTunesRating > 0 && iTunesRating != mp3Rating) {
            log.info("Modifying rating from '" + mp3.getFile().getAbsolutePath() + "' to " + iTunesRating + " from " + mp3Rating);
            mp3.getHead().setRating(iTunesRating);
            fileModified = true;
        }

        if (isAddPlayCount()) {
            int mp3PlayCount = mp3.getHead().getPlayCount();
            if (mp3PlayCount < 0)
                mp3PlayCount = 0;
            int iTunesPlayCount = track.getPlayCount() != null ? track.getPlayCount() : 0;

            int playCount = mp3PlayCount + iTunesPlayCount;
            if (playCount > 0) {
                log.info("Adding library play count to " + playCount + " from " + iTunesPlayCount);
                track.setPlayCount(playCount);
                libraryEntryModified = true;
                log.info("Adding play count of '" + mp3.getFile().getAbsolutePath() + "' to " + playCount + " from " + mp3PlayCount);
                mp3.getHead().setPlayCount(playCount);
                fileModified = true;
            }

        } else {
            int mp3PlayCount = mp3.getHead().getPlayCount();
            if (mp3PlayCount > 0 && (track.getPlayCount() == null || mp3PlayCount > track.getPlayCount())) {
                int iTunesPlayCount = track.getPlayCount() != null ? track.getPlayCount() : 0;
                log.info("Modifying library play count to " + mp3PlayCount + " from " + iTunesPlayCount);
                track.setPlayCount(mp3PlayCount);
                libraryEntryModified = true;
            }

            int iTunesPlayCount = track.getPlayCount() != null ? track.getPlayCount() : -1;
            if (iTunesPlayCount > mp3PlayCount) {
                log.info("Modifying play count of '" + mp3.getFile().getAbsolutePath() + "' to " + iTunesPlayCount + " from " + mp3PlayCount);
                mp3.getHead().setPlayCount(iTunesPlayCount);
                fileModified = true;
            }
        }

        Calendar mp3PlayTime = mp3.getHead().getPlayTime();
        if (mp3PlayTime != null && (track.getPlayTime() == null || mp3PlayTime.after(track.getPlayTime()))) {
            log.info("Modifying library play time to " + DATE_FORMAT.format(mp3PlayTime.getTime()));
            track.setPlayTime(mp3PlayTime);
            libraryEntryModified = true;
        }

        Calendar iTunesPlayTime = track.getPlayTime();
        if (iTunesPlayTime != null && (mp3PlayTime == null || iTunesPlayTime.after(mp3PlayTime))) {
            log.info("Modifying play time from '" + mp3.getFile().getAbsolutePath() + "' to " + DATE_FORMAT.format(iTunesPlayTime.getTime()) +
                    " from " + (mp3PlayTime != null ? DATE_FORMAT.format(mp3PlayTime.getTime()) : "<null>"));
            mp3.getHead().setPlayTime(iTunesPlayTime);
            fileModified = true;
        }

        track.setSynchronizationTime(Calendar.getInstance());

        if (fileModified) {
            modifiedFileCount++;

            log.info("Writing '" + mp3.getFile().getAbsolutePath());
            // TODO make this dependant on user interaktion or profile later
            mp3.setID3v2(true);
            mp3.setID3v1(false);
            try {
                mp3.write();
            } catch (Exception e) {
                log.severe("Could not write mp3 " + mp3 + ": " + e.getMessage());
            }
        }

        if (libraryEntryModified)
            modifiedLibraryEntryCount++;
    }

    protected void removePlaylists(Plist plist) {
        int removeIndex = -1;
        List<Object> objects = plist.getDict().getKeyAndArrayOrData();
        for (int i = 0; i < objects.size(); i += 2) {
            JAXBElement key = (JAXBElement) objects.get(i);
            if ("Playlists".equals(key.getValue())) {
                removeIndex = i;
            }
        }
        if (removeIndex != -1) {
            objects.remove(removeIndex + 1);
            objects.remove(removeIndex);
        }
    }
}
