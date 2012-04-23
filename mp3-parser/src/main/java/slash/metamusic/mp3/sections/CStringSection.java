/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.sections;

import slash.metamusic.mp3.ID3v2Header;
import slash.metamusic.util.StringHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A section that contains a string in C format, i.e.
 * it ends with a zero 0x00 byte.
 *
 * @author Christian Pesch
 * @version $Id: CStringSection.java 958 2007-02-28 14:44:37Z cpesch $
 */

public abstract class CStringSection extends AbstractSection {

    private int findNullBytesIndex(byte[] bytes, int startIndex, int nullByteCount) {
        int endIndex = startIndex;
        int nullBytesFound = 0;
        for (; endIndex < bytes.length; endIndex++) {
            byte aByte = bytes[endIndex];

            // count null bytes
            if (aByte == 0)
                nullBytesFound++;
            else
                nullBytesFound = 0;

            // if found return
            if (nullByteCount == nullBytesFound)
                break;
        }
        return endIndex;
    }

    protected ParseResult parse(byte[] data, int startIndex, String encoding) throws IOException {
        int nullByteLength = encoding.equals(ID3v2Header.ISO_8859_1_ENCODING) ? 1 : 2;
        int endIndex = findNullBytesIndex(data, startIndex, nullByteLength);
        if (endIndex == data.length) {
            // there are files out there that claim to be encoded in UTF-8
            // but which use only one $00 as termination
            if (nullByteLength == 2 && findNullBytesIndex(data, startIndex, 1) == startIndex) {
                log.warning("Only one null byte at the end of " + encoding + " string");
                nullByteLength = 1;
            }
            return new ParseResult(null, nullByteLength);
        }
        String string = StringHelper.trim(new String(data, startIndex, endIndex - startIndex, encoding));
        return new ParseResult(string, endIndex - startIndex + nullByteLength);
    }

    protected byte[] getBytes(String string, String encoding) throws UnsupportedEncodingException {
        if (string != null) {
            // for Unicode, Strings have to start with the Unicode byte order mark (BOM)
            // this is $FF FE or $FE FF; they are always finished by a null byte $00 00
            if (!encoding.equals(ID3v2Header.ISO_8859_1_ENCODING))
                string = "\uFFFE" + string;
            string = string + "\u0000";
        }
        byte[] bytes = string != null ? string.getBytes(encoding) : new byte[0];
        return bytes;
    }

    protected class ParseResult {
        private String string;
        private int length;

        private ParseResult(String string, int length) {
            this.string = string;
            this.length = length;
        }

        protected String getString() {
            return string;
        }

        protected int getLength() {
            return length;
        }
    }
}
