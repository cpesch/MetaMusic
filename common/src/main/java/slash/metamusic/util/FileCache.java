/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * A FileCache maintains a cache directory, in which it puts
 * files under a key and returns them when queried with a key.
 *
 * @author Christian Pesch
 */

public class FileCache {
    private String cacheDirectoryName;
    private File cacheDirectory = null;

    public File getCacheDirectory() {
        initialize();
        return cacheDirectory;
    }

    public String getCacheDirectoryName() {
        return cacheDirectoryName;
    }

    public void setCacheDirectoryName(String cacheDirectoryName) {
        this.cacheDirectoryName = cacheDirectoryName;
    }

    protected synchronized void initialize() {
        if (cacheDirectory == null) {
            cacheDirectory = new File(getCacheDirectoryName());
            if (!cacheDirectory.exists()) {
                if (!cacheDirectory.mkdirs())
                    throw new IllegalArgumentException("Could not create cache directory " + getCacheDirectoryName());
            }
        }
    }

    public List<File> values() {
        initialize();
        File[] files = cacheDirectory.listFiles();
        return Arrays.asList(files);
    }

    protected String replaceForFileName(String fileName) {
        fileName = fileName.replace(File.separator, "");
        return fileName;
    }

    public synchronized File get(String key) {
        key = replaceForFileName(key);
        initialize();
        File file = new File(cacheDirectory, key);
        return file.exists() ? file : null;
    }

    public synchronized String getFileAsString(String key) throws IOException {
        byte[] content = getFileAsBytes(key);
        return content != null ? new String(content) : null;
    }

    public synchronized Object getFileAsObject(String key) throws IOException {
        byte[] content = getFileAsBytes(key);
        if (content != null) {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new ByteArrayInputStream(content)));
            try {
                return ois.readObject();
            } catch (ClassNotFoundException e) {
                System.err.println("Cannot deserialize object");
                e.printStackTrace();
            } finally {
                ois.close();
            }
        }
        return null;
    }

    public synchronized byte[] getFileAsBytes(String key) throws IOException {
        File file = get(key);
        return file != null ? URLLoader.getContents(file) : null;
    }

    public synchronized InputStream getFileAsInputStream(String key) throws IOException {
        File file = get(key);
        return file != null ? new FileInputStream(file) : null;
    }


    public synchronized File putAsString(String key, String value) throws IOException {
        return put(key, value.getBytes());
    }

    public synchronized File putAsObject(String key, Object value) throws IOException {
        File file = put(key);
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        try {
            out.writeObject(value);
        }
        finally {
            out.close();
        }
        return file;
    }

    public synchronized File put(String key, byte[] value) throws IOException {
        return putAsInputStream(key, new ByteArrayInputStream(value));
    }

    public synchronized File putAsInputStream(String key, InputStream input) throws IOException {
        File file = put(key);

        OutputStream out = new FileOutputStream(file);
        InputOutput inout = new InputOutput(input, out);
        inout.start();
        inout.close();

        return file;
    }

    public synchronized File put(String key) throws IOException {
        key = replaceForFileName(key);
        initialize();
        File file = remove(key);
        if (file == null)
            file = new File(cacheDirectory, key);
        return file;
    }


    public synchronized File remove(String key) {
        File file = get(key);
        if (file != null) {
            if(!file.delete())
                throw new IllegalArgumentException("Could not delete " + file);
        }
        return file;
    }
}
