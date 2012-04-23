/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import junit.framework.TestCase;

import java.util.Arrays;

public class ContributorHelperTest extends TestCase {
    public void testParseArtist() {
        assertEquals(Arrays.asList("A"), ContributorHelper.parseArtist("A"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseArtist("A and B"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseArtist("A feat. B"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseArtist("A Feat. B"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseArtist("A featuring B"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseArtist("A Featuring B"));
    }

    public void testParseTrack() {
        assertEquals(Arrays.asList("A"), ContributorHelper.parseTrack("A"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseTrack("A (feat. B)"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseTrack("A (Feat. B)"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseTrack("A (featuring B)"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseTrack("A (Featuring B)"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseTrack("A (with B)"));
        assertEquals(Arrays.asList("A", "B"), ContributorHelper.parseTrack("A (With B)"));
    }
}