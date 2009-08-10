/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.mp3.util;

/**
 * My instances represent a 16-Bit CRC checksum.
 *
 * @author Christian Pesch
 * @version $Id: CRC16.java 159 2003-12-01 09:43:25Z cpesch $
 */

public final class CRC16 {

    /**
     * Construct object and reset with initial value.
     */
    public CRC16() {
        reset();
    }

    /**
     * Call Constructor and update.
     */
    public CRC16(int bitstring, int length) {
        this();
        update(bitstring, length);
    }

    /**
     * Resets CRC-16 to initial value
     */
    public void reset() {
        this.crc = (short) 0xFFFF;
    }

    /**
     * Feed a bitstring to the crc calculation (0 < length <= 32).
     */
    public void update(int bitstring, int length) {
        int bitmask = 1 << (length - 1);
        do
            if (((crc & 0x8000) == 0) ^ ((bitstring & bitmask) == 0)) {
                crc <<= 1;
                crc ^= polynomial;
            } else
                crc <<= 1;
        while ((bitmask >>>= 1) != 0);
    }

    /**
     * Return the calculated checksum.
     * Erase it for next calls to addBits().
     */
    public short getValue() {
        short sum = crc;
        crc = (short) 0xFFFF;
        return sum;
    }

    // --- member variables ------------------------------------

    private short polynomial = (short) 0x8005;
    private short crc;
}

