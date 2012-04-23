/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3;

import slash.metamusic.test.AbstractFileTest;
import slash.metamusic.util.Files;

import java.io.File;
import java.io.IOException;

/**
 * Tests for ID3FileName
 */

public class ID3FileNameTest extends AbstractFileTest {

    public ID3FileNameTest(String name) {
        super(name);
    }

    public void testSimpleID3FileName() {
        ID3FileName id3FileName = new ID3FileName("track", "artist", "album", 4711, false);
        String fileName = "artist - album - 4711 - track.mp3";
        String filePath = "artist" + File.separator + "album";
        assertEquals(filePath + File.separator + fileName, id3FileName.getAbsolutePath());
        assertEquals(fileName, id3FileName.getFileName());
        assertEquals(filePath, id3FileName.getFilePath());
    }

    public void testSpecialCharactersID3FileName() {
        ID3FileName id3FileName = new ID3FileName("\\/track\"", "an-a`artist*?", "hits: von 1975 - 2000!", 1234, false);
        String fileName = "an-a'artist - hits von 1975-2000 - 1234 - track.mp3";
        String filePath = "an-a'artist" + File.separator + "hits von 1975-2000";
        assertEquals(filePath + File.separator + fileName, id3FileName.getAbsolutePath());
        assertEquals(fileName, id3FileName.getFileName());
        assertEquals(filePath, id3FileName.getFilePath());
    }

    public void testVariousCollectionID3FileName() {
        ID3FileName id3FileName = new ID3FileName("track", "artist", "album", 9876, true);
        String fileName = "artist - album - 9876 - track.mp3";
        String filePath = "album";
        assertEquals(filePath + File.separator + fileName,
                id3FileName.getAbsolutePath());
        assertEquals(fileName, id3FileName.getFileName());
        assertEquals(filePath, id3FileName.getFilePath());
    }

    public void testTooLongFileName() {
        String longName = createString(1024);
        ID3FileName id3FileName = new ID3FileName(longName, longName, longName, Integer.MAX_VALUE, false);
        String fileName = longName + " - " + longName + " - " + Integer.MAX_VALUE + " - " + longName + ".mp3";
        String filePath = longName + File.separator + longName;
        assertEquals(filePath + File.separator + fileName,
                id3FileName.getAbsolutePath());
        assertEquals(fileName, id3FileName.getFileName());
        assertEquals(filePath, id3FileName.getFilePath());
    }

    private void checkFileName(ID3FileName id3) {
        assertTrue(id3.isValid());
        String fileName = "A - B - 4711 - C.mp3";
        assertEquals(fileName, id3.getFileName());
        String filePath = "A/B";
        assertEquals(Files.replaceSeparators(filePath), id3.getFilePath());
        assertEquals(Files.replaceSeparators(filePath + "/" + fileName), id3.getAbsolutePath());
        assertEquals("B", id3.getAlbum());
        assertEquals("A", id3.getArtist());
        assertEquals(4711, id3.getIndex());
        assertEquals("C", id3.getTrack());
        assertFalse(id3.isVarious());

        assertEquals(-1, id3.getReadSize());
        assertEquals(-1, id3.getYear());
        assertNull(id3.getGenre());
        assertNull(id3.getComment());
    }

    public void testID3FileName() throws IOException {
        File file = new File("A - B - 4711 - C.mp3");
        ID3FileName id3 = new ID3FileName(file);
        checkFileName(id3);
    }

    public void testHyphenID3FileName() throws IOException {
        File file = new File("A-B-4711-C.mp3");
        ID3FileName id3 = new ID3FileName(file);
        checkFileName(id3);
    }

    public void testDotID3FileName() throws IOException {
        File file = new File("A.B.4711.C.mp3");
        ID3FileName id3 = new ID3FileName(file);
        checkFileName(id3);
    }

