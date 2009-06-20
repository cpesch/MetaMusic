/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

/*
 * ---------------------------------------------------------
 * Antelmann.com Java Framework by Holger Antelmann
 * Copyright (c) 2002 Holger Antelmann <info@antelmann.com>
 * For details, see also http://www.antelmann.com/developer/
 * ---------------------------------------------------------
 */

package slash.metamusic.freedb;

import slash.metamusic.discid.DiscId;
import slash.metamusic.mp3.ID3Genre;
import slash.metamusic.util.StringHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * CDDBXmcdParser provides methods to read the file content
 * from a FreeDB raw file in xmcd format.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: CDDBXmcdParser.java 743 2006-03-17 13:49:36Z cpesch $
 */

class CDDBXmcdParser {
    private String content;

    /**
     * The content must be derived from FreeDB raw file in xmcd format.
     */
    public CDDBXmcdParser(String content) throws ParseException {
        this.content = content;
        String e = checkForErrors();
        if (e != null)
            throw new ParseException(e, 0);
    }

    /**
     * checks for consistency and returns either a string with
     * the problem found or null if the file is found to be ok
     */
    String checkForErrors() {
        try {
            String tmp;
            if (content.indexOf("# xmcd") != 0) {
                return ("not a xmcd file (CDDB format); xmcd tag is missing");
            }
            StringTokenizer st = new StringTokenizer(content, "\r\n");
            int lcount = 0;
            while (st.hasMoreTokens()) {
                lcount++;
                String line = st.nextToken();
                if (line.length() > 254) {
                    return ("line " + lcount + " has too many characters");
                }
                if (line.equals("") && st.hasMoreTokens()) {
                    return ("illegal empty line at " + lcount);
                }
            }
            String[] ids = readDiscIds();
            if (ids.length < 1) return "no disc id found";
            for (int i = 0; i < ids.length; i++) {
                tmp = ids[i];
                if ((tmp == null) || (tmp.length() != 8)) {
                    return "disc id " + i + " (" + tmp + ") invalid";
                }
            }
            tmp = readAlbum();
            if ((tmp == null) || (tmp.length() < 1)) return "no CD track found";
            tmp = getTagText("DYEAR");
            try {
                Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                if (!tmp.equals("")) return "year is not readable";
            }
            int[] os = readOffsets();
            if (os.length < 1) return "no track offsets found";
            for (int i = 0; i < os.length; i++) {
                readTrackAlbum(i);
                readTrackExtension(i);
                readTrackArtist(i);
            }
            readGenre();
            readLength();
            readRevision();
            readSubmitter();
            readProcessedBy();
            readPlayOrder();
            try {
                readYear();
            } catch (XmcdFormatException e) {
                if (getTagText("DYEAR").length() > 0) {
                    return "the year value is not readable";
                }
            }
            if (count(content, "\nDYEAR=") != 1) {
                return "multiple year entries present";
            }
            readGenre();
            readExtension();
            readPlayOrder();
        } catch (Exception e) {
            return e.getMessage();
        }
        return null;
    }


    /**
     * counts how many times the given pattern occurs in the given text.
     * Example: <code>count("ababababab", "abab")</code> returns 2.
     */
    public static int count(String text, String pattern) {
        int count = 0;
        int pos = text.indexOf(pattern);
        while (pos > -1) {
            count++;
            pos = text.indexOf(pattern, pos + pattern.length());
        }
        return count;
    }


    /**
     * Return the content in xcmd format (used during construction)
     */
    public String getContent() {
        return content;
    }

    /**
     * checks the xmcd file for completeness (this should return null before
     * the underlying content would be subject to a CDDB submission).
     *
     * @return an array of warnings discovered during processing
     *         or null if no warnings present
     */
    public String[] checkWarnings() {
        List<String> warnings = new ArrayList<String>();
        String tmp = readAlbum();
        if (tmp.indexOf(" / ") < 1) {
            warnings.add("Artist and CD track are not properly separated");
        } else if (tmp.indexOf(" / ") != tmp.lastIndexOf(" / ")) {
            warnings.add("multiple ' / ' encountered in track");
        }
        if (readGenre().length() < 1)
            warnings.add("no genre entered");
        try {
            int year = readYear();
            if ((year < 1900) || (year > (new java.util.GregorianCalendar().get(java.util.Calendar.YEAR) + 1))) {
                warnings.add("the year seems a bit off; you entered " + year);
            }
        } catch (XmcdFormatException e) {
            warnings.add("no year value entered");
        }
        int[] os = readOffsets();
        for (int i = 0; i < os.length; i++) {
            if (readTrackAlbum(i).length() < 1) {
                warnings.add("no track added for track " + i);
            }
        }
        if (warnings.size() == 0) return null;
        return warnings.toArray(new String[warnings.size()]);
    }


    /**
     * Parses for the offsets and returns a DiscId object using the first disc id
     * found in the DISCID field
     */
    public DiscId readDiscId() {
        return new DiscId(readDiscIds()[0], readNumberOfTracks(), readOffsets(), readLength(), true);
    }

    /**
     * Returns the number of tracks
     */
    public int readNumberOfTracks() {
        return readOffsets().length;
    }

