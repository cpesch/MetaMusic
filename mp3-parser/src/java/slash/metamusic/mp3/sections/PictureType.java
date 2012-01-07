/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.sections;

/**
 * Defines the type of pictures in APIC frames.
 *
 * @author Christian Pesch
 * @version $Id: PictureType.java 226 2012-01-07 11:24:50Z cpesch $
 */

public class PictureType {
    public static final PictureType DEFAULT_PICTURE_TYPE = new PictureType(0x00, "Other");
    public static final PictureType[] PICTURE_TYPES = {
            DEFAULT_PICTURE_TYPE,
            new PictureType(0x01, "32x32 pixels 'file icon'"),
            new PictureType(0x02, "Other file icon"),
            new PictureType(0x03, "Cover (front)"),
            new PictureType(0x04, "Cover (back)"),
            new PictureType(0x05, "Leaflet page"),
            new PictureType(0x06, "Media"),
            new PictureType(0x07, "Lead artist/lead performer/solist"),
            new PictureType(0x08, "Artist/performer"),
            new PictureType(0x09, "Conductor"),
            new PictureType(0x0A, "Band/orchestra"),
            new PictureType(0x0B, "Composer"),
            new PictureType(0x0C, "Lyricist/text writer"),
            new PictureType(0x0D, "Recording location"),
            new PictureType(0x0E, "During recording"),
            new PictureType(0x0F, "During performance"),
            new PictureType(0x10, "Movie/video screen capture"),
            new PictureType(0x11, "A bright coloured fish"),
            new PictureType(0x12, "Illustration"),
            new PictureType(0x13, "Band/artist logotype"),
            new PictureType(0x14, "Publisher/studio logotype")
    };

    public static boolean isKnownPictureType(int pictureTypeCode) {
        return pictureTypeCode >= 0x00 && pictureTypeCode <= 0x14;
    }

    public static PictureType getPictureType(int pictureTypeCode) {
        for (PictureType pictureType : PICTURE_TYPES) {
            if (pictureType.getCode() == pictureTypeCode)
                return pictureType;
        }
        return DEFAULT_PICTURE_TYPE;
    }

    private PictureType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    private int code;
    private String description;

    public String toString() {
        return "PictureType[code=" + getCode() + ", description=" + getDescription() + "]";
    }
}
