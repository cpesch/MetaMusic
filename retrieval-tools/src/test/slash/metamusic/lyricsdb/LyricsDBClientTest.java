/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.lyricsdb;

import junit.framework.TestCase;
import slash.metamusic.util.StringHelper;

import java.io.File;
import java.io.IOException;

public class LyricsDBClientTest extends TestCase {
    private LyricsDBClient client = new LyricsDBClient();
    private File tempDir;

    private static final String PLAIN = "Oh, how 'bout a round of applause?\n" +
            "Yeah, standin' ovation\n" +
            "Oooh, oh yeah, yeah y-yeah yeah\n" +
            "\n" +
            "You look so dumb right now\n" +
            "Standin' outside my house\n" +
            "Tryin' to apologize\n" +
            "You're so ugly when you cry\n" +
            "Please, just cu[...]";

    protected void setUp() throws Exception {
        super.setUp();
        tempDir = File.createTempFile("lyrics", ".cache");
        assertTrue(tempDir.delete());
        assertTrue(tempDir.mkdir());
    }

    protected void tearDown() throws Exception {
        assertTrue(tempDir.delete());
        super.tearDown();
    }

    public void testDecodeEntities() {
        assertEquals("f\u00fchr", StringHelper.decodeEntities("f&#252;hr"));
    }

    public void testDownloadLyrics() throws IOException {
        client.setLyricsDirectoryName(tempDir.getAbsolutePath());
        assertEquals(PLAIN, client.downloadLyrics("Rihanna", "Take a bow"));
    }
}
