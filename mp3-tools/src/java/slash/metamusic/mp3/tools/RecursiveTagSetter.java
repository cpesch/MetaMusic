/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3.tools;

import slash.metamusic.mp3.ID3Genre;
import slash.metamusic.mp3.MP3File;
import slash.metamusic.util.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Sets sets of tag values on sets of files
 *
 * @author Christian Pesch
 * @version $Id: RecursiveTagSetter.java 949 2007-01-14 11:27:54Z cpesch $
 */

public class RecursiveTagSetter {
    private File startingDirectory;
    private Map<String, Object> tagsToSet = new HashMap<String, Object>();
    private static final String ARG_NAME_ALBUM = "album";
    private static final String ARG_NAME_ARTIST = "artist";
    private static final String ARG_NAME_BAND = "band";
    private static final String ARG_NAME_COMMENT = "comment";
    private static final String ARG_NAME_GENRE = "genre";
    private static final String ARG_NAME_COMPILATION = "compilation";
    private static final String ARG_NAME_KEY = "year";

    public static void main(String[] args) {
        RecursiveTagSetter recursiveTagSetter = null;
        try {
            recursiveTagSetter = new RecursiveTagSetter(args);
        } catch (ExceptionInInitializerError e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        recursiveTagSetter.setTags(recursiveTagSetter.startingDirectory);
        System.exit(0);
    }

    public RecursiveTagSetter(String[] args) {
        if (args.length < 3 || (args.length - 1) % 2 != 0)
            throw new ExceptionInInitializerError("RecursiveTagSetter <directory> -[album|compilation|artist|band|comment|genre|year] <value>");

        startingDirectory = new File(args[0]);
        if (!startingDirectory.exists() || !startingDirectory.isDirectory())
            throw new ExceptionInInitializerError(args[0] + " isn't a directory");

        for (int i = 1; i < args.length; i = i + 2) {
            String tagName = args[i].substring(1).trim();
            Object tagValue = args[i + 1].trim();
            if (tagName.equals(ARG_NAME_GENRE)) {
                tagValue = new ID3Genre((String) tagValue);
                if (((ID3Genre) tagValue).getId() == -1) {
                    StringBuffer allGenres = new StringBuffer();
                    for (String genre : ID3Genre.getGenreNamesAsSet())
                        allGenres.append(genre).append(" ");
                    throw new ExceptionInInitializerError("Specified genre does not exist. available genres: " + allGenres);
                }
            }
            if (tagName.equals(ARG_NAME_GENRE)) {
                try {
                    tagValue = new Integer((String) tagValue);
                } catch (NumberFormatException nfe) {
                    throw new ExceptionInInitializerError("Could not parse given year " + tagValue);
                }
            }
            if (tagName.equals(ARG_NAME_COMPILATION)) {
                try {
                    tagValue = Boolean.valueOf((String) tagValue);
                } catch (NumberFormatException nfe) {
                    throw new ExceptionInInitializerError("Could not parse given compilation " + tagValue);
                }
            }
            tagsToSet.put(tagName, tagValue);
        }
    }

    public void setTags(File directory) {
        String album = (String) tagsToSet.get(ARG_NAME_ALBUM);
        String artist = (String) tagsToSet.get(ARG_NAME_ARTIST);
        String band = (String) tagsToSet.get(ARG_NAME_BAND);
        String comment = (String) tagsToSet.get(ARG_NAME_COMMENT);
        ID3Genre genre = (ID3Genre) tagsToSet.get(ARG_NAME_GENRE);
        Integer year = (Integer) tagsToSet.get(ARG_NAME_KEY);
        Boolean compilation = (Boolean) tagsToSet.get(ARG_NAME_COMPILATION);

        File[] children = directory.listFiles();
        for (File file : children) {
            if (file.isDirectory())
                setTags(file);
            else if ("mp3".equals(Files.getExtension(file).toLowerCase())) {
                MP3File mp3 = MP3File.readValidFile(file);
                if (mp3 != null) {
                    if (album != null) mp3.setAlbum(album);
                    if (artist != null) mp3.setArtist(artist);
                    if (band != null) mp3.getHead().setBand(band);
                    if (comment != null) mp3.setComment(comment);
                    if (genre != null) mp3.setGenre(genre);
                    if (year != null) mp3.setYear(year);
                    if (compilation != null) mp3.getHead().setCompilation(compilation);
                    try {
                        mp3.write();
                    } catch (IOException e) {
                        System.out.println("Error writing " + file.getAbsolutePath() + ":" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
