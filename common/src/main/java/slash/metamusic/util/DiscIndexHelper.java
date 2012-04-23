/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some useful functions for
 * <ul>
 * <li>disc indices
 * </ul>
 * of albums
 *
 * @author Christian Pesch
 */

public class DiscIndexHelper {
    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(DiscIndexHelper.class.getName());

    private static final Pattern DISC_INDEX_PATTERN = Pattern.compile("(.+?)((\\(|\\[)([d|D][i|I][s|S][c|C]|[c|C][d|D]).*?(\\d+)(\\)|\\]))(.*+)");

    /**
     * Removes disc indices like
     * <ul>
     * <li>Album A (disc 1)</li>
     * <li>Album B (CD2)</li>
     * </ul>
     * from album names
     *
     * @param album the name of an album potentially containing a disc index
     * @return the name of the given album without disc indices
     */
    public static String removeDiscIndexPostfix(String album) {
        Matcher matcher = DISC_INDEX_PATTERN.matcher(album);
        boolean matches = matcher.matches();
        if (matches) {
            album = matcher.group(1).trim();
        }
        return album;
    }

    /**
     * Parses the disc index from an album name like
     * <ul>
     * <li>Album A (disc 1)</li>
     * <li>Album B (CD2)</li>
     * </ul>
     *
     * @param album the name of an album potentially containing a disc index
     * @return the index of the album or -1 if no disc index is present
     */
    public static int parseDiscIndex(String album) {
        if (album != null) {
            Matcher matcher = DISC_INDEX_PATTERN.matcher(album);
            boolean matches = matcher.matches();
            if (matches) {
                String index = matcher.group(5);
                if (StringHelper.isANumber(index)) {
                    return Integer.parseInt(index);
                }
            }
        }
        return -1;
    }

    public static String formatDiscIndex(String album, int discIndex) {
        return album + (discIndex != -1 ? " (disc " + discIndex + ")" : "");
    }
}
