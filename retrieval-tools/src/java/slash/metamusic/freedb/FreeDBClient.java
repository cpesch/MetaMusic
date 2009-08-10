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
import slash.metamusic.util.OperationSystem;
import slash.metamusic.util.URLLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * FreeDBClient implements the connection to a freedb.org server
 * or one of its mirrors. <p>
 * The freedb.org server fully supports the CDDB protocol.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: FreeDBClient.java 959 2007-03-11 08:21:11Z cpesch $
 * @see #setServer(FreeDBServer)
 */

public class FreeDBClient {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(FreeDBClient.class.getName());

    private static final String LINEBREAK = "\r\n";
    private static final String DEFAULT_USER = "unknown";
    private static final String CLIENT = "slash.metamusic.freedb.FreeDBClient";
    private static final String VERSION = "1.0";

    private String user;
    private FreeDBServer server;

    private static final String COMMAND = "@command@";
    private static final String COMMAND_CAT = "cddb lscat";
    private static final String COMMAND_DISCID = "cd ";
    private static final String COMMAND_MSG = "motd";
    private static final String COMMAND_QUERY = "cddb query ";
    private static final String COMMAND_READ = "cddb read ";
    private static final String COMMAND_SITES = "sites";

    private FreeDBCache freeDBCache = new FreeDBCache();
    private boolean useCache = true;


    /**
     * uses FreeDBServer.DEFAULT_SERVER
     *
     * @see FreeDBServer#DEFAULT_SERVER
     */
    public FreeDBClient() {
        this(FreeDBServer.DEFAULT_SERVER, fetchUser());
    }

    public FreeDBClient(FreeDBServer server, String user) {
        setUser(user);
        setServer(server);
    }

