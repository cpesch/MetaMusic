/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses additional contributors from artist or track names.
 *
 * @author Christian Pesch
 */

public class ContributorHelper {

    /**
     * Parses additional contributors from an artist name like
     * <ul>
     * <li>Artist A featuring Artist B</li>
     * <li>Artist A and Artist B</li>
     * </ul>
     *
     * @param artist the name of an artist potentially containing contributors
     * @return the artist and its contributors
     */
    public static List<String> parseArtist(String artist) {
        Pattern pattern = Pattern.compile("(.+)(( (F|f)eat(\\. |\\.| |uring ))(.+))");
        Matcher matcher = pattern.matcher(artist);
        boolean matches = matcher.matches();
        if (matches) {
            String lead = matcher.group(1).trim();
            String featuring = matcher.group(6).trim();
            return Arrays.asList(lead, featuring);
        }

        pattern = Pattern.compile("(.+)(( (A|a|U|u)nd )|&)(.+)");
        matcher = pattern.matcher(artist);
        matches = matcher.matches();
        if (matches) {
            String lead = matcher.group(1).trim();
            String featuring = matcher.group(5).trim();
            return Arrays.asList(lead, featuring);
        }

        return Arrays.asList(artist);
    }

    /**
     * Parses additional contributors from a track name like
     * <ul>
     * <li>Track A (featuring Artist B)</li>
     * <li>Track A (with Artist B)</li>
     * </ul>
     *
     * @param track the name of a track potentially containing contributors
     * @return the track and its contributors
     */
    public static List<String> parseTrack(String track) {
        Pattern pattern = Pattern.compile("(.+)(\\((F|f)eat(\\. |\\.| |uring )(.+))\\)");
        Matcher matcher = pattern.matcher(track);
        boolean matches = matcher.matches();
        if (matches) {
            String title = matcher.group(1).trim();
            String featuring = matcher.group(5).trim();
            return Arrays.asList(title, featuring);
        }

        pattern = Pattern.compile("(.+)(\\((W|w)ith(.+))\\)");
        matcher = pattern.matcher(track);
        matches = matcher.matches();
        if (matches) {
            String title = matcher.group(1).trim();
            String featuring = matcher.group(4).trim();
            return Arrays.asList(title, featuring);
        }

        return Arrays.asList(track);
    }
}