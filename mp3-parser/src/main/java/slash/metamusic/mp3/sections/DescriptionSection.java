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
 * My instances represent the ID3v2Frame description information section
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.6.
 *
 * @author Christian Pesch
 * @version $Id: DescriptionSection.java 796 2006-04-23 14:25:07 +0200 (So, 23 Apr 2006) cpesch $
 */

public class DescriptionSection extends CStringSection {

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        ParseResult result = parse(data, offset, frame.getTextEncoding());
        setDescription(result.getString());
        return result.getLength();
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        return getBytes(getDescription(), frame.getTextEncoding());
    }

    public String getStringContent() {
        return getDescription();
    }

    // --- get/set object --------------------------------------

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "Description[" +
                "description=" + getDescription() +
                "]";
    }

    // --- member variables ------------------------------------

    protected String description = "";
}
