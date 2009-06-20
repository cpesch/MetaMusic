/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2001-2003 Christian Pesch. All Rights Reserved.
*/

/*
 * Copyright (c) 2000, Ronald Lenk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice unmodified, this list of conditions, and the following
 *    disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package slash.metamusic.discid;

import slash.metamusic.mp3.util.TimeConversion;
import slash.metamusic.util.LibraryLoader;
import slash.metamusic.util.OperationSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A class to represent the information contained in a Compact Disc
 * table of contents.
 *
 * @author Christian Pesch based on work from Ronald Lenk
 * @version $Id: DiscId.java 959 2007-03-11 08:21:11Z cpesch $
 */

public class DiscId implements Serializable {

    /**
     * Logging output
     */
    protected static final Logger log = Logger.getLogger(DiscId.class.getName());

    private static boolean libraryLoaded = false;

    static {
        try {
            LibraryLoader.loadLibrary(DiscId.class.getClassLoader(), "discid");
            libraryLoaded = true;
        } catch (IOException e) {
            log.severe("Cannot load native library 'discid': " + e.getMessage());
        }
    }

    /**
     * Read the track offset array from the specified device. The
     * method returns the number of tracks in the TOC, not including
     * the leadout track, or -1 if an error occurs.
     */
    private static native int deviceRead(String deviceName, int trackOffset[])
            throws IOException;

    /**
     * Number of frames one second on a CD consists of.
     */
    public static final int FRAMES_PER_SECOND = 75;

    /**
     * The disc id of the disc.
     */
    private int discId;

    /**
     * The number of tracks in the disc's table of contents. If the
     * table of contents has not been read from the device, or the
     * last read resulted in an error, the value is -1.
     */
    private int trackCount;

    /**
     * The array containing the frame offset for each track on the
     * disc: trackOffsets[0] through trackOffsets[trackCount - 1]
     * contain the offsets for the tracks on the disc,
     * trackOffsets[trackCount] contains the frame offset of the
     * leadout area on the disc. This array is created by the
     * constructor, and always contains 100 elements, regardless of
     * the number of tracks actually on the disc.
     */
    private int trackOffsets[];

    /**
     * The length of the disc in milliseconds.
     */
    private int discLength;

    /**
     * Is true if the DiscId data has been read successfully.
     */
    private boolean valid;

    /**
     * Return whether the DiscId calculation is supported on this plattform.
     *
     * @return true, if the DiscId calculation is supported on this plattform
     */
    public static boolean isSupported() {
        return libraryLoaded;
    }

    /**
     * Construct a <code>DiscId</code> object with the given data.
     */
    public DiscId(int discId, int trackCount, int[] trackOffsets, int discLength, boolean valid) {
        this.discId = discId;
        this.trackCount = trackCount;
        this.trackOffsets = trackOffsets;
        this.discLength = discLength;
        this.valid = valid;
    }

    /**
     * Construct a <code>DiscId</code> object with the given data.
     */
    public DiscId(String discId, int trackCount, int[] trackOffsets, int discLength, boolean valid) {
        this(decodeDiscId(discId), trackCount, trackOffsets, discLength, valid);
    }

    /**
     * Construct an (invalid) <code>DiscId</code> object.
     * Call read() to have a valid DiscId object.
     */
    public DiscId() {
        this(-1, -1, new int[100], -1, false);
    }

    /**
     * Calculate a <code>DiscId</code> object from the given data.
     */
    public DiscId(int trackCount, int[] trackOffsets) {
        calculateData(trackCount, trackOffsets);
    }

    /**
     * Construct a <code>DiscId</code> object, and attempt to read
     * the table of contents from the device specified in
     * <code>device</code>.
     *
     * @param device A <code>File</code> for the CD device to read from.
     *               On Solaris, this is the physical device, such as "/dev/rdsk/c0t2d0s0".
     *               On Windows, this should always be specified as the "cdaudio" pseudo device.
     *               On Linux, this is the physical device, such as "/dev/sdb" or "/dev/hdb".
     * @throws IOException If an error occurred while
     *                     reading from the specified device.
     */
    public DiscId(File device) throws IOException {
        this();
        read(device);
    }

