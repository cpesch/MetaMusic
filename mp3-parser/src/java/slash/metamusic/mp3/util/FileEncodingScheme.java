/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.BitSet;

/**
 * This class provides a encoding scheme for file name strings.
 * An encoded string should be a valid path name on all target platforms.
 *
 * @author Christian Pesch
 * @version $Id: FileEncodingScheme.java 384 2004-10-15 15:21:10Z cpesch $
 */

public class FileEncodingScheme {

    // used by encode() to determine which characters to escape:
    protected BitSet dontNeedEncoding = new BitSet(256);

    static final private int caseDiff = ('a' - 'A');

    // character to use for escaping dangerous chars (must be a valid URL character, so don't use '%'):
    protected char escapeChar = '_';

    public FileEncodingScheme() {
        int i;
        for (i = 'a'; i <= 'z'; i++)
            doesNotNeedEncoding(i);
        for (i = 'A'; i <= 'Z'; i++)
            doesNotNeedEncoding(i);
        for (i = '0'; i <= '9'; i++)
            doesNotNeedEncoding(i);
        doesNotNeedEncoding('&');
        doesNotNeedEncoding('+');
        doesNotNeedEncoding('-');
        doesNotNeedEncoding('_');
        doesNotNeedEncoding('.');
        doesNotNeedEncoding(',');
        doesNotNeedEncoding(',');
        doesNotNeedEncoding('\'');
        doesNotNeedEncoding(' ');

        needsEncoding(escapeChar);
    }

    /**
     * Make sure that the given character will be not encoded in the
     * receivers encoding scheme
     */
    public void doesNotNeedEncoding(char ch) {
        dontNeedEncoding.set(ch);
    }

    /**
     * Make sure that the given character will be not encoded in the
     * receivers encoding scheme
     */
    public void doesNotNeedEncoding(int i) {
        doesNotNeedEncoding((char) i);
    }

    /**
     * Make sure that the given character will be encoded in the
     * receivers encoding scheme
     */
    public void needsEncoding(char ch) {
        dontNeedEncoding.clear(ch);
    }

    /**
     * Encode the given String in such a way that it becomes a valid file name.
     *
     * @param s <code>String</code> to be translated.
     * @return the translated <code>String</code>.
     */
    public String encode(String s) {
        byte[] bytes = s.getBytes();
        StringBuffer out = new StringBuffer(bytes.length);

        for (int i = 0; i < bytes.length; i++) {

            int c = BitConversion.unsignedByteToInt(bytes[i]);

            if (dontNeedEncoding.get(c)) {
                out.append((char) c);
            } else if ((char) c == escapeChar) {
                out.append(escapeChar);
                out.append(escapeChar);
            } else {
                out.append(escapeChar);
                char ch = Character.forDigit((c >> 4) & 0xF, 16);
                // converting to use uppercase letter as part of
                // the hex value if ch is a letter.
                if (Character.isLetter(ch)) {
                    ch -= caseDiff;
                }
                out.append(ch);
                ch = Character.forDigit(c & 0xF, 16);
                if (Character.isLetter(ch)) {
                    ch -= caseDiff;
                }
                out.append(ch);
            }
        }

        return out.toString();
    }

    /**
     * Reverse the effect of #encode()
     *
     * @param s <code>String</code> returned by #encode()
     * @return the string originally given to #encode().
     */
    public String decode(String s) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StringReader reader = new StringReader(s);

        try {
            int c = reader.read();
            while (c != -1) {
                int decoded;

                if (c == escapeChar) {
                    int hi = reader.read();
                    if (hi == -1 || (char) hi == escapeChar) {
                        decoded = c;
                    } else {
                        int lo = reader.read();
                        if (lo == -1) {
                            baos.write(escapeChar);
                            decoded = hi;
                        } else {
                            hi = Character.digit((char) hi, 16);
                            lo = Character.digit((char) lo, 16);
                            decoded = (hi << 4 | lo);
                        }
                    }
                } else {
                    decoded = c;
                }

                baos.write(decoded);
                c = reader.read();
            }

            return new String(baos.toByteArray());
        } catch (IOException e) {
            // can not happen since no one else may closes this StringReader
            e.printStackTrace();
            return null;
        }
    }

}
