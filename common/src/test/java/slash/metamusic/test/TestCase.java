/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2003 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.test;

import slash.metamusic.util.ArrayHelper;

import java.util.logging.Logger;

/**
 * Base class for tests
 */

public abstract class TestCase extends junit.framework.TestCase {
    protected static Logger log = Logger.getLogger(TestCase.class.getName());

    public TestCase(String name) {
        super(name);
        log = Logger.getLogger(getClass().getName());
    }

    public static void assertNotEquals(int expected, int was) {
        assertTrue("expected:<" + expected + "> but was:<" + was + ">", expected != was);
    }

    public static void assertNotEquals(Object expected, Object was) {
        assertTrue("expected:<" + expected + "> but was:<" + was + ">", !expected.equals(was));
    }

    public static void assertGreaterThan(int expected, int was) {
        assertTrue("expected greater than:<" + expected + "> but was:<" + was + ">", was > expected);
    }

    public void assertContains(String[] strings, String expected) {
        for (String string : strings)
            if (string.equals(expected))
                return;
        assertTrue(ArrayHelper.printArrayToString(strings) + " does not contain " + expected, false);
    }

    protected String createString(int length) {
        StringBuffer buffer = new StringBuffer(length > 0 ? Integer.toString(length) : "");
        while (buffer.length() < length)
            buffer.append('a');
        return buffer.toString();
    }

    protected byte[] createBytes(int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = 'a';
        }
        return result;
    }
}
