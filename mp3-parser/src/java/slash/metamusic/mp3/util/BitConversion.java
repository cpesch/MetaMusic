/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Some useful functions for
 * <ul>
 * <li>shifting bits
 * <li>converting byte arrays to int and vica versa
 * <li>endianess handling
 * </ul>
 *
 * @author Christian Pesch
 * @version $Id: BitConversion.java 938 2007-01-05 18:22:34Z cpesch $
 */

public class BitConversion {

    public static int powN(int pow, double base) {
        return new Double(Math.pow(base, pow)).intValue();
    }

    public static int pow2(int value) {
        return 1 << value;
    }

    public static int bitmask(int numberOfBits) {
        return pow2(numberOfBits) - 1;
    }

    public static int shiftAnd(int value, int firstBit, int numberOfBits) {
        return (value >>> firstBit) & bitmask(numberOfBits);
    }

    /**
     * Check if selected bit is set in <code>input</code>.
     *
     * @param value Value to check
     * @param bit   Bit number to check (0..7 with 7 MSB)
     * @return 1 if bit is set, 0 otherwise
     */
    public static int getBit(int value, int bit) {
        return ((value & (1 << bit)) > 0) ? 1 : 0;
    }

    /**
     * Sets the bit at the specified location in the byte given.  Location
     * should be between 0 and 7.  If an invalid location is specified, 0
     * will be returned.
     *
     * @param b        the byte to set the index in
     * @param location the index to set in b
     * @return b with the bit at location set to 1
     */
    public static byte setBit(byte b, int location) {
        byte ret = 0;

        if ((location >= 0) && (location < 8)) {
            ret = (byte) (b | (byte) (1 << location));
        }

        return ret;
    }

    public static int stringToInt(String value) {
        int result;
        try {
            result = new Integer(value.trim()).intValue();
        } catch (NumberFormatException e) {
            result = -1;
        }
        return result;
    }

    public static int unsignedByteToInt(byte value) {
        return value & 0xFF;
    }

    public static void printBits(int value) {
        for (int i = 0; i < 32; i++) {
            System.out.print(" " + i + (i < 10 ? " " : ""));
        }
        System.out.println();
        for (int i = 0; i < 32; i++) {
            System.out.print(" " + shiftAnd(value, i, 1) + " ");
        }
        System.out.println();
    }


    public static boolean findString(InputStream in, String find, String encoding) throws IOException {
        byte[] header = new byte[find.length()];
        in.read(header);
        return find.equals(new String(header, encoding));
    }


    public static byte[] swapEndianess(byte[] buffer) {
        byte[] temp = new byte[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            System.arraycopy(buffer, i, temp, temp.length - 1 - i, 1);
        }
        return temp;
    }

    // -- big endian operations -------------------------------------------------------

    public static int extract2BigEndian(InputStream in) throws IOException {
        byte[] buffer = new byte[2];
        in.read(buffer);
        return extract2Endian(buffer);
    }

    public static long extract4BigEndian(InputStream in) throws IOException {
        byte[] buffer = new byte[4];
        in.read(buffer);
        return extract4BigEndian(buffer);
    }

    public static int extract4BigEndian(byte[] buffer, int offset) {
        byte[] temp = new byte[4];
        System.arraycopy(buffer, offset, temp, 0, temp.length);
        return extract4BigEndian(temp);
    }

    public static int extract4BigEndian(byte[] buffer) {
        return unsignedByteToInt(buffer[0]) << 24 |
                unsignedByteToInt(buffer[1]) << 16 |
                extract2BigEndian(buffer, 2);
    }

    public static int extract3BigEndian(byte[] buffer, int offset) {
        byte[] temp = new byte[4];
        System.arraycopy(buffer, offset, temp, 1, 3);
        return extract4BigEndian(temp);
    }

    public static int extract2BigEndian(byte[] buffer, int offset) {
        byte[] temp = new byte[2];
        System.arraycopy(buffer, offset, temp, 0, temp.length);
        return extract2Endian(temp);
    }

    public static int extract2Endian(byte[] buffer) {
        return unsignedByteToInt(buffer[0]) << 8 | unsignedByteToInt(buffer[1]);
    }


    public static byte[] create2BigEndian(int number) {
        return new byte[]{(byte) (number >> 8), (byte) number};
    }

    public static byte[] create3BigEndian(int number) {
        return new byte[]{(byte) (number >> 16), (byte) (number >> 8), (byte) number};
    }

    public static byte[] create4BigEndian(int number) {
        return new byte[]{(byte) (number >> 24), (byte) (number >> 16), (byte) (number >> 8), (byte) number};
    }

    // -- little endian operations ----------------------------------------------------

    public static int extract2LittleEndian(byte[] buffer) {
        return extract2Endian(swapEndianess(buffer));
    }

    public static int extract2LittleEndian(byte[] buffer, int offset) {
        byte[] temp = new byte[2];
        System.arraycopy(buffer, offset, temp, 0, temp.length);
        return extract2LittleEndian(temp);
    }

    public static int extract4LittleEndian(byte[] buffer) {
        return extract2LittleEndian(buffer, 0) |
                unsignedByteToInt(buffer[2]) << 16 +
                        unsignedByteToInt(buffer[3]) << 24;
    }

    public static int extract4LittleEndian(byte[] buffer, int offset) {
        byte[] temp = new byte[4];
        System.arraycopy(buffer, offset, temp, 0, temp.length);
        return extract4LittleEndian(temp);
    }


    public static int extract2LittleEndian(InputStream in) throws IOException {
        byte[] buffer = new byte[2];
        in.read(buffer);
        return extract2LittleEndian(buffer);
    }

    public static long extract4LittleEndian(InputStream in) throws IOException {
        byte[] buffer = new byte[4];
        in.read(buffer);
        return extract4LittleEndian(buffer);
    }
}
