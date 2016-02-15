/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import org.junit.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static slash.metamusic.util.ContributorHelper.*;

public class ContributorHelperTest {

    @Test
    public void testParseArtist() {
        assertEquals(singletonList("A"), parseArtist("A"));
        assertEquals(asList("A", "B"), parseArtist("A and B"));
        assertEquals(asList("A", "B"), parseArtist("A feat. B"));
        assertEquals(asList("A", "B"), parseArtist("A Feat. B"));
        assertEquals(asList("A", "B"), parseArtist("A featuring B"));
        assertEquals(asList("A", "B"), parseArtist("A Featuring B"));
        assertEquals(singletonList("A"), parseArtist(formatContributors("A", Collections.<String>emptyList())));
        assertEquals(asList("A", "B"), parseArtist(formatContributors("A", singletonList("B"))));
        assertEquals(asList("A", "B", "C"), parseArtist(formatContributors("A", asList("B", "C"))));
    }

    @Test
    public void testParseTrack() {
        assertEquals(singletonList("A"), parseTrack("A"));
        assertEquals(asList("A", "B"), parseTrack("A (feat. B)"));
        assertEquals(asList("A", "B"), parseTrack("A (Feat. B)"));
        assertEquals(asList("A", "B"), parseTrack("A (featuring B)"));
        assertEquals(asList("A", "B"), parseTrack("A (Featuring B)"));
        assertEquals(asList("A", "B"), parseTrack("A (with B)"));
        assertEquals(asList("A", "B"), parseTrack("A (With B)"));
    }

    @Test
    public void testFormatContributors() {
        assertEquals("A", formatContributors("A", Collections.<String>emptyList()));
        assertEquals("A featuring B", formatContributors("A", singletonList("B")));
        assertEquals("A featuring B, C", formatContributors("A", asList("B", "C")));
        assertEquals("A featuring B, C, D", formatContributors("A", asList("B", "C", "D")));
    }
}