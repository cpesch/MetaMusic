/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2005 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.tools;

import slash.metamusic.coverdb.LastFmCoverClient;
import slash.metamusic.coverdb.WindowsMediaPlayerCoverClient;
import slash.metamusic.mp3.ID3Genre;
import slash.metamusic.mp3.ID3v2Frame;
import slash.metamusic.mp3.ID3v2Header;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.StringHelper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Logger;

/**
 * A class to
 * <ul>
 * <li>remove RIFF etc. prefixes from MP3 files</li>
 * <li>remove iTunes tags</li>
 * <li>remove MusicBrainz tags</li>
 * <li>remove MusicMatch tags</li>
 * <li>remove Windows Media Player tags</li>
 * <li>remove Windows Media Player cover files</li>
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: MP3Cleaner.java 944 2007-01-10 17:12:53Z cpesch $
 */

public class MP3Cleaner extends BaseMP3Modifier {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(MP3Cleaner.class.getName());

    private static final String MUSICBRAINZ_TRM_ID_PREFIX = "\u0000musicbrainz trm id\u0000";
    /**
     * Length of a MusicBrain Artist/Album/Track Id
     */
    private static final int MB_ID_LENGTH = 36;
    private static final int MUSICBRAINZ_TAG_ID_REQUIRED_LENGTH =
            MUSICBRAINZ_TRM_ID_PREFIX.length() + MB_ID_LENGTH;
    private static final String WMP_PROVIDER_PREFIX = "WM/Provider";

    private static final Set<String> ITUNES_TAGS_TO_REMOVE = new TreeSet<String>();

    static {
        ITUNES_TAGS_TO_REMOVE.addAll(Arrays.asList("TCMP", "TSO2", "TSOC"));
    }

    private static final Set<String> TAGS_TO_REMOVE = new TreeSet<String>();

    static {
        TAGS_TO_REMOVE.addAll(Arrays.asList("GEOB", "MCDI", "NCON", "PRIV", "TCOP", "TFLT",
                "TMED", "TOPE", "TORY", "TSSE", "TXXX", "XDOR", "XSOP", "WCOM", "WOAF", "WOAR", "WXXX"));
    }


    private boolean removeiTunesTags = false, removeMusicBrainzTags = true, removeMusicMatchTags = true,
            removeWindowsMediaPlayerTags = true, unifyTags = true;

    public boolean isRemoveiTunesTags() {
        return removeiTunesTags;
    }

    public void setRemoveiTunesTags(boolean removeiTunesTags) {
        this.removeiTunesTags = removeiTunesTags;
    }

    public boolean isRemoveMusicBrainzTags() {
        return removeMusicBrainzTags;
    }

    public void setRemoveMusicBrainzTags(boolean removeMusicBrainzTags) {
        this.removeMusicBrainzTags = removeMusicBrainzTags;
    }

    public boolean isRemoveMusicMatchTags() {
        return removeMusicMatchTags;
    }

    public void setRemoveMusicMatchTags(boolean removeMusicMatchTags) {
        this.removeMusicMatchTags = removeMusicMatchTags;
    }

    public boolean isRemoveWindowsMediaPlayerTags() {
        return removeWindowsMediaPlayerTags;
    }

    public void setRemoveWindowsMediaPlayerTags(boolean removeWindowsMediaPlayerTags) {
        this.removeWindowsMediaPlayerTags = removeWindowsMediaPlayerTags;
    }

    public boolean isUnifyTags() {
        return unifyTags;
    }

    public void setUnifyTags(boolean unifyTags) {
        this.unifyTags = unifyTags;
    }


    /**
     * Clean the given file, i.e.
     * <ul>
     * <li>remove RIFF etc. prefixes from the file and</li>
     * <li>convert its file name to filesystem conventions.</li>
     * </ul>
     *
     * @param file the {@link File} to operate on
     * @throws IOException if something wents wrong
     */
    public void clean(File file) throws IOException {
        MP3File mp3 = MP3File.readValidFile(file);
        if (mp3 == null) {
            throw new IOException("Invalid MP3 file " + file.getAbsolutePath());
        }
        clean(mp3);
    }

