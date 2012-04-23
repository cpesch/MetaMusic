/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * A ZipCache maintains a cache archive file, in which it puts
 * files under a key and returns them when queried with a key.
 *
 * @author Christian Pesch
 */

public class ZipCache {
    private String cacheFileName;
    private ZipFile cacheFile = null;

    public File getCacheDirectory() {
        throw new UnsupportedOperationException();
    }

    public String getCacheFileName() {
        return cacheFileName;
    }

    public void setCacheFileName(String cacheFileName) {
        this.cacheFileName = cacheFileName;
    }

    protected synchronized boolean existsCacheFile() {
        if (cacheFile == null) {
            File file = new File(getCacheFileName());
            if (file.exists()) {
                try {
                    cacheFile = new ZipFile(file);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Could not create cache file " + file);
                }
            }
        }
        return cacheFile != null;
    }

    public List<File> values() {
        throw new UnsupportedOperationException();
    }

    protected String replaceForFileName(String fileName) {
        fileName = fileName.replace(File.separator, "");
        return fileName;
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
        InputStream inputStream = getFileAsInputStream(key);
        return inputStream != null ? URLLoader.getContents(inputStream) : null;
    }

    public synchronized InputStream getFileAsInputStream(String key) throws IOException {
        key = replaceForFileName(key);
        if (!existsCacheFile())
            return null;
        ZipEntry entry = cacheFile.getEntry(key);
        return entry != null ? cacheFile.getInputStream(entry) : null;
    }


    public synchronized void putAsString(String key, String value) throws IOException {
        put(key, value.getBytes());
    }

    public synchronized void putAsObject(String key, Object value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        try {
            out.writeObject(value);
        }
        finally {
            out.close();
        }
        put(key, baos.toByteArray());
    }

    public synchronized void put(String key, byte[] value) throws IOException {
        putAsInputStream(key, new ByteArrayInputStream(value));
    }

    public synchronized void putAsInputStream(String key, InputStream input) throws IOException {
        key = replaceForFileName(key);

        File currentFile = new File(getCacheFileName());
        File currentDirectory = currentFile.getParentFile();
        if (!currentDirectory.exists()) {
            if (!currentDirectory.mkdirs())
                throw new IOException("Could not create cache directory " + currentDirectory);
        }

        File newFile = File.createTempFile("newfile", ".zip", currentFile.getParentFile());
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(newFile));
        if (existsCacheFile()) {
            Enumeration<? extends ZipEntry> enumeration = cacheFile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = enumeration.nextElement();
                out.putNextEntry(entry);
                out.write(URLLoader.getContents(cacheFile.getInputStream(entry)));
            }

            cacheFile.close();
            cacheFile = null;
        }

        out.putNextEntry(new ZipEntry(key));
        out.write(URLLoader.getContents(input));
        out.close();

        if (currentFile.exists()) {
            File oldFile = new File(newFile.getAbsolutePath() + ".bak");
            if (!currentFile.renameTo(oldFile))
                throw new IOException("Could not rename " + currentFile + " to " + oldFile);
        }

        if (!newFile.renameTo(currentFile))
            throw new IOException("Could not rename " + newFile + " to " + currentFile);
    }
}