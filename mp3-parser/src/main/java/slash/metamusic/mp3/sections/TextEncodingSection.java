/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.sections;

import slash.metamusic.mp3.ID3v2Frame;
import slash.metamusic.mp3.ID3v2Header;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * My instances represent the ID3v2Frame text encoding byte
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.2.
 *
 * @author Christian Pesch
 * @version $Id: TextEncodingSection.java 925 2006-12-29 14:37:25Z cpesch $
 */

public class TextEncodingSection extends AbstractSection {
    public static final int ENCODING_SIZE = 1;

    /**
     * Encoding to use when converting from Unicode (String) to bytes.
     */
    protected static final String[] ENCODINGS = {ID3v2Header.ISO_8859_1_ENCODING, "UTF16", "UTF-16BE", "UTF-8"};


    protected int getEncodingIndex(String encoding) {
        for (int i = 0; i < ENCODINGS.length; i++) {
            if (ENCODINGS[i].equals(encoding))
                return i;
        }
        return -1;
    }

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        byte encoding = data[offset];
        // default to ISO8859-1 encoding
        if (encoding < 0 || encoding > ENCODINGS.length)
            encoding = 0;
        setEncoding(encoding);
        return ENCODING_SIZE;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        int encodingIndex = getEncodingIndex(getEncoding());
        if (encodingIndex < 0)
            encodingIndex = 0;
        return new byte[]{(byte) encodingIndex};
    }

    public String getStringContent() {
        return null;
    }

    // --- get/set object --------------------------------------

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (getEncodingIndex(encoding) < 0)
            throw new IllegalArgumentException("Encoding '" + encoding + "' is not known");
        this.encoding = encoding;
    }

    private void setEncoding(int encoding) {
        if (encoding < 0 || encoding > ENCODINGS.length)
            throw new IllegalArgumentException("Encoding '" + encoding + "' is not known");
        setEncoding(ENCODINGS[encoding]);
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "TextEncoding[" +
                "encoding=" + getEncoding() +
                "]";
    }

    // --- member variables ------------------------------------

    // ID3v2 2.0 always uses ISO-8859-1 encoding, 3.0 defaults to ISO-8859-1
    protected String encoding = ID3v2Header.ISO_8859_1_ENCODING;
}
