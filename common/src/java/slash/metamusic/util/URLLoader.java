/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.io.*;
import java.net.URL;

/**
 * A utility class that allows you to get the contents of a URL as a string.
 *
 * @author Christian Pesch
 */

public class URLLoader {

    public static String getContents(String url, boolean addLineBreak) throws IOException {
        return getContents(new URL(url), addLineBreak);
    }

    public static String getContents(URL url, boolean addLineBreak) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String data;
            while ((data = reader.readLine()) != null) {
                buffer.append(data);
                if (addLineBreak)
                    buffer.append("\n");
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioe) {
                    // do nothing...
                }
            }
        }
        return buffer.toString();
    }

    public static byte[] getContents(File file) throws IOException, OutOfMemoryError {
        InputStream in = new FileInputStream(file);
        return getContents(in);
    }

    public static byte[] getContents(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputOutput inout = new InputOutput(in, out);
        try {
            inout.start();
        } finally {
            inout.close();
        }
        return out.toByteArray();
    }

    public static void setContents(File file, byte[] bytes) throws IOException, OutOfMemoryError {
        OutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }
}
