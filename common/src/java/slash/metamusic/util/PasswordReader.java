/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
*/

package slash.metamusic.util;

import java.io.*;
import java.util.Arrays;

/**
 * Helper to read passwords from a {@link PushbackInputStream}.
 */

public class PasswordReader {

    /**
     * Reads chars from in until an end-of-line sequence (EOL) or end-of-file (EOF) is encountered,
     * and then returns the data as a char[].
     * <p/>
     * The EOL sequence may be any of the standard formats: '\n' (unix), '\r' (mac), "\r\n" (dos).
     * The EOL sequence is always completely read off the stream but is never included in the result.
     * <i>Note:</i> this means that the result will never contain the chars '\n' or '\r'.
     * In order to guarantee reading thru but not beyond the EOL sequence for all formats (unix, mac, dos),
     * this method requires that a PushbackReader and not a more general Reader be supplied.
     * <p/>
     * The code is secure: no Strings are used, only char arrays,
     * and all such arrays other than the result are guaranteed to be blanked out after last use to ensure privacy.
     * Thus, this method is suitable for reading in sensitive information such as passwords.
     * <p/>
     * This method never returns null; if no data before the EOL or EOF is read, a zero-length char[] is returned.
     * <p/>
     *
     * @throws IllegalArgumentException if in == null
     * @throws IOException              if an I/O problem occurs
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/security/jce/JCERefGuide.html#PBEEx">Password based encryption code examples from JCE documentation</a>
     */
    public static char[] readLineSecure(PushbackReader in) throws IllegalArgumentException, IOException {
        if (in == null) throw new IllegalArgumentException("in == null");

        char[] buffer = null;
        try {
            buffer = new char[128];
            int offset = 0;

            loop:
            while (true) {
                int c = in.read();
                switch (c) {
                    case -1:
                    case '\n':
                        break loop;

                    case '\r':
                        int c2 = in.read();
                        if ((c2 != '\n') && (c2 != -1))
                            in.unread(c2);    // guarantees that mac & dos line end sequences are completely read thru but not beyond
                        break loop;

                    default:
                        buffer = checkBuffer(buffer, offset);
                        buffer[offset++] = (char) c;
                        break;
                }
            }

            char[] result = new char[offset];
            System.arraycopy(buffer, 0, result, 0, offset);
            return result;
        } finally {
            eraseChars(buffer);
        }
    }

    /**
     * Checks if buffer is sufficiently large to store an element at an index == offset.
     * If it is, then buffer is simply returned.
     * If it is not, then a new char[] of more than sufficient size is created and initialized with buffer's current elements and returned;
     * the original supplied buffer is guaranteed to be blanked out upon method return in this case.
     * <p/>
     *
     * @throws IllegalArgumentException if buffer == null; offset < 0
     */
    public static char[] checkBuffer(char[] buffer, int offset) throws IllegalArgumentException {
        if (buffer == null) throw new IllegalArgumentException("buffer == null");
        if (offset < 0) throw new IllegalArgumentException("offset = " + offset + " is < 0");

        if (offset < buffer.length)
            return buffer;
        else {
            try {
                char[] bufferNew = new char[offset + 128];
                System.arraycopy(buffer, 0, bufferNew, 0, buffer.length);
                return bufferNew;
            } finally {
                eraseChars(buffer);
            }
        }
    }

    /**
     * If buffer is not null, fills buffer with space (' ') chars.
     */
    public static void eraseChars(char[] buffer) {
        if (buffer != null) Arrays.fill(buffer, ' ');
    }


    /**
     * Reads and returns some sensitive piece of information (e.g. a password)
     * from the console (i.e. System.in and System.out) in a secure manner.
     * <p/>
     * For top security, all console input is masked out while the user types in the password.
     * Once the user presses enter, the password is read via a call to {@link #readLineSecure readLineSecure(pr)},
     * using a PushbackReader that wraps System.in.
     * <p/>
     * This method never returns null.
     * <p/>
     *
     * @throws IOException          if an I/O problem occurs
     * @throws InterruptedException if the calling thread is interrupted while it is waiting at some point
     * @see <a href="http://java.sun.com/features/2002/09/pword_mask.html">Password masking in console</a>
     */
    public static char[] readConsoleSecure(String prompt) throws IOException, InterruptedException {
        // start a separate thread which will mask out all chars typed on System.in by overwriting them using System.out:
        StreamMasker masker = new StreamMasker(System.out, prompt);
        Thread threadMasking = new Thread(masker);
        threadMasking.start();

        // Goal: block this current thread (allowing masker to mask all user input)
        // while the user is in the middle of typing the password.
        // This may be achieved by trying to read just the first byte from System.in,
        // since reading from System.in blocks until it detects that an enter has been pressed.
        // Wrap System.in with a PushbackReader because this byte will be unread below.
        PushbackReader pr = new PushbackReader(new InputStreamReader(System.in));
        int c = pr.read();

        // When current thread gets here, the block on reading System.in is over (e.g. the user pressed enter, or some error occurred?)

        // signal threadMasking to stop and wait till it is dead:
        masker.stop();
        threadMasking.join();

        // check for stream errors:
        if (c == -1) throw new IOException("end-of-file was detected in System.in without any data being read");
        if (System.out.checkError()) throw new IOException("an I/O problem was detected in System.out");

        // pushback the first byte and supply the now unaltered stream to readLineSecure which will return the complete password:
        pr.unread(c);
        return readLineSecure(pr);
    }

