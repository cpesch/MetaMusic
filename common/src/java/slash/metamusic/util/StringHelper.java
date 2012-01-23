/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2004 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Some useful functions for
 * <ul>
 * <li>strings
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: StringHelper.java 961 2007-03-25 16:46:20Z cpesch $
 */

public class StringHelper {

    public static String trim(String string) {
        if (string == null)
            return null;
        StringBuffer buffer = new StringBuffer(string);
        for (int i = 0; i < buffer.length(); i++) {
            char c = buffer.charAt(i);
            if (Character.isWhitespace(c))
                buffer.setCharAt(i, ' ');
        }
        return buffer.toString().trim();
    }

    public static String trimButKeepLineFeeds(String string) {
        if (string == null)
            return null;
        StringBuffer buffer = new StringBuffer(string);
        for (int i = 0; i < buffer.length(); i++) {
            char c = buffer.charAt(i);
            if (Character.isWhitespace(c) && c != '\r' && c != '\n')
                buffer.setCharAt(i, ' ');
        }
        return buffer.toString().trim();
    }

    private static final Map<String, String> entities = new HashMap<String, String>();

    static {
        // Quotation mark
        entities.put("quot", "\"");
        // Ampersand
        entities.put("amp", "\u0026");
        // Less than
        entities.put("lt", "\u003C");
        // Greater than
        entities.put("gt", "\u003E");
        // Nonbreaking space
        entities.put("nbsp", "\u00A0");
        // Inverted exclamation point
        entities.put("iexcl", "\u00A1");
        // Cent sign
        entities.put("cent", "\u00A2");
        // Pound sign
        entities.put("pound", "\u00A3");
        // General currency sign
        entities.put("curren", "\u00A4");
        // Yen sign
        entities.put("yen", "\u00A5");
        // Broken vertical bar
        entities.put("brvbar", "\u00A6");
        // Section sign
        entities.put("sect", "\u00A7");
        // Umlaut
        entities.put("uml", "\u00A8");
        // Copyright
        entities.put("copy", "\u00A9");
        // Feminine ordinal
        entities.put("ordf", "\u00AA");
        // Left angle quote
        entities.put("laquo", "\u00AB");
        // Not sign
        entities.put("not", "\u00AC");
        // Soft hyphen
        entities.put("shy", "\u00AD");
        // Registered trademark
        entities.put("reg", "\u00AE");
        // Macron accent
        entities.put("macr", "\u00AF");
        // Degree sign
        entities.put("deg", "\u00B0");
        // Plus or minus
        entities.put("plusmn", "\u00B1");
        // Superscript 2
        entities.put("sup2", "\u00B2");
        // Superscript 3
        entities.put("sup3", "\u00B3");
        // Acute accent
        entities.put("acute", "\u00B4");
        // Micro sign (Greek mu)
        entities.put("micro", "\u00B5");
        // Paragraph sign
        entities.put("para", "\u00B6");
        // Middle dot
        entities.put("middot", "\u00B7");
        // Cedilla
        entities.put("cedil", "\u00B8");
        // Superscript 1
        entities.put("sup1", "\u00B9");
        // Masculine ordinal
        entities.put("ordm", "\u00BA");
        // Right angle quote
        entities.put("raquo", "\u00BB");
        // Fraction one-fourth
        entities.put("frac14", "\u00BC");
        // Fraction one-half
        entities.put("frac12", "\u00BD");
        // Fraction three-fourths
        entities.put("frac34", "\u00BE");
        // Inverted question mark
        entities.put("iquest", "\u00BF");
        // Capital A, grave accent
        entities.put("Agrave", "\u00C0");
        // Capital A, acute accent
        entities.put("Aacute", "\u00C1");
        // Capital A, circumflex accent
        entities.put("Acirc", "\u00C2");
        // Capital A, tilde
        entities.put("Atilde", "\u00C3");
        // Capital A, umlaut
        entities.put("Auml", "\u00C4");
        // Capital A, ring
        entities.put("Aring", "\u00C5");
        // Capital AE ligature
        entities.put("AElig", "\u00C6");
        // Capital C, cedilla
        entities.put("Ccedil", "\u00C7");
        // Capital E, grave accent
        entities.put("Egrave", "\u00C8");
        // Capital E, acute accent
        entities.put("Eacute", "\u00C9");
        // Capital E, circumflex accent
        entities.put("Ecirc", "\u00CA");
        // Capital E, umlaut
        entities.put("Euml", "\u00CB");
        // Capital I, grave accent
        entities.put("Igrave", "\u00CC");
        // Capital I, acute accent
        entities.put("Iacute", "\u00CD");
        // Capital I, circumflex accent
        entities.put("Icirc", "\u00CE");
        // Capital I, umlaut
        entities.put("Iuml", "\u00CF");
        // Capital eth, Icelandic
        entities.put("ETH", "\u00D0");
        // Capital N, tilde
        entities.put("Ntilde", "\u00D1");
        // Capital O, grave accent
        entities.put("Ograve", "\u00D2");
        // Capital O, acute accent
        entities.put("Oacute", "\u00D3");
        // Capital O, circumflex accent
        entities.put("Ocirc", "\u00D4");
        // Capital O, tilde
        entities.put("Otilde", "\u00D5");
        // Capital O, umlaut
        entities.put("Ouml", "\u00D6");
        // Multiply sign
        entities.put("times", "\u00D7");
        // Capital O, slash
        entities.put("Oslash", "\u00D8");
        // Capital U, grave accent
        entities.put("Ugrave", "\u00D9");
        // Capital U, acute accent
        entities.put("Uacute", "\u00DA");
        // Capital U, circumflex accent
        entities.put("Ucirc", "\u00DB");
        // Capital U, umlaut
        entities.put("Uuml", "\u00DC");
        // Capital Y, acute accent
        entities.put("Yacute", "\u00DD");
        // Capital thorn, Icelandic
        entities.put("THORN", "\u00DE");
        // Small sz ligature, German
        entities.put("szlig", "\u00DF");
        // Small a, grave accent
        entities.put("agrave", "\u00E0");
        // Small a, acute accent
        entities.put("aacute", "\u00E1");
        // Small a, circumflex accent
        entities.put("acirc", "\u00E2");
        // Small a, tilde
        entities.put("atilde", "\u00E3");
        // Small a, umlaut
        entities.put("auml", "\u00E4");
        // Small a, ring
        entities.put("aring", "\u00E5");
        // Small ae ligature
        entities.put("aelig", "\u00E6");
        // double low-9 quotation mark
        entities.put("bdquo", "\u201E");
        // Small c, cedilla
        entities.put("ccedil", "\u00E7");
        // Small e, grave accent
        entities.put("egrave", "\u00E8");
        // Small e, acute accent
        entities.put("eacute", "\u00E9");
        // Small e, circumflex accent
        entities.put("ecirc", "\u00EA");
        // Small e, umlaut
        entities.put("euml", "\u00EB");
        // Small i, grave accent
        entities.put("igrave", "\u00EC");
        // Small i, acute accent
        entities.put("iacute", "\u00ED");
        // Small i, circumflex accent
        entities.put("icirc", "\u00EE");
        // Small i, umlaut
        entities.put("iuml", "\u00EF");
        // Small eth, Icelandic
        entities.put("eth", "\u00F0");
        // Small n, tilde
        entities.put("ntilde", "\u00F1");
        // Small o, grave accent
        entities.put("ograve", "\u00F2");
        // Small o, acute accent
        entities.put("oacute", "\u00F3");
        // Small o, circumflex accent
        entities.put("ocirc", "\u00F4");
        // Small o, tilde
        entities.put("otilde", "\u00F5");
        // Small o, umlaut
        entities.put("ouml", "\u00F6");
        // Division sign
        entities.put("divide", "\u00F7");
        // Small o, slash
        entities.put("oslash", "\u00F8");
        // Small u, grave accent
        entities.put("ugrave", "\u00F9");
        // Small u, acute accent
        entities.put("uacute", "\u00FA");
        // Small u, circumflex accent
        entities.put("ucirc", "\u00FB");
        // Small u, umlaut
        entities.put("uuml", "\u00FC");
        // Small y, acute accent
        entities.put("yacute", "\u00FD");
        // Small thorn, Icelandic
        entities.put("thorn", "\u00FE");
        // Small y, umlaut
        entities.put("yuml", "\u00FF");
    }

