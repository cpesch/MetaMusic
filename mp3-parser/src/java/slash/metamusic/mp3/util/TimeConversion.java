/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.util;

/**
 * Some useful functions to convert time in ordinal numbers to strings.
 *
 * @author Christian Pesch
 * @version $Id: TimeConversion.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class TimeConversion {
    public static String getTimeFromSeconds(long timeInSeconds) {
        long minutes = timeInSeconds / 60;
        long seconds = timeInSeconds - minutes * 60;
        return Long.toString(minutes) + ":" + (seconds < 10 ? "0" : "") + Long.toString(seconds);
    }

    public static String getTimeFromMilliSeconds(long timeInMilliSeconds) {
        return getTimeFromSeconds(timeInMilliSeconds / 1000);
    }
}
