/**
 * $Id: FieldUtilsTest.java 129 2014-03-18 23:25:36Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/FieldUtilsTest.java $
 * FieldUtilsTest.java - genericdao - May 21, 2008 5:14:52 PM - azeckoski
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

import junit.framework.TestCase;
import org.azeckoski.reflectutils.classes.*;
import org.azeckoski.reflectutils.exceptions.FieldSetValueException;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Testing the FieldUtils
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class FieldUtilsTest extends TestCase {

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#analyzeClass(java.lang.Class)}.
     */
    public void testAnalyzeClass() {
        FieldUtils fu = new FieldUtils();
        ClassFields<?> cf;

        cf = fu.analyzeClass(TestBean.class);
        assertNotNull(cf);
        assertEquals(TestBean.class, cf.getFieldClass());

        cf = fu.analyzeClass(TestExtendBean.class);
        assertNotNull(cf);
        assertEquals(TestExtendBean.class, cf.getFieldClass());

        cf = fu.analyzeClass(TestNesting.class);
        assertNotNull(cf);
        assertEquals(TestNesting.class, cf.getFieldClass());
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#analyzeObject(java.lang.Object)}.
     */
    public void testAnalyzeObject() {
        FieldUtils fu = new FieldUtils();
        ClassFields<?> cf;

        cf = fu.analyzeObject(new TestBean());
        assertNotNull(cf);
        assertEquals(TestBean.class, cf.getFieldClass());

        cf = fu.analyzeObject(new TestExtendBean());
        assertNotNull(cf);
        assertEquals(TestExtendBean.class, cf.getFieldClass());

        cf = fu.analyzeObject(new TestNesting());
        assertNotNull(cf);
        assertEquals(TestNesting.class, cf.getFieldClass());
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#getFieldNames(java.lang.Class)}.
     */
    public void testGetFieldNames() {
        FieldUtils fu = new FieldUtils();
        List<String> names;

        names = fu.getFieldNames(TestBean.class);
        assertEquals(2, names.size());
        assertTrue( names.contains("myInt") );
        assertTrue( names.contains("myString") );

        names = fu.getFieldNames(TestPea.class);
        assertEquals(2, names.size());
        assertTrue( names.contains("id") );
        assertTrue( names.contains("entityId") );
    }

    /**
     * Test method for findFieldValue
     */
    @SuppressWarnings({"unchecked", "UnusedAssignment"})
    public void testFindFieldValue() {
        FieldUtils fu = new FieldUtils();
        ClassFields<?> cf;
        ClassProperty cp;
        Object value;

        TestBean tb = new TestBean();
        cf = new ClassFields(tb.getClass());
        cp = cf.getClassProperty("myInt");
        value = fu.findFieldValue(tb, cp);
        assertNotNull(value);
        assertEquals(0, value);

        cp = cf.getClassProperty("myString");
        value = fu.findFieldValue(tb, cp);
        assertNotNull(value);
        assertEquals("woot", value);

        tb.setMyString(null);
        cp = cf.getClassProperty("myString");
        value = fu.findFieldValue(tb, cp);
        assertEquals(null, value);

        try {
            value = fu.findFieldValue(tb, null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.findFieldValue(null, cp);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        cf = new ClassFields(tn.getClass());
        cp = cf.getClassProperty("id");
        value = fu.findFieldValue(tn, cp);
        assertNotNull(value);
        assertEquals(8, value);

        cp = cf.getClassProperty("title");
        value = fu.findFieldValue(tn, cp);
        assertNotNull(value);
        assertEquals("AZ", value);

        cp = cf.getClassProperty("extra");
        value = fu.findFieldValue(tn, cp);
        assertEquals(null, value);

        cp = cf.getClassProperty("myArray");
        value = fu.findFieldValue(tn, cp);
        assertNotNull(value);
        assertTrue(value.getClass().isArray());
        assertEquals(2, ((String[])value).length );

        cp = cf.getClassProperty("sList");
        value = fu.findFieldValue(tn, cp);
        assertNotNull(value);
        assertTrue(List.class.isAssignableFrom(value.getClass()));
        assertEquals(3, ((List)value).size() );

        cp = cf.getClassProperty("sMap");
        value = fu.findFieldValue(tn, cp);
        assertNotNull(value);
        assertTrue(Map.class.isAssignableFrom(value.getClass()));
        assertEquals(2, ((Map)value).size() );
    }

    /**
     * Test method for assignFieldValue
     */
    @SuppressWarnings("unchecked")
    public void testAssignFieldValue() {
        FieldUtils fu = new FieldUtils();
        ClassFields<?> cf;
        ClassProperty cp;

        TestBean tb = new TestBean();
        cf = new ClassFields(tb.getClass());
        cp = cf.getClassProperty("myInt");
        fu.assignFieldValue(tb, cp, 100);
        assertEquals(100, tb.getMyInt());

        cp = cf.getClassProperty("myInt");
        fu.assignFieldValue(tb, cp, 50);
        assertEquals(50, tb.getMyInt());

        cp = cf.getClassProperty("myString");
        fu.assignFieldValue(tb, cp, "apple");
        assertEquals("apple", tb.getMyString());

        cp = cf.getClassProperty("myString");
        fu.assignFieldValue(tb, cp, null);
        assertEquals(null, tb.getMyString());

        try {
            fu.assignFieldValue(null, cp, "apple");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.assignFieldValue(tb, null, "apple");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        cf = new ClassFields(tn.getClass());
        cp = cf.getClassProperty("id");
        fu.assignFieldValue(tn, cp, 88);
        assertEquals(88, tn.getId());

        cp = cf.getClassProperty("title");
        fu.assignFieldValue(tn, cp, "stuff");
        assertEquals("stuff", tn.getTitle());

        cp = cf.getClassProperty("extra");
        fu.assignFieldValue(tn, cp, "Xtra");
        assertEquals("Xtra", tn.getExtra());

        cp = cf.getClassProperty("myArray");
        fu.assignFieldValue(tn, cp, new String[] {"A", "C", "T", "G"});
        assertEquals(4, tn.getMyArray().length);
        assertEquals("A", tn.getMyArray()[0]);

        cp = cf.getClassProperty("sList");
        fu.assignFieldValue(tn, cp, new ArrayList<String>());
        assertEquals(0, tn.getSList().size());

        cp = cf.getClassProperty("sMap");
        fu.assignFieldValue(tn, cp, new HashMap<String, String>());
        assertEquals(0, tn.getSMap().size());
    }

    /**
     * Test method for getSimpleValue
     */
    public void testGetSimpleValue() {
        FieldUtils fu = new FieldUtils();
        Object value;

        TestBean tb = new TestBean();
        value = fu.getSimpleValue(tb, "myInt");
        assertNotNull(value);
        assertEquals(0, value);

        value = fu.getSimpleValue(tb, "myString");
        assertNotNull(value);
        assertEquals("woot", value);

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        value = fu.getSimpleValue(tn, "id");
        assertNotNull(value);
        assertEquals(8, value);

        value = fu.getSimpleValue(tn, "title");
        assertNotNull(value);
        assertEquals("AZ", value);

        value = fu.getSimpleValue(tn, "extra");
        assertEquals(null, value);

        value = fu.getSimpleValue(tn, "myArray");
        assertNotNull(value);
        assertTrue(value.getClass().isArray());
        assertEquals(2, ((String[])value).length );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#getIndexedValue(java.lang.Object, java.lang.String)}.
     */
    @SuppressWarnings("UnusedAssignment")
    public void testGetIndexedValue() {
        FieldUtils fu = new FieldUtils();
        Object value;

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "two", "three"});
        value = fu.getIndexedValue(tn, "myArray[0]");
        assertNotNull(value);
        assertEquals("A", value);

        value = fu.getIndexedValue(tn, "myArray[1]");
        assertNotNull(value);
        assertEquals("B", value);

        value = fu.getIndexedValue(tn, "sList[0]");
        assertNotNull(value);
        assertEquals("1", value);

        value = fu.getIndexedValue(tn, "sList[1]");
        assertNotNull(value);
        assertEquals("two", value);

        try {
            value = fu.getIndexedValue(tn, "myArray[5]");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getIndexedValue(tn, "sList[99]");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getIndexedValue(tn, "sMap[1]");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // ensure we can get the indexed value out of an array or list
        String[] sarray = new String[] {"10", "20", "30"};
        value = fu.getIndexedValue(sarray, "[0]");
        assertNotNull(value);
        assertEquals("10", value);

        value = fu.getIndexedValue(sarray, "[2]");
        assertNotNull(value);
        assertEquals("30", value);

        List<Integer> myList = new ArrayList<Integer>();
        myList.add(5);
        myList.add(10);
        myList.add(20);
        value = fu.getIndexedValue(myList, "[0]");
        assertNotNull(value);
        assertEquals(5, value);

        value = fu.getIndexedValue(myList, "[1]");
        assertNotNull(value);
        assertEquals(10, value);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#getMappedValue(java.lang.Object, java.lang.String)}.
     */
    @SuppressWarnings("UnusedAssignment")
    public void testGetMappedValue() {
        FieldUtils fu = new FieldUtils();
        Object value;

        TestNesting tn = new TestNesting();
        value = fu.getMappedValue(tn, "sMap(A1)");
        assertNotNull(value);
        assertEquals("ONE", value);

        value = fu.getMappedValue(tn, "sMap(B2)");
        assertNotNull(value);
        assertEquals("TWO", value);

        value = fu.getMappedValue(tn, "sMap(XXX)");
        assertEquals(null, value);

        try {
            value = fu.getMappedValue(null, "sMap(B2)");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getMappedValue(tn, null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getMappedValue(tn, "sList(1)");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test that we can get the map only
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        value = fu.getMappedValue(myMap, "(A)");
        assertNotNull(value);
        assertEquals(10, value);

        value = fu.getMappedValue(myMap, "(C)");
        assertNotNull(value);
        assertEquals(24, value);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#getValueOfMap(java.util.Map, java.lang.String)}.
     */
    public void testGetValueOfMap() {
        FieldUtils fu = new FieldUtils();
        Object value;

        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        value = fu.getValueOfMap(myMap, "A");
        assertNotNull(value);
        assertEquals(10, value);

        value = fu.getValueOfMap(myMap, "(B)");
        assertNotNull(value);
        assertEquals(2, value);

        value = fu.getValueOfMap(myMap, "C");
        assertNotNull(value);
        assertEquals(24, value);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#setSimpleValue(java.lang.Object, java.lang.String, java.lang.Object)}.
     */
    public void testSetSimpleValue() {
        FieldUtils fu = new FieldUtils();

        TestBean tb = new TestBean();
        fu.setSimpleValue(tb, "myInt", 11);
        assertEquals(11, tb.getMyInt());

        fu.setSimpleValue(tb, "myString", "XXXXX");
        assertEquals("XXXXX", tb.getMyString());

        TestNesting tn = new TestNesting();
        fu.setSimpleValue(tn, "extra", "XTRA");
        assertEquals("XTRA", tn.getExtra());

        fu.setSimpleValue(tn, "extra", null);
        assertEquals(null, tn.getExtra());

        fu.setSimpleValue(tn, "myArray", new String[] {"A1","B2","C3"});
        assertEquals(3, tn.getMyArray().length);
        assertEquals("B2", tn.getMyArray()[1]);

        try {
            fu.setSimpleValue(null, "extra", "XTRA");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setSimpleValue(tn, null, "XTRA");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setSimpleValue(tn, "XXXX", null);
            fail("should have thrown exception");
        } catch (FieldnameNotFoundException e) {
            assertNotNull(e.getMessage());
        }      
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#setIndexedValue(java.lang.Object, java.lang.String, java.lang.Object)}.
     */
    public void testSetIndexedValue() {
        FieldUtils fu = new FieldUtils();

        TestNesting tn = new TestNesting();
        fu.setIndexedValue(tn, "myArray[0]", "AAA");
        assertEquals("AAA", tn.getMyArray()[0]);

        fu.setIndexedValue(tn, "myArray[1]", "BBBB");
        assertEquals("BBBB", tn.getMyArray()[1]);

        fu.setIndexedValue(tn, "sList[0]", "alpha");
        assertEquals("alpha", tn.getSList().get(0));

        fu.setIndexedValue(tn, "sList[1]", "beta");
        assertEquals("beta", tn.getSList().get(1));

        fu.setIndexedValue(tn, "myArray[99]", "zeta");
        assertEquals("zeta", tn.getMyArray()[99]);

        fu.setIndexedValue(tn, "sList[99]", "zeta");
        assertEquals("zeta", tn.getSList().get(99));

        try {
            fu.setIndexedValue(null, "myArray[1]", "BBBB");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }      

        try {
            fu.setIndexedValue(tn, null, "BBBB");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // set on array and list directly
        String[] sarray = new String[] {"10", "20", "30"};
        fu.setIndexedValue(sarray, "[0]", "AAA");
        assertEquals("AAA", sarray[0]);

        List<Integer> myList = new ArrayList<Integer>();
        myList.add(5);
        myList.add(10);
        myList.add(20);
        fu.setIndexedValue(myList, "[1]", "BBB");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("BBB", myList.get(1));
    }

    public void testCollections() {
        FieldUtils fu = new FieldUtils();

        // testing collections
        TestCollections tc = new TestCollections(true);
        fu.setFieldValue(tc, "map(name)", "AZ");
        assertEquals(1, tc.map.size());
        assertEquals("AZ", tc.map.get("name"));

        fu.setFieldValue(tc, "list[3]", "AZ");
        assertEquals(4, tc.list.size());
        assertEquals("AZ", tc.list.get(3));

        fu.setFieldValue(tc, "list[4]", "BZ");
        assertEquals(5, tc.list.size());
        assertEquals("BZ", tc.list.get(4));

        assertEquals(10, tc.array.length);
        fu.setFieldValue(tc, "array[2]", "AZ");
        assertEquals(10, tc.array.length);
        assertEquals("AZ", tc.array[2]);

        fu.setFieldValue(tc, "array[3]", "BZ");
        assertEquals(10, tc.array.length);
        assertEquals("BZ", tc.array[3]);

        // testing null collections
        TestCollections tnc = new TestCollections(false);
        fu.setFieldValue(tnc, "map(name)", "AZ");
        assertEquals(1, tnc.map.size());
        assertEquals("AZ", tnc.map.get("name"));

        fu.setFieldValue(tnc, "list[3]", "AZ");
        assertEquals(4, tnc.list.size());
        assertEquals("AZ", tnc.list.get(3));

        fu.setFieldValue(tnc, "list[4]", "BZ");
        assertEquals(5, tnc.list.size());
        assertEquals("BZ", tnc.list.get(4));

        fu.setFieldValue(tnc, "array[2]", "AZ");
        assertEquals(3, tnc.array.length);
        assertEquals("AZ", tnc.array[2]);

        fu.setFieldValue(tnc, "array[3]", "BZ");
        assertEquals(4, tnc.array.length);
        assertEquals("BZ", tnc.array[3]);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#setMappedValue(java.lang.Object, java.lang.String, java.lang.Object)}.
     */
    public void testSetMappedValue() {
        FieldUtils fu = new FieldUtils();

        TestNesting tn = new TestNesting();
        fu.setMappedValue(tn, "sMap(A1)", "UNO");
        assertEquals("UNO", tn.getSMap().get("A1"));

        fu.setMappedValue(tn, "sMap(XXX)", "xray");
        assertEquals("xray", tn.getSMap().get("XXX"));

        try {
            fu.setMappedValue(null, "sMap(A1)", "UNO");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setMappedValue(tn, null, "UNO");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setMappedValue(null, "sMap(A1)", 120);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setMappedValue(tn, "sList(A1)", "UNO");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test that we can set the map only
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        fu.setMappedValue(myMap, "(A)", "alpha");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("alpha", myMap.get("A"));

        fu.setMappedValue(myMap, "(D)", "delta");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("delta", myMap.get("D"));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#setValueOfMap(java.util.Map, java.lang.String, java.lang.Object)}.
     */
    public void testSetValueOfMap() {
        FieldUtils fu = new FieldUtils();

        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        fu.setValueOfMap(myMap, "A", "alpha");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("alpha", myMap.get("A"));

        fu.setValueOfMap(myMap, "(B)", "beta");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("beta", myMap.get("B"));

        fu.setValueOfMap(myMap, "D", "delta");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("delta", myMap.get("D"));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#getFieldValue(java.lang.Object, java.lang.String)}.
     */
    @SuppressWarnings({"unchecked", "UnusedAssignment"})
    public void testGetFieldValue() {
        FieldUtils fu = new FieldUtils();
        Object value;

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        value = fu.getFieldValue(tn, "id");
        assertNotNull(value);
        assertEquals(8, value);

        value = fu.getFieldValue(tn, "title");
        assertNotNull(value);
        assertEquals("AZ", value);

        value = fu.getFieldValue(tn, "extra");
        assertEquals(null, value);

        value = fu.getFieldValue(tn, "testEntity.id");
        assertNotNull(value);
        assertEquals((long) 3, value);

        value = fu.getFieldValue(tn, "myArray");
        assertNotNull(value);
        assertTrue(value.getClass().isArray());
        assertEquals(2, ((String[])value).length );

        value = fu.getFieldValue(tn, "sList");
        assertNotNull(value);
        assertTrue(List.class.isAssignableFrom(value.getClass()));
        assertEquals(3, ((List)value).size() );

        value = fu.getFieldValue(tn, "sMap");
        assertNotNull(value);
        assertTrue(Map.class.isAssignableFrom(value.getClass()));
        assertEquals(2, ((Map)value).size() );

        try {
            value = fu.getFieldValue(null, "extra");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getFieldValue(tn, null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test getting indexed values
        value = fu.getFieldValue(tn, "myArray[0]");
        assertNotNull(value);
        assertEquals("A", value);

        value = fu.getFieldValue(tn, "myArray[1]");
        assertNotNull(value);
        assertEquals("B", value);

        value = fu.getFieldValue(tn, "sList[0]");
        assertNotNull(value);
        assertEquals("1", value);

        value = fu.getFieldValue(tn, "sList[1]");
        assertNotNull(value);
        assertEquals("B", value);

        try {
            value = fu.getFieldValue(tn, "myArray[5]");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getFieldValue(tn, "sList[99]");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            value = fu.getFieldValue(tn, "sMap[1]");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test get mapped value
        value = fu.getFieldValue(tn, "sMap(A1)");
        assertNotNull(value);
        assertEquals("ONE", value);

        value = fu.getFieldValue(tn, "sMap(B2)");
        assertNotNull(value);
        assertEquals("TWO", value);

        value = fu.getFieldValue(tn, "sMap(XXX)");
        assertEquals(null, value);

        try {
            value = fu.getFieldValue(tn, "sList(1)");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test getting map values
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        value = fu.getFieldValue(myMap, "A");
        assertNotNull(value);
        assertEquals(10, value);

        value = fu.getFieldValue(myMap, "(B)");
        assertNotNull(value);
        assertEquals(2, value);

        value = fu.getFieldValue(myMap, "C");
        assertNotNull(value);
        assertEquals(24, value);
    }

    public void testGetFieldValueAsType() {
        FieldUtils fu = FieldUtils.getInstance();
        Object value;
        String sval;

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        value = fu.getFieldValue(tn, "id");
        assertNotNull(value);
        assertEquals(8, value);

        sval = fu.getFieldValue(tn, "id", String.class);
        assertNotNull(sval);
        assertEquals("8", sval);

        value = fu.getFieldValue(tn, "testEntity.id");
        assertNotNull(value);
        assertEquals((long) 3, value);

        sval = fu.getFieldValue(tn, "testEntity.id", String.class);
        assertNotNull(sval);
        assertEquals("3", sval);

        value = fu.getFieldValue(tn, "myArray");
        assertNotNull(value);
        assertTrue(value.getClass().isArray());
        assertEquals(2, ((String[])value).length );

        sval = fu.getFieldValue(tn, "myArray", String.class);
        assertNotNull(sval);
        assertEquals("A,B", sval);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#setFieldValue(java.lang.Object, java.lang.String, java.lang.Object)}.
     */
    public void testSetFieldValue() {
        FieldUtils fu = new FieldUtils();

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        fu.setFieldValue(tn, "id", 88);
        assertEquals(88, tn.getId());

        fu.setFieldValue(tn, "title", "stuff");
        assertEquals("stuff", tn.getTitle());

        fu.setFieldValue(tn, "extra", "Xtra");
        assertEquals("Xtra", tn.getExtra());

        fu.setFieldValue(tn, "myArray", new String[] {"A", "C", "T", "G"});
        assertEquals(4, tn.getMyArray().length);
        assertEquals("A", tn.getMyArray()[0]);

        ArrayList<String> al = new ArrayList<String>();
        al.add("A");
        al.add("B");
        al.add("C");
        fu.setFieldValue(tn, "sList", al);
        assertEquals(3, tn.getSList().size());

        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("A1", "A");
        hm.put("B2", "B");
        hm.put("C3", "C");
        fu.setFieldValue(tn, "sMap", hm);
        assertEquals(3, tn.getSMap().size());

        // test setting index values
        fu.setFieldValue(tn, "myArray[0]", "AAA");
        assertEquals("AAA", tn.getMyArray()[0]);

        fu.setFieldValue(tn, "myArray[1]", "BBBB");
        assertEquals("BBBB", tn.getMyArray()[1]);

        fu.setFieldValue(tn, "sList[0]", "alpha");
        assertEquals("alpha", tn.getSList().get(0));

        fu.setFieldValue(tn, "sList[1]", "beta");
        assertEquals("beta", tn.getSList().get(1));

        fu.setIndexedValue(tn, "myArray[99]", "zeta");
        assertEquals("zeta", tn.getMyArray()[99]);

        fu.setIndexedValue(tn, "sList[99]", "zeta");
        assertEquals("zeta", tn.getSList().get(99));

        try {
            fu.setFieldValue(null, "myArray[1]", "BBBB");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }      

        try {
            fu.setFieldValue(tn, null, "BBBB");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test setting mapped values
        fu.setFieldValue(tn, "sMap(A1)", "UNO");
        assertEquals("UNO", tn.getSMap().get("A1"));

        fu.setFieldValue(tn, "sMap(XXX)", "xray");
        assertEquals("xray", tn.getSMap().get("XXX"));

        try {
            fu.setFieldValue(null, "sMap(A1)", "UNO");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setFieldValue(tn, null, "UNO");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setFieldValue(null, "sMap(A1)", 120);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        try {
            fu.setFieldValue(tn, "sList(A1)", "UNO");
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test setting map values
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        fu.setFieldValue(myMap, "A", "alpha");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("alpha", myMap.get("A"));

        fu.setFieldValue(myMap, "(B)", "beta");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("beta", myMap.get("B"));

        fu.setFieldValue(myMap, "D", "delta");
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("delta", myMap.get("D"));
    }

    public void testSetNoConvert() {
        FieldUtils fu = new FieldUtils();

        TestNesting tn = new TestNesting(8, "AZ", new String[] {"1", "B", "three"});
        fu.setFieldValue(tn, "id", 88, false);
        assertEquals(88, tn.getId());

        try {
            fu.setFieldValue(tn, "id", "888", false);
            fail("should have thrown exception");
        } catch (FieldSetValueException e) {
            assertNotNull(e.getMessage());
        }

        fu.setFieldValue(tn, "title", "stuff", false);
        assertEquals("stuff", tn.getTitle());

        try {
            fu.setFieldValue(tn, "title", 1234, false);
            fail("should have thrown exception");
        } catch (FieldSetValueException e) {
            assertNotNull(e.getMessage());
        }

        fu.setFieldValue(tn, "myArray", "A,C,T,G", true);
        assertEquals(4, tn.getMyArray().length);
        assertEquals("A", tn.getMyArray()[0]);

        try {
            fu.setFieldValue(tn, "myArray", "A,C,T,G", false);
            fail("should have thrown exception");
        } catch (FieldSetValueException e) {
            assertNotNull(e.getMessage());
        }

        fu.setFieldValue(tn, "sList", new String[] {"A", "C", "T", "G"}, true);

        try {
            fu.setFieldValue(tn, "sList", new String[] {"A", "C", "T", "G"}, false);
            fail("should have thrown exception");
        } catch (FieldSetValueException e) {
            assertNotNull(e.getMessage());
        }

        // test setting map values
        Map<String, Integer> myMap = new HashMap<String, Integer>();
        myMap.put("A", 10);
        myMap.put("B", 2);
        myMap.put("C", 24);
        fu.setFieldValue(myMap, "A", "alpha", false);
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("alpha", myMap.get("A"));

        fu.setFieldValue(myMap, "(B)", "beta", false);
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("beta", myMap.get("B"));

        fu.setFieldValue(myMap, "D", "delta", false);
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals("delta", myMap.get("D"));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.FieldUtils#getFieldValues(java.lang.Object)}.
     */
    public void testGetFieldValues() {
        FieldUtils fu = new FieldUtils();
        Map<String, Object> m;

        m = fu.getFieldValues( new TestNone() );
        assertNotNull(m);
        assertEquals(0, m.size());

        m = fu.getFieldValues( new TestPea() );
        assertNotNull(m);
        assertEquals(2, m.size());
        assertTrue(m.containsKey("id"));
        assertEquals("id", m.get("id"));
        assertTrue(m.containsKey("entityId"));
        assertEquals("EID", m.get("entityId"));

        m = fu.getFieldValues( new TestBean() ); // READABLE
        assertNotNull(m);
        assertEquals(2, m.size());
        assertTrue(m.containsKey("myInt"));
        assertTrue(m.containsKey("myString"));
        assertEquals(0, m.get("myInt"));
        assertEquals("woot", m.get("myString"));

        m = fu.getFieldValues( new TestBean(), ClassFields.FieldsFilter.WRITEABLE, false );
        assertNotNull(m);
        assertEquals(0, m.size());

        m = fu.getFieldValues( new TestBean(), ClassFields.FieldsFilter.COMPLETE, false );
        assertNotNull(m);
        assertEquals(2, m.size());

        m = fu.getFieldValues( new TestBean(), ClassFields.FieldsFilter.SERIALIZABLE, false );
        assertNotNull(m);
        assertEquals(2, m.size());

        m = fu.getFieldValues( new TestBean(), ClassFields.FieldsFilter.ALL, false );
        assertNotNull(m);
        assertEquals(2, m.size());

        m = fu.getFieldValues( new TestEntity() ); // READABLE
        assertNotNull(m);
        assertEquals(8, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("entityId"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("sArray"));
        assertTrue(m.containsKey("bool"));
        assertTrue(m.containsKey("fieldOnly"));
        assertTrue(m.containsKey("transStr"));
        assertTrue(m.containsKey("prefix"));
        assertEquals((long) 3, m.get("id"));
        assertEquals("33", m.get("entityId"));
        assertEquals(null, m.get("extra"));
        assertTrue(m.get("sArray").getClass().isArray());

        m = fu.getFieldValues( new TestEntity(), ClassFields.FieldsFilter.WRITEABLE, false );
        assertNotNull(m);
        assertEquals(0, m.size());

        m = fu.getFieldValues( new TestEntity(), ClassFields.FieldsFilter.COMPLETE, false );
        assertNotNull(m);
        assertEquals(6, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("entityId"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("sArray"));
        assertTrue(m.containsKey("bool"));
        assertTrue(m.containsKey("fieldOnly"));

        m = fu.getFieldValues( new TestEntity(), ClassFields.FieldsFilter.SERIALIZABLE, false );
        assertNotNull(m);
        assertEquals(7, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("entityId"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("sArray"));
        assertTrue(m.containsKey("bool"));
        assertTrue(m.containsKey("fieldOnly"));
        assertTrue(m.containsKey("prefix"));

        m = fu.getFieldValues( new TestEntity(), ClassFields.FieldsFilter.SERIALIZABLE_FIELDS, false );
        assertNotNull(m);
        assertEquals(7, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("entityId"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("sArray"));
        assertTrue(m.containsKey("bool"));
        assertTrue(m.containsKey("fieldOnly"));
        assertTrue(m.containsKey("privateFieldOnly"));


        m = fu.getFieldValues( new TestEntity(), ClassFields.FieldsFilter.ALL, false );
        assertNotNull(m);
        assertEquals(9, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("entityId"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("sArray"));
        assertTrue(m.containsKey("bool"));
        assertTrue(m.containsKey("fieldOnly"));
        assertTrue(m.containsKey("privateFieldOnly"));
        assertTrue(m.containsKey("transStr"));
        assertTrue(m.containsKey("prefix"));

        TestNesting tn = new TestNesting();
        tn.setTestBean( new TestBean() );
        m = fu.getFieldValues( tn );
        assertNotNull(m);
        assertEquals(9, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("title"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("myArray"));
        assertTrue(m.containsKey("sList"));
        assertTrue(m.containsKey("sMap"));
        assertTrue(m.containsKey("testPea"));
        assertTrue(m.containsKey("testBean"));
        assertTrue(m.containsKey("testEntity"));
        assertEquals(5, m.get("id"));
        assertEquals("55", m.get("title"));
        assertEquals(null, m.get("extra"));
        assertTrue(m.get("myArray").getClass().isArray());
        assertTrue(List.class.isAssignableFrom(m.get("sList").getClass()));
        assertTrue(Map.class.isAssignableFrom(m.get("sMap").getClass()));
        assertNotNull(m.get("testBean"));
        assertNull(m.get("testPea"));
        assertNotNull(m.get("testEntity"));
    }

    public void testAutoCreateBeans() {
        FieldUtils fu = new FieldUtils();

        // check that the objects are auto created
        TestNesting nested = new TestNesting(10, "100", new String[] {"A", "B"});

        fu.setFieldValue(nested, "testBean.myString", "stuff");
        assertEquals("stuff", nested.getTestBean().getMyString());

        fu.setFieldValue(nested, "testPea.id", "AZ");
        assertEquals("AZ", nested.testPea.id);

        // test that collections are auto created
        TestCollections collect = new TestCollections(false);

        fu.setFieldValue(collect, "list[0]", "AZ");
        assertEquals("AZ", collect.list.get(0));

        fu.setFieldValue(collect, "array[0]", "AZ");
        assertEquals("AZ", collect.array[0]);

        fu.setFieldValue(collect, "map(aaron)", "AZ");
        assertEquals("AZ", collect.map.get("aaron"));

        // test collection nesting (mostly for maps)
        TestCollections tc = new TestCollections(false);

        fu.setFieldValue(tc, "map.user.name", "AZ");
        assertNotNull(tc.map);
        assertEquals(1, tc.map.size());
        assertTrue(tc.map.containsKey("user"));
        assertEquals("AZ", fu.getFieldValue(tc, "map.user.name"));
    }

    public void testNonVisibleFields() {
        FieldUtils fu = FieldUtils.getInstance();
        Object value;

        TestPea tp = new TestPea();
        // test getting
        value = fu.getFieldValue(tp, "prot");
        assertNotNull(value);
        assertEquals("prot", value);

        value = fu.getFieldValue(tp, "priv");
        assertNotNull(value);
        assertEquals("priv", value);

        // test types
        Class<?> type = fu.getFieldType(tp, "prot");
        assertNotNull(type);
        assertEquals(String.class, type);

        type = fu.getFieldType(tp, "priv");
        assertNotNull(type);
        assertEquals(String.class, type);

        // test setting
        String newVal = "aaronz";
        fu.setFieldValue(tp, "prot", newVal);
        value = fu.getFieldValue(tp, "prot");
        assertNotNull(value);
        assertEquals("aaronz", value);

        fu.setFieldValue(tp, "priv", newVal);
        value = fu.getFieldValue(tp, "priv");
        assertNotNull(value);
        assertEquals("aaronz", value);

    }

}
