/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3;

import junit.framework.TestCase;

/**
 * Tests for ID3Genre
 */

public class ID3GenreTest extends TestCase {
    private static final String WELL_KNOWN_GENRE_NAME = "Soul";
    private static final int WELL_KNOWN_GENRE_ID = 42;
    private static final String UNUSUAL_GENRE_NAME = "Speech & Spoken";
    private static final int UNUSUAL_GENRE_ID = -1;

    public ID3GenreTest(String name) {
        super(name);
    }

    public void testWellKnownGenreByName() {
        ID3Genre genre = new ID3Genre(WELL_KNOWN_GENRE_NAME);
        assertEquals(WELL_KNOWN_GENRE_ID, genre.getId());
        assertEquals(WELL_KNOWN_GENRE_NAME, genre.getName());
        assertEquals(WELL_KNOWN_GENRE_NAME + "(" + WELL_KNOWN_GENRE_ID + ")", genre.getFormattedName());
        assertEquals(true, genre.isWellKnown());
        assertEquals(new ID3Genre(WELL_KNOWN_GENRE_NAME), genre);
        assertEquals(new ID3Genre(WELL_KNOWN_GENRE_ID), genre);
    }

    public void testWellKnownGenreById() {
        ID3Genre genre = new ID3Genre(WELL_KNOWN_GENRE_ID);
        assertEquals(WELL_KNOWN_GENRE_ID, genre.getId());
        assertEquals(WELL_KNOWN_GENRE_NAME, genre.getName());
        assertEquals(WELL_KNOWN_GENRE_NAME + "(" + WELL_KNOWN_GENRE_ID + ")", genre.getFormattedName());
        assertEquals(true, genre.isWellKnown());
        assertEquals(new ID3Genre(WELL_KNOWN_GENRE_NAME), genre);
        assertEquals(new ID3Genre(WELL_KNOWN_GENRE_ID), genre);
    }

    public void testUnusualGenreByName() {
        ID3Genre genre = new ID3Genre(UNUSUAL_GENRE_NAME);
        assertEquals(UNUSUAL_GENRE_ID, genre.getId());
        assertEquals(UNUSUAL_GENRE_NAME, genre.getName());
        assertEquals(UNUSUAL_GENRE_NAME, genre.getFormattedName());
        assertEquals(false, genre.isWellKnown());
        assertEquals(new ID3Genre(UNUSUAL_GENRE_NAME), genre);
    }

    public void testUnusualGenreById() {
        ID3Genre genre = new ID3Genre(UNUSUAL_GENRE_ID);
        assertEquals(UNUSUAL_GENRE_ID, genre.getId());
        assertNull(genre.getName());
        assertEquals("null", genre.getFormattedName());
        assertEquals(false, genre.isWellKnown());
        assertEquals(new ID3Genre(UNUSUAL_GENRE_ID).getId(), genre.getId());
        assertEquals(new ID3Genre(UNUSUAL_GENRE_ID).getName(), genre.getName());
        assertEquals(new ID3Genre(UNUSUAL_GENRE_NAME).getId(), genre.getId());
    }

    public void testAltRock() {
        ID3Genre genre = new ID3Genre("Alt. Rock");
        assertEquals(40, genre.getId());
        assertEquals("Alt. Rock", genre.getName());
        assertEquals("Alt. Rock(40)", genre.getFormattedName());
        assertEquals(true, genre.isWellKnown());
    }

    public void testAltRock40() {
        ID3Genre genre = new ID3Genre("Alt. Rock(40)");
        assertEquals(40, genre.getId());
        assertEquals("Alt. Rock", genre.getName());
        assertEquals("Alt. Rock(40)", genre.getFormattedName());
        assertEquals(true, genre.isWellKnown());
    }

    public void testAlternativeRock() {
        ID3Genre genre = new ID3Genre("Alternative Rock");
        assertEquals(-1, genre.getId());
        assertEquals("Alternative Rock", genre.getName());
        assertEquals("Alternative Rock", genre.getFormattedName());
        assertEquals(false, genre.isWellKnown());
    }

}
