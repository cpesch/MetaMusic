/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3;

/**
 * My instances represent a ID3v2 version of the ID3v2 header.
 *
 * @author Christian Pesch
 * @version $Id: ID3v2Version.java 941 2007-01-06 17:06:13Z cpesch $
 */

public class ID3v2Version {
    public static final int VERSION_SIZE = 2;
    public static final int TAG_2_0_SIZE = 3;
    public static final int TAG_3_0_SIZE = 4;

    public ID3v2Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public ID3v2Version() {
        this(3, 0);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getVersionString() {
        return major + "." + minor;
    }

    public byte[] getBytes() {
        return new byte[]{(byte) major, (byte) minor};
    }

    public boolean isObsolete() {
        return major == 2;
    }

    public int getTagSize() {
        return isObsolete() ? TAG_2_0_SIZE : TAG_3_0_SIZE;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ID3v2Version that = (ID3v2Version) o;

        if (major != that.major) return false;
        if (minor != that.minor) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = major;
        result = 29 * result + minor;
        return result;
    }

    public String toString() {
        return "ID3v2Version[major=" + major + ", minor=" + minor + "]";
    }

    // --- member variables ------------------------------------

    /**
     * data
     */
    protected int major;
    protected int minor;
}
