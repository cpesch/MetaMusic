/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.lyricsdb;

import junit.framework.TestCase;
import slash.metamusic.util.StringHelper;

public class LyricsDBClientTest extends TestCase {
    LyricsDBClient client = new LyricsDBClient();

    private static final String HTML = "<html><head><title>Lyrc - Search for lyrics by artist or song</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"><meta http-equiv=\"Expires\" content=\"0\"><meta name=\"rating\" content=\"General\"><meta name=\"revisit-after\" content=\"1 days\"><meta name=\"ROBOTS\" content=\"ALL\"><meta name=\"DC.Title\" content=\"Lyrc\"><meta name=\"DC.Subject\" content=\"A Plugin for Winamp and others player which shows lyrics to MiniBrowser.\"><meta name=\"DC.Description\" content=\"A Plugin for Winamp and others player which shows lyrics to MiniBrowser.\"><meta name=\"DC.Language\" content=\"ES/EN\"><meta name=\"DC.Coverage.PlaceName\"content=\"Argentina,internacional,\"><meta name=\"keywords\" content=\"  Lyrics Lyrc Lirc Letras canciones argentina plugin plug in latinoamerica Argentine winamp wma xmms tomas rawski hernan gips\"><meta name=\"Description\" content=\"A Plugin for Winamp, Xmms & QCD and others player which shows lyrics to MiniBrowser.\"><style type=\"text/css\"><!--body { scrollbar-face-color: #FFFFFF; scrollbar-shadow-color: #000000; scrollbar-highlight-color: #000000; scrollbar-3dlight-color: #000000; scrollbar-darkshadow-color: #000000; scrollbar-track-color: #000000; scrollbar-arrow-color: #000000;}.hola1 {  font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 1pt; color: #FFFFFF}.BOT {  font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-weight: bold; color: #FFFFFF; background-color: #279A75; border-color: #8DE2C7 #1D7055 #1D7055 #8DE2C7; border-style: solid; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px}a:active {  color: #339999}a:hover {  color: #FFFFFF}a:link {  color: #669966}a:visited {  color: #33CC99}--></style></head><body bgcolor=\"black\"><script type=\"text/javascript\">var _wid=4;</script><script type=\"text/javascript\" src=\"/include/statcode_105.js\"></script><font face=\"verdana\" size=\"1\" color=\"white\"><center><script type=\"text/javascript\"><!--google_ad_client = \"pub-5856745585538965\";google_alternate_ad_url = \"http://www.lyrc.com.ar/banner/\";google_ad_width = 468;google_ad_height = 60;google_ad_format = \"468x60_as\";google_ad_channel =\"0437267677\";google_color_border = \"333333\";google_color_bg = \"000000\";google_color_link = \"FFFFFF\";google_color_url = \"999999\";google_color_text = \"CCCCCC\";//--></script><script type=\"text/javascript\" src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\"></script><noscript><br /><b>Warning</b>:  main(banner/indexNOJS.php): failed to open stream: No such file or directory in <b>/lyrc/www/lyrc/en/tema1en.php</b> on line <b>217</b><br /><br /><b>Warning</b>:  main(): Failed opening 'banner/indexNOJS.php' for inclusion (include_path='.:/usr/share/php:/usr/share/pear') in <b>/lyrc/www/lyrc/en/tema1en.php</b> on line <b>217</b><br /></noscript><br></center><br><font size='2'    ><b>Auch im regen</b><br><u><font size='2'    >Rosenstolz</font></u></font><br><table align=\"left\"><tr><td><script type=\"text/javascript\"><!--google_ad_client = \"pub-5856745585538965\";google_ad_width = 120;google_ad_height = 240;google_ad_format = \"120x240_as\";google_ad_type = \"text_image\";google_ad_channel =\"7373406255\";google_color_border = \"000000\";google_color_bg = \"000000\";google_color_link = \"FFFFFF\";google_color_url = \"999999\";google_color_text = \"CCCCCC\";//--></script><script type=\"text/javascript\"  src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\"></script></td></tr></table>Wie kannst du dir so sicher sein<br />Du bist doch viel zu w&#252;tend<br />Um irgendwas zu sehn<br /><br />Du schreibst dich selbst mal gross, mal klein<br />Am Ende ist&#180;s verwirrend<br />Und sehr schwer zu verstehn<br /><br />Ich weiss es wird regnen<br />Und h&#246;rt so schnell nicht auf<br />Glaub mir es wird k&#228;lter<br />Wann h&#246;rt das wieder auf<br /><br />Ist da draussen kein Licht<br />Wolken nehm dir die Sicht<br />Auch im Regen<br />Auch im Regen<br />Siehst du mich<br />Wenn dein Boot untergeht<br />Und du gar nichts mehr verstehst<br />Auch im Regen<br />Selbst im Regen<br />Find ich dich<br /><br />Zers&#228;g den Ast auf dem du sitzt<br />Spring ab,fang endlich an zu laufen<br />Das kann ich f&#252;r dich nicht<br /><br />Lass von dir h&#246;ren wenn&#180;s soweit ist<br />Du musst dich nicht &#228;ndern<br />Es &#228;ndert sich f&#252;r dich<br /><br />Ich weiss nur es wird regnen<br />Und h&#246;rt so schnell nicht auf<br />Glaub mir es wird k&#228;lter<br />Wann h&#246;rt das wieder auf<br /><br />Ist da draussen kein Licht<br />Wolken nehmen die dir Sicht<br />Auch im Regen<br />Auch im Regen<br />Siehst du mich<br />Wenn dein Boot untergeht<br />Und du gar nicht&#180;s mehr verstehst<br />Auch im Regen<br />Selbst im Regen<br />Find ich dich<br /><br /><br />Bald siehst du Land<br />halt bitte noch durch<br />Ich seh ganz sicher dort hinten ein Licht<br />Schwimm um den Verstand<br />Gib jetzt noch nicht auf<br />Ich f&#252;hr dich aus diesem Irrgarten raus<br /><br />Auch im regen<br />Auch im regen<br />Siehst du mich<br />Auch im RegenSelbst im regen<br />Find ich dich<br><br><a href=\"#\" onClick=\"javascript:window.open('badsong.php?songname=Auch im regen&artist=Rosenstolz','aa','width=200,height=100')\">BADSONG</a><br><br><script type=\"text/javascript\"><!--google_ad_client = \"pub-5856745585538965\";google_alternate_ad_url = \"http://www.lyrc.com.ar/banner/index2.php\";google_ad_width = 468;google_ad_height = 60;google_ad_format = \"468x60_as\";google_ad_channel =\"0437267677\";google_color_border = \"333333\";google_color_bg = \"000000\";google_color_link = \"FFFFFF\";google_color_url = \"999999\";google_color_text = \"CCCCCC\";//--></script><script type=\"text/javascript\" src=\"http://pagead2.googlesyndication.com/pagead/show_ads.js\"></script></font><script src=\"http://www.google-analytics.com/urchin.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">_uacct = \"UA-175891-1\";urchinTracker();</script></body></html>";
    private static final String PLAIN = "Wie kannst du dir so sicher sein\n" +
            "Du bist doch viel zu wütend\n" +
            "Um irgendwas zu sehn\n" +
            "\n" +
            "Du schreibst dich selbst mal gross, mal klein\n" +
            "Am Ende ist´s verwirrend\n" +
            "Und sehr schwer zu verstehn\n" +
            "\n" +
            "Ich weiss es wird regnen\n" +
            "Und hört so schnell nicht auf\n" +
            "Glaub mir es wird kälter\n" +
            "Wann hört das wieder auf\n" +
            "\n" +
            "Ist da draussen kein Licht\n" +
            "Wolken nehm dir die Sicht\n" +
            "Auch im Regen\n" +
            "Auch im Regen\n" +
            "Siehst du mich\n" +
            "Wenn dein Boot untergeht\n" +
            "Und du gar nichts mehr verstehst\n" +
            "Auch im Regen\n" +
            "Selbst im Regen\n" +
            "Find ich dich\n" +
            "\n" +
            "Zersäg den Ast auf dem du sitzt\n" +
            "Spring ab,fang endlich an zu laufen\n" +
            "Das kann ich für dich nicht\n" +
            "\n" +
            "Lass von dir hören wenn´s soweit ist\n" +
            "Du musst dich nicht ändern\n" +
            "Es ändert sich für dich\n" +
            "\n" +
            "Ich weiss nur es wird regnen\n" +
            "Und hört so schnell nicht auf\n" +
            "Glaub mir es wird kälter\n" +
            "Wann hört das wieder auf\n" +
            "\n" +
            "Ist da draussen kein Licht\n" +
            "Wolken nehmen die dir Sicht\n" +
            "Auch im Regen\n" +
            "Auch im Regen\n" +
            "Siehst du mich\n" +
            "Wenn dein Boot untergeht\n" +
            "Und du gar nicht´s mehr verstehst\n" +
            "Auch im Regen\n" +
            "Selbst im Regen\n" +
            "Find ich dich\n" +
            "\n" +
            "\n" +
            "Bald siehst du Land\n" +
            "halt bitte noch durch\n" +
            "Ich seh ganz sicher dort hinten ein Licht\n" +
            "Schwimm um den Verstand\n" +
            "Gib jetzt noch nicht auf\n" +
            "Ich führ dich aus diesem Irrgarten raus\n" +
            "\n" +
            "Auch im regen\n" +
            "Auch im regen\n" +
            "Siehst du mich\n" +
            "Auch im RegenSelbst im regen\n" +
            "Find ich dich";

    public void testDecodeEntities() {
        assertEquals("führ", StringHelper.decodeEntities("f&#252;hr"));
    }

    public void testParseHtml() {
        assertEquals(PLAIN, client.parseHtml(HTML));
    }
}
