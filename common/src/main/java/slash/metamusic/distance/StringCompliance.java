/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.distance;

/**
 * Determine the compliance of two strings based on
 * Levenshtein distance and the string length.
 *
 * @author Christian Pesch
 * @version $Id: StringCompliance.java 205 2004-03-18 14:56:02Z cpesch $
 */

public class StringCompliance {
    public static final double NO_COMPLIANCE = 0.0;
    public static final double MINIMUM_COMPLIANCE_TO_PREFER = 50.1;
    public static final double COMPLETE_COMPLIANCE = 100.0;

    /**
     * Determines the compliance of two strings based on
     * Levenshtein distance and the string length on a
     * scale from 0.0 to 100.0 percent.
     * <p/>
     * <pre>
     * 				      Amount of equals characters
     *  Compliance of two strings [%] = -------------------------- * 100.0
     * 				      Amount of characters
     * </pre>
     * <p/>
     * To not discriminate longer to shorter strings, the maximum
     * length of the first and the second string is taken. This
     * seems to favor longer strings, though. On the other hand,
     * longer strings are less ambigious.
     *
     * @param first  the first string
     * @param second the second string
     * @return the compliance of two strings based on
     *         Levenshtein distance and the string length on a
     *         scale from 0.0 to 100.0 percent.
     */
    public static double compliance(String first, String second) {
        int distance = Levenshtein.distance(first, second);

        int length = Math.max(first.length(), second.length());
        double compliance = 100.0 * (length - distance) / length;

        /* compliance is 0.0 to 100.0 percent */
        if (compliance > COMPLETE_COMPLIANCE)
            compliance = COMPLETE_COMPLIANCE;
        else if (compliance < NO_COMPLIANCE)
            compliance = NO_COMPLIANCE;
        return compliance;
    }
}
