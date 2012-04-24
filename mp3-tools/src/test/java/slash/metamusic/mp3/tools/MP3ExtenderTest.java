/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import slash.metamusic.coverdb.CoverDBCache;
import slash.metamusic.lyricsdb.LyricsDBCache;
import slash.metamusic.mp3.*;
import slash.metamusic.mp3.sections.DescriptionSection;
import slash.metamusic.mp3.sections.LanguageSection;
import slash.metamusic.mp3.sections.MimeTypeSection;
import slash.metamusic.mp3.sections.PictureTypeSection;
import slash.metamusic.test.AbstractFileTest;
import slash.metamusic.trm.TRM;
import slash.metamusic.util.URLLoader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Tests for the MP3 Extender and Cleaner
 */

public class MP3ExtenderTest extends AbstractFileTest {

    public MP3ExtenderTest(String name) {
        super(name);
    }

    private void checkMP3Properties(MP3File mp3) {
        assertEquals(true, mp3.isMP3());
        assertEquals(256000, mp3.getBitRate());
        assertEquals(48000, mp3.getSampleFrequency());
        assertEquals(1, mp3.getSeconds());
    }

    private void setID3WithoutGenreAndCount(ID3MetaData mp3) {
        mp3.setArtist("Artist");
        mp3.setComment("Comment");
        mp3.setAlbum("Album");
        mp3.setTrack("Track");
        mp3.setIndex(42);
        mp3.setYear(2001);
    }

    private void setID3(MP3File mp3) {
        setID3WithoutGenreAndCount(mp3);
        mp3.setGenre(new ID3Genre("Freestyle"));
        mp3.setCount(84);
    }

    private void checkID3WithoutGenreAndCommentAndCount(MP3File mp3) {
        assertEquals(768, ((MP3Properties) mp3.getProperties()).getFrameSize());
        assertEquals(21, ((MP3Properties) mp3.getProperties()).getFrames());
        checkMP3Properties(mp3);
        assertEquals("Artist", mp3.getArtist());
        assertEquals("Album", mp3.getAlbum());
        assertEquals("Track", mp3.getTrack());
        assertEquals(42, mp3.getIndex());
        assertEquals(2001, mp3.getYear());
    }

    public void testMP3CleanerBasics() throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        setID3(mp3);
        ID3v2Frame txxx = mp3.getHead().addID3v2Frame("TXXX");
        txxx.setText("\u0000musicbrainz Whatsoever");
        ID3v2Frame priv = mp3.getHead().addID3v2Frame("PRIV");
        priv.setBytes("WM/Whatsoever".getBytes());
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeToBeCleaned.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = new MP3File();
        mp3.read(tempFile);

        MP3Cleaner cleaner = new MP3Cleaner();
        cleaner.removeMusicBrainzTags(mp3);
        cleaner.removeWindowsMediaPlayerTags(mp3);
        cleaner.removeRedundantTags(mp3);
        mp3.write();

        mp3 = new MP3File();
        mp3.read(tempFile);
        checkID3WithoutGenreAndCommentAndCount(mp3);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());

        txxx = mp3.getHead().getFrame("TXXX");
        assertNull(txxx);

        priv = mp3.getHead().getFrame("PRIV");
        assertNull(priv);

        ID3v2Frame comm = mp3.getHead().getFrame("COMM");
        assertNull(comm);
    }

    public void testMP3ExtenderBasics() throws Exception {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + "noid3.mp3");
        copyToTempFile(src);

        MP3File mp3 = new MP3File();
        mp3.setID3v1(false);
        mp3.setID3v2(true);
        setID3(mp3);
        mp3.write(tempFile);
        File dest = new File(tempFile.getParentFile(), "writeToBeExtended.mp3");
        tempFile.renameTo(dest);
        tempFile = dest;

        mp3 = new MP3File();
        mp3.read(tempFile);

        byte[] pictureData = URLLoader.getContents(new File(AbstractFileTest.PATH_TO_TEST_IMAGE_FILES + "dontpanic.gif"));
        CoverDBCache coverDBCache = new CoverDBCache();
        coverDBCache.setCacheDirectoryName(tempFile.getParentFile().getAbsolutePath());
        coverDBCache.storeCover("Artist", "Album", pictureData);
        assertTrue(Arrays.equals(pictureData, coverDBCache.peekCover("Artist", "Album")));

        String textData = new String(URLLoader.getContents(new File(AbstractFileTest.PATH_TO_TEST_TEXT_FILES + "samplelyrics.txt")));
        LyricsDBCache lyricsDBCache = new LyricsDBCache();
        lyricsDBCache.setCacheDirectoryName(tempFile.getParentFile().getAbsolutePath());
        lyricsDBCache.storeLyrics("Artist", "Track", textData);
        assertEquals(textData, lyricsDBCache.peekLyrics("Artist", "Track"));

        MP3Extender extender = new MP3Extender();
        extender.setCoverDirectoryName(tempFile.getParentFile().getAbsolutePath());
        extender.setLyricsDirectoryName(tempFile.getParentFile().getAbsolutePath());
        extender.addCover(mp3);
        extender.addLyrics(mp3);
        extender.addMusicBrainzId(mp3);
        mp3.write();

        mp3 = new MP3File();
        mp3.read(tempFile);
        checkID3WithoutGenreAndCommentAndCount(mp3);
        assertEquals(false, mp3.isID3v1());
        assertEquals(false, mp3.isID3v1dot1());
        assertEquals(true, mp3.isID3v2());

        ID3v2Frame apic = mp3.getHead().getFrame("APIC");
        assertNotNull(apic);
        assertEquals("image/gif", apic.findSection(MimeTypeSection.class).getMimeType().toString());
        assertEquals("cover", apic.findSection(DescriptionSection.class).getDescription());
        assertEquals(0x03, apic.findSection(PictureTypeSection.class).getPictureType().getCode());
        assertTrue(Arrays.equals(pictureData, apic.getByteContent()));

        ID3v2Frame uslt = mp3.getHead().getFrame("USLT");
        assertNotNull(uslt);
        assertEquals("English", uslt.findSection(LanguageSection.class).getLanguage());
        assertEquals("Lyrics from http://www.lyrc.com.ar", uslt.findSection(DescriptionSection.class).getDescription());
        assertEquals(textData, uslt.getTextContent());

        if (TRM.isSupported()) {
            ID3v2Frame ufid = mp3.getHead().getFrame("UFID");
            assertNotNull(ufid);
        }

        coverDBCache.removeCover("Artist", "Album");
        assertNull(coverDBCache.peekCover("Artist", "Album"));

        lyricsDBCache.removeLyrics("Artist", "Track");
        assertNull(lyricsDBCache.peekLyrics("Artist", "Track"));
    }
}
