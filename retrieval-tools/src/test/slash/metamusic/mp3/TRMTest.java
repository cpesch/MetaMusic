/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.mp3;

import slash.metamusic.test.AbstractFileTest;
import slash.metamusic.trm.TRM;

import java.io.File;
import java.io.IOException;

/**
 * Simple tests for TRMs
 */

public class TRMTest extends AbstractFileTest {

    public TRMTest(String name) {
        super(name);
    }

    public void testInstantiateTRMObject() {
        new TRM();
    }

    public void testCalculateTRM() throws IOException {
        // catch as testInstantiateTRMObject would have failed before
        try {
            calculateTRM("Air - Moon Safari - 02 - Sexy boy.mp3",
                    "2bda7982-aa58-40f2-adf4-ef760f41347e", 299205);
            calculateTRM("Depeche Mode - Exciter - 03 - The Sweetest Condition.mp3",
                    "ced986fb-7e15-4d94-b0c5-edd95b28111d", 223042);
            calculateTRM("REM - Daysleeper.mp3",
                    "8b5baa15-e68c-4766-8938-fe756f01367e", 220136);
        }
        catch (UnsupportedOperationException e) {
        }
    }

    private void calculateTRM(String fileName, String trackId, long duration) throws IOException {
        File src = new File(AbstractFileTest.PATH_TO_TEST_MP3_FILES + fileName);

        TRM trm = new TRM();
        MP3File mp3 = new MP3File();
        assertTrue(mp3.read(src));
        log.fine("mp3 is " + mp3);

        trm.read(mp3);
        log.fine("trm is " + trm);

        assertEquals(trackId, trm.getSignature());
        assertEquals(duration, trm.getDuration());
    }
}