    private void calculateData(int trackCount, int[] readOffsets) {
        this.trackOffsets = new int[100];
        System.arraycopy(readOffsets, 0, trackOffsets, 0, readOffsets.length);

        if (trackCount > 99) {
            log.warning("TOC claims to have too many tracks (" + trackCount + "), limiting to 99");
            trackCount = 99;
        }

        // not checking the last track
        int positiveFrameSizeTrackCount = 0;
        for (int i = 0, c = trackCount; i < c; i++) {
            int trackLength = trackOffsets[i + 1] - trackOffsets[i];
            if (trackLength > 0)
                positiveFrameSizeTrackCount++;
        }
        boolean filledUpEntries = trackCount == 99 && trackOffsets[positiveFrameSizeTrackCount] == 150 &&
                trackOffsets[trackCount] == 375;
        boolean negativeTrackSize = trackOffsets[trackCount - 2] > trackOffsets[trackCount - 1] &&
                trackOffsets[trackCount] == 375;

        if (filledUpEntries) {
            log.warning("TOC is copy protected by filling up entries");
            trackCount = positiveFrameSizeTrackCount;
            trackOffsets[0] = 150;
            trackOffsets[trackCount] = trackOffsets[trackCount - 1] + 300;
        } else if (negativeTrackSize) {
            log.warning("TOC is copy protected with negative track size");
            trackCount--;
            trackOffsets[trackCount] = trackOffsets[trackCount - 1] + 600;
        } else if (positiveFrameSizeTrackCount != trackCount) {
            log.warning("TOC says " + trackCount + " tracks, but only " + positiveFrameSizeTrackCount + " tracks with positive size");
            trackCount = positiveFrameSizeTrackCount;
        }

        this.trackCount = trackCount;
        this.discLength = trackOffsets[trackCount] / FRAMES_PER_SECOND - trackOffsets[0] / FRAMES_PER_SECOND;

        int counter = 0;
        for (int i = 0, c = trackCount; i < c; i++) {
            int trackOffset = trackOffsets[i] / FRAMES_PER_SECOND;
            while (trackOffset > 0) {
                counter += trackOffset % 10;
                trackOffset /= 10;
            }
        }

        this.discId = (counter & 0xFF) << 24 | discLength << 8 | trackCount;

        // CDEx and Feurio do not remove the first track offset from the disc length
        // if(negativeTrackSize)
        //  discLength += trackOffsets[0] / FRAMES_PER_SECOND;

        this.valid = discId != -1 && trackCount > 0 && trackOffsets.length > 0 && discLength > 0;
    }

    /**
     * Read the table of contents from the device.
     *
     * @param device A <code>File</code> for the CD device to read from.
     *               On Solaris, this is the physical device, such as "/dev/rdsk/c0t2d0s0".
     *               On Windows, this should always be specified as the "cdaudio" pseudo device.
     *               On Linux, this is the physical device, such as "/dev/sdb" or "/dev/hdb".
     * @throws IOException If an error occurs while
     *                     reading from the device.
     */
    public void read(File device) throws IOException {
        if (!libraryLoaded)
            throw new UnsupportedOperationException("Native 'discid' library not loaded");

        int[] trackOffsets = new int[100];
        // do not absolutize path here since that make Windows "cdaudio" device useless
        int trackCount = deviceRead(device.getPath(), trackOffsets);
        calculateData(trackCount, trackOffsets);
    }

    /**
     * Returns if the DiscId has been read successfully.
     *
     * @return true if the DiscId has been read successfully
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Return the disc id.
     */
    public int getDiscId() {
        return discId;
    }

    /**
     * Get the number of tracks on the disc, not including the leadout
     * track. If the table of contents has not been read, or the last
     * read resulted in an error, the result is undefined.
     * <p/>
     * Note: this value is used to query the FreeDB
     *
     * @return The number of tracks on the disc, not including the
     *         leadout track.
     */
    public int getTrackCount() {
        return trackCount;
    }

    /**
     * Compute the FreeDB disc identifier using the algorithm specified in
     * the server documentation and return it as hexadecimal encoded string
     * ready to feed into FreeDB requests.
     */
    public String getEncodedDiscId() {
        return encodeDiscId(getDiscId());
    }

    /**
     * Convert the discId from an int to a format required for FreeDB queries.
     * <p/>
     * Freedb access requires that the software computes a "disc ID" which is
     * an identifier that is used to access thefreedb.  The disc ID is a
     * 8-digit hexadecimal (base-16) number, computed using data from a CD's
     * Table-of-Contents (TOC) in MSF (Minute Second Frame) form.
     * The algorithm is listed below in Appendix A.
     *
     * @param discId the discId as an int
     * @return the discId encoded as above
     * @see #decodeDiscId(String)
     */
    public static String encodeDiscId(int discId) {
        String encoded = Integer.toHexString(discId);
        while (encoded.length() < 8)
            encoded = "0" + encoded;
        return encoded;
    }

