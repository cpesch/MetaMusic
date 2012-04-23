/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import javax.activation.MimeType;
import java.io.File;
import java.io.FileInputStream;

/**
 * Tries to guess the {@link MimeType} from a list of bytes.
 *
 * @author Christian Pesch
 * @version $Id: MimeTypeGuesser.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class MimeTypeGuesser {
    private static int bytesNeeded;
    private static MimeTypeDescriptor[] knownTypes;

    static {
        try {
            knownTypes = new MimeTypeDescriptor[]{
                    new MimeTypeDescriptor(new MimeType("application", "mac-binhex40"), 11, "must be converted with BinHex".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("application", "x-gzip"), 0, new byte[]{31, 139 - 256}),
                    new MimeTypeDescriptor(new MimeType("application", "pdf"), 0, "%PDF-".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("application", "postscript"), 0, "%!PS-".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("application", "zip"), 0, "PK\u0003\u0004".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("image", "tiff"), 0, new byte[]{77, 77}),
                    new MimeTypeDescriptor(new MimeType("image", "tiff"), 0, new byte[]{73, 73}),
                    new MimeTypeDescriptor(new MimeType("image", "gif"), 0, "GIF87a".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("image", "gif"), 0, "GIF89a".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("image", "jpg"), 0, new byte[]{255 - 256, 216 - 256, 255 - 256, 224 - 256}),
                    new MimeTypeDescriptor(new MimeType("image", "jpg"), 0, new byte[]{255 - 256, 216 - 256, 255 - 256, 238 - 256}),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<!DOCTYPE HTML".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<!doctype html".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<HEAD".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<head".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<TITLE".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<title".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<HTML".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "html"), 0, "<html".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "xml"), 0, "<?XML".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("text", "xml"), 0, "<?xml".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("image", "x-rgb"), 0, new byte[]{1, 218 - 256}),
                    new MimeTypeDescriptor(new MimeType("image", "png"), 0, "\u0089PNG".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("image", "bmp"), 0, "BM".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("audio", "basic"), 0, ".snd".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("audio", "x-wav"), 8, "WAVE".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("audio", "x-mpeg"), 0, new byte[]{255 - 256, 251 - 256}),
                    new MimeTypeDescriptor(new MimeType("audio", "x-mpeg"), 0, "ID3".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("video", "mpeg"), 0, new byte[]{0, 0, 1, 179 - 256}),
                    new MimeTypeDescriptor(new MimeType("audio", "x-midi"), 0, "MThd".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("application", "octet-stream"), 0, new byte[]{77, 90, 144 - 256, 0, 3, 0, 0, 0, 4}),
                    new MimeTypeDescriptor(new MimeType("application", "vnd.rn-realmedia"), 1, "RMF".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("application", "x-shockwave-flash"), 0, "FWS".getBytes("ISO8859_1")),
                    new MimeTypeDescriptor(new MimeType("application", "vnd.ms-powerpoint"), 1152,
                            // at position 0x480: "PowerPoint Document" in Unicode
                            new byte[]{80, 0, 111, 0, 119, 0, 101, 0, 114, 0,
                                    80, 0, 111, 0, 105, 0, 110, 0, 116, 0, 32, 0,
                                    68, 0, 111, 0, 99, 0, 117, 0, 109, 0, 101, 0, 110, 0, 116, 0})
            };
        } catch (Throwable t) {
        }
        for (MimeTypeDescriptor knownType : knownTypes)
            bytesNeeded = Math.max(bytesNeeded, knownType.offset + knownType.bytes.length);
    }


    public MimeType guess(final byte[] bytes) {
        for (MimeTypeDescriptor knownType : knownTypes)
            if (knownType.matches(bytes))
                return knownType.mimeType;
        return null;
    }

    public MimeType guess(final File file) throws java.io.IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] needed = new byte[bytesNeeded];
            int bytesRead = in.read(needed);
            if (bytesRead != bytesNeeded)
                System.err.println("Could read only " + bytesRead + " out of  " + bytesNeeded + " bytes from '" + file.getAbsolutePath() + "'");
            return guess(needed);
        } finally {
            in.close();
        }
    }

    /**
     * Answer the number of bytes that are needed in order to determine the mime type.
     *
     * @return The number of bytes that are needed in order to determine the mime type.
     */
    public int getBytesNeeded() {
        return bytesNeeded;
    }

    private static class MimeTypeDescriptor {
        private MimeType mimeType;
        private int offset;
        private byte[] bytes;

        private MimeTypeDescriptor(MimeType mimeType, int offset, byte[] bytes) {
            this.mimeType = mimeType;
            this.offset = offset;
            this.bytes = bytes;
        }

        public boolean matches(byte[] data) {
            if (data.length < offset + bytes.length)
                return false;

            for (int i = 0; i < bytes.length; i++)
                if (data[offset + i] != bytes[i])
                    return false;

            return true;
        }
    }


    public static void main(String args[]) throws Exception {
        if (args.length != 1) {
            System.out.println("slash.metamusic.util.MimeTypeGuesser <file>");
            System.exit(1);
        }

        File file = new File(args[0]);
        MimeTypeGuesser guesser = new MimeTypeGuesser();
        MimeType mimeType = guesser.guess(file);
        System.out.println("File '" + file.getAbsolutePath() + "' has MIME type " + mimeType);
        System.exit(0);
    }
}
