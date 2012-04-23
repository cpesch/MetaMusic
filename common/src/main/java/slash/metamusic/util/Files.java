/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Some useful methods to create and manipulate File objects
 *
 * @author Christian Pesch
 * @version $Id: Files.java 942 2007-01-10 17:11:12Z cpesch $
 */

public class Files {

    /**
     * Returns the extension of the file, which are the characters
     * behind the last dot in the file name.
     *
     * @param file the file to find the extension
     */
    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    /**
     * Returns the extension of a file name, which are the characters
     * behind the last dot in the file name.
     *
     * @param name the file name to find the extension
     */
    public static String getExtension(String name) {
        int index = name.lastIndexOf(".");
        if (index == -1)
            return "";
        return name.substring(index + 1, name.length());
    }

    /**
     * Add the given extension to the given file name, even if
     * there is already one.
     *
     * @param fileName  the file name to add the extension to
     * @param extension the extension to add to the file name
     * @return the file name with the added extension
     */
    public static String addExtension(String fileName, String extension) {
        return fileName + "." + extension;
    }

    /**
     * Remove the extension of the given file name, if there is any.
     *
     * @param fileName the file name to remove the extension
     * @return the file name without an extension
     */
    public static String removeExtension(String fileName) {
        String extension = getExtension(fileName);
        return extension.length() > 0 ? fileName.substring(0, fileName.length() - extension.length() - 1) : fileName;
    }

    /**
     * Replace the extension of the given file name with the
     * given extension.
     *
     * @param fileName  the file name to replace the extension
     * @param extension the new extension for the file name
     * @return the file name with the given extension
     */
    public static String replaceExtension(String fileName, String extension) {
        String withoutExtension = removeExtension(fileName);
        String withExtension = withoutExtension + "." + extension;
        return withExtension;
    }

    /**
     * Tries to rename the given file with the new extension
     *
     * @param file      the file to rename
     * @param extension the new extension for the file
     * @return true if the file was successfully renamed
     */
    public static boolean setExtension(File file, String extension) {
        String oldName = file.getName();
        String newName = replaceExtension(oldName, extension);
        return file.renameTo(new File(file.getParentFile(), newName));
    }

    /**
     * Replace the slash characters in the string with the
     * plattform-specific File.separatorChar.
     *
     * @param string the string to operate on
     * @return a string, where slash characters "/" are replaced with File.separatorChar
     * @see File#separatorChar
     */
    public static String replaceSeparators(String string) {
        return string.replace('/', File.separatorChar);
    }

    /**
     * Append a slash character "/" to the string if the string
     * does not end with a slash.
     *
     * @param string the string to operate on
     * @return a string where the last character is a slash character "/"
     */
    public static String appendSlash(String string) {
        string = replaceSeparators(string);
        if (!string.endsWith(File.separator))
            string += File.separatorChar;
        return string;
    }

    /**
     * Collects files/directories with the given extension in the given
     * set. If path is a directory, it recursively descends the directory
     * tree. If no extension is given, all files are collected.
     *
     * @param path               the path to collect files below
     * @param collectDirectories decides whether directories are collected
     * @param collectFiles       decides whether file are collected
     * @param extension          the extension in lower case
     * @param set                the set to add hits to
     */
    private static void recursiveCollect(File path,
                                         final boolean collectDirectories,
                                         final boolean collectFiles,
                                         final String extension,
                                         final Set<File> set) {
        if (path.isFile()) {
            if (collectFiles &&
                    (extension == null || getExtension(path).toLowerCase().equals(extension)))
                set.add(path);

        } else {
            if (collectDirectories)
                set.add(path);

            path.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    recursiveCollect(file, collectDirectories, collectFiles, extension, set);
                    return true;
                }
            });
        }
    }

    /**
     * Collects files below the given path with the given extension.
     * If path is a directory, the collection recursively descends the
     * directory tree. The extension comparison is case insensitive
     *
     * @param path      the path to collect files below
     * @param extension the case insensitively compare extension
     * @return the list of files found below the given path and
     *         with the given extension
     */
    public static List<File> collectFiles(File path, String extension) {
        Set<File> set = new TreeSet<File>();
        extension = extension != null ? extension.toLowerCase() : null;
        recursiveCollect(path, false, true, extension, set);
        return new ArrayList<File>(set);
    }

    /**
     * Collects all files below the given path. If path is a directory,
     * the collection recursively descends the directory tree.
     *
     * @param path the path to collect files below
     * @return the list of files found below the given path
     */
    public static List<File> collectFiles(File path) {
        return collectFiles(path, null);
    }

    /**
     * Collects all directories below the given path. If path is a directory,
     * the collection recursively descends the directory tree.
     *
     * @param path the path to collect directories below
     * @return the list of directories found below the given path
     */
    public static List<File> collectDirectories(File path) {
        Set<File> set = new TreeSet<File>();
        recursiveCollect(path, true, false, null, set);
        return new ArrayList<File>(set);
    }

    public static File findExistingPath(File path) {
        while (path != null && !path.exists()) {
            path = path.getParentFile();
        }
        return path != null && path.exists() ? path : null;
    }

    /**
     * Relativizes the given file to the given path.
     * <p/>
     * Note, that this currently just strips off a prefix.
     *
     * @param path the path to relativize to
     * @param file the file to relativize
     * @return the relativized path for the given file
     */
    public static String relativize(String path, String file) {
        if (path == null)
            return file;
        path = Files.replaceSeparators(new File(path).getAbsolutePath());
        file = Files.replaceSeparators(new File(file).getAbsolutePath());
        if (file.startsWith(path))
            file = file.substring(path.length() + 1);
        return file;
    }

    /**
     * Copies the given source file to the given destination file.
     *
     * @param source      the source file to copy
     * @param destination where to copy the source to
     * @return true, if the source has been copied to the destination
     */
    public static boolean copy(File source, File destination) throws IOException {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(destination);
        InputOutput inputOutput = new InputOutput(in, out);
        inputOutput.start();
        inputOutput.close();
        return true;
    }

    /**
     * Deletes all files and directories below the given path. If path is a directory,
     * the collection recursively descends the directory tree.
     *
     * @param path the path to delete files and directories below
     * @return if the given path has been deleted
     */
    public static boolean delete(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    delete(file);
                } else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }

    /**
     * Deletes all files and directories below the given path. If path is a directory,
     * the collection recursively descends the directory tree.
     *
     * @param path the path to delete files and directories below
     * @return if the given path has been deleted
     */
    public static boolean delete(String path) {
        return delete(new File(path));
    }
}