    /**
     * Convert the discId from the format required for FreeDB
     * queries to an int. If the discId is not valid, -1 is returned
     *
     * @param discId the discId as a String
     * @return the discId as an int
     * @see #encodeDiscId(int)
     */
    public static int decodeDiscId(String discId) {
        try {
            return Long.valueOf(discId, 16).intValue();
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Get the length of the disc in terms of the number of 2352 byte
     * (1/75 sec) frames. If the table of contents has not been read,
     * or the last read resulted in an error, the result is undefined.
     *
     * @return The length of the disc in terms of 2352 byte (1/75 sec)
     *         frames.
     */
    public int getDiscLengthFrames() {
        return getTrackStartFrame(getTrackCount()) - getTrackStartFrame(0);
    }

    /**
     * Get the length of the disc including the leadin in milliseconds.
     * If the table of contents has not been read, or the last read
     * resulted in an error, the result is undefined.
     *
     * @return The length of the disc in milliseconds.
     */
    public int getDiscLengthMillis() {
        return getDiscLengthSeconds() * 1000;
    }

    /**
     * Get the length of the disc including the leadin in seconds.
     * If the table of contents has not been read, or the last read
     * resulted in an error, the result is undefined.
     *
     * @return the length of the disc in seconds.
     */
    public int getDiscLengthSeconds() {
        return discLength;
    }

    /**
     * Return the query string which may be used for a FreeDB query.
     *
     * @return the query string which may be used to for a FreeDB query
     */
    public String getFreeDBQueryString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getEncodedDiscId()).append(" ").append(getTrackCount()).append(" ");
        for (int i = 0, c = getTrackCount(); i < c; i++) {
            buffer.append(getTrackStartFrame(i)).append(" ");
        }
        buffer.append(getDiscLengthSeconds());
        return buffer.toString();
    }

    /**
     * Get the starting frame of the specified track. If the table of
     * contents has not been read, or the last read resulted in an
     * error, the result is undefined.
     * <p/>
     * Note: this value is used to access the FreeDB
     *
     * @param trackNumber An integer containing the track number,
     * @return The starting frame of the specified track.
     */
    public int getTrackStartFrame(int trackNumber) {
        return trackOffsets[trackNumber];
    }

    /**
     * Get the length of the specified track in 2352 byte (1/75 sec)
     * frames. If the table of contents has not been read, or the last
     * read resulted in an error, the result is undefined.
     *
     * @param trackNumber An integer containing the track number.
     * @return The length of the track in frames.
     */
    public int getTrackLengthFrames(int trackNumber) {
        int endFrame = trackNumber + 1 < trackOffsets.length ?
                getTrackStartFrame(trackNumber + 1) :
                getDiscLengthSeconds() * FRAMES_PER_SECOND;
        int startFrame = getTrackStartFrame(trackNumber);
        return endFrame - startFrame;
    }

    /**
     * Get the length of the specified track in milliseconds.
     * If the table of contents has not been read, or the last
     * read resulted in an error, result is undefined.
     *
     * @param trackNumber An integer containing the track number.
     * @return The length of the track in milliseconds.
     */
    public int getTrackLengthMillis(int trackNumber) {
        return (int) Math.ceil(getTrackLengthFrames(trackNumber) * 1000.0 / FRAMES_PER_SECOND);
    }

    /**
     * Get the length of the specified track in seconds.
     * If the table of contents has not been read, or the last
     * read resulted in an error, result is undefined.
     *
     * @param trackNumber An integer containing the track number.
     * @return The length of the track in seconds.
     */
    public int getTrackLengthSeconds(int trackNumber) {
        return (int) Math.ceil(getTrackLengthFrames(trackNumber) / FRAMES_PER_SECOND);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscId)) return false;

        final DiscId discId1 = (DiscId) o;

        if (discId != discId1.discId) return false;
        if (discLength != discId1.discLength) return false;
        if (trackCount != discId1.trackCount) return false;
        if (valid != discId1.valid) return false;
        if (!Arrays.equals(trackOffsets, discId1.trackOffsets)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = discId;
        result = 29 * result + trackCount;
        result = 29 * result + discLength;
        result = 29 * result + (valid ? 1 : 0);
        for (int trackOffset : trackOffsets) result = 29 * result + trackOffset;
        return result;
    }


    public String toString() {
        return super.toString() + "[discId=" + getEncodedDiscId() +
                ", trackCount=" + getTrackCount() +
                ", discLengthSeconds=" + getDiscLengthSeconds() + "]";
    }

    public static void main(String[] args) throws Exception {
        String device = args.length == 0 ? OperationSystem.getDefaultDeviceName() : args[0];
        DiscId discId = new DiscId(new File(device));
        System.out.print("FreeDB query string: " + discId.getFreeDBQueryString());
        System.out.println();
        System.out.println("disc id:             " + discId.getEncodedDiscId());
        System.out.println("track count:         " + discId.getTrackCount());
        System.out.println("disc length:         " + discId.getDiscLengthSeconds() + " seconds " +
                "(" + TimeConversion.getTimeFromSeconds(discId.getDiscLengthMillis() / 1000) + ") " +
                discId.getDiscLengthFrames() + " frames");
        for (int i = 0, c = discId.getTrackCount(); i < c; i++) {
            System.out.println(i + ". track: " +
                    discId.getTrackLengthMillis(i) / 1000 + " seconds (" +
                    TimeConversion.getTimeFromMilliSeconds(discId.getTrackLengthMillis(i)) + ") " +
                    discId.getTrackLengthFrames(i) + " frames");
        }
        System.exit(0);
    }
}
