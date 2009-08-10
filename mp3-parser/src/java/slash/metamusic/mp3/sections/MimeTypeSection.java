/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.sections;

import slash.metamusic.mp3.ID3v2Frame;
import slash.metamusic.mp3.ID3v2Header;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * My instances represent the ID3v2Frame MIME type information section
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.14.
 *
 * @author Christian Pesch
 * @version $Id: MimeTypeSection.java 905 2006-12-22 11:36:31Z cpesch $
 */

public class MimeTypeSection extends CStringSection {

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        ParseResult result = parse(data, offset, ID3v2Header.ISO_8859_1_ENCODING);
        String mimeType = result.getString();
        if (mimeType.length() == 0)
            mimeType = "unknown";
        // in ID3v2.2 not a MIME type but file extensions are written
        if (mimeType.indexOf("/") == -1)
            mimeType = "image/" + mimeType.toLowerCase();
        try {
            setMimeType(new MimeType(mimeType));
        } catch (MimeTypeParseException e) {
            throw new IOException("Cannot parse MIME type " + mimeType + ": " + e.getMessage());
        }
        return result.getLength();
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        String string = getStringContent();
        if (string == null)
            return new byte[0];
        // in ID3v2.2 not a MIME type but file extensions are written
        if (frame.getVersion().isObsolete()) {
            string = string.substring(string.indexOf("/") + 1).toUpperCase();
            if (string.length() > 3)
                string = string.substring(0, 3);
        }
        return getBytes(string, ID3v2Header.ISO_8859_1_ENCODING);
    }

    public String getStringContent() {
        MimeType mimeType = getMimeType();
        return mimeType != null ? mimeType.toString() : null;
    }

    // --- get/set object --------------------------------------

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "MimeType[" +
                "mimeType=" + getMimeType() +
                "]";
    }

    // --- member variables ------------------------------------

    protected MimeType mimeType;
}
