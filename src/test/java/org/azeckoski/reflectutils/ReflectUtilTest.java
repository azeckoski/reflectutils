/**
 * $Id: ReflectUtilTest.java 129 2014-03-18 23:25:36Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/ReflectUtilTest.java $
 * ReflectUtilTest.java - entity-broker - Apr 13, 2008 8:26:39 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2
 * 
 * A copy of the Apache License, Version 2 has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import junit.framework.TestCase;
import org.azeckoski.reflectutils.annotations.TestAnnote;
import org.azeckoski.reflectutils.classes.*;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;
import org.azeckoski.reflectutils.interfaces.TestInterfaceFour;
import org.azeckoski.reflectutils.interfaces.TestInterfaceOne;

import java.io.Serializable;
import java.util.*;

/**
 * Testing the reflection utils
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class ReflectUtilTest extends TestCase {

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getFieldValue(java.lang.Object, java.lang.String)}.
     */
    @SuppressWarnings("unchecked")
    public void testGetFieldValueObjectString() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Object value = null;
        Object thing = null;

        thing = new TestEntity();
        value = reflectUtil.getFieldValue( thing, "entityId");
        assertNotNull(value);
        assertEquals("33", value);

        value = reflectUtil.getFieldValue( thing, "id");
        assertNotNull(value);
        assertEquals(new Long(3), value);

        TestNesting nested = new TestNesting(10, "100", new String[] {"A", "B"});
        value = reflectUtil.getFieldValue( nested, "id");
        assertNotNull(value);
        assertEquals(10, value);

        value = reflectUtil.getFieldValue( nested, "testEntity.id");
        assertNotNull(value);
        assertEquals(new Long(3), value);

        value = reflectUtil.getFieldValue( nested, "testBean");
        assertNull(value);

        try {
            value = reflectUtil.getFieldValue( nested, "testBean.myString");
            fail("should have thrown exception");
        } catch (RuntimeException e1) {
            assertNotNull(e1);
        }

        // get list value
        value = reflectUtil.getFieldValue( nested, "sList[0]");
        assertNotNull(value);
        assertEquals("A", value);

        value = reflectUtil.getFieldValue( nested, "sList[1]");
        assertNotNull(value);
        assertEquals("B", value);

        value = reflectUtil.getFieldValue( nested, "sList");
        assertNotNull(value);
        assertEquals("A", ((List)value).get(0));

        // get map value
        value = reflectUtil.getFieldValue( nested, "sMap(A1)");
        assertNotNull(value);
        assertEquals("ONE", value);

        value = reflectUtil.getFieldValue( nested, "sMap(B2)");
        assertNotNull(value);
        assertEquals("TWO", value);

        value = reflectUtil.getFieldValue( thing, "extra");
        assertNull(value);

        value = reflectUtil.getFieldValue( thing, "sArray");
        assertNotNull(value);
        assertTrue(value.getClass().isArray());
        assertEquals("1", ((String[])value)[0]);
        assertEquals("2", ((String[])value)[1]);

        // test ultra nesting
        TestUltraNested ultra = new TestUltraNested("ULTRA", new TestNesting());
        value = reflectUtil.getFieldValue(ultra, "title");
        assertEquals("ULTRA", value);

        value = reflectUtil.getFieldValue(ultra, "testNesting.id");
        assertEquals(5, value);

        value = reflectUtil.getFieldValue(ultra, "testNesting.testEntity.id");
        assertEquals(new Long(3), value);

        value = reflectUtil.getFieldValue(ultra, "testNesting.sList[0]");
        assertEquals("A", value);

        value = reflectUtil.getFieldValue(ultra, "testNesting.sMap(A1)");
        assertEquals("ONE", value);

        value = reflectUtil.getFieldValue(ultra, "testNesting.myArray[1]");
        assertEquals("B", value);

        // basic pea support
        thing = new TestPea();
        value = reflectUtil.getFieldValue( thing, "id");
        assertNotNull(value);
        assertEquals("id", value);

        thing = new TestBean();
        try {
            value = reflectUtil.getFieldValue(thing, "id");
            fail("Should have thrown exception");
        } catch (FieldnameNotFoundException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ReflectUtils#getFieldValueAsString(java.lang.Object, java.lang.String, java.lang.Class)}.
     */
    public void testGetFieldValueAsString() {
        ReflectUtils reflectUtil = new ReflectUtils();
        String value = null;
        Object thing = null;

        thing = new TestEntity();
        value = reflectUtil.getFieldValueAsString(thing, "entityId", null);
        assertNotNull(value);
        assertEquals("33", value);

        value = reflectUtil.getFieldValueAsString( thing, "id", null);
        assertNotNull(value);
        assertEquals("3", value);

        TestNesting nested = new TestNesting(10, "100", new String[] {"A", "B"});
        value = reflectUtil.getFieldValueAsString( nested, "id", null);
        assertNotNull(value);
        assertEquals("10", value);

        value = reflectUtil.getFieldValueAsString( nested, "testEntity.id", null);
        assertNotNull(value);
        assertEquals("3", value);

        value = reflectUtil.getFieldValueAsString( nested, "testBean", null);
        assertNull(value);
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#setFieldValue(java.lang.Object, java.lang.String, java.lang.Object)}.
     */
    public void testSetFieldValue() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Object thing = null;

        thing = new TestBean();
        reflectUtil.setFieldValue(thing, "myString", "TEST");
        assertEquals("TEST", ((TestBean)thing).getMyString());

        thing = new TestBean();
        reflectUtil.setFieldValue(thing, "myInt", 5);
        assertEquals(5, ((TestBean)thing).getMyInt());

        // test nested set
        TestNesting nested = new TestNesting(10, "100", new String[] {"A", "B"});
        reflectUtil.setFieldValue(nested, "title", "STUFF");
        assertEquals("STUFF", nested.getTitle());

        reflectUtil.setFieldValue(nested, "testEntity.extra", "XTRA");
        assertEquals("XTRA", nested.getTestEntity().getExtra());

        // make it so objects are auto created as needed
        reflectUtil.setFieldValue(nested, "testBean.myString", "stuff");
        assertEquals("stuff", nested.getTestBean().getMyString());

        // test set maps
        Map<String, String> tMap = new HashMap<String, String>();
        tMap.put("AA", "alpha");
        tMap.put("BB", "beta");
        reflectUtil.setFieldValue(nested, "sMap", tMap);
        assertEquals("alpha", nested.getSMap().get("AA"));
        assertEquals("beta", nested.getSMap().get("BB"));

        reflectUtil.setFieldValue(nested, "sMap(AA)", "reset");
        assertEquals("reset", nested.getSMap().get("AA"));

        reflectUtil.setFieldValue(nested, "sMap(CC)", "charlie");
        assertEquals("charlie", nested.getSMap().get("CC"));

        nested.setSMap(null);
        reflectUtil.setFieldValue(nested, "sMap(CC)", "charlie");
        assertEquals("charlie", nested.getSMap().get("CC"));

        // test set lists
        List<String> tList = new ArrayList<String>();
        tList.add("AA");
        tList.add("BB");
        reflectUtil.setFieldValue(nested, "sList", tList);
        assertEquals("AA", nested.getSList().get(0));
        assertEquals("BB", nested.getSList().get(1));

        reflectUtil.setFieldValue(nested, "sList[0]", "AAA");
        assertEquals("AAA", nested.getSList().get(0));

        reflectUtil.setFieldValue(nested, "sList[3]", "CC");
        assertEquals("CC", nested.getSList().get(3));

        reflectUtil.setFieldValue(nested, "sList[5]", "EE");
        assertEquals("EE", nested.getSList().get(5));

        nested.setSList(null);
        reflectUtil.setFieldValue(nested, "sList[1]", "BB");
        assertEquals("BB", nested.getSList().get(1));

        // test arrays
        TestEntity te = new TestEntity();
        reflectUtil.setFieldValue(te, "sArray", new String[] {"A", "B", "C"});
        assertEquals(3, te.getSArray().length);
        assertEquals("A", te.getSArray()[0]);
        assertEquals("B", te.getSArray()[1]);
        assertEquals("C", te.getSArray()[2]);

        reflectUtil.setFieldValue(te, "sArray[0]", "AA");
        assertEquals("AA", te.getSArray()[0]);

        reflectUtil.setFieldValue(te, "sArray[3]", "D");
        assertEquals("D", te.getSArray()[3]);

        te.setSArray(null);
        reflectUtil.setFieldValue(te, "sArray[0]", "AA");
        assertEquals("AA", te.getSArray()[0]);

        // test ultra nesting
        TestUltraNested ultra = new TestUltraNested("ULTRA", new TestNesting());
        reflectUtil.setFieldValue(ultra, "title", "Ultra");
        assertEquals("Ultra", ultra.getTitle());

        reflectUtil.setFieldValue(ultra, "testNesting.id", 20);
        assertEquals(20, ultra.getTestNesting().getId());

        reflectUtil.setFieldValue(ultra, "testNesting.testEntity.id", new Long(6));
        assertEquals(new Long(6), ultra.getTestNesting().getTestEntity().getId());

        reflectUtil.setFieldValue(ultra, "testNesting.sList[0]", "AAA");
        assertEquals("AAA", ultra.getTestNesting().getSList().get(0));

        reflectUtil.setFieldValue(ultra, "testNesting.sMap(A1)", "UNO");
        assertEquals("UNO", ultra.getTestNesting().getSMap().get("A1"));

        reflectUtil.setFieldValue(ultra, "testNesting.myArray[1]", "BBB");
        assertEquals("BBB", ultra.getTestNesting().getMyArray()[1]);

        thing = new TestBean();
        try {
            reflectUtil.setFieldValue(thing, "id", "uhohes");
            fail("Should have thrown exception");
        } catch (FieldnameNotFoundException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#setFieldStringValue(java.lang.Object, java.lang.String, java.lang.String)}.
     */
    public void testSetFieldStringValue() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Object thing = null;

        thing = new TestBean();
        reflectUtil.setFieldValue(thing, "myString", "TEST");
        assertEquals("TEST", ((TestBean)thing).getMyString());

        thing = new TestBean();
        reflectUtil.setFieldValue(thing, "myInt", "10");
        assertEquals(10, ((TestBean)thing).getMyInt());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "id", "6");
        assertEquals(new Long(6), ((TestEntity)thing).getId());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "sArray", "A, B, C");
        assertEquals(3, ((TestEntity)thing).getSArray().length);
        assertEquals("A", ((TestEntity)thing).getSArray()[0]);
        assertEquals("B", ((TestEntity)thing).getSArray()[1]);
        assertEquals("C", ((TestEntity)thing).getSArray()[2]);
    }

    public void testSetFieldValueWithConversion() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Object thing = null;

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "id", "10", true);
        assertEquals(new Long(10), ((TestEntity)thing).getId());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "id", 10, true);
        assertEquals(new Long(10), ((TestEntity)thing).getId());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "id", new String[] {"10"}, true);
        assertEquals(new Long(10), ((TestEntity)thing).getId());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "bool", true, true);
        assertEquals(Boolean.TRUE, ((TestEntity)thing).getBool());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "bool", "true", true);
        assertEquals(Boolean.TRUE, ((TestEntity)thing).getBool());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "bool", "false", true);
        assertEquals(Boolean.FALSE, ((TestEntity)thing).getBool());

        //        thing = new TestEntity();
        //        reflectUtil.setFieldValueWithConversion(thing, "bool", "xxxx");
        //        assertEquals(Boolean.FALSE, ((TestEntity)thing).getBool());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "bool", "", true);
        assertEquals(Boolean.FALSE, ((TestEntity)thing).getBool());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "extra", "stuff", true);
        assertEquals("stuff", ((TestEntity)thing).getExtra());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "extra", 100, true);
        assertEquals("100", ((TestEntity)thing).getExtra());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "extra", new String[] {"stuff"}, true);
        assertEquals("stuff", ((TestEntity)thing).getExtra());

        thing = new TestEntity();
        reflectUtil.setFieldValue(thing, "extra", new String[] {"stuff", "plus"}, true);
        assertEquals("stuff,plus", ((TestEntity)thing).getExtra());
    }


    /**
     * Test method for {@link org.azeckoski.reflectutils.ReflectUtils#constructClass(java.lang.Class)}.
     */
    @SuppressWarnings("unchecked")
    public void testConstructClass() {
        ReflectUtils reflectUtil = new ReflectUtils();

        TestBean testBean = reflectUtil.constructClass(TestBean.class);
        assertNotNull(testBean);

        TestNesting testNesting = reflectUtil.constructClass(TestNesting.class);
        assertNotNull(testNesting);

        TestUltraNested ultraNested = reflectUtil.constructClass(TestUltraNested.class);
        assertNotNull(ultraNested);

        // test constructing the various collections
        ArrayList arrayList = reflectUtil.constructClass(ArrayList.class);
        assertNotNull(arrayList);

        HashSet hashSet = reflectUtil.constructClass(HashSet.class);
        assertNotNull(hashSet);

        HashMap hashMap = reflectUtil.constructClass(HashMap.class);
        assertNotNull(hashMap);

        // test constructing the various collections by interface
        List list = reflectUtil.constructClass(List.class);
        assertNotNull(list);

        Set set = reflectUtil.constructClass(Set.class);
        assertNotNull(set);

        Map map = reflectUtil.constructClass(Map.class);
        assertNotNull(map);

        // now test constructing some simple objects
        String string = reflectUtil.constructClass(String.class);
        assertNotNull(string);

        Boolean bool = reflectUtil.constructClass(Boolean.class);
        assertNotNull(bool);

        // now for the really simple ones
        int i = reflectUtil.constructClass(int.class);
        assertEquals(0, i);

        boolean b = reflectUtil.constructClass(boolean.class);
        assertEquals(false, b);

        // arrays
        Integer[] integers = reflectUtil.constructClass(Integer[].class);
        assertNotNull(integers);

        TestBean[] testBeans = reflectUtil.constructClass(TestBean[].class);
        assertNotNull(testBeans);

        boolean[] bools = reflectUtil.constructClass(boolean[].class);
        assertNotNull(bools);

        try {
            reflectUtil.constructClass(null);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testConvert() {
        ReflectUtils reflectUtil = new ReflectUtils();

        assertEquals(123, (int) reflectUtil.convert("123", int.class) );
        assertEquals("123", reflectUtil.convert("123", String.class) );
        assertEquals(new Integer(123), reflectUtil.convert("123", Integer.class) );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ReflectUtils#pdNameCompare(java.lang.String, java.lang.String)}.
     */
    public void testPdNameCompare() {
        assertFalse( ReflectUtils.pdNameCompare(null, "") );
        assertFalse( ReflectUtils.pdNameCompare("", "") );
        assertFalse( ReflectUtils.pdNameCompare("fieldXXX", "fieldYYY") );

        assertTrue( ReflectUtils.pdNameCompare("fieldXXX", "fieldXXX") );
        assertTrue( ReflectUtils.pdNameCompare("myThing", "myThing") );
        assertTrue( ReflectUtils.pdNameCompare("sField", "sField") );
        assertTrue( ReflectUtils.pdNameCompare("sField", "SField") );
        assertTrue( ReflectUtils.pdNameCompare("SField", "sField") );
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getFieldTypes()}.
     */
    public void testGetFieldTypes() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Map<String, Class<?>> types;

        types = reflectUtil.getFieldTypes(TestBean.class);
        assertNotNull(types);
        assertEquals(2, types.size());
        assertEquals(String.class, types.get("myString"));
        assertEquals(int.class, types.get("myInt"));
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getFieldType(java.lang.Class, java.lang.String)}.
     */
    public void testGetFieldType() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Class<?> type;

        type = reflectUtil.getFieldType(TestBean.class, "myString");
        assertNotNull(type);
        assertEquals(String.class, type);

        type = reflectUtil.getFieldType(TestBean.class, "myInt");
        assertNotNull(type);
        assertEquals(int.class, type);

        type = reflectUtil.getFieldType(TestPea.class, "id");
        assertNotNull(type);
        assertEquals(String.class, type);

        type = reflectUtil.getFieldType(TestNesting.class, "sList");
        assertNotNull(type);
        assertEquals(List.class, type);

        type = reflectUtil.getFieldType(TestNesting.class, "sMap");
        assertNotNull(type);
        assertEquals(Map.class, type);
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getObjectValues(java.lang.Object)}.
     */
    public void testGetObjectValues() {
        Map<String, Object> m = null;
        ReflectUtils reflectUtil = new ReflectUtils();

        m = reflectUtil.getObjectValues( new TestNone() );
        assertNotNull(m);
        assertEquals(0, m.size());

        m = reflectUtil.getObjectValues( new TestPea() );
        assertNotNull(m);
        assertEquals(2, m.size());
        assertTrue(m.containsKey("id"));
        assertEquals("id", m.get("id"));
        assertTrue(m.containsKey("entityId"));
        assertEquals("EID", m.get("entityId"));

        m = reflectUtil.getObjectValues( new TestBean() );
        assertNotNull(m);
        assertEquals(2, m.size());
        assertTrue(m.containsKey("myInt"));
        assertTrue(m.containsKey("myString"));
        assertEquals(0, m.get("myInt"));
        assertEquals("woot", m.get("myString"));

        m = reflectUtil.getObjectValues( new TestEntity() );
        assertNotNull(m);
        assertEquals(8, m.size());
        assertTrue(m.containsKey("id"));
        assertTrue(m.containsKey("entityId"));
        assertTrue(m.containsKey("extra"));
        assertTrue(m.containsKey("sArray"));
        assertTrue(m.containsKey("bool"));
        assertTrue(m.containsKey("prefix"));
        assertEquals(new Long(3), m.get("id"));
        assertEquals("33", m.get("entityId"));
        assertEquals(null, m.get("extra"));
        assertEquals("crud", m.get("prefix"));
        assertTrue(m.get("sArray").getClass().isArray());
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getFieldNameWithAnnotation(java.lang.Class, java.lang.Class)}.
     */
    public void testGetFieldNameWithAnnotation() {
        ReflectUtils reflectUtil = new ReflectUtils();
        String fieldName = null;

        fieldName = reflectUtil.getFieldNameWithAnnotation(TestBean.class, TestAnnote.class);
        assertEquals("myInt", fieldName);

        fieldName = reflectUtil.getFieldNameWithAnnotation(TestEntity.class, TestAnnote.class);
        assertEquals("entityId", fieldName);
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getFieldValueAsString(java.lang.Object, java.lang.String, java.lang.Class)}.
     */
    public void testGetFieldValueObjectStringClassOfQextendsAnnotation() {
        String value = null;
        ReflectUtils reflectUtil = new ReflectUtils();

        try {
            value = reflectUtil.getFieldValueAsString( new TestBean(), "id", null);
            fail("Should have thrown exception");
        } catch (FieldnameNotFoundException e) {
            assertNotNull(e.getMessage());
        }

        value = reflectUtil.getFieldValueAsString( new TestEntity(), "extra", null);
        assertNull(value);

        value = reflectUtil.getFieldValueAsString( new TestPea(), "id", null);
        assertNotNull(value);
        assertEquals("id", value);

        value = reflectUtil.getFieldValueAsString( new TestEntity(), "id", null);
        assertNotNull(value);
        assertEquals("3", value);

        value = reflectUtil.getFieldValueAsString( new TestPea(), "id", TestAnnote.class);
        assertNotNull(value);
        assertEquals("EID", value);

        value = reflectUtil.getFieldValueAsString( new TestEntity(), "id", TestAnnote.class);
        assertNotNull(value);
        assertEquals("33", value);

        try {
            value = reflectUtil.getFieldValueAsString( new TestNone(), "id", TestAnnote.class);
            fail("Should have thrown exception");
        } catch (FieldnameNotFoundException e) {
            assertNotNull(e.getMessage());
        }

    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#deepClone(java.lang.Object, int, java.lang.String[])}.
     */
    public void testClone() {
        ReflectUtils reflectUtil = new ReflectUtils();

        TestBean tb = new TestBean();
        tb.setMyInt(100);
        tb.setMyString("1000");
        TestBean tbClone = reflectUtil.clone(tb, 0, null);
        assertNotNull(tbClone);
        assertEquals(tb.getMyInt(), tbClone.getMyInt());
        assertEquals(tb.getMyString(), tbClone.getMyString());

        // test skipping values
        tbClone = reflectUtil.clone(tb, 0, new String[] {"myInt"});
        assertNotNull(tbClone);
        assertTrue(tb.getMyInt() != tbClone.getMyInt());
        assertEquals(tb.getMyString(), tbClone.getMyString());

        tbClone = reflectUtil.clone(tb, 5, null);
        assertNotNull(tbClone);
        assertEquals(tb.getMyInt(), tbClone.getMyInt());
        assertEquals(tb.getMyString(), tbClone.getMyString());

        // TODO test cloning maps

        // TODO test cloning nested objects

        // TODO test cloning collections

    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#deepCopy(java.lang.Object, java.lang.Object, int, java.lang.String[], boolean)}.
     */
    public void testCopy() {
        ReflectUtils reflectUtil = new ReflectUtils();

        TestBean orig = new TestBean();
        orig.setMyInt(100);
        orig.setMyString("1000");
        TestBean dest = new TestBean();
        assertNotSame(orig.getMyInt(), dest.getMyInt());
        assertNotSame(orig.getMyString(), dest.getMyString());
        reflectUtil.copy(orig, dest, 0, null, false);
        assertNotNull(dest);
        assertEquals(orig.getMyInt(), dest.getMyInt());
        assertEquals(orig.getMyString(), dest.getMyString());

        dest = new TestBean();
        reflectUtil.copy(orig, dest, 0, new String[] {"myInt"}, false);
        assertNotNull(dest);
        assertNotSame(orig.getMyInt(), dest.getMyInt());
        assertEquals(orig.getMyString(), dest.getMyString());

        dest = new TestBean();
        reflectUtil.copy(orig, dest, 5, null, true);
        assertNotNull(dest);
        assertEquals(orig.getMyInt(), dest.getMyInt());
        assertEquals(orig.getMyString(), dest.getMyString());
    }

    public void testSimpleCopy() {
        ReflectUtils reflectUtil = new ReflectUtils();

        // copy to immutable fails
        try {
            reflectUtil.copy(new TestBean(), "AZ", 0, null, true);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // immutable copy should fail
        String az = "AZ";
        String bz = "BZ";
        try {
            reflectUtil.copy(az, bz, 0, null, true);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test array copying
        String[] array = new String[3];
        reflectUtil.copy(new String[] {"A","B","C"}, array, 0, null, true);
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("B", array[1]);
        assertEquals("C", array[2]);

        // test scalar to array conversion
        array = new String[3];
        reflectUtil.copy("A,B,C", array, 0, null, true);
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("B", array[1]);
        assertEquals("C", array[2]);
    }

    public void testPopulate() {
        ReflectUtils reflectUtil = new ReflectUtils();
        List<String> results = null;
        Map<String, Object> properties = new HashMap<String, Object>();

        // empty should be ok and should not change anything
        TestBean target = new TestBean();
        results = reflectUtil.populate(target, properties);
        assertNotNull(results);
        assertEquals(0, results.size());
        assertNotNull(target);
        assertEquals(0, target.getMyInt());
        assertEquals("woot", target.getMyString());

        // non matching fields should be ok
        properties.put("xxxxxxx", "xxxxxx");
        properties.put("yyyyyyy", 1000000);
        results = reflectUtil.populate(target, properties);
        assertNotNull(results);
        assertEquals(0, results.size());
        assertNotNull(target);
        assertEquals(0, target.getMyInt());
        assertEquals("woot", target.getMyString());

        // strings should be ok
        properties.put("myInt", "100");
        properties.put("myString", "NEW");
        results = reflectUtil.populate(target, properties);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(target);
        assertEquals(100, target.getMyInt());
        assertEquals("NEW", target.getMyString());

        // string arrays should be ok also
        properties.put("myInt", new String[] {"1000"});
        properties.put("myString", new String[] {"OLD","BLUE"});
        results = reflectUtil.populate(target, properties);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(target);
        assertEquals(1000, target.getMyInt());
        assertEquals("OLD,BLUE", target.getMyString());

        // objects
        properties.put("myInt", new Long(222));
        properties.put("myString", 55555);
        results = reflectUtil.populate(target, properties);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(target);
        assertEquals(222, target.getMyInt());
        assertEquals("55555", target.getMyString());
    }

    public void testPopulateFromParams() {
        ReflectUtils reflectUtil = new ReflectUtils();
        List<String> results = null;
        Map<String, String[]> properties = new HashMap<String, String[]>();

        TestEntity target = new TestEntity();

        properties.put("id", new String[] {"1000"});
        properties.put("extra", new String[] {"OLD"});
        properties.put("sArray", new String[] {"AA","BB","CC"});
        results = reflectUtil.populateFromParams(target, properties);
        assertNotNull(results);
        assertEquals(3, results.size());
        assertNotNull(target);
        assertEquals(new Long(1000), target.getId());
        assertEquals("OLD", target.getExtra());
        assertEquals("33", target.getEntityId());
        assertEquals(null, target.getBool());
        assertEquals(3, target.getSArray().length);

    }

    public void testPopulateFromParamsDates() {
        ReflectUtils reflectUtil = new ReflectUtils();
        List<String> results = null;
        Map<String, String[]> properties = new HashMap<String, String[]>();

        TestDateSpecial target = new TestDateSpecial();

        Date now = new Date();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        long nowLong = now.getTime();
        String nowString = nowLong+"";
        properties.put("id", new String[] {"1000"});
        properties.put("longing", new String[] {nowString});
        properties.put("date", new String[] {nowString});
        properties.put("calendar", new String[] {nowString});

        results = reflectUtil.populateFromParams(target, properties);
        assertNotNull(results);
        assertEquals(4, results.size());
        assertNotNull(target);
        assertEquals("1000", target.id);
        assertEquals(nowLong, target.longing);
        assertEquals(now, target.date);
        assertEquals(nowCal, target.calendar);
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#makeFieldNameFromMethod(java.lang.String)}.
     */
    public void testMakeFieldNameFromMethod() {
        String name = null;

        name = ReflectUtils.makeFieldNameFromMethod("getStuff");
        assertEquals("stuff", name);

        name = ReflectUtils.makeFieldNameFromMethod("getSomeStuff");
        assertEquals("someStuff", name);

        name = ReflectUtils.makeFieldNameFromMethod("setStuff");
        assertEquals("stuff", name);

        name = ReflectUtils.makeFieldNameFromMethod("isStuff");
        assertEquals("stuff", name);

        name = ReflectUtils.makeFieldNameFromMethod("stuff");
        assertEquals("stuff", name);
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getSuperclasses(java.lang.Class)}.
     */
    public void testGetSuperclasses() {
        List<Class<?>> superClasses = null;

        superClasses = ReflectUtils.getSuperclasses(TestNone.class);
        assertNotNull(superClasses);
        assertEquals(1, superClasses.size());
        assertEquals(TestNone.class, superClasses.get(0));

        superClasses = ReflectUtils.getSuperclasses(TestImplOne.class);
        assertNotNull(superClasses);
        assertEquals(3, superClasses.size());
        assertTrue( superClasses.contains(TestImplOne.class) );
        assertTrue( superClasses.contains(TestInterfaceOne.class) );
        assertTrue( superClasses.contains(Serializable.class) );

        superClasses = ReflectUtils.getSuperclasses(TestImplFour.class);
        assertNotNull(superClasses);
        assertTrue(superClasses.size() >= 7);
        assertTrue( superClasses.contains(TestImplFour.class) );
        assertTrue( superClasses.contains(TestInterfaceOne.class) );
        assertTrue( superClasses.contains(TestInterfaceFour.class) );
        assertTrue( superClasses.contains(Serializable.class) );
        assertTrue( superClasses.contains(Runnable.class) );
        assertTrue( superClasses.contains(Cloneable.class) );
        assertTrue( superClasses.contains(Readable.class) );
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#getClassFromCollection(java.util.Collection)}.
     */
    @SuppressWarnings("unchecked")
    public void testGetClassFromCollection() {
        Class<?> result = null;

        // null returns object class
        result = ReflectUtils.getClassFromCollection(null);
        assertNotNull(result);
        assertEquals(Object.class, result);

        // empty collection is always object
        result = ReflectUtils.getClassFromCollection( new ArrayList<String>() );
        assertNotNull(result);
        assertEquals(Object.class, result);

        // NOTE: Cannot get real type from empty collections

        // try with collections that have things in them
        List<Object> l = new ArrayList<Object>();
        l.add(new String("testing"));
        result = ReflectUtils.getClassFromCollection(l);
        assertNotNull(result);
        assertEquals(String.class, result);

        HashSet<Object> s = new HashSet<Object>();
        s.add(new Double(22.0));
        result = ReflectUtils.getClassFromCollection(s);
        assertNotNull(result);
        assertEquals(Double.class, result);

        List v = new Vector<Object>();
        v.add( new Integer(30) );
        result = ReflectUtils.getClassFromCollection(v);
        assertNotNull(result);
        assertEquals(Integer.class, result);
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#contains(T[], java.lang.Object)}.
     */
    public void testContains() {
        assertFalse( ReflectUtils.contains(new String[] {}, "stuff") );
        assertFalse( ReflectUtils.contains(new String[] {"apple"}, "stuff") );
        assertTrue( ReflectUtils.contains(new String[] {"stuff"}, "stuff") );
        assertTrue( ReflectUtils.contains(new String[] {"stuff","other","apple"}, "stuff") );
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#capitalize(java.lang.String)}.
     */
    public void testCapitalize() {
        assertTrue( ReflectUtils.capitalize("lower").equals("Lower") );
        assertTrue( ReflectUtils.capitalize("UPPER").equals("UPPER") );
        assertTrue( ReflectUtils.capitalize("myStuff").equals("MyStuff") );
        assertTrue( ReflectUtils.capitalize("MyStuff").equals("MyStuff") );
        assertTrue( ReflectUtils.capitalize("").equals("") );
        assertTrue( ReflectUtils.capitalize("m").equals("M") );
    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#unCapitalize(java.lang.String)}.
     */
    public void testUnCapitalize() {
        assertTrue( ReflectUtils.unCapitalize("lower").equals("lower") );
        assertTrue( ReflectUtils.unCapitalize("UPPER").equals("uPPER") );
        assertTrue( ReflectUtils.unCapitalize("MyStuff").equals("myStuff") );
        assertTrue( ReflectUtils.unCapitalize("myStuff").equals("myStuff") );
        assertTrue( ReflectUtils.unCapitalize("").equals("") );
        assertTrue( ReflectUtils.unCapitalize("M").equals("m") );
    }

    @SuppressWarnings("unchecked")
    public void testObjectToMap() {
        ReflectUtils reflectUtil = new ReflectUtils();
        Map<String,Object> map = null;
        Map<String,Object> m = null;

        map = reflectUtil.map(new TestBean(), 0, null, false, false, null);
        assertNotNull(map);
        assertEquals(0, map.get("myInt"));
        assertEquals("woot", map.get("myString"));

        map = reflectUtil.map(new TestPea(), 0, null, false, false, null);
        assertNotNull(map);
        assertEquals("id", map.get("id"));
        assertEquals("EID", map.get("entityId"));

        map = reflectUtil.map(new TestNesting(), 10, null, false, false, null);
        assertNotNull(map);
        assertEquals(5, map.get("id"));
        assertEquals("55", map.get("title"));
        assertTrue(map.containsKey("extra"));
        assertEquals(null, map.get("extra"));
        assertNotNull(map.get("sMap"));
        assertNotNull(map.get("sList"));
        assertNotNull(map.get("myArray"));
        m = (Map<String, Object>) map.get("sMap"); 
        assertNotNull(m);
        assertEquals("ONE", m.get("A1"));
        assertEquals("TWO", m.get("B2"));
        // 2 null objects
        assertTrue(map.containsKey("testPea"));
        assertTrue(map.containsKey("testBean"));
        assertEquals(null, m.get("testPea"));
        assertEquals(null, m.get("testBean"));
        // get non-null object data
        m = (Map<String, Object>) map.get("testEntity"); 
        assertNotNull(m);
        assertEquals(new Long(3), m.get("id"));
        assertEquals("33", m.get("entityId"));
        assertTrue(m.containsKey("extra"));
        assertEquals(null, m.get("extra")); // null
        assertTrue(m.containsKey("bool"));
        assertEquals(null, m.get("bool")); // null
        assertEquals("crud", m.get("prefix"));
        String[] sArray = (String[]) m.get("sArray");
        assertNotNull(sArray);
        assertEquals(2, sArray.length);
        assertEquals("1", sArray[0]);
        assertEquals("2", sArray[1]);

        map = reflectUtil.map(new TestUltraNested("az",new TestNesting()), 10, null, false, false, null);
        assertNotNull(map);
        assertFalse(map.containsKey("id"));
        assertEquals("az", map.get("title"));
        m = (Map<String, Object>) map.get("testNesting"); 
        assertNotNull(m);
        assertEquals(9, m.size());

        // test ignore nulls
        map = reflectUtil.map(new TestNesting(), 10, null, true, false, null);
        assertNotNull(map);
        assertEquals(5, map.get("id"));
        assertEquals("55", map.get("title"));
        assertFalse(map.containsKey("extra"));
        assertNotNull(map.get("sMap"));
        assertNotNull(map.get("sList"));
        assertNotNull(map.get("myArray"));
        m = (Map<String, Object>) map.get("sMap"); 
        assertNotNull(m);
        assertEquals("ONE", m.get("A1"));
        assertEquals("TWO", m.get("B2"));
        // 2 null objects
        assertFalse(map.containsKey("testPea"));
        assertFalse(map.containsKey("testBean"));
        // get non-null object data
        m = (Map<String, Object>) map.get("testEntity"); 
        assertNotNull(m);
        assertEquals(new Long(3), m.get("id"));
        assertEquals("33", m.get("entityId"));
        assertFalse(m.containsKey("extra"));
        assertEquals(null, m.get("extra")); // null
        assertFalse(m.containsKey("bool"));
        assertEquals(null, m.get("bool")); // null
        assertEquals("crud", m.get("prefix"));
        sArray = (String[]) m.get("sArray");
        assertNotNull(sArray);
        assertEquals(2, sArray.length);
        assertEquals("1", sArray[0]);
        assertEquals("2", sArray[1]);

        // test handling of arrays, lists, maps
        map = reflectUtil.map( new String[] {"A","B","C"} , 1, null, false, false, null);
        assertNotNull(map);
        assertEquals(1, map.size());
        sArray = (String[]) map.get("data");
        assertEquals("A", sArray[0]);
        assertEquals("B", sArray[1]);
        assertEquals("C", sArray[2]);

        List<String> l = new ArrayList<String>();
        l.add("A");
        l.add("B");
        l.add("C");
        map = reflectUtil.map(l, 1, null, false, false, null);
        assertNotNull(map);
        assertEquals(1, map.size());
        List<String> sList = (List<String>) map.get("data");
        assertEquals("A", sList.get(0));
        assertEquals("B", sList.get(1));
        assertEquals("C", sList.get(2));
        
        Map<String,String> sm = new HashMap<String, String>();
        sm.put("A", "AZ");
        sm.put("B", "BZ");
        sm.put("C", "CZ");
        map = reflectUtil.map(sm, 1, null, false, false, null);
        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals("AZ", map.get("A"));
        assertEquals("BZ", map.get("B"));
        assertEquals("CZ", map.get("C"));
        
        
        // test handling of simple things like strings and the like
        map = reflectUtil.map("AZ", 1, null, false, false, null);
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("AZ", map.get("data"));
    }

}
