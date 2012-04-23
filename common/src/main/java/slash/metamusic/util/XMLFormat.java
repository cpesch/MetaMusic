/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Helper class for XML en- and decoding.
 *
 * @author Christian Pesch
 * @version $Id: XMLFormat.java 167 2003-12-08 11:41:43Z cpesch $
 */

public class XMLFormat {

    /**
     * The default xml encoding format for dates - <code>dd.MM.yyyy HH:mm:ss</code>
     */
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * The name of the referring to id attibute - <code>ref</code>
     */
    public static final String REFERENCE_ID = "ref";


    public static boolean isName(String string) {
        char c = string.charAt(0);
        return Character.isLetter(c) || c == '_' || c == ':';
    }

    public static String escapeName(String string) {
        if (string == null || string.length() == 0)
            return null;

        if (isName(string))
            return string;
        else
            return "_" + string;
    }

    public static String deescapeName(String name) {
        if (name.startsWith("_") && name.length() > 1) {
            String deescaped = name.substring(1);
            if (!isName(deescaped))
                name = deescaped;
        }
        return name;
    }
}
