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

import static slash.metamusic.mp3.sections.PictureType.DEFAULT_PICTURE_TYPE;
import static slash.metamusic.mp3.sections.PictureType.getPictureType;
import static slash.metamusic.mp3.sections.PictureType.isKnownPictureType;

/**
 * My instances represent the ID3v2Frame picture type byte
 * of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.14.
 *
 * @author Christian Pesch
 * @version $Id: PictureTypeSection.java 461 2012-01-07 12:17:32Z cpesch $
 */

public class PictureTypeSection extends AbstractSection {

    /**
     * Size of the picture type section. This is one byte.
     */
    public static final int TYPE_SIZE = 1;

    // --- read/write object ------------------------------------

    public int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException {
        byte pictureTypeCode = data[offset];
        if (isKnownPictureType(pictureTypeCode))
            setPictureType(pictureTypeCode);
        return TYPE_SIZE;
    }

    public byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException {
        return new byte[]{(byte) getPictureType().getCode()};
    }

    public String getStringContent() {
        return null;
    }

    // --- get/set object --------------------------------------

    public PictureType getPictureType() {
        return type;
    }

    public void setPictureType(int pictureTypeCode) {
        if (!isKnownPictureType(pictureTypeCode))
            throw new IllegalArgumentException("Picture type code " + pictureTypeCode + " is not known");
        setPictureType(PictureType.getPictureType(pictureTypeCode));
    }

    public void setPictureType(PictureType type) {
        this.type = type;
    }

    // --- overwrites Object -----------------------------------

    public String toString() {
        return "PictureType[" +
                "pictureType=" + getPictureType() +
                "]";
    }

    // --- member variables ------------------------------------

    protected PictureType type = DEFAULT_PICTURE_TYPE;
}