    /**
     * Parses for the track offsets
     */
    public int[] readOffsets() {
        int pos = content.indexOf("\n# Track frame offsets:");
        StringTokenizer st = new StringTokenizer(content.substring(pos), "\r\n");
        List<Integer> offsets = new ArrayList<Integer>();
        st.nextToken(); // skip over the beginning line
        while (st.hasMoreTokens()) {
            String line = st.nextToken().substring(1).trim();
            try {
                offsets.add(new Integer(line));
            } catch (NumberFormatException e) {
                break;
                /*
           the lines below would actually be correct, but again,
           CDDB entries do not always have that blank comment line
        */
                //if (line.equals("")) {
                //    break;
                //} else {
                //    throw new XmcdFormatException(
                //        "track offset could not be read as a number");
                //}
            }
        }
        int[] os = new int[offsets.size()];
        for (int i = 0; i < os.length; i++) {
            os[i] = offsets.get(i);
        }
        return os;
    }


    /**
     * Parses for the disc length and returns the seconds
     */
    public int readLength() {
        String tag = "\n# Disc length:";
        String s;
        try {
            s = parseTag(tag, "seconds", 0);
        } catch (XmcdFormatException e) {
            // this is an inconsistency widely found in CDDB entries
            // that is against their own specs, I'm afraid
            try {
                s = parseTag(tag, "secs", 0);
            } catch (XmcdFormatException x) {
                // I've even found several CDDB entries without the seconds
                s = parseTag(tag, "\n", 0);
            }
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new XmcdFormatException("Disc length tag not a proper number", null, null);
        }
    }


    /**
     * Parses for the revision number of the entry
     */
    public int readRevision() {
        String tag = "\n# Revision: ";
        try {
            return Integer.parseInt(parseTag(tag, "\n", 0));
        } catch (NumberFormatException e) {
            throw new XmcdFormatException("Revision tag not a proper number", null, null);
        }
    }


    /**
     * Parses for the "Submitted via" entry
     */
    public String readSubmitter() {
        return parseTag("\n# Submitted via: ", "\n", 0);
    }


    /**
     * Parses for the "Processed by" entry (an optional entry)
     */
    public String readProcessedBy() {
        try {
            return parseTag("\n# Processed by: ", "\n", 0);
        } catch (XmcdFormatException e) {
            return null;
        }
    }