    public void testNoIndexID3FileName() throws IOException {
        File file = new File("A-B-C.mp3");
        ID3FileName id3 = new ID3FileName(file);
        assertTrue(id3.isValid());
        String fileName = "A - B - C.mp3";
        assertEquals(fileName, id3.getFileName());
        String filePath = "A/B";
        assertEquals(Files.replaceSeparators(filePath), id3.getFilePath());
        assertEquals(Files.replaceSeparators(filePath + "/" + fileName), id3.getAbsolutePath());
        assertEquals("B", id3.getAlbum());
        assertEquals("A", id3.getArtist());
        assertEquals(-1, id3.getIndex());
        assertEquals("C", id3.getTrack());

        id3.setIndex(4711);
        checkFileName(id3);
    }

    public void testNoAlbumID3FileName() throws IOException {
        File file = new File("A-C.mp3");
        ID3FileName id3 = new ID3FileName(file);
        assertTrue(id3.isValid());
        String fileName = "A - C.mp3";
        assertEquals(fileName, id3.getFileName());
        assertNull(id3.getAlbum());
        assertEquals("A", id3.getArtist());
        assertEquals(-1, id3.getIndex());
        assertEquals("C", id3.getTrack());

        id3.setAlbum("B");
        id3.setIndex(4711);
        checkFileName(id3);
    }

    public void testNoTrackID3FileName() throws IOException {
        File file = new File("C.mp3");
        ID3FileName id3 = new ID3FileName(file);
        assertTrue(id3.isValid());
        String fileName = "C.mp3";
        assertEquals(fileName, id3.getFileName());
        assertNull(id3.getAlbum());
        assertEquals("C", id3.getArtist());
        assertEquals(-1, id3.getIndex());
        assertNull(id3.getTrack());

        id3.setAlbum("B");
        id3.setArtist("A");
        id3.setIndex(4711);
        id3.setTrack("C");
        checkFileName(id3);
    }

    public void testWithinWindowsFileNameLengthLimits() throws IOException {
        File file = File.createTempFile("renametest", ".mp3");
        assertTrue(file.exists());
        int pathLength = file.getAbsolutePath().length();
        assertTrue(pathLength < ID3FileName.WINDOWS_PATH_LENGTH_LIMIT);
        ID3FileName id3 = new ID3FileName(file);
        id3.setAlbum("B");
        String artist = createString(ID3FileName.WINDOWS_PATH_LENGTH_LIMIT - pathLength);
        assertTrue(artist.length() > 0);
        id3.setArtist(artist);
        id3.setIndex(4711);
        id3.setTrack("C");
        File renamed = id3.rename(file);
        assertNotEquals(renamed.getAbsolutePath(), file.getAbsolutePath());
        int fileNameLengthAfterRename = renamed.getAbsolutePath().length();
        assertTrue(renamed.delete());
        assertEquals(ID3FileName.WINDOWS_PATH_LENGTH_LIMIT, fileNameLengthAfterRename);
    }

    public void testAboveWindowsFileNameLengthLimits() throws IOException {
        File file = File.createTempFile("renametest", ".mp3");
        assertTrue(file.exists());
        int pathLength = file.getAbsolutePath().length();
        assertTrue(pathLength < ID3FileName.WINDOWS_PATH_LENGTH_LIMIT);
        ID3FileName id3 = new ID3FileName(file);
        id3.setAlbum("B");
        String artist = createString(ID3FileName.WINDOWS_PATH_LENGTH_LIMIT - pathLength + 1);
        assertTrue(artist.length() > 0);
        id3.setArtist(artist);
        id3.setIndex(4711);
        id3.setTrack("C");
        File renamed = id3.rename(file);
        assertNotEquals(renamed.getAbsolutePath(), file.getAbsolutePath());
        int fileNameLengthAfterRename = renamed.getAbsolutePath().length();
        assertTrue(renamed.delete());
        assertEquals(ID3FileName.WINDOWS_PATH_LENGTH_LIMIT, fileNameLengthAfterRename);
    }
}