    static String fetchUser() {
        String user = System.getProperty("user.name");
        if (user == null)
            user = DEFAULT_USER;
        if (user.indexOf(" ") > -1) {
            StringTokenizer st = new StringTokenizer(user, " ");
            user = st.nextToken();
        }
        if (user.length() < 1)
            user = DEFAULT_USER;
        return user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public FreeDBServer getServer() {
        return server;
    }

    /**
     * Changes the site location used to access the service; the protocol
     * for the server must be <dfn>http</dfn>
     *
     * @throws IllegalArgumentException if the server protocol is not <dfn>http</dfn>
     * @see #getSites()
     * @see FreeDBServer
     */
    public void setServer(FreeDBServer server) throws IllegalArgumentException {
        if (!"http".equals(server.getProtocol())) {
            throw new IllegalArgumentException("given server doesn't support http");
        }
        this.server = server;
    }

    private boolean isReachable() {
        return getServer().isReachable();
    }

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    protected String getQueryTemplate() throws UnknownHostException {
        return "http://" + server.getSite() + ":" +
                server.getPort() + server.getUri() +
                "?cmd=" + COMMAND + "&hello=" + user +
                "+" + InetAddress.getLocalHost().getHostName() +
                "+" + CLIENT + "+" + VERSION +
                "&" + "proto=5";
    }


    /**
     * This method is called for every command supported by the interface
     * and returns the result as it is provided by the service. <p>
     * Currently, this method is implemented to use the HTTP protocol
     * with GET. If other protocols were to be supported (i.e. HTTP POST or
     * Telnet), only this method needs to be changed/overwritten.
     *
     * @param command the entire CLIENT command as specified in the CDDB
     *                protocol; example: "<dfn>cddb lscat</dfn>"
     */
    protected String performCommand(String command) throws IOException {
        String query = getQueryTemplate().replaceFirst(COMMAND, URLEncoder.encode(command, "UTF-8"));
        URL url = new URL(query);
        return URLLoader.getContents(url, true);
    }

    /**
     * Returns a 'message of the day' quote from the freedb server
     */
    public String getMessageOfTheDay() throws IOException {
        return performCommand(COMMAND_MSG);
    }

    /**
     * Returns sites that can be used as a mirror for this service. <p>
     *
     * @see #setServer(FreeDBServer)
     * @see FreeDBServer
     */
    public FreeDBServer[] getSites() throws IOException {
        String result = performCommand(COMMAND_SITES);
        try {
            int code = Integer.parseInt(result.substring(0, 3));
            List<FreeDBServer> servers = new ArrayList<FreeDBServer>();
            if (code == 210) {
                //sites avaliable
                StringTokenizer line = new StringTokenizer(result, LINEBREAK);
                line.nextToken(); // initial line w/ code
                String entry = line.nextToken();
                do {
                    CDDBArgumentParser t = new CDDBArgumentParser(entry);
                    FreeDBServer server = new FreeDBServer(t.nextArgument(),
                            t.nextArgument(), Integer.parseInt(t.nextArgument()),
                            t.nextArgument(), t.nextArgument(), t.nextArgument(),
                            t.getRemainder());
                    servers.add(server);
                    entry = line.nextToken();
                } while (!entry.equals("."));
            } else {
                // probably a 401 code - no site information available
                // let's still return an empty array
                log.severe("no site information available");
            }
            return servers.toArray(new FreeDBServer[servers.size()]);
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new CDDBProtocolException("could not retrieve sites",
                    getQueryTemplate(), COMMAND_SITES, result, e);
        }
    }

    /**
     * Returns the categories supported by the freedb server
     */
    public String[] getCategories() throws IOException {
        String result = performCommand(COMMAND_CAT);
        try {
            List<String> categories = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(result, LINEBREAK);
            st.nextToken(); // ignore the first result line
            while (st.hasMoreTokens()) {
                String item = st.nextToken();
                if (item.equals("."))
                    break;
                categories.add(item);
            }
            return categories.toArray(new String[categories.size()]);
        } catch (Exception e) {
            throw new CDDBProtocolException("could not retrieve categories",
                    getQueryTemplate(), COMMAND_CAT, result, e);
        }
    }

    /**
     * Tries to verify the disc id embedded in the DiscId object
     * by querying the service to recalculate the data.
     *
     * @return true only if the calculated disc id by the service
     *         matches the id stored in the DiscId object
     */
    public boolean verifyDiscID(DiscId discId) throws IOException {
        CDDBArgumentParser p = new CDDBArgumentParser(discId.getFreeDBQueryString());
        p.nextArgument(); // embedded cd
        String command = COMMAND_DISCID + p.getRemainder();
        String result = performCommand(command);
        try {
            int code = Integer.parseInt(result.substring(0, 3));
            if (code != 200)
                return false;
            p = new CDDBArgumentParser(result);
            p.nextArgument(); // code
            p.nextArgument(); // "Disc"
            p.nextArgument(); // "ID"
            p.nextArgument(); // "is"
            return discId.getEncodedDiscId().equals(p.nextArgument());
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new CDDBProtocolException("could not verify disc id",
                    getQueryTemplate(), command, result, e);
        }
    }

    /**
     * Return whether a result for the given <code>DiscId</code> is cached.
     *
     * @param discId the <code>DiscId</code> to search the cache for
     * @return true, if a result for the given <code>DiscId</code> is cached
     * @throws IOException if an error occurs during the search
     */
    public boolean isDiscIdCached(DiscId discId) throws IOException {
        return freeDBCache.peekResult(discId) != null;
    }

    /**
     * Returns <code>CDDBRecord</code>s for the given <code>DiscId</code>.
     *
     * @param discId the <code>DiscId</code> to query FreeDB for
     * @return an array of <code>CDDBRecord</code>s
     * @throws IOException if an error occurs during the query
     */
    public CDDBRecord[] queryDiscId(DiscId discId) throws IOException {
        String command = COMMAND_QUERY + discId.getFreeDBQueryString();

        String result = null;
        if (isUseCache()) {
            result = freeDBCache.peekResult(discId);
            if (result != null)
                log.info("Query for disc id " + discId.getEncodedDiscId() + " is cached");
        }

        if (result == null) {
            result = performCommand(command);
            if (result != null) {
                log.info("Caching query for disc id " + discId.getEncodedDiscId());
                freeDBCache.storeResult(discId, result);
            }
        }

        if (result == null)
            throw new IOException("No result while quering disc id " + discId);

        try {
            int code = Integer.parseInt(result.substring(0, 3));
            List<CDDBRecord> records = new ArrayList<CDDBRecord>();
            if (code == 200) {
                // found exact match
                CDDBArgumentParser item = new CDDBArgumentParser(result.substring(3));
                CDDBRecord r = new CDDBRecord(discId, item.nextArgument(),
                        item.nextArgument(), item.getRemainder());
                records.add(r);
            } else if ((code == 211) || (code == 210)) {
                // found inexact matches (211) or multiple excat matches (210)
                // code 210 is CDDB protocol level 4
                StringTokenizer line = new StringTokenizer(result, LINEBREAK);
                line.nextToken(); // ignore the first line
                while (line.hasMoreTokens()) {
                    String entry = line.nextToken();
                    if (entry.equals("."))
                        break;
                    CDDBArgumentParser item = new CDDBArgumentParser(entry);
                    CDDBRecord r = new CDDBRecord(discId, item.nextArgument(),
                            item.nextArgument(), item.getRemainder(), code == 210);
                    records.add(r);
                }
            } else {
                // nothing of value found; possible codes:
                // 202 no match found
                // 403 database entry is corrupt
                // 409 no handshake
                log.severe("No information for disc id " + discId.getEncodedDiscId() + " found");
            }
            return records.toArray(new CDDBRecord[records.size()]);
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new CDDBProtocolException("could not query disc id",
                    getQueryTemplate(), command, result, e);
        }
    }

    /**
     * It is suggested that the given record was obtained through a call
     * to queryDiscId(), so that the record is known to exist.
     *
     * @return a CDDBEntry instance
     * @throws CDDBProtocolException if the record doesn't exist
     * @see CDDBEntry
     */
    public CDDBEntry readCDInfo(CDDBRecord record) throws IOException {
        String command = COMMAND_READ + record.getCategory() + " " + record.getDiscId();

        String result = null;
        if (isUseCache()) {
            result = freeDBCache.peekResult(record);
            if (result != null)
                log.info("Xmcd for disc id " + record.getDiscId() + " is cached");
        }

        if (result == null) {
            result = performCommand(command);
            if (result != null) {
                log.info("Caching Xmcd for disc id " + record.getDiscId());
                freeDBCache.storeResult(record, result);
            }
        }

        try {
            int code = Integer.parseInt(result.substring(0, 3));
            if (code == 210) {
                BufferedReader in = new BufferedReader(new StringReader(result));
                in.readLine(); // ignore first line
                String fileContent = null;
                while (in.ready()) {
                    if (fileContent == null) {
                        fileContent = "";
                    } else {
                        fileContent += LINEBREAK;
                    }
                    String line = in.readLine();
                    if (line.length() > 255)
                        throw new CDDBProtocolException("a line has been found that exceeds 255 characters",
                                getQueryTemplate(), command, result, null);
                    if (line.equals(".")) break;
                    fileContent += line;
                }
                return new CDDBEntry(record, fileContent);
            } else {
                String msg;
                switch (code) {
                    case 401:
                        msg = "Specified CDDB entry not found.";
                        break;
                    case 402:
                        msg = "Server error.";
                        break;
                    case 403:
                        msg = "Database entry is corrupt.";
                        break;
                    case 409:
                        msg = "No handshake.";
                        break;
                    default:
                        msg = "Unknown error message";
                }
                throw new RuntimeException(msg);
            }
        } catch (Exception e) {
            if (e instanceof IOException) throw (IOException) e;
            throw new CDDBProtocolException("could not read cd",
                    getQueryTemplate(), command, result, e);
        }
    }

    public CDDBRecord[] parseDatafile(DiscId discId, String content) throws IOException {
        List<CDDBRecord> records = new ArrayList<CDDBRecord>();
        // found exact match
        CDDBArgumentParser item = new CDDBArgumentParser(content);
        CDDBRecord r = new CDDBRecord(discId, item.nextArgument(), item.nextArgument(), item.getRemainder());
        records.add(r);
        return records.toArray(new CDDBRecord[records.size()]);
    }

    public static void main(String[] args) throws Exception {
        FreeDBClient client = new FreeDBClient();
        System.out.println("Server: " + client.getServer());
        System.out.println("User: " + client.getUser());

        if (client.isReachable()) {
            System.out.println("MOTD:   " + client.getMessageOfTheDay());

            FreeDBServer[] servers = client.getSites();
            for (int i = 0; i < servers.length; i++) {
                FreeDBServer server = servers[i];
                System.out.println(i + ". server: " + server);
            }

            String[] categories = client.getCategories();
            for (int i = 0; i < categories.length; i++) {
                String category = categories[i];
                System.out.println(i + ". category: " + category);
            }
        }

        String device = args.length == 0 ? OperationSystem.getDefaultDeviceName() : args[0];
        DiscId discId = new DiscId(new File(device));
        CDDBRecord[] records = client.queryDiscId(discId);
        for (int i = 0; i < records.length; i++) {
            CDDBRecord record = records[i];
            System.out.println(i + ". record: " + record);
            CDDBEntry entry = client.readCDInfo(record);
            System.out.println(i + ". entry: " + entry);
        }
        System.exit(0);
    }
}
