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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A FreeDBServer represents a server that can act as a mirror to a
 * FreeDB service. <p>
 * This class has all elements as they can be retrieved from a CDDB
 * service supporting protocol level 3-5.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: FreeDBServer.java 743 2006-03-17 13:49:36Z cpesch $
 * @see FreeDBClient#setServer(FreeDBServer)
 * @see FreeDBClient#getSites()
 */

public class FreeDBServer {

    /**
     * the default server: freedb.freedb.org with http on port 80
     */
    public static final FreeDBServer DEFAULT_SERVER =
            new FreeDBServer("freedb.freedb.org", "http", 80, "/~cddb/cddb.cgi",
                    "N000.00", "W000.00",
                    "Random freedb server"
            );

    private String site;
    private String protocol;
    private int port;
    private String uri;
    private String latitude;
    private String longitude;
    private String description;

    public FreeDBServer(String site, String protocol, int port, String uri,
                        String latitude, String longitude,
                        String description) {
        setSite(site);
        setProtocol(protocol);
        setPort(port);
        setUri(uri);
        setLatitude(latitude);
        setLongitude(longitude);
        setDescription(description);
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isReachable() {
        try {
            return InetAddress.getByName(getSite()) != null;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public String toString() {
        return super.toString() + " - " +
                "Site: " + getSite() +
                ", Protocol: " + getProtocol() +
                ", Port: " + getPort() +
                ", Uri: " + getUri() +
                ", Latitude: " + getLatitude() +
                ", Longitude: " + getLongitude() +
                ", Description: " + getDescription();
    }
}