/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.coverdb;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import slash.metamusic.distance.Levenshtein;
import slash.metamusic.util.DiscIndexHelper;
import slash.metamusic.util.URLLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

/**
 * A client that calls amazon for music information.
 *
 * @author Christian Pesch
 * @version $Id: AmazonMusicClient.java 925 2006-12-29 14:37:25Z cpesch $
 */

public class AmazonMusicClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(AmazonMusicClient.class.getName());

    private static final String WEBSERVICE_URL = "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&SubscriptionId=0RD4MWP106NB420YMMR2&Operation=ItemSearch&SearchIndex=Music&Keywords=";
    private static final Namespace ns = Namespace.getNamespace("http://webservices.amazon.com/AWSECommerceService/2005-10-05");
    private static long lastWebserviceCall = 0;

    private static String encode(String request) throws UnsupportedEncodingException {
        return URLEncoder.encode(request, "UTF-8");
    }

    private static URL asURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    protected Document fetchDocument(String url) throws IOException {
        SAXBuilder builder = new SAXBuilder();
        URL queryUrl = asURL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(queryUrl.openStream()));
        try {
            return builder.build(in);
        }
        catch (JDOMException e) {
            throw new IOException("Cannot build up document for " + url + ": " + e.getMessage());
        } finally {
            in.close();
        }
    }

    public SearchResult search(String artist, String album) throws IOException {
        if (artist == null || album == null)
            return null;
        artist = artist.trim();
        album = DiscIndexHelper.removeDiscIndexPostfix(album);
        album = album.trim();

        Document doc1, doc2;
        synchronized (this) {
            // ensure we're doing not more than 1 call per second
            while (true) {
                long wait = lastWebserviceCall + 1000 - System.currentTimeMillis();
                if (wait <= 0)
                    break;

                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    // doesn't matter
                }
            }

            try {
                doc1 = fetchDocument(WEBSERVICE_URL + encode(artist + " " + album) + "&ResponseGroup=Images");
                doc2 = fetchDocument(WEBSERVICE_URL + encode(artist + " " + album));
            }
            finally {
                lastWebserviceCall = System.currentTimeMillis();
            }
        }

        List<Element> list1 = doc1.getRootElement().getChild("Items", ns).getChildren("Item", ns);
        Element items2 = doc2.getRootElement().getChild("Items", ns);
        List<Element> list2 = items2.getChildren("Item", ns);
        log.fine("Amazon music search for '" + artist + "' and '" + album + "' has " + items2.getChild("TotalResults", ns).getText() + " results");

        for (Element e1 : list1) {
            for (Element e2 : list2) {
                if (e1.getChild("MediumImage", ns) != null &&
                        e1.getChild("ASIN", ns).getText().equals(e2.getChild("ASIN", ns).getText())) {
                    SearchResult result = new SearchResult();

                    Element attributes = e2.getChild("ItemAttributes", ns);
                    if (attributes.getChild("Artist", ns) != null) {
                        result.artist = attributes.getChild("Artist", ns).getText();
                    } else if (attributes.getChild("Creator", ns) != null) {
                        result.artist = attributes.getChild("Creator", ns).getText();
                    } else if (attributes.getChild("Author", ns) != null) {
                        result.artist = attributes.getChild("Author", ns).getText();
                    }

                    result.album = attributes.getChild("Title", ns).getText();

                    if (attributes.getChild("Manufacturer", ns) != null) {
                        result.publisher = attributes.getChild("Manufacturer", ns).getText();
                    }

                    if (e1.getChild("SmallImage", ns) != null) {
                        result.smallImageUrl = asURL(e1.getChild("SmallImage", ns).getChild("URL", ns).getText());
                    }

                    result.mediumImageUrl = asURL(e1.getChild("MediumImage", ns).getChild("URL", ns).getText());

                    if (e1.getChild("LargeImage", ns) != null) {
                        result.largeImageUrl = asURL(e1.getChild("LargeImage", ns).getChild("URL", ns).getText());
                    }

                    if (result.artist != null && Levenshtein.distance(artist, result.artist) < 3 &&
                            result.album != null && Levenshtein.distance(album, result.album) < 3) {
                        log.info("Amazon music search for '" + artist + "' and '" + album + "' result is " + result);
                        return result;
                    } else
                        log.fine("Skipping result for '" + artist + "' and '" + album + "': " + result);
                }
            }
        }
        return null;
    }

    public byte[] downloadCover(String artist, String album) {
        try {
            SearchResult result = search(artist, album);
            if (result != null)
                return result.getLargestAvailableImage();
        } catch (IOException e) {
            log.severe("Cannot search cover for '" + artist + "' and '" + album + ": " + e.getMessage());
        }
        return null;
    }

    public String searchPublisher(String artist, String album) {
        try {
            SearchResult result = search(artist, album);
            if (result != null)
                return result.publisher;
        } catch (IOException e) {
            log.severe("Cannot search publisher for '" + artist + "' and '" + album + ": " + e.getMessage());
        }
        return null;
    }

    public static class SearchResult {
        public String artist;
        public String album;
        public String publisher;
        public URL smallImageUrl;
        public URL mediumImageUrl;
        public URL largeImageUrl;

        private byte[] getBytes(URL url) {
            try {
                if (url != null)
                    return URLLoader.getContents(url.openStream());
            } catch (IOException e) {
                log.severe("Cannot fetch " + url + ": " + e.getMessage());
            }
            return null;
        }

        public byte[] getSmallImage() {
            return getBytes(smallImageUrl);
        }

        public byte[] getMediumImage() {
            return getBytes(mediumImageUrl);
        }

        public byte[] getLargeImage() {
            return getBytes(largeImageUrl);
        }

        public byte[] getLargestAvailableImage() {
            byte[] result = getLargeImage();
            if (result == null)
                result = getMediumImage();
            if (result == null)
                result = getSmallImage();
            return result;
        }

        public String toString() {
            return artist + " - " + album + " - " + publisher + "\n" + smallImageUrl + "\n" + mediumImageUrl + "\n" + largeImageUrl;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("slash.metamusic.coverdb.AmazonMusicClient <artist> <album>");
            System.exit(1);
        }

        AmazonMusicClient client = new AmazonMusicClient();
        SearchResult result = client.search(args[0], args[1]);
        if (result != null) {
            byte[] largestAvailableImage = result.getLargestAvailableImage();
            if (largestAvailableImage != null)
                System.out.println("Largest available image has " + largestAvailableImage.length + " bytes");
        }
        System.exit(0);
    }
}
