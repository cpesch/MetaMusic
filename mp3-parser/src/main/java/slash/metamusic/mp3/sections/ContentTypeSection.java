/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.sections;

import slash.metamusic.mp3.ID3v2Frame;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * My instances represent the ID3v2Frame content type byte
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.9.
 *
 * @author Christian Pesch
 * @version $Id: ContentTypeSection.java 925 2006-12-29 14:37:25Z cpesch $
 */

public class ContentTypeSection extends AbstractSection {
    public static final int TYPE_SIZE = 1;

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        byte type = data[offset];
        if (type < 0x00 || type > 0x09)
            type = 0;
        setType(type);
        return TYPE_SIZE;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        return new byte[]{(byte) getType()};
    }

    public String getStringContent() {
        return null;
    }

    // --- get/set object --------------------------------------

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (type < 0x00 || type > 0x09)
            throw new IllegalArgumentException("Content type " + type + " is not known");
        this.type = type;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "ContentType[" +
                "type=" + getType() +
                "]";
    }

    // --- member variables ------------------------------------

    protected int type = 0;
}
