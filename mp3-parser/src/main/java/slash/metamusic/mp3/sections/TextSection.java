/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.sections;

import slash.metamusic.mp3.ID3v2Frame;
import slash.metamusic.mp3.ID3v2Header;
import slash.metamusic.util.StringHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * My instances represent the ID3v2Frame text information section
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.2.
 *
 * @author Christian Pesch
 * @version $Id: TextSection.java 942 2007-01-10 17:11:12Z cpesch $
 */

public class TextSection extends AbstractSection {

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        String encoding = frame.getTextEncoding();
        // amount of bytes for String content
        int dataLength = data.length - offset;
        // amount of bytes to put into new String(...)
        int toStringLength = dataLength;
        if (dataLength > 0) {
            // for Unicode, Strings have to start with the Unicode byte order mark (BOM)
            // this is $FF FE or $FE FF and they are finished by a null byte $00 00
            // the BOM is automatically removed by new String(... , encoding=UTF16)
            // we strip off the null byte here
            if (!encoding.equals(ID3v2Header.ISO_8859_1_ENCODING)) {
                if (data.length >= 2 && data[data.length - 2] == 0 && data[data.length - 1] == 0) {
                    toStringLength -= 2;
                } else
                    // WinAmp 5.21 does not write a null byte $00 00
                    log.warning("No null byte at the end of " + encoding + " string");
            }
        }
        // keep the line feeds of lyrics USLT frames
        String string = StringHelper.trimButKeepLineFeeds(new String(data, offset, toStringLength, encoding));
        setText(string);
        return dataLength;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        String text = getText();
        String encoding = frame.getTextEncoding();
        if (!encoding.equals(ID3v2Header.ISO_8859_1_ENCODING))
            text = text + "\u0000\u0000";
        return text != null ? text.getBytes(encoding) : new byte[0];
    }

    public String getStringContent() {
        return StringHelper.trim(getText());
    }

    // --- get/set object --------------------------------------

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "Text[" +
                "text=" + getText() +
                "]";
    }

    // --- member variables ------------------------------------

    protected String text = "";
}
