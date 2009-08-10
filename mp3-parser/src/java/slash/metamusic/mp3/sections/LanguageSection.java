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
 * My instances represent the ID3v2Frame type section
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.2.
 *
 * @author Christian Pesch
 * @version $Id: LanguageSection.java 910 2006-12-23 12:18:38Z cpesch $
 */

public class LanguageSection extends AbstractSection {
    public static final int LANGUAGE_SIZE = 3;

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        try {
            if (offset + LANGUAGE_SIZE <= data.length) {
                String string = StringHelper.trim(new String(data, offset, LANGUAGE_SIZE, ID3v2Header.ISO_8859_1_ENCODING));
                setCode(string);
                return LANGUAGE_SIZE;
            }
        }
        catch (IllegalArgumentException e) {
            // intentionally left empty
        }
        return 0;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        String string = getType() == null ? "XXX" : getType().getCode();
        return string.getBytes(ID3v2Header.ISO_8859_1_ENCODING);
    }

    public String getStringContent() {
        return getType() == null ? "XXX" : getType().getLanguage() + "(" + getType().getCode() + ")";
    }

    // --- get/set object --------------------------------------

    public ISO639Type getType() {
        return type;
    }

    public String getLanguage() {
        return getType() != null ? getType().getLanguage() : "";
    }

    public void setCode(String iso639Code) {
        // MP3-Editor-3.06 and MusicMatch 7.00 use three zeros as language encoding
        if (iso639Code.equals("XXX") || iso639Code.equals("\u0000\u0000\u0000"))
            this.type = null;
        else if (!ISO639Type.isKnownISO639Type(iso639Code))
            throw new IllegalArgumentException("Language type code " + iso639Code + " is not known");
        setType(ISO639Type.getISO639Type(iso639Code));
    }

    public void setLanguage(String language) {
        if (language.equals(""))
            this.type = null;
        else if (!ISO639Type.isKnownISO639Language(language))
            throw new IllegalArgumentException("Language " + language + " is not known");
        setType(ISO639Type.getISO639Language(language));
    }

    public void setType(ISO639Type iso639Code) {
        this.type = iso639Code;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "Language[" +
                "type=" + getType() +
                "]";
    }

    // --- member variables ------------------------------------

    // default for type is XXX, as lame uses it
    protected ISO639Type type = null;
}
