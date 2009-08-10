/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

/**
 * Some useful functions for
 * <ul>
 * <li>arrays
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: ArrayHelper.java 203 2004-03-18 14:33:32Z cpesch $
 */

public class ArrayHelper {

    /**
     * Print an Object ArrayHelper to a String.
     */
    public static String printArrayToString(Object[] array) {
        if (array == null)
            return "null";

        StringBuffer buffer = new StringBuffer();
        buffer.append('{');
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                buffer.append(',');
            buffer.append(array[i]);
        }
        buffer.append('}');
        return buffer.toString();
    }

    /**
     * Returns if the given int arrays have the same length
     * and contain the same values in the same order
     *
     * @param first  the first array to compare
     * @param second the second array to compare
     * @return true if the given int arrays have the same length
     *         and contain the same values in the same order
     */
    public static boolean equals(int[] first, int[] second) {
        if (first == second)
            return true;

        if (first == null || second == null)
            return false;

        if (first.length != second.length)
            return false;

        for (int i = 0; i < second.length; i++) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    /**
     * Returns if the given byte arrays have the same length
     * and contain the same values in the same order
     *
     * @param first  the first array to compare
     * @param second the second array to compare
     * @return true if the given byte arrays have the same length
     *         and contain the same values in the same order
     */
    public static boolean equals(byte[] first, byte[] second) {
        if (first == second)
            return true;

        if (first == null || second == null)
            return false;

        if (first.length != second.length)
            return false;

        for (int i = 0; i < second.length; i++) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }
}
