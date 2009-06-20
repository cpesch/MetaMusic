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

import java.io.IOException;

/**
 * CDDBProtocolException is thrown when the response from a FreeDB
 * source did not comply to the expected protocol.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: CDDBProtocolException.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class CDDBProtocolException extends IOException {
    private String protocol;
    private String query;
    private String result;

    public CDDBProtocolException() {
    }

    public CDDBProtocolException(String message) {
        super(message);
    }

    public CDDBProtocolException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    public CDDBProtocolException(String message, String protocol,
                                 String query, String result, Throwable cause) {
        super(message);
        initCause(cause);
        setProtocol(protocol);
        setQuery(query);
        setResult(result);
    }

    /**
     * returns information about the protocol used (URL, port, POST/GET-method if applicable)
     */
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * returns the query sent to the server
     */
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * returns the result received from the server - if available
     */
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