    /**
     * Clean the given MP3 file, i.e.
     * <ul>
     * <li>remove RIFF etc. prefixes from the file and</li>
     * <li>convert its file name to filesystem conventions.</li>
     * </ul>
     *
     * @param file the {@link MP3File} to operate on
     * @throws IOException if something wents wrong
     */
    public void clean(MP3File file) throws IOException {
        boolean haveToWrite = cleanTags(file);

        if (haveToWrite) {
            write(file);
            log.info("Cleaned " + file.getFile().getAbsolutePath());
        }
    }

    public boolean cleanTags(MP3File file) throws IOException {
        boolean removedPrefix = removePrefix(file);
        boolean removediTunesTags = isRemoveiTunesTags() && removeiTunesTags(file);
        boolean removedMusicBrainzTags = isRemoveMusicBrainzTags() && removeMusicBrainzTags(file);
        boolean removedMusicMatchTags = isRemoveMusicMatchTags() && removeMusicMatchTags(file);
        boolean removedWindowsMediaPlayerTags = isRemoveWindowsMediaPlayerTags() && removeWindowsMediaPlayerTags(file);
        boolean unifiedTagContent = isUnifyTags() && unifyTagContent(file);
        boolean removedRedundantTags = isUnifyTags() && removeRedundantTags(file);

        boolean cleaned = removedPrefix || removediTunesTags || removedMusicBrainzTags || removedMusicMatchTags ||
                removedWindowsMediaPlayerTags || unifiedTagContent || removedRedundantTags;

        log.fine("cleaned: " + cleaned + " prefix: " + removedPrefix +
                " iTunes: " + removediTunesTags + " MusicBrainz: " + removedMusicBrainzTags +
                " MusicMatch: " + removedMusicMatchTags + " WindowsMediaPlayer: " + removedWindowsMediaPlayerTags +
                " unified tags: " + unifiedTagContent + " removed tags: " + removedRedundantTags);
        return cleaned;
    }

    public boolean removePrefix(MP3File file) throws IOException {
        long prefixSize = file.getProperties().getReadSize() - file.getHead().getReadSize();
        if (prefixSize > 0) {
            log.info("Found prefix of " + prefixSize + " bytes, removing it");
            return true;
        }
        return false;
    }

    public boolean removeMusicBrainzTags(MP3File file) {
        List<ID3v2Frame> removeHeaders = new ArrayList<ID3v2Frame>();
        String trmId = null, releaseDate = null, sortName = null;

        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String content = f.getStringContent();
            // TXXX [User defined text information frame]:  MusicBrainz TRM Id 01d6885d-1b52-47f0-9b67-55881d66f0da<56 bytes>
            // TXXX [User defined text information frame]:  MusicBrainz Artist Id b2dbfc09-b332-408b-a235-1850e41971c5<59 bytes>
            // TXXX [User defined text information frame]:  MusicBrainz Album Id ef663a55-2c1a-4c71-8324-5bca1f88a644<58 bytes>
            // TXXX [User defined text information frame]:  MusicBrainz Album Type album<29 bytes>
            // TXXX [User defined text information frame]:  MusicBrainz Album Status official<34 bytes>
            // TXXX [User defined text information frame]:  MusicBrainz Album Artist Id <29 bytes>
            if (f.getTagName().equals("TXXX") && content.toLowerCase().startsWith("\u0000musicbrainz")) {
                if (content.toLowerCase().startsWith(MUSICBRAINZ_TRM_ID_PREFIX) &&
                        content.length() >= MUSICBRAINZ_TAG_ID_REQUIRED_LENGTH) {
                    trmId = content.substring(MUSICBRAINZ_TRM_ID_PREFIX.length(), MUSICBRAINZ_TAG_ID_REQUIRED_LENGTH);
                }
                removeHeaders.add(f);

            } else if (f.getTagName().equals("XDOR")) {
                releaseDate = content;
                removeHeaders.add(f);

            } else if (f.getTagName().equals("XSOP")) {
                sortName = content;
                removeHeaders.add(f);
            }
        }

        for (ID3v2Frame f : removeHeaders) {
            head.remove(f);
        }

        if (trmId != null && head.getMusicBrainzId() == null) {
            log.info("Transferring MusicBrainzId " + trmId);
            head.setMusicBrainzId(trmId);
        }

        if (releaseDate != null && releaseDate.length() > 0) {
            if (head.getFrame("TDRL") == null) {
                log.info("Transferring MusicBrainz release date " + releaseDate);
                ID3v2Frame f = head.addID3v2Frame("TDRL");
                f.setText(releaseDate);
            }
            if (file.getYear() == -1) {
                int releaseYear = parseYear(releaseDate);
                if (releaseYear != .1) {
                    log.info("Transferring MusicBrainz release year " + releaseYear);
                    file.setYear(releaseYear);
                }
            }
        }

        if (sortName != null && sortName.length() > 0) {
            if (head.getFrame("TSOP") == null) {
                log.info("Transferring MusicBrainz sort name " + sortName);
                ID3v2Frame f = head.addID3v2Frame("TSOP");
                f.setText(sortName);
            }
        }

        boolean result = removeHeaders.size() > 0;
        if (result)
            log.info("Removed " + removeHeaders.size() + " MusicBrainz headers");
        return result;
    }

    public boolean removeMusicMatchTags(MP3File file) {
        List<ID3v2Frame> removeHeaders = new ArrayList<ID3v2Frame>();

        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String content = f.getStringContent();
            if (f.getTagName().equals("COMM")) {
                // COMM [Comments]: English(eng),MusicMatch_Tempo,Moderate
                // COMM [Comments]: English(eng),MusicMatch_Preference,None
                if (content.contains("MusicMatch_Tempo") || content.contains("MusicMatch_Preference")) {
                    removeHeaders.add(f);
                }
            }
        }

        for (ID3v2Frame f : removeHeaders) {
            head.remove(f);
        }

        boolean result = removeHeaders.size() > 0;
        if (result)
            log.info("Removed " + removeHeaders.size() + " MusicMatch headers");
        return result;
    }

    public boolean removeWindowsMediaPlayerTags(MP3File file) {
        List<ID3v2Frame> removeHeaders = new ArrayList<ID3v2Frame>();
        String publisher = null;

        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String content = f.getStringContent();
            // PRIV [Private frame]: WM/MediaClassPrimaryID...<39 bytes>
            // PRIV [Private frame]: WM/MediaClassSecondaryID...<41 bytes>
            // PRIV [Private frame]: WM/Provider...<20 bytes>
            // PRIV [Private frame]: WM/UniqueFileIdentifier...<138 bytes>
            // PRIV [Private frame]: WM/WMCollectionID...<34 bytes>
            // PRIV [Private frame]: WM/WMCollectionGroupID...<39 bytes>
            // PRIV [Private frame]: WM/WMContentID...<31 bytes>
            // PRIV [Private frame]: PeakValue ...<14 bytes>
            // PRIV [Private frame]: AverageLevel ...<17 bytes>
            if (f.getTagName().equals("PRIV")) {
                if (content.startsWith(WMP_PROVIDER_PREFIX)) {
                    String temp = content.substring(WMP_PROVIDER_PREFIX.length());
                    int strip = temp.indexOf('<') - 1;
                    if (strip > 0 && strip < temp.length())
                        temp = temp.substring(0, strip);
                    try {
                        publisher = new String(temp.getBytes(), "UTF16");
                        publisher = StringHelper.trim(publisher);
                    } catch (UnsupportedEncodingException e) {
                        // cannot happen as UTF16 is builtin
                    }
                }
                // move tags to regular ID3v2 tags as shown on http://www.adhitsoft.com/jsp/product_pgvtag_tagmap.jsp?
                if (content.startsWith("WM/") || content.startsWith("AverageLevel") ||
                        content.startsWith("PeakValue")) {
                    removeHeaders.add(f);
                }
            }
        }

        for (ID3v2Frame f : removeHeaders) {
            head.remove(f);
        }

        if (publisher != null && file.getHead().getPublisher() == null) {
            log.info("Transferring publisher " + publisher);
            file.getHead().setPublisher(publisher);
        }

        boolean result = removeHeaders.size() > 0;
        if (result)
            log.info("Removed " + removeHeaders.size() + " Windows Media Player headers");
        return result;
    }

    public boolean removeiTunesTags(MP3File file) {
        List<ID3v2Frame> removeHeaders = new ArrayList<ID3v2Frame>();

        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String name = f.getTagName();
            if (name.equals("COMM")) {
                String description = f.getDescription();
                if (description.startsWith("iTun")) {
                    removeHeaders.add(f);
                }
            }
            if (ITUNES_TAGS_TO_REMOVE.contains(name)) {
                removeHeaders.add(f);
            }
        }

        for (ID3v2Frame f : removeHeaders) {
            head.remove(f);
        }

        boolean result = removeHeaders.size() > 0;
        if (result)
            log.info("Removed " + removeHeaders.size() + " iTunes headers");
        return result;
    }

    public boolean removeRedundantTags(MP3File file) {
        List<ID3v2Frame> removeHeaders = new ArrayList<ID3v2Frame>();

        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String content = f.getStringContent();
            if (f.getTagName().equals("TBPM") && "0".equals(content)) {
                removeHeaders.add(f);
            }
            if (f.getTagName().equals("TOPE") && "".equals(content)) {
                removeHeaders.add(f);
            }
            if (f.getTagName().equals("TCON") && ("(0)".equals(content) || content.startsWith(ID3Genre.UNKNOWN))) {
                removeHeaders.add(f);
            }
            if (f.getTagName().equals("COMM")) {
                // TODO this is for some very broken files
                if (!("iTunNORM".equals(f.getDescription()) || "Written".equals(f.getDescription()))) {
                    removeHeaders.add(f);
                }
            }
            if (TAGS_TO_REMOVE.contains(f.getTagName())) {
                removeHeaders.add(f);
            }
        }

        for (ID3v2Frame f : removeHeaders) {
            head.remove(f);
        }

        String artist = file.getArtist();
        String band = file.getHead().getBand();
        if (artist != null && band != null && artist.equals(band)) {
            log.info("Removing band " + band + " since its the same as artist");
            head.removeID3v2Frame("TPE2");
        }

        String track = file.getTrack();
        String group = file.getHead().getStringContent("TIT1");
        if (track != null && group != null && track.equals(group)) {
            log.info("Removing content group description " + group + " since its the same as track");
            head.removeID3v2Frame("TIT1");
        }

        boolean result = removeHeaders.size() > 0;
        if (result)
            log.info("Removed " + removeHeaders.size() + " irrelevant headers");
        return result;
    }

    private int parseYear(String year) {
        try {
            if (year != null) {
                if (year.length() > 4)
                    year = year.substring(0, 4);
                return Integer.parseInt(year);
            }
        }
        catch (NumberFormatException e) {
            // don't care
        }
        return -1;
    }

    public boolean unifyTagContent(MP3File file) {
        int count = 0;
        int releaseYear = -1;
        String encoder = null;

        ID3v2Header head = file.getHead();
        for (ID3v2Frame f : head.getFrames()) {
            String content = f.getStringContent();
            if (f.getTagName().equals("TLAN") && content.toLowerCase().equals("eng")) {
                f.setText("English");
                count++;
            }
            if (f.getTagName().equals("TORY")) {
                releaseYear = parseYear(content);
                count++;
            }
            if (f.getTagName().equals("TSSE")) {
                encoder = content;
                count++;
            }
        }

        if (releaseYear != -1 && file.getYear() == -1) {
            log.info("Transferring release year " + releaseYear);
            file.setYear(releaseYear);
        }

        if (encoder != null && head.getFrame("TENC") == null) {
            log.info("Transferring encoder " + encoder);
            ID3v2Frame f = head.addID3v2Frame("TENC");
            f.setText(encoder);
        }

        boolean result = count > 0;
        if (result)
            log.info("Unified " + count + " headers");
        return result;
    }

    public void removeCovers(File file) {
        removeWindowsMediaPlayerCovers(file);
        removeLastFmCovers(file);
    }

    private void removeWindowsMediaPlayerCovers(File file) {
        WindowsMediaPlayerCoverClient wmpClient = new WindowsMediaPlayerCoverClient();
        wmpClient.removeCover(file);
    }

    private void removeLastFmCovers(File file) {
        LastFmCoverClient lfClient = new LastFmCoverClient();
        lfClient.removeCover(file);
    }


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.mp3.tools.MP3Cleaner <file>");
            System.exit(1);
        }

        File file = new File(args[0]);
        MP3Cleaner cleaner = new MP3Cleaner();
        cleaner.clean(file);
        cleaner.removeCovers(file);
        System.exit(0);
    }
}
