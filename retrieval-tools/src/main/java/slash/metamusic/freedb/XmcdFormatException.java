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

/**
 * Thrown to indicate that parsing the Xmcd format resulted in errors.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: XmcdFormatException.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class XmcdFormatException extends RuntimeException {
    private String location;

    public XmcdFormatException() {
    }

    public XmcdFormatException(String message, String location, Throwable cause) {
        super(message, cause);
        this.location = location;
    }

    /**
     * Returns either the DiskID or the File name that the exception is
     * associated with.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}