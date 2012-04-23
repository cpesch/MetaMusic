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
import java.util.logging.Logger;

/**
 * My instances represent a section of the content of an
 * ID3v2 frame of the ID3v2 header as described in
 * http://www.id3.org/id3v2.3.0.html#sec4.
 *
 * @author Christian Pesch
 * @version $Id: AbstractSection.java 221 2004-03-20 19:06:37Z cpesch $
 */

public abstract class AbstractSection {

    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(AbstractSection.class.getName());


    /**
     * Pulls out information from the content.
     *
     * @param data   the content data
     * @param offset the offset from which to read
     * @param frame  the frame for which this content is parsed
     */
    public abstract int parse(byte[] data, int offset, ID3v2Frame frame) throws IOException;

    /**
     * Return the byte representation of this section.
     *
     * @param frame the frame for which the content is needed
     * @return an array with the byte representation of this section
     * @throws UnsupportedEncodingException if some encoding fails
     */
    public abstract byte[] getBytes(ID3v2Frame frame) throws UnsupportedEncodingException;


    /**
     * Return a string representation of this section.
     * If this is not feasible, null is returned.
     *
     * @return a string representation of this section,
     *         or null if this is not feasible
     */
    public abstract String getStringContent();

}