    /**
     * Parses for possibly multiple disc ids to support
     * searching for disc ids that link to the same file
     */
    public String[] readDiscIds() {
        String line = getTagText("DISCID");
        StringTokenizer st = new StringTokenizer(line, ", ");
        List<String> discIds = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            discIds.add(st.nextToken());
        }
        return discIds.toArray(new String[discIds.size()]);
    }


    /**
     * Parses for the full track
     */
    public String readAlbum() {
        return getTagText("DTITLE");
    }


    /**
     * Parses the track for the part after ' / ' which by convention refers
     * to the CD track; if no track separator is found, the full track
     * (same as <code>readAlbum()</code>) is returned
     */
    public String readCDAlbum() {
        String t = readAlbum();
        int i = t.indexOf(" / ");
        if (i < 0) {
            return t;
        } else {
            return t.substring(i + 3);
        }
    }


    /**
     * Parses the track for the part before ' / ' which refers
     * to the artist specified as &lt;firstName lastName&gt; by convention;
     * null is returned if no artist separator could be found
     */
    public String readCDArtist() {
        String t = readAlbum();
        int i = t.indexOf(" / ");
        if (i < 0) {
            return null;
        } else {
            return t.substring(0, i);
        }
    }


    /**
     * Parses for extended CD information
     */
    public String readExtension() {
        return getTagText("EXTD");
    }

    /**
     * Parses for ID3 Genre information. Returns -1 if no genre was found.
     */
    public int readExtdGenre() {
        try {
            String s = parseTag("ID3G:", "\n", 0);
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Parses for extended year; returns -1 if no year was entered
     */
    public int readExtdYear() {
        try {
            String s = parseTag("YEAR:", "\n", 0);
            return Integer.parseInt(s);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Parses for the year; returns -1 if no year was entered
     */
    public int readYear() {
        try {
            return Integer.parseInt(getTagText("DYEAR"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }


    /**
     * Parses for the genre
     */
    public String readGenre() {
        return getTagText("DGENRE");
    }


    /**
     * Parses for the entire track title (including artist if available)
     */
    public String readAlbum(int track) {
        return getTagText("TTITLE" + track);
    }


    private String removePrefixNumber(String string, char prefixDivider) {
        int pos = string.indexOf(prefixDivider);
        if (pos > 0) {
            String beforeDivider = string.substring(0, pos).trim();
            String afterDivider = string.substring(pos + 1).trim();
            if (StringHelper.isANumber(beforeDivider))
                return removePrefixNumber(afterDivider, prefixDivider);
            if (StringHelper.isANumber(afterDivider))
                return removePrefixNumber(beforeDivider, prefixDivider);
        }
        return string;
    }

    private String removeTrackIndices(String string) {
        return removePrefixNumber(removePrefixNumber(string, '/'), '-');
    }

    /**
     * Parses for the track with the given index
     * (without artist info if applicable).
     * <p/>
     * Track artists may be prefixed to track titles and separated
     * with " - " or " / ".
     */
    public String readTrackAlbum(int trackNumber) {
        String album = removeTrackIndices(readAlbum(trackNumber));

        // The artist / The track album
        int pos = album.indexOf(" / ");
        if (pos > 0)
            return album.substring(pos + 3);

        // The artist - The track album
        pos = album.indexOf(" - ");
        if (pos > 0)
            return album.substring(pos + 3);

        return album;
    }

    /**
     * Parses for the artist of the track with the given index
     * (using first the track artist info, then the CD artist info).
     * <p/>
     * Track artists may be prefixed to track titles and separated
     * with " - " or " / ".
     */
    public String readTrackArtist(int trackNumber) {
        String album = removeTrackIndices(readAlbum(trackNumber));
        // The artist / The track album
        int pos = album.indexOf(" / ");
        if (pos > 0)
            return album.substring(0, pos);
        // The artist - The track album
        pos = album.indexOf(" - ");
        if (pos > 0)
            return album.substring(0, pos);

        return readCDArtist();
    }


    /**
     * Parses for extended track info
     */
    public String readTrackExtension(int track) {
        return getTagText("EXTT" + track);
    }


    /**
     * Parses for the play order
     */
    public int[] readPlayOrder() {
        try {
            StringTokenizer st = new StringTokenizer(getTagText("PLAYORDER"), ",\n\r\t ");
            int[] list = new int[st.countTokens()];
            for (int i = 0; i < list.length; i++) {
                list[i] = Integer.parseInt(st.nextToken());
            }
            return list;
        } catch (Exception e) {
            XmcdFormatException ex = new XmcdFormatException("could not extract play order", null, null);
            ex.initCause(e);
            throw ex;
        }
    }

    /**
     * Replaces every occurrence of oldSubString with
     * newSubString within the original String and returns the
     * resulting string (no regular expressions are used).
     */
    static String replace(String original, String oldSubString, String newSubString) {
        String s = original;
        int pointer = s.indexOf(oldSubString);
        while (pointer > -1) {
            s = s.substring(0, pointer) + newSubString
                    + s.substring(pointer
                    + oldSubString.length(), s.length());
            pointer = s.indexOf(oldSubString, pointer + newSubString.length());
        }
        return s;
    }

    static String translate(String txt) {
        txt = replace(txt, "\\n", "\n");
        txt = replace(txt, "\\t", "\t");
        txt = replace(txt, "\\\\", "\\");
        return txt;
    }

    /**
     * Returns the text for the tag that could be found in multiple lines
     */
    protected String getTagText(String tag) throws XmcdFormatException {
        String ftag = "\n" + tag + "=";
        int pos = content.indexOf(ftag);
        if (pos < 0) throw new XmcdFormatException("tag \"" + tag + "\" not found", null, null);
        String result = "";
        while (pos > -1) {
            result += content.substring(pos + tag.length() + 2,
                    content.indexOf("\n", pos + tag.length() + 2)).trim();
            pos = content.indexOf(ftag, pos + tag.length() + 2);
        }
        return translate(result.trim());
    }

    /**
     * Returns the text between btag and etag after index;
     * leading and tailing white space is removed
     */
    protected String parseTag(String btag, String etag, int index) throws XmcdFormatException {
        int begin = content.indexOf(btag);
        if (begin < 1)
            throw new XmcdFormatException("begin tag \"" + btag + "\" not found", null, null);
        int end = content.indexOf(etag, begin + btag.length());
        if (end < 1)
            throw new XmcdFormatException("end tag \"" + etag + "\" after \"" + btag + "\" not found", null, null);
        return content.substring(begin + btag.length(), end).trim();
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString() + "[");
        buffer.append("artist=").append(readCDArtist());
        buffer.append(", CD album=").append(readCDAlbum());
        buffer.append(", disc id=").append(readDiscId());
        buffer.append(", disc ids=");
        String[] discIds = readDiscIds();
        for (String discId : discIds) {
            buffer.append(discId).append(",");
        }
        buffer.append(" extension=").append(readExtension());
        buffer.append(", genre=").append(readGenre());
        buffer.append(", ID3 genre=").append(readExtdGenre()).append("/").append(ID3Genre.getGenreName(readExtdGenre()));
        buffer.append(", length=").append(readLength()).append(" seconds");
        buffer.append(", tracks=").append(readNumberOfTracks());
        // offsets, playorder
        buffer.append(", processed by=").append(readProcessedBy());
        buffer.append(", revision=").append(readRevision());
        buffer.append(", submitter=").append(readSubmitter());
        buffer.append(",\n tracks=[");
        for (int i = 0; i < readNumberOfTracks(); i++) {
            buffer.append(i).append(". track album=").append(readTrackAlbum(i));
            buffer.append(", extension=").append(readTrackExtension(i));
            buffer.append(", artist=").append(readTrackArtist(i)).append(", ");
        }
        buffer.append("]]");
        return buffer.toString();
    }
}
