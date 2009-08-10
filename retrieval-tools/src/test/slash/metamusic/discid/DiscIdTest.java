/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.discid;

import junit.framework.TestCase;

import java.io.IOException;

public class DiscIdTest extends TestCase {
    public static DiscId VALID_DISC_ID = new DiscId(11, new int[]{150, 19360, 39528, 61785, 76765, 94058, 111020, 129548, 145165, 161665, 180393, 198888});
    public static DiscId NEGATIVE_SIZE_TOC_DISC_ID = new DiscId(8, new int[]{750, 14914, 97838, 180520, 238858, 293756, 350936, 346586, 375});
    public static DiscId FILLED_UP_ENTRIES_TOC_DISC_ID = new DiscId(99, new int[]{149, 5707, 12754, 134669, 221889, 265174, 318124, 344601, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 150,
            150, 150, 150, 150, 150, 150, 150, 150, 150, 375});
    public static DiscId GROUP_ARTIST_DISC_ID = new DiscId(13, new int[]{150, 5005, 25442, 44407, 64727, 82387, 104457, 125880, 145450, 165992, 186297, 225572, 245692, 254474});

    public DiscIdTest(String name) {
        super(name);
    }

    public void testInstantiateDiscIdObject() {
        new DiscId();
    }

    public void testCalculateDiscId() throws IOException {
        DiscId discId = new DiscId();
        assertEquals(false, discId.isValid());
    }

    public void checkTrackLengthFrames(DiscId discId, String discIdString, int trackCount, int firstOffset, int lastOffset, int discLength) {
        assertEquals(trackCount, discId.getTrackCount());
        assertEquals(discIdString, discId.getEncodedDiscId());
        assertEquals(DiscId.decodeDiscId(discIdString), discId.getDiscId());
        int lastTrack = discId.getTrackCount() - 1;
        for (int i = 0; i < lastTrack; i++) {
            assertEquals(discId.getTrackStartFrame(i + 1) - discId.getTrackStartFrame(i),
                    discId.getTrackLengthFrames(i));
            assertEquals(discId.getTrackLengthFrames(i) / DiscId.FRAMES_PER_SECOND,
                    discId.getTrackLengthSeconds(i));
        }
        assertEquals(firstOffset, discId.getTrackStartFrame(0));
        assertEquals(lastOffset, discId.getTrackStartFrame(lastTrack));
        assertEquals(discLength, discId.getDiscLengthSeconds());
    }

    public void testValidDiscId() {
        checkTrackLengthFrames(VALID_DISC_ID, "860a590b", 11, 150, 180393, 2649);
    }

    public void testNegativeSizeTOCDiscId() {
        checkTrackLengthFrames(NEGATIVE_SIZE_TOC_DISC_ID, "64124507", 7, 750, 350936, 4677);
    }

    public void testFilledUpEntriesTOCDiscId() {
        checkTrackLengthFrames(FILLED_UP_ENTRIES_TOC_DISC_ID, "7611f408", 8, 150, 344601, 4596);
    }

    public void testIterateOverTrackLength() {
        DiscId discId = VALID_DISC_ID;
        long frames = 0;
        long millis = 0;
        long seconds = 0;
        for (int i = 0; i < discId.getTrackCount(); i++) {
            frames += discId.getTrackLengthFrames(i);
            millis += discId.getTrackLengthMillis(i);
            seconds += discId.getTrackLengthSeconds(i);
        }
        assertEquals(discId.getDiscLengthFrames(), frames);
        assertEquals(2649843, millis);
        assertEquals(2645, seconds);
        // allow interval due to rounding errors
        int discLengthMillis = discId.getDiscLengthMillis();
        assertTrue(discLengthMillis > millis - 11 * 100 &&
                discLengthMillis < millis + 11 * 100);
        assertTrue(discId.getDiscLengthSeconds() > seconds &&
                discId.getDiscLengthSeconds() < seconds + 11);
    }
}
