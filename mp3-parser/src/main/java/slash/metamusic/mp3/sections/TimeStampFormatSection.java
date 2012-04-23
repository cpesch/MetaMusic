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
 * My instances represent the ID3v2Frame time stamp format byte
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.9.
 *
 * @author Christian Pesch
 * @version $Id: TimeStampFormatSection.java 223 2004-03-22 06:52:32Z cpesch $
 */

public class TimeStampFormatSection extends AbstractSection {
    public static final int FORMAT_SIZE = 1;

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        setFormat(data[offset]);
        return FORMAT_SIZE;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        return new byte[]{(byte) getFormat()};
    }

    public String getStringContent() {
        return null;
    }

    // --- get/set object --------------------------------------

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        if (format < 0x01 || format > 0x02)
            throw new IllegalArgumentException("Time stamp format " + format + " is not known");
        this.format = format;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "TimeStampFormat[" +
                "format=" + getFormat() +
                "]";
    }

    // --- member variables ------------------------------------

    protected int format = 0x02;
}
