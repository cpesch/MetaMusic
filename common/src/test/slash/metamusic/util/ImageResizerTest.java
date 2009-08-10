/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2005 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import slash.metamusic.test.AbstractFileTest;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Simple tests for ImageResizer
 */

public class ImageResizerTest extends AbstractFileTest {
    private ImageResizer resizer = new ImageResizer();
    private MimeTypeGuesser guesser = new MimeTypeGuesser();

    public ImageResizerTest(String name) {
        super(name);
    }

    public void testWriters() throws IOException {
        String[] writerFormatNames = ImageIO.getWriterFormatNames();
        assertContains(writerFormatNames, "bmp");
        assertContains(writerFormatNames, "jpg");
        assertContains(writerFormatNames, "png");
        assertContains(writerFormatNames, "wbmp");

        String[] writerMIMETypes = ImageIO.getWriterMIMETypes();
        assertContains(writerMIMETypes, "image/bmp");
        assertContains(writerMIMETypes, "image/jpeg");
        assertContains(writerMIMETypes, "image/png");
        assertContains(writerMIMETypes, "image/vnd.wap.wbmp");
    }

    public void testResizeGifAsJpg() throws IOException {
        byte[] imageData = URLLoader.getContents(new File(AbstractFileTest.PATH_TO_TEST_IMAGE_FILES + "dontpanic.gif"));
        assertEquals("image/gif", guesser.guess(imageData).toString());
        assertEquals(101, resizer.getWidth(imageData));
        assertEquals(72, resizer.getHeight(imageData));

        byte[] resizedData = resizer.resize(imageData, "jpg", 25, 20);
        assertNotEquals(imageData.length, resizedData.length);
        assertEquals("image/jpg", guesser.guess(resizedData).toString());
        assertEquals(25, resizer.getWidth(resizedData));
        assertEquals(17, resizer.getHeight(resizedData));
    }

    public void testResizeJpg() throws IOException {
        byte[] imageData = URLLoader.getContents(new File(AbstractFileTest.PATH_TO_TEST_IMAGE_FILES + "dontpanic.jpg"));
        assertEquals("image/jpg", guesser.guess(imageData).toString());
        assertEquals(101, resizer.getWidth(imageData));
        assertEquals(72, resizer.getHeight(imageData));

        byte[] resizedData = resizer.resize(imageData, "jpg", 25, 20);
        assertNotEquals(imageData.length, resizedData.length);
        assertEquals("image/jpg", guesser.guess(resizedData).toString());
        assertEquals(25, resizer.getWidth(resizedData));
        assertEquals(17, resizer.getHeight(resizedData));
    }
}
