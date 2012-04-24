/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.test;

import slash.metamusic.util.Files;

import java.io.File;
import java.io.IOException;

import static java.io.File.separator;

/**
 * Base class for tests with test files
 */

public abstract class AbstractFileTest extends TestCase {
    public static final String SAMPLES_PATH = System.getProperty("samples", "test" + separator + "src" + separator);
    public static final String PATH_TO_TEST_IMAGE_FILES = SAMPLES_PATH + separator + "data" + separator + "image" + separator;
    public static final String PATH_TO_TEST_TEXT_FILES = SAMPLES_PATH + separator + "data" + separator + "text" + separator;
    public static final String PATH_TO_TEST_MP3_FILES = SAMPLES_PATH + separator + "data" + separator + "mp3" + separator;
    protected static boolean cleanup = true;
    protected File tempFile = null;

    public AbstractFileTest(String name) {
        super(name);
    }

    public void tearDown() {
        if (AbstractFileTest.cleanup) {
            if (tempFile != null)
                assertTrue(tempFile.delete());
        }
    }

    protected void copyToTempFile(File file) throws IOException {
        tempFile = File.createTempFile("filetest", "." + Files.getExtension(file));
        assertNotNull(tempFile);
        Files.copy(file, tempFile);
        assertEquals(file.length(), tempFile.length());
    }
}
