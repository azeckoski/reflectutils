/**
 * $Id: ConversionUtilsTest.java 65 2010-04-05 14:53:55Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/ConversionUtilsTest.java $
 * ConversionUtilsTest.java - genericdao - Sep 8, 2008 6:24:24 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.azeckoski.reflectutils.classes.TestBean;
import org.azeckoski.reflectutils.classes.TestPea;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;

import junit.framework.TestCase;


/**
 * Testing the conversion utilities
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ConversionUtilsTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testConvertObject() {
        ConversionUtils convertUtils = new ConversionUtils();

        String[] abc = new String[] {"A","B","C"};

        // equivalent conversion also should be instantaneous since no conversion happens
        assertEquals(123, (int) convertUtils.convert(123, int.class) );
        assertEquals(123, (int) convertUtils.convert(new Integer(123), int.class) );
        assertEquals("123", convertUtils.convert("123", String.class) );
        assertEquals(abc, convertUtils.convert(abc, String[].class) );

        // object conversion should be instant
        assertEquals(new Integer(123), (Integer) convertUtils.convert(123, Object.class) );
        assertEquals("123", convertUtils.convert("123", Object.class) );

        // now do actual conversions
        assertEquals(123, (int) convertUtils.convert("123", int.class) );
        assertEquals("123", convertUtils.convert("123", String.class) );
        assertEquals(new Integer(123), convertUtils.convert("123", Integer.class) );

        // array to scalar
        assertEquals(123, (int) convertUtils.convert(new int[] {123,456}, int.class) );
        assertEquals(123, (int) convertUtils.convert(new String[] {"123","456"}, int.class) );

        // scalar to array
        int[] nums = new int[] {123};
        nums = convertUtils.convert(123, int[].class);
        assertEquals(123, nums[0]);
        nums = convertUtils.convert("123", int[].class);
        assertEquals(123, nums[0]);

        // array to string
        assertEquals("A,B,C", convertUtils.convert(abc, String.class) );

        // array to list and vice versa
        List<String> l = convertUtils.convert(new String[] {"A","B","C"}, List.class);
        assertEquals(l.get(0), abc[0]);
        assertEquals(l.get(1), abc[1]);
        assertEquals(l.get(2), abc[2]);

        LinkedList<String> ll = convertUtils.convert(new String[] {"A","B","C"}, LinkedList.class);
        assertEquals(ll.get(0), abc[0]);
        assertEquals(ll.get(1), abc[1]);
        assertEquals(ll.get(2), abc[2]);

        String[] sarray = convertUtils.convert(l, String[].class);
        assertEquals(sarray[0], l.get(0));
        assertEquals(sarray[1], l.get(1));
        assertEquals(sarray[2], l.get(2));

        // array to collection and map
        Set<String> s = convertUtils.convert(new String[] {"A","B","C"}, Set.class);
        assertNotNull(s);
        assertEquals(3, s.size());
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("C"));

        Map<String, Object> map = convertUtils.convert(new String[] {"A","B","C"}, Map.class);
        assertNotNull(map);
        assertEquals(3, map.size());
    }

    public void testStringToArray() {
        ConversionUtils convertUtils = new ConversionUtils();

        String[] sa = convertUtils.convert("A, B, C", String[].class);
        assertNotNull(sa);
        assertEquals(3, sa.length);
        assertEquals("A", sa[0]);
        assertEquals("B", sa[1]);
        assertEquals("C", sa[2]);

        int[] ia = convertUtils.convert("1,2,3", int[].class);
        assertNotNull(ia);
        assertEquals(3, ia.length);
        assertEquals(1, ia[0]);
        assertEquals(2, ia[1]);
        assertEquals(3, ia[2]);

        int[] empty = convertUtils.convert("", int[].class);
        assertNotNull(empty);
        assertEquals(0, empty.length);

    }
    
    public void testConvertDates() {
        ConversionUtils convertUtils = new ConversionUtils();

        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1975, 9, 29);
        Date d = calendar.getTime();
        long l = d.getTime();
        Timestamp t = new Timestamp(l);

        assertEquals(d, convertUtils.convert(l, Date.class));
        assertEquals(d, convertUtils.convert("19751029", Date.class));
        assertEquals(d, convertUtils.convert(t, Date.class));

        assertEquals(calendar, convertUtils.convert(l, Calendar.class));
        assertEquals(calendar, convertUtils.convert("19751029", Calendar.class));
    }

    public void testConvertToString() {
        ConversionUtils convertUtils = new ConversionUtils();

        assertEquals("3", convertUtils.convertToString("3"));
        assertEquals("3", convertUtils.convertToString(3));
        assertEquals("3", convertUtils.convertToString(new int[] {3,2,1}));
        
    }

    public void testConvertString() {
        ConversionUtils convertUtils = new ConversionUtils();

        assertEquals(3, convertUtils.convertString("3", int.class));
        assertEquals(new Integer(3), convertUtils.convertString("3", Integer.class));
        int[] intArray = (int[]) convertUtils.convertString("3", int[].class);
        assertEquals(3, intArray[0]);
        assertEquals("XXX", convertUtils.convertString("XXX", Integer.class));
    }

    private enum TestEnum {ONE, TWO, THREE};
    public void testConvertEnums() {
        ConversionUtils convertUtils = new ConversionUtils();

        assertEquals("ONE", convertUtils.convert(TestEnum.ONE, String.class));
        assertEquals("TWO", convertUtils.convert(TestEnum.TWO, String.class));

        String[] sa = convertUtils.convert(TestEnum.values(), String[].class);
        assertEquals(3, sa.length);
        assertEquals("ONE", sa[0]);
        assertEquals("TWO", sa[1]);
        assertEquals("THREE", sa[2]);

        assertEquals(TestEnum.ONE, convertUtils.convert("ONE", TestEnum.class));
        assertEquals(TestEnum.TWO, convertUtils.convert("TWO", TestEnum.class));
    }

    @SuppressWarnings("unchecked")
    public void testObjectToMap() { // simple conversion
        ConversionUtils convertUtils = new ConversionUtils();

        TestPea tp = new TestPea();
        Map<String,Object> m = convertUtils.convert(tp, Map.class);
        assertNotNull(m);
        assertEquals(2, m.size());
        assertEquals("id", m.get("id"));
        assertEquals("EID", m.get("entityId"));

        TestBean tb = new TestBean();
        Map<String,Object> m1 = convertUtils.convert(tb, Map.class);
        assertNotNull(m1);
        assertEquals(2, m1.size());
        assertEquals(0, m1.get("myInt"));
        assertEquals("woot", m1.get("myString"));
    }

    public void testMapToObject() { // simple conversion
        ConversionUtils convertUtils = new ConversionUtils();

        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", "AZ");
        m.put("entityId", "aaronz");
        m.put("stuff", "blah");
        TestPea tp = convertUtils.convert(m, TestPea.class);
        assertNotNull(tp);
        assertEquals("AZ", tp.id);
        assertEquals("aaronz", tp.entityId);

        m.clear();
        m.put("myInt", "123");
        TestBean tb = convertUtils.convert(m, TestBean.class);
        assertNotNull(tb);
        assertEquals(123, tb.getMyInt());
        assertEquals("woot", tb.getMyString());

        String s = convertUtils.convert(m, String.class);
        assertNotNull(s);
        assertEquals("123", s);
    }

    @SuppressWarnings("unchecked")
    public void testConvertMaps() {
        ConversionUtils convertUtils = new ConversionUtils();

        Map<String, String> sm = new HashMap<String, String>();
        sm.put("A", "AZ");
        sm.put("B", "BZ");
        sm.put("C", "CZ");

        Map<String, String> aom = convertUtils.convert(sm, ArrayOrderedMap.class);
        assertNotNull(aom);
        
    }

}