    /**
     * Masks an InputStream by overwriting blank chars to the PrintStream corresponding to its output.
     * A typical application is for password input masking.
     * <p/>
     *
     * @see <a href="http://java.sun.com/features/2002/09/pword_mask.html">Password masking in console</a>
     */
    public static class StreamMasker implements Runnable {
        private static final String TEN_BLANKS = repeatChars(' ', 10);
        private final PrintStream out;
        private final String promptOverwrite;
        private volatile boolean doMasking;    // MUST be volatile to ensure update by one thread is instantly visible to other threads

        /**
         * Constructor.
         * <p/>
         *
         * @throws IllegalArgumentException if out == null; prompt == null; prompt contains the char '\r' or '\n'
         */
        public StreamMasker(PrintStream out, String prompt) throws IllegalArgumentException {
            if (out == null) throw new IllegalArgumentException("out == null");
            if (prompt == null) throw new IllegalArgumentException("prompt == null");
            if (prompt.indexOf('\r') != -1) throw new IllegalArgumentException("prompt contains the char '\\r'");
            if (prompt.indexOf('\n') != -1) throw new IllegalArgumentException("prompt contains the char '\\n'");

            this.out = out;
            String setCursorToStart = repeatChars('\b', prompt.length() + TEN_BLANKS.length());
            this.promptOverwrite =
                    setCursorToStart +    // sets cursor back to beginning of line:
                            prompt +              // writes prompt (critical: this reduces visual flicker in the prompt text that otherwise occurs if simply write blanks here)
                            TEN_BLANKS +          // writes 10 blanks beyond the prompt to mask out any input; go 10, not 1, spaces beyond end of prompt to handle the (hopefully rare) case that input occurred at a rapid rate
                            setCursorToStart +    // sets cursor back to beginning of line:
                            prompt;               // writes prompt again; the cursor will now be positioned immediately after prompt (critical: overwriting only works if all input text starts here)
        }

        /**
         * Returns a String of the specified length which consists of entirely of the char c.
         * <p/>
         *
         * @throws IllegalArgumentException if length < 0
         */
        private static String repeatChars(char c, int length) throws IllegalArgumentException {
            if (length < 0) throw new IllegalArgumentException("length = " + length + " is < 0");

            StringBuffer sb = new StringBuffer(length);
            for (int i = 0; i < length; i++) {
                sb.append(c);
            }
            return sb.toString();
        }

        /**
         * Repeatedly overwrites the current line of out with prompt followed by blanks.
         * This effectively masks any chars coming on out, as long as the masking occurs fast enough.
         * <p/>
         * To help ensure that masking occurs when system is in heavy use, the calling thread will have its priority
         * boosted to the max for the duration of the call (with its original priority restored upon return).
         * Interrupting the calling thread will eventually result in an exit from this method,
         * and the interrupted status of the calling thread will be set to true.
         * <p/>
         *
         * @throws RuntimeException if an error in the masking process is detected
         */
        public void run() throws RuntimeException {
            int priorityOriginal = Thread.currentThread().getPriority();
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                doMasking = true;    // do this assignment here and NOT at variable declaration line to allow this instance to be restarted if desired
                while (doMasking) {
                    out.print(promptOverwrite);
                    // call checkError, which first flushes out, and then lets us confirm that everything was written correctly:
                    if (out.checkError())
                        throw new RuntimeException("an I/O problem was detected in out");    // should be an IOException, but that would break method contract

                    // limit the masking rate to fairly share the cpu; interruption breaks the loop
                    try {
                        Thread.sleep(1);    // have experimentally found that sometimes see chars for a brief bit unless set this to its min value
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();    // resets the interrupted status, which is typically lost when an InterruptedException is thrown, as per our method contract; see Lea, "Concurrent Programming in Java Second Edition", p. 171
                        return;    // return, NOT break, since now want to skip the lines below where write bunch of blanks since typically the user will not have pressed enter yet
                    }
                }
                // erase any prompt that may have been spuriously written on the NEXT line after the user pressed enter
                out.print('\r');
                for (int i = 0; i < promptOverwrite.length(); i++) out.print(' ');
                out.print('\r');
            } finally {
                Thread.currentThread().setPriority(priorityOriginal);
            }
        }

        /**
         * Signals any thread executing run to stop masking and exit run.
         */
        public void stop() {
            doMasking = false;
        }
    }
}
