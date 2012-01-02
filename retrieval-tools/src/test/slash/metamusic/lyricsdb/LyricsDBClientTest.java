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

    private static final String PLAIN_TRIMMED = "Oh, how 'bout a round of applause?\n" +
            "Yeah, standin' ovation\n" +
            "Oooh, oh yeah, yeah y-yeah yeah\n" +
            "\n" +
            "You look so dumb right now\n" +
            "Standin' outside my house\n" +
            "Tryin' to apologize\n" +
            "You're so ugly when you cry\n" +
            "Please, just cu[...]";

    private static final String HTML = "\n" +
            "<!doctype html>\n" +
            "<html lang=\"en\" dir=\"ltr\">\n" +
            "<head>\n" +
            "\t<title>Rihanna:Take A Bow Lyrics - Lyric Wiki - song lyrics, music lyrics</title>\n" +
            "\t\t</head>\n" +
            "<body class=\"mediawiki ltr ns-0 ns-subject page-Rihanna_Take_A_Bow TOCimprovements skin-oasis\">\n" +
            "<div class=\"noprint\" style=\"clear:both; border:solid #BBA34A 1px; margin:0 0 2px 0.5em; padding:2px; font-size:90%; background:#EBDAAC; width:246px; text-align:center; float:right\"><div style=\"float:left; padding:2px\"><img alt=\"Wikipedia\" src=\"http://images1.wikia.nocookie.net/__cb20101130031544/lyricwiki/images/thumb/5/54/Wikipedia_sphere.png/35px-Wikipedia_sphere.png\" width=\"35\" height=\"32\" /></div><div>Wikipedia has an article on<div><i><b><a href=\"http://en.wikipedia.org/wiki/Take_a_Bow_(Rihanna_song)\" class=\"extiw\" title=\"wikipedia:Take a Bow (Rihanna song)\">Take a Bow</a></b></i></div></div></div>\n" +
            "<noscript><div class='gracenote-header'>You must enable javascript to view this page.  This is a requirement of our licensing agreement with music Gracenote.</div><style type='text/css'>.lyricbox{display:none !important;}</style></noscript>\n" +
            "<div class='lyricbox'><div class='rtMatcher'><a href='http://www.ringtonematcher.com/co/ringtonematcher/02/noc.asp?sid=WILWros&amp;artist=Rihanna&amp;song=Take%2BA%2BBow' rel='nofollow' target='_blank'><img src='http://images.wikia.nocookie.net/common/__cb46229/extensions/3rdparty/LyricWiki/phone_left.gif' alt='phone' width='16' height='17'/> Send \"Take A Bow\" Ringtone to your Cell <img src='http://images.wikia.nocookie.net/common/__cb46229/extensions/3rdparty/LyricWiki/phone_right.gif' alt='phone' width='16' height='17'/></a></div>&#79;&#104;&#44;&#32;&#104;&#111;&#119;&#32;&#39;&#98;&#111;&#117;&#116;&#32;&#97;&#32;&#114;&#111;&#117;&#110;&#100;&#32;&#111;&#102;&#32;&#97;&#112;&#112;&#108;&#97;&#117;&#115;&#101;&#63;<br />&#89;&#101;&#97;&#104;&#44;&#32;&#115;&#116;&#97;&#110;&#100;&#105;&#110;&#39;&#32;&#111;&#118;&#97;&#116;&#105;&#111;&#110;<br />&#79;&#111;&#111;&#104;&#44;&#32;&#111;&#104;&#32;&#121;&#101;&#97;&#104;&#44;&#32;&#121;&#101;&#97;&#104;&#32;&#121;&#45;&#121;&#101;&#97;&#104;&#32;&#121;&#101;&#97;&#104;<br /><br />&#89;&#111;&#117;&#32;&#108;&#111;&#111;&#107;&#32;&#115;&#111;&#32;&#100;&#117;&#109;&#98;&#32;&#114;&#105;&#103;&#104;&#116;&#32;&#110;&#111;&#119;<br />&#83;&#116;&#97;&#110;&#100;&#105;&#110;&#39;&#32;&#111;&#117;&#116;&#115;&#105;&#100;&#101;&#32;&#109;&#121;&#32;&#104;&#111;&#117;&#115;&#101;<br />&#84;&#114;&#121;&#105;&#110;&#39;&#32;&#116;&#111;&#32;&#97;&#112;&#111;&#108;&#111;&#103;&#105;&#122;&#101;<br />&#89;&#111;&#117;&#39;&#114;&#101;&#32;&#115;&#111;&#32;&#117;&#103;&#108;&#121;&#32;&#119;&#104;&#101;&#110;&#32;&#121;&#111;&#117;&#32;&#99;&#114;&#121;<br />&#80;&#108;&#101;&#97;&#115;&#101;&#44;&#32;&#106;&#117;&#115;&#116;&#32;&#99;&#117;&#116;&#32;&#105;&#116;&#32;&#111;&#117;&#116;<br /><br />&#68;&#111;&#110;&#39;&#116;&#32;&#116;&#101;&#108;&#108;&#32;&#109;&#101;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#115;&#111;&#114;&#114;&#121;&#32;&#39;&#99;&#97;&#117;&#115;&#101;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#110;&#111;&#116;<br />&#66;&#97;&#98;&#121;&#32;&#119;&#104;&#101;&#110;&#32;&#73;&#32;&#107;&#110;&#111;&#119;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#111;&#110;&#108;&#121;&#32;&#115;&#111;&#114;&#114;&#121;&#32;&#121;&#111;&#117;&#32;&#103;&#111;&#116;&#32;&#99;&#97;&#117;&#103;&#104;&#116;<br /><br />&#66;&#117;&#116;&#32;&#121;&#111;&#117;&#32;&#112;&#117;&#116;&#32;&#111;&#110;&#32;&#113;&#117;&#105;&#116;&#101;&#32;&#97;&#32;&#115;&#104;&#111;&#119;&#32;&#40;&#111;&#111;&#104;&#41;<br />&#82;&#101;&#97;&#108;&#108;&#121;&#32;&#104;&#97;&#100;&#32;&#109;&#101;&#32;&#103;&#111;&#105;&#110;&#103;<br />&#66;&#117;&#116;&#32;&#110;&#111;&#119;&#32;&#105;&#116;&#39;&#115;&#32;&#116;&#105;&#109;&#101;&#32;&#116;&#111;&#32;&#103;&#111;&#32;&#40;&#111;&#111;&#104;&#41;<br />&#67;&#117;&#114;&#116;&#97;&#105;&#110;&#115;&#32;&#102;&#105;&#110;&#97;&#108;&#108;&#121;&#32;&#99;&#108;&#111;&#115;&#105;&#110;&#103;<br /><br />&#84;&#104;&#97;&#116;&#32;&#119;&#97;&#115;&#32;&#113;&#117;&#105;&#116;&#101;&#32;&#97;&#32;&#115;&#104;&#111;&#119;&#32;&#40;&#111;&#111;&#104;&#41;<br />&#86;&#101;&#114;&#121;&#32;&#101;&#110;&#116;&#101;&#114;&#116;&#97;&#105;&#110;&#105;&#110;&#103;<br />&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;<br />&#40;&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;&#41;<br />&#71;&#111;&#32;&#111;&#110;&#32;&#97;&#110;&#100;&#32;&#116;&#97;&#107;&#101;&#32;&#97;&#32;&#98;&#111;&#119;<br />&#79;&#104;&#32;&#111;&#104;&#32;&#111;&#104;<br /><br />&#71;&#114;&#97;&#98;&#32;&#121;&#111;&#117;&#114;&#32;&#99;&#108;&#111;&#116;&#104;&#101;&#115;&#32;&#97;&#110;&#100;&#32;&#103;&#101;&#116;&#32;&#103;&#111;&#110;&#101;&#32;&#40;&#71;&#101;&#116;&#32;&#103;&#111;&#110;&#101;&#41;<br />&#89;&#97;&#32;&#98;&#101;&#116;&#116;&#101;&#114;&#32;&#104;&#117;&#114;&#114;&#121;&#32;&#117;&#112;&#32;&#98;&#101;&#102;&#111;&#114;&#101;&#32;&#116;&#104;&#101;&#32;&#115;&#112;&#114;&#105;&#110;&#107;&#108;&#101;&#114;&#115;&#32;&#99;&#111;&#109;&#101;&#32;&#111;&#110;&#32;&#40;&#67;&#111;&#109;&#101;&#32;&#111;&#110;&#41;<br />&#84;&#97;&#108;&#107;&#105;&#110;&#39;&#32;&#39;&#98;&#111;&#117;&#116;&#44;&#32;&#34;&#71;&#105;&#114;&#108;&#32;&#73;&#32;&#108;&#111;&#118;&#101;&#32;&#121;&#111;&#117;&#44;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#116;&#104;&#101;&#32;&#111;&#110;&#101;&#46;&#34;<br />&#84;&#104;&#105;&#115;&#32;&#106;&#117;&#115;&#116;&#32;&#108;&#111;&#111;&#107;&#115;&#32;&#108;&#105;&#107;&#101;&#32;&#97;&#32;&#114;&#101;&#114;&#117;&#110;<br />&#80;&#108;&#101;&#97;&#115;&#101;&#44;&#32;&#119;&#104;&#97;&#116;&#32;&#101;&#108;&#115;&#101;&#32;&#105;&#115;&#32;&#111;&#110;&#63;<br /><br />&#65;&#110;&#100;&#32;&#100;&#111;&#110;&#39;&#116;&#32;&#116;&#101;&#108;&#108;&#32;&#109;&#101;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#115;&#111;&#114;&#114;&#121;&#32;&#99;&#97;&#117;&#115;&#101;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#110;&#111;&#116;&#32;&#40;&#109;&#104;&#109;&#109;&#41;<br />&#66;&#97;&#98;&#121;&#32;&#119;&#104;&#101;&#110;&#32;&#73;&#32;&#107;&#110;&#111;&#119;&#32;&#121;&#111;&#117;&#39;&#114;&#101;&#32;&#111;&#110;&#108;&#121;&#32;&#115;&#111;&#114;&#114;&#121;&#32;&#121;&#111;&#117;&#32;&#103;&#111;&#116;&#32;&#99;&#97;&#117;&#103;&#104;&#116;<br /><br />&#66;&#117;&#116;&#32;&#121;&#111;&#117;&#32;&#112;&#117;&#116;&#32;&#111;&#110;&#32;&#113;&#117;&#105;&#116;&#101;&#32;&#97;&#32;&#115;&#104;&#111;&#119;&#32;&#40;&#111;&#111;&#104;&#41;<br />&#82;&#101;&#97;&#108;&#108;&#121;&#32;&#104;&#97;&#100;&#32;&#109;&#101;&#32;&#103;&#111;&#105;&#110;&#103;<br />&#66;&#117;&#116;&#32;&#110;&#111;&#119;&#32;&#105;&#116;&#39;&#115;&#32;&#116;&#105;&#109;&#101;&#32;&#116;&#111;&#32;&#103;&#111;<br />&#67;&#117;&#114;&#116;&#97;&#105;&#110;&#115;&#32;&#102;&#105;&#110;&#97;&#108;&#108;&#121;&#32;&#99;&#108;&#111;&#115;&#105;&#110;&#103;<br /><br />&#84;&#104;&#97;&#116;&#32;&#119;&#97;&#115;&#32;&#113;&#117;&#105;&#116;&#101;&#32;&#97;&#32;&#115;&#104;&#111;&#119;<br />&#86;&#101;&#114;&#121;&#32;&#101;&#110;&#116;&#101;&#114;&#116;&#97;&#105;&#110;&#105;&#110;&#103;<br />&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;<br />&#40;&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;&#41;<br />&#71;&#111;&#32;&#111;&#110;&#32;&#97;&#110;&#100;&#32;&#116;&#97;&#107;&#101;&#32;&#97;&#32;&#98;&#111;&#119;<br /><br />&#79;&#104;&#104;&#44;&#32;&#97;&#110;&#100;&#32;&#116;&#104;&#101;&#32;&#97;&#119;&#97;&#114;&#100;&#32;&#102;&#111;&#114;&#32;&#116;&#104;&#101;&#32;&#98;&#101;&#115;&#116;&#32;&#108;&#105;&#97;&#114;&#32;&#103;&#111;&#101;&#115;&#32;&#116;&#111;&#32;&#121;&#111;&#117;<br />&#70;&#111;&#114;&#32;&#109;&#97;&#107;&#105;&#110;&#103;&#32;&#109;&#101;&#32;&#98;&#101;&#108;&#105;&#101;&#118;&#101;&#32;&#116;&#104;&#97;&#116;&#32;&#121;&#111;&#117;&#32;&#99;&#111;&#117;&#108;&#100;&#32;&#98;&#101;&#32;&#102;&#97;&#105;&#116;&#104;&#102;&#117;&#108;&#32;&#116;&#111;&#32;&#109;&#101;<br />&#76;&#101;&#116;&#39;&#115;&#32;&#104;&#101;&#97;&#114;&#32;&#121;&#111;&#117;&#114;&#32;&#115;&#112;&#101;&#101;&#99;&#104;&#32;&#111;&#117;&#116;<br /><br />&#72;&#111;&#119;&#32;&#39;&#98;&#111;&#117;&#116;&#32;&#97;&#32;&#114;&#111;&#117;&#110;&#100;&#32;&#111;&#102;&#32;&#97;&#112;&#112;&#108;&#97;&#117;&#115;&#101;&#63;<br />&#65;&#32;&#115;&#116;&#97;&#110;&#100;&#105;&#110;&#39;&#32;&#111;&#118;&#97;&#116;&#105;&#111;&#110;<br /><br />&#66;&#117;&#116;&#32;&#121;&#111;&#117;&#32;&#112;&#117;&#116;&#32;&#111;&#110;&#32;&#113;&#117;&#105;&#116;&#101;&#32;&#97;&#32;&#115;&#104;&#111;&#119;<br />&#82;&#101;&#97;&#108;&#108;&#121;&#32;&#104;&#97;&#100;&#32;&#109;&#101;&#32;&#103;&#111;&#105;&#110;&#103;<br />&#78;&#111;&#119;&#32;&#105;&#116;&#39;&#115;&#32;&#116;&#105;&#109;&#101;&#32;&#116;&#111;&#32;&#103;&#111;<br />&#67;&#117;&#114;&#116;&#97;&#105;&#110;&#115;&#32;&#102;&#105;&#110;&#97;&#108;&#108;&#121;&#32;&#99;&#108;&#111;&#115;&#105;&#110;&#103;<br /><br />&#84;&#104;&#97;&#116;&#32;&#119;&#97;&#115;&#32;&#113;&#117;&#105;&#116;&#101;&#32;&#97;&#32;&#115;&#104;&#111;&#119;<br />&#86;&#101;&#114;&#121;&#32;&#101;&#110;&#116;&#101;&#114;&#116;&#97;&#105;&#110;&#105;&#110;&#103;<br />&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;<br />&#40;&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;&#41;<br />&#71;&#111;&#32;&#111;&#110;&#32;&#97;&#110;&#100;&#32;&#116;&#97;&#107;&#101;&#32;&#97;&#32;&#98;&#111;&#119;<br /><br />&#66;&#117;&#116;&#32;&#105;&#116;&#39;&#115;&#32;&#111;&#118;&#101;&#114;&#32;&#110;&#111;&#119;&#46;&#46;&#46;<!-- \n" +
            "--><div class='rtMatcher'><a href='http://www.ringtonematcher.com/co/ringtonematcher/02/noc.asp?sid=WILWros&amp;artist=Rihanna&amp;song=Take%2BA%2BBow' rel='nofollow' target='_blank'><img src='http://images.wikia.nocookie.net/common/__cb46229/extensions/3rdparty/LyricWiki/phone_left.gif' alt='phone' width='16' height='17'/> Send \"Take A Bow\" Ringtone to your Cell <img src='http://images.wikia.nocookie.net/common/__cb46229/extensions/3rdparty/LyricWiki/phone_right.gif' alt='phone' width='16' height='17'/></a></div><div class='lyricsbreak'></div>\n" +
            "</div>\n" +
            "</body>\n" +
            "</html>";

    private static final String PLAIN = "Oh, how 'bout a round of applause?\n" +
            "Yeah, standin' ovation\n" +
            "Oooh, oh yeah, yeah y-yeah yeah\n" +
            "\n" +
            "You look so dumb right now\n" +
            "Standin' outside my house\n" +
            "Tryin' to apologize\n" +
            "You're so ugly when you cry\n" +
            "Please, just cut it out\n" +
            "\n" +
            "Don't tell me you're sorry 'cause you're not\n" +
            "Baby when I know you're only sorry you got caught\n" +
            "\n" +
            "But you put on quite a show (ooh)\n" +
            "Really had me going\n" +
            "But now it's time to go (ooh)\n" +
            "Curtains finally closing\n" +
            "\n" +
            "That was quite a show (ooh)\n" +
            "Very entertaining\n" +
            "But it's over now\n" +
            "(But it's over now)\n" +
            "Go on and take a bow\n" +
            "Oh oh oh\n" +
            "\n" +
            "Grab your clothes and get gone (Get gone)\n" +
            "Ya better hurry up before the sprinklers come on (Come on)\n" +
            "Talkin' 'bout, \"Girl I love you, you're the one.\"\n" +
            "This just looks like a rerun\n" +
            "Please, what else is on?\n" +
            "\n" +
            "And don't tell me you're sorry cause you're not (mhmm)\n" +
            "Baby when I know you're only sorry you got caught\n" +
            "\n" +
            "But you put on quite a show (ooh)\n" +
            "Really had me going\n" +
            "But now it's time to go\n" +
            "Curtains finally closing\n" +
            "\n" +
            "That was quite a show\n" +
            "Very entertaining\n" +
            "But it's over now\n" +
            "(But it's over now)\n" +
            "Go on and take a bow\n" +
            "\n" +
            "Ohh, and the award for the best liar goes to you\n" +
            "For making me believe that you could be faithful to me\n" +
            "Let's hear your speech out\n" +
            "\n" +
            "How 'bout a round of applause?\n" +
            "A standin' ovation\n" +
            "\n" +
            "But you put on quite a show\n" +
            "Really had me going\n" +
            "Now it's time to go\n" +
            "Curtains finally closing\n" +
            "\n" +
            "That was quite a show\n" +
            "Very entertaining\n" +
            "But it's over now\n" +
            "(But it's over now)\n" +
            "Go on and take a bow\n" +
            "\n" +
            "But it's over now...";

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
        assertEquals(PLAIN_TRIMMED, client.downloadLyrics("Rihanna", "Take a bow"));
    }

    public void testExtractLyrics() {
        assertEquals(PLAIN, client.extractLyrics(HTML));
    }

    public void testScrapeLyrics() throws IOException {
        assertEquals(PLAIN, client.scrapeLyrics("Rihanna", "Take a bow"));
    }
}
