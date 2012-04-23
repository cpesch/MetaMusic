/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Tries to resize a given image.
 *
 * @author Christian Pesch
 * @version $Id: ImageResizer.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class ImageResizer {

    public byte[] resize(byte[] image, String format, int widthLimit, int heightLimit) {
        try {
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(image));
            if (src != null && (src.getWidth() > widthLimit || src.getHeight() > heightLimit)) {
                double widthFactor = (double) src.getWidth() / (double) widthLimit;
                double heightFactor = (double) src.getHeight() / (double) heightLimit;
                double scalingFactor = widthFactor > heightFactor ? widthFactor : heightFactor;
                int scaledWidth = (int) (src.getWidth() / scalingFactor);
                int scaledHeight = (int) (src.getHeight() / scalingFactor);
                Image scaledImage = src.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                BufferedImage dest = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = dest.createGraphics();
                try {
                    graphics.drawImage(scaledImage, 0, 0, null);
                    /* this was proposed in some forums but produces solid coloured images only ;-(
                    AffineTransform at = AffineTransform.getScaleInstance(scaledWidth, scaledHeight);
                    graphics.drawRenderedImage(src, at);
                    */
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(dest, format, out);
                    return out.toByteArray();
                } finally {
                    graphics.dispose();
                    scaledImage.flush();
                }
            }
        }
        catch (IOException e) {
            System.err.println("Cannot resize image with " + image.length + " bytes as " + format);
        }
        return image;
    }

    public int getWidth(byte[] image) {
        try {
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(image));
            return src.getWidth();
        } catch (IOException e) {
            System.err.println("Cannot calculate width of " + image.length + " bytes");
            return -1;
        }
    }

    public int getHeight(byte[] image) {
        try {
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(image));
            return src.getHeight();
        } catch (IOException e) {
            System.err.println("Cannot calculate height of " + image.length + " bytes");
            return -1;
        }
    }
}

