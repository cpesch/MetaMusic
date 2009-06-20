/*
  You may freely copy, distribute, modify and use this class as long
  as the original author attribution remains intact.  See message
  below.

  Copyright (C) 2007 Christian Pesch. All Rights Reserved.
 */

package slash.metamusic.util;

import junit.framework.TestCase;

public class DiscIndexHelperTest extends TestCase {
    public void testParseDiscIndex() {
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (disc 2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text [disc 2]"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (disc2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (Disc 2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (dISc 2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (cd 2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (Cd2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (CD2)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (cD 2)"));
        assertEquals(12345, DiscIndexHelper.parseDiscIndex("text (Cd12345)"));
        assertEquals(-1, DiscIndexHelper.parseDiscIndex("text (CD)"));
        assertEquals(-1, DiscIndexHelper.parseDiscIndex("text (CE5)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (disc 2)(disc 3)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (CD 2)(disc 3)"));
        assertEquals(2, DiscIndexHelper.parseDiscIndex("text (CD2)(disc3)"));
    }

    public void testRemoveDiscIndex() {
        assertEquals("Text", DiscIndexHelper.removeDiscIndexPostfix("Text (disc 2)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text [Disc 3]"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (disc2)"));
        assertEquals("teSt", DiscIndexHelper.removeDiscIndexPostfix("teSt (Disc 2)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (cd 2)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (CD2)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (Cd12345)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (disc 2) (disc 3)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (CD 2) (disc 3)"));
        assertEquals("text", DiscIndexHelper.removeDiscIndexPostfix("text (CD2) (disc3)"));
    }
}