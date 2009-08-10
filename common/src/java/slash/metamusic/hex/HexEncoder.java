/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.hex;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Helps to encode bytes, shorts, ints to hexadecimal encoded
 * digits or an Outputstream to an encoded Writer.
 *
 * @author Christian Pesch
 * @version $Id: HexEncoder.java 819 2006-10-21 12:43:36Z cpesch $
 */

public class HexEncoder extends OutputStream {
    private Writer writer;

    public HexEncoder(Writer writer) {
        this.writer = writer;
    }

    public void write(int anInt) throws IOException {
        writer.write(lowByte(anInt));
        writer.write(highByte(anInt));
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void close() throws IOException {
        writer.close();
    }


    private static final char[] encoding = new char[16];

    static {
        encoding[0] = '0';
        encoding[1] = '1';
        encoding[2] = '2';
        encoding[3] = '3';
        encoding[4] = '4';
        encoding[5] = '5';
        encoding[6] = '6';
        encoding[7] = '7';
        encoding[8] = '8';
        encoding[9] = '9';
        encoding[10] = 'A';
        encoding[11] = 'B';
        encoding[12] = 'C';
        encoding[13] = 'D';
        encoding[14] = 'E';
        encoding[15] = 'F';
    }

    private static char lowByte(int aByte) {
        return encoding[((aByte >> 4) & 0x0f)];
    }

    private static char highByte(int aByte) {
        return encoding[(aByte & 0x0f)];
    }

    public static String encodeByte(byte aByte) {
        char[] chars = new char[2];
        chars[0] = lowByte(aByte);
        chars[1] = highByte(aByte);
        return new String(chars);
    }

    public static String encodeBytes(byte[] bytes) {
        StringWriter writer = new StringWriter(2 * bytes.length);
        HexEncoder encoder = new HexEncoder(writer);
        try {
            encoder.write(bytes);
            encoder.close();
        } catch (IOException e) {
            throw new RuntimeException("No io exception is possible here");
        }
        return writer.toString();
    }

    public static String encodeShort(short aShort) {
        return encodeByte((byte) ((aShort >> 8) & 0x00ff)) +
                encodeByte((byte) (aShort & 0x00ff));
    }

    public static String encodeInt(int anInt) {
        return encodeShort((short) ((anInt >> 16) & 0x0000ffff)) +
                encodeShort((short) (anInt & 0x0000ffff));
    }

    public static String encodeLong(long aLong) {
        return encodeInt((int) ((aLong >> 32))) +
                encodeInt((int) (aLong));
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(HexEncoder.class + " <int1> [<int2> ...]");
            System.exit(1);
        }

        for (String arg : args) {
            int decimal = Integer.parseInt(arg);
            System.out.println("Dec " + decimal + " is hex " + HexEncoder.encodeInt(decimal));
        }
        System.exit(0);
    }
}
