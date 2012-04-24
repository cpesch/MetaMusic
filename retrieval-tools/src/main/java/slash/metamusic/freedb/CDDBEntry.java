/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2004 Christian Pesch. All Rights Reserved.
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

import java.io.IOException;
import java.text.ParseException;

/**
 * CDDBEntry represents an entry for a CD in a CDDB database
 * that contains all known properties about the associated CD.
 * <p/>
 * CDDBEntry also contains methods to generate Artist and Composition
 * objects to bridge from a CDDB database to a more sophisticated internal
 * CD database.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: CDDBEntry.java 714 2005-08-22 18:40:22Z cpesch $
 * @see CDDBXmcdParser, CDDBRecord
 */

public class CDDBEntry {
    private CDDBXmcdParser parser;
    private CDDBRecord record;

    /**
     * The fileContent must be in xmcd format specified by CDDB and must contain
     * the disc id from the record.
     *
     * @throws ParseException if the fileContent is not in valid xmcd format
     *                        or inconsistent with the given record
     */
    public CDDBEntry(CDDBRecord record, String fileContent) throws ParseException {
        this.record = record;
        parser = new CDDBXmcdParser(fileContent);
        String[] ids = parser.readDiscIds();
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(record.getDiscId()))
                return;
        }
        throw new ParseException("disc id in record not found in file content", -1);
    }

    CDDBXmcdParser getParser() {
        return parser;
    }

    String getXmcdContent() {
        return parser.getContent();
    }

    public CDDBRecord getCDDBRecord() {
        return record;
    }


    /**
     * From the given set of entries, find the most specific entry.
     * This is an entry, which is not part of the misc category.
     * If the only entry is of the misc category, it is returned.
     *
     * @param entries the set of entries to find the most specific one
     * @return the most specific entry, i.e. that is an entry, which
     *         is not part of the misc category.
     */
    public static CDDBEntry findMostSpecificEntry(CDDBEntry[] entries) {
        int categoryIndex = 0;
        for (int i = 0; i < entries.length; i++) {
            CDDBEntry entry = entries[i];
            if (entry != null) {
                String category = entry.getCDDBRecord().getCategory();
                if (!"misc".equals(category) && !"data".equals(category))
                    categoryIndex = i;
            }
        }
        return entries[categoryIndex];
    }

    /**
     * Create <code>CDDBEntry</code>s from the given <code>DiscId</code>
     * by querying the FreeDB data base
     *
     * @param discId the <code>DiscId</code> to query the FreeDB data base
     * @return the queried <code>CDDBEntry</code>s
     * @throws IOException if an access error occurs
     *                     and the request cannot be processed
     */
    public static CDDBEntry[] fetchCDDBEntries(DiscId discId) throws IOException {
        FreeDBClient client = new FreeDBClient();
        CDDBRecord[] records = client.queryDiscId(discId);
        FreeDBClient.log.info("Found " + records.length + " matches for disc id " + discId);

        CDDBEntry[] entries = new CDDBEntry[records.length];
        for (int i = 0; i < records.length; i++) {
            try {
                entries[i] = client.readCDInfo(records[i]);
            } catch (IOException e) {
                FreeDBClient.log.severe("Cannot read CD info for record " + records[i] + ": " + e.getMessage());
            }
        }
        return entries;
    }


    public String getArtist() {
        return parser.readCDArtist();
    }

    public String getAlbum() {
        return parser.readCDAlbum();
    }

    public int getYear() {
        int year = parser.readYear();
        if (year == -1)
            year = parser.readExtdYear();
        return year;
    }

    /**
     * Read the genre from an ID3G extension or from the DGENRE attribute.
     *
     * @return the genre from an ID3G extension or from the DGENRE attribute
     */
    public ID3Genre getID3Genre() {
        int genreId = parser.readExtdGenre();
        if (genreId == -1) {
            Integer id = ID3Genre.getGenreId(parser.readGenre());
            if (id == null)
                id = ID3Genre.getGenreId(record.getCategory());
            if (id != null)
                genreId = id;
        }
        return new ID3Genre(genreId);
    }

    public int getTrackCount() {
        return parser.readNumberOfTracks();
    }

    public String getTrackArtist(int trackNumber) {
        return parser.readTrackArtist(trackNumber);
    }

    public String getTrackAlbum(int trackNumber) {
        return parser.readTrackAlbum(trackNumber);
    }

    public String getSubmitter() {
        return parser.readSubmitter();
    }

    public int[] getOffsets() {
        return parser.readOffsets();
    }

    public int getLength() {
        return parser.readLength();
    }

    public long getBitRate() {
        return 144000 * 8;
    }

    public long getSampleFrequency() {
        return 44100;
    }

    public String toString() {
        return super.toString() + "[" +
                "record=" + getCDDBRecord() +
                ", parser=" + getParser() +
                ", content=" + getXmcdContent() +
                "]";
    }
}
