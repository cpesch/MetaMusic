/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * Writes to a logger using level info
 */

public class LoggingWriter extends Writer {
    private Logger delegate;

    public LoggingWriter(Logger delegate) {
        this.delegate = delegate;
    }

    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        String message = new String(cbuf, off, len);
        // cut off trailing CR / CRLF
        if (message.endsWith("\r\n")) {
            message = message.substring(0, message.length() - 2);
        } else if (message.endsWith("\n")) {
            message = message.substring(0, message.length() - 1);
        }
        delegate.info(message);
    }
}