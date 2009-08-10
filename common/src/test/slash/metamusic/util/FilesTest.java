/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import junit.framework.TestCase;

public class FilesTest extends TestCase {
    public void testRelativize() {
        assertEquals("My Music\\my.mp3",
                Files.relativize("c:\\Documents\\User\\My Files", "c:\\Documents\\User\\My Files\\My Music\\my.mp3"));
        assertEquals("User\\My Files\\My Music\\my.mp3",
                Files.relativize("c:\\Documents", "c:\\Documents\\User\\My Files\\My Music\\my.mp3"));
        assertEquals("c:\\Documents\\my.mp3",
                Files.relativize("c:\\Documents\\User\\My Files", "c:\\Documents\\my.mp3"));
        assertEquals("c:\\my.mp3",
                Files.relativize("c:\\Documents\\User\\My Files", "c:\\my.mp3"));
        assertEquals("c:\\my.mp3",
                Files.relativize("c:\\Documents\\User\\My Files", "c:\\my.mp3"));
    }
}
