/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Parses additional contributors from artist or track names.
 *
 * @author Christian Pesch
 */

public class ContributorHelper {
    private static List<String> createContributorList(String lead, String featuring) {
        List<String> result = new ArrayList<String>();
        result.add(lead);
        for (String contributor : featuring.split(",")) {
            String trimmed = contributor.trim();
            if (trimmed.length() > 0)
                result.add(trimmed);
        }
        return result;
    }

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
            return createContributorList(lead, featuring);
        }

        pattern = Pattern.compile("(.+)(( (A|a|U|u)nd )|&)(.+)");
        matcher = pattern.matcher(artist);
        matches = matcher.matches();
        if (matches) {
            String lead = matcher.group(1).trim();
            String featuring = matcher.group(5).trim();
            return createContributorList(lead, featuring);
        }

        return singletonList(artist);
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
            return createContributorList(title, featuring);
        }

        pattern = Pattern.compile("(.+)(\\((W|w)ith(.+))\\)");
        matcher = pattern.matcher(track);
        matches = matcher.matches();
        if (matches) {
            String title = matcher.group(1).trim();
            String featuring = matcher.group(4).trim();
            return createContributorList(title, featuring);
        }

        return singletonList(track);
    }

    public static String formatContributors(String artist, List<String> contributors) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(artist);
        if (contributors.size() > 0)
            buffer.append(" featuring ");
        for (int i = 0, c = contributors.size(); i < c; i++) {
            String contributor = contributors.get(i);
            buffer.append(contributor);
            if (i < c - 1)
                buffer.append(", ");
        }
        return buffer.toString();
    }
}