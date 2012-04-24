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

/**
 * Represents a unique entry in a FreeDB database. <p>
 * This CDDBRecord object can be used to read the content
 * from a CDDB in form of a CDInfo object.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: CDDBRecord.java 456 2004-12-20 16:19:54Z cpesch $
 */

public class CDDBRecord {
    private DiscId queryDiscId;
    private String discid, category, album;
    private int trackCount;
    private boolean exactMatch;

    /**
     * Produces an exact match record with the given parameters;
     * the order of the parameters is according to the output of the CDDB protocol.
     */
    public CDDBRecord(DiscId queryDiscId, String category, String foundDiscId, String album) {
        this(queryDiscId, category, foundDiscId, album, true);
    }

    /**
     * initializes a record that can be used to query CDDB for this entry.
     * The exactMatch property is only relevant when the instance is created
     * through a CDDB query.
     *
     * @see FreeDBClient#queryDiscId(DiscId)
     */
    public CDDBRecord(DiscId queryDiscId, String category, String foundDiscId, String album, boolean exactMatch) {
        setQueryDiscId(queryDiscId);
        setDiscId(foundDiscId);
        setCategory(category);
        setAlbum(album);
        setExactMatch(exactMatch);
    }

    /**
     * Returns the DiscId that was used to query the CDDBRecord.
     *
     * @return the DiscId that was used to query the CDDBRecord
     */
    public DiscId getQueryDiscId() {
        return queryDiscId;
    }

    /**
     * Sets the DiscId that was used to query the CDDBRecord.
     *
     * @param queryDiscId the DiscId that was used to query the CDDBRecord
     */
    public void setQueryDiscId(DiscId queryDiscId) {
        this.queryDiscId = queryDiscId;
    }


    /**
     * Returns the album of this record
     *
     * @return the album of this record
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Sets the album of this record
     *
     * @param album the album of this record
     */
    public void setAlbum(String album) {
        this.album = album;
    }


    /**
     * Returns the encoded disc id that can be used to match the
     * record with a DiscId object
     *
     * @return the encoded disc id that can be used to match the
     *         record with a DiscId object
     * @see DiscId#getEncodedDiscId()
     */
    public String getDiscId() {
        return discid;
    }

    /**
     * Sets the encoded disc id that can be used to match the
     * record with a DiscId object
     *
     * @param discId he encoded disc id that can be used to match the
     *               record with a DiscId object
     */
    public void setDiscId(String discId) {
        this.discid = discId;
    }


    /**
     * returns the category under which this record is filed
     */
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    /**
     * returns the track count of this record
     */
    public int getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }


    /**
     * relevant only when derived from a CDDB query: returns whether the
     * CDDBRecord was the result of an exact match of the CD used for the query.
     *
     * @see FreeDBClient#queryDiscId(slash.metamusic.discid.DiscId)
     */
    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }


    /**
     * hashes the disc id
     */
    public int hashCode() {
        return getDiscId().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof CDDBRecord)) return false;
        CDDBRecord other = (CDDBRecord) obj;
        if (!other.getCategory().equals(getCategory())) return false;
        if (!other.getDiscId().equals(getDiscId())) return false;
        if (!other.getAlbum().equals(getAlbum())) return false;
        if (other.isExactMatch() != isExactMatch()) return false;
        return true;
    }

    public String toString() {
        return super.toString() + "[" +
                "category=" + getCategory() +
                ", discId=" + getDiscId() +
                ", album=" + getAlbum() +
                ", " + (isExactMatch() ? "exact match" : "inexact match") +
                "]";
    }
}
