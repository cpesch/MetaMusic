/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

/*
 * ---------------------------------------------------------
 * Antelmann.com Java Framework by Holger Antelmann
 * Copyright (c) 2002 Holger Antelmann <info@antelmann.com>
 * For details, see also http://www.antelmann.com/developer/
 * ---------------------------------------------------------
 */

package slash.metamusic.freedb;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * CDDBArgumentParser is a helper class that parses arguments in results
 * according to CDDB Protocol level 2.
 * It works similarly to a StringTokenizer, but it properly handles
 * arguments enclosed with quotes.
 *
 * @author Christian Pesch based on work from Holger Antelmann
 * @version $Id: CDDBArgumentParser.java 914 2006-12-26 20:44:49Z cpesch $
 */

public class CDDBArgumentParser {
    private String line;
    protected String delimiter = " \t\n\r\f";

    public CDDBArgumentParser(String line) {
        this.line = line;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getRemainder() {
        while (hasMoreArguments() && isDelimiter(line.charAt(0)))
            line = line.substring(1);
        String rest = line;
        line = "";
        return rest;
    }

    public boolean hasMoreArguments() {
        return line.length() > 0;
    }

    public String nextArgument() throws NoSuchElementException {
        if (!hasMoreArguments()) {
            throw new NoSuchElementException();
        }
        // strip slack on the beginning
        while (hasMoreArguments() && isDelimiter(line.charAt(0)))
            line = line.substring(1);
        if (line.length() < 1) {
            return "";
        }
        int pos;
        String arg;
        if (line.charAt(0) == '\"') {
            // handle quoted argument
            pos = findNextQuote(1);
            arg = line.substring(1, pos);
        } else {
            // handle unquoted argument
            pos = findNextDelimiter(1);
            arg = line.substring(0, pos);
        }
        if (pos >= line.length()) {
            line = "";
        } else {
            line = line.substring(pos + 1);
        }
        return arg;
    }

    int findNextQuote(int from) {
        int i = from;
        for (; (i < line.length()) && !(line.charAt(i) == '\"'); i++) ;
        return i;
    }

    int findNextDelimiter(int from) {
        int i = from;
        for (; (i < line.length()) && !isDelimiter(line.charAt(i)); i++) ;
        return i;
    }

    boolean isDelimiter(char c) {
        for (int i = 0; i < delimiter.length(); i++) {
            if (c == delimiter.charAt(i)) return true;
        }
        return false;
    }


    public static String[] getAll(String line) {
        CDDBArgumentParser parser = new CDDBArgumentParser(line);
        List<String> strings = new ArrayList<String>();
        while (parser.hasMoreArguments()) {
            strings.add(parser.nextArgument());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public static void main(String[] args) {
        System.out.println(args[0]);
        CDDBArgumentParser ap = new CDDBArgumentParser(args[0]);
        while (ap.hasMoreArguments()) {
            System.out.println("\"" + ap.nextArgument() + "\"");
        }
        System.exit(0);
    }
}