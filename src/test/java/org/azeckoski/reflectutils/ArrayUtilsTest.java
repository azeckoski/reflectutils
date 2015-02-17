/**
 * $Id: ArrayUtilsTest.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/ArrayUtilsTest.java $
 * ArrayUtilsTest.java - genericdao - May 18, 2008 10:07:01 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests the various methods used to analyze a class and cache the information
 * about fields and annotations
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class ArrayUtilsTest extends TestCase {

    /**
     * Test method for {@link org.azeckoski.reflectutils.ArrayUtils#resize(T[], int)}.
     */
    public void testResize() {
        String[] array = new String[] {"A","B","C","D"};

        String[] s = array;
        assertEquals(4, s.length);
        assertEquals("A", s[0]);
        assertEquals("B", s[1]);
        assertEquals("C", s[2]);
        assertEquals("D", s[3]);

        s = ArrayUtils.resize(array, 10);
        assertNotNull(s);
        assertEquals(10, s.length);
        assertEquals("A", s[0]);
        assertEquals("B", s[1]);
        assertEquals("C", s[2]);
        assertEquals("D", s[3]);
        assertEquals(null, s[4]);
        assertEquals(null, s[5]);

        s = ArrayUtils.resize(array, 4);
        assertNotNull(s);
        assertEquals(4, s.length);
        assertEquals("A", s[0]);
        assertEquals("B", s[1]);
        assertEquals("C", s[2]);
        assertEquals("D", s[3]);
        
        s = ArrayUtils.resize(array, 2);
        assertNotNull(s);
        assertEquals(2, s.length);
        assertEquals("A", s[0]);
        assertEquals("B", s[1]);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ArrayUtils#removeDuplicates(java.util.List)}.
     */
    public void testRemoveDuplicates() {
        List<String> list = new ArrayList<String>();
        list.add("A");
        list.add("B");
        list.add("B");
        list.add("C");
        list.add("A");
        assertEquals(5, list.size());

        ArrayUtils.removeDuplicates(list);
        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

}
