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
 * My instances represent a ID3v2Frame bytes section
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4
 *
 * @author Christian Pesch
 * @version $Id: BytesSection.java 812 2006-06-25 09:27:12Z cpesch $
 */

public class BytesSection extends AbstractSection {

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        byte[] bytes = new byte[data.length - offset];
        System.arraycopy(data, offset, bytes, 0, bytes.length);
        setBytes(bytes);
        return bytes.length;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        return getBytes();
    }

    public String getStringContent() {
        byte[] bytes = getBytes();
        if (bytes == null)
            bytes = new byte[0];
        return (bytes.length < 1000 ? new String(bytes) : "") +
                "<" + bytes.length + " bytes>";
    }

    // --- get/set object --------------------------------------

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "Bytes[" +
                "bytes=" + getStringContent() +
                "]";
    }

    // --- member variables ------------------------------------

    protected byte[] bytes = new byte[0];
}
