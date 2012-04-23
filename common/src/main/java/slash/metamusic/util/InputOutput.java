/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Reads from input and writes to output. If start is called this
 * class' instances will read all available data from the InputStream
 * and write it to the OutputStream. Think of it as a U-pipe with a
 * buffer.
 *
 * @author Christian Pesch
 * @version $Id: InputOutput.java 159 2003-12-01 09:43:25Z cpesch $
 */

public class InputOutput {
    public static final int CHUNK_SIZE = (4 * 1024);

    private int chunkSize = CHUNK_SIZE;
    private InputStream input;
    private OutputStream output;

    /**
     * Create U-pipe from input to output.
     */
    public InputOutput(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Get the amount of bytes to transfer in one chunk.
     */
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * Set the amount of bytes to transfer in one chunk.
     */
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * When started this read all available data from the
     * InputStream and write it to the OutputStream.
     *
     * @return the amount of bytes copied
     */
    public long start() throws IOException {
        byte[] chunk = new byte[chunkSize];
        long count = 0;

        while (true) {
            int read = input.read(chunk);
            if (read == -1) {
                // no more data available
                break;
            }

            output.write(chunk, 0, read);
            count += read;
        }
        return count;
    }

    /**
     * Copy the given amount of bytes from the
     * InputStream and write it to the OutputStream.
     *
     * @param bytes the amount of bytes to copy
     * @return the amount of bytes copied
     */
    public long copy(long bytes) throws IOException {
        byte[] chunk = new byte[chunkSize];
        long count = 0;

        while (count < bytes) {
            // calculate number of bytes to read
            long read = bytes - count < chunk.length ? bytes - count : chunk.length;
            long len = input.read(chunk, 0, (int) read);
            if (len == -1) {
                // no more data available
                break;
            }

            // full chunk
            if (len == chunk.length) {
                output.write(chunk);
                count += chunk.length;

            } else {
                // partial chunk
                byte[] rightSize = new byte[(int) len];
                System.arraycopy(chunk, 0, rightSize, 0, (int) len);
                output.write(rightSize);
                count += rightSize.length;
            }
        }

        return count;
    }

    /**
     * Close the streams.
     */
    public void close() throws IOException {
        input.close();
        output.close();
    }

    /**
     * Reads the bytes from the given URL.
     */
    public static byte[] readBytes(URL url) throws IOException {
        return readBytes(url.openStream());
    }

    /**
     * Reads the bytes from the given InputStream.
     */
    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputOutput pipe = new InputOutput(in, out);
        pipe.start();
        pipe.close();
        return out.toByteArray();
    }
}