    public static String decodeEntities(String str) {
        StringBuilder builder = new StringBuilder();
        int semicolonIndex = 0;
        while (semicolonIndex < str.length()) {
            int ampersandIndex = str.indexOf("&", semicolonIndex);
            if (ampersandIndex == -1) {
                builder.append(str.substring(semicolonIndex, str.length()));
                break;
            }
            builder.append(str.substring(semicolonIndex, ampersandIndex));
            semicolonIndex = str.indexOf(";", ampersandIndex);
            if (semicolonIndex == -1) {
                builder.append(str.substring(ampersandIndex, str.length()));
                break;
            }

            String tok = str.substring(ampersandIndex + 1, semicolonIndex);
            if (tok.charAt(0) == '#') {
                tok = tok.substring(1);
                try {
                    int radix = 10;
                    if (tok.trim().charAt(0) == 'x') {
                        radix = 16;
                        tok = tok.substring(1, tok.length());
                    }
                    builder.append((char) Integer.parseInt(tok, radix));
                } catch (NumberFormatException exp) {
                    builder.append('?');
                }
            } else {
                tok = entities.get(tok);
                if (tok != null)
                    builder.append(tok);
                else
                    builder.append('?');
            }
            semicolonIndex++;
        }
        return builder.toString();
    }

    /**
     * Creates a mixed mode string out of a string. Each space separated
     * substring will have an uppercase first letter and a lowercase rest.
     *
     * @param string the string to create a mixed mode string from
     * @return a mixed mode string out of a string. Each space separated
     *         substring will have an uppercase first letter and a lowercase rest
     */
    public static String toMixedCase(String string) {
        StringBuilder builder = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(string, "_ ", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.length() > 1)
                builder.append(token.substring(0, 1).toUpperCase()).append(token.substring(1).toLowerCase());
            else
                builder.append(token);
        }
        return builder.toString();
    }

    public static String replaceWhitespaces(String string) {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (Character.isWhitespace(c))
                builder.setCharAt(i, '_');
        }
        return builder.toString();
    }

    public static String removeAdjacentUnderscores(String string) {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < builder.length() - 1; i++) {
            if (builder.charAt(i) == '_' && builder.charAt(i + 1) == '_') {
                builder.deleteCharAt(i);
                i--;
            }
        }
        return builder.toString();
    }

    public static String removeNonLetterOrDigits(String string) {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (!(Character.isLetterOrDigit(c) || Character.isWhitespace(c)) || c == '\'') {
                builder.deleteCharAt(i);
                i--;
            }
        }
        return builder.toString();
    }

    public static String formatNumber(long number, int digits) {
        return formatNumber(number, digits, '0');
    }

    public static String formatNumber(long number, int length, char fill) {
        return formatString(Long.toString(number), length, fill, true);
    }

    public static String formatString(String string, int length, char fill, boolean rightAligned) {
        StringBuilder builder = new StringBuilder(string);
        while (builder.length() < length) {
            if (rightAligned)
                builder.insert(0, fill);
            else
                builder.append(fill);
        }
        return builder.toString();
    }

    public static String shortenString(String string, int minimumLength, int lengthToShortenBy) {
        int minIndex = Math.min(string.length(), minimumLength);
        int maxIndex = string.length() - lengthToShortenBy;
        return string.substring(0, Math.max(minIndex, maxIndex));
    }

    public static boolean isANumber(String string) {
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String replaceForURI(String string) {
        string = string.trim();
        string = removeNonLetterOrDigits(string);
        string = replaceWhitespaces(string);
        string = removeAdjacentUnderscores(string);
        return string.trim();
    }
}
