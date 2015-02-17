/**
 * $Id: DeepUtilsTest.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/DeepUtilsTest.java $
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.azeckoski.reflectutils.classes.TestBean;
import org.azeckoski.reflectutils.classes.TestCollections;
import org.azeckoski.reflectutils.classes.TestCompound;
import org.azeckoski.reflectutils.classes.TestDateSpecial;
import org.azeckoski.reflectutils.classes.TestEntity;
import org.azeckoski.reflectutils.classes.TestNesting;
import org.azeckoski.reflectutils.classes.TestPea;
import org.azeckoski.reflectutils.classes.TestUltraNested;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;

/**
 * Testing the deep utils
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class DeepUtilsTest extends TestCase {

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#deepClone(java.lang.Object, int, java.lang.String[])}.
     */
    public void testClone() {
        DeepUtils deepUtils = new DeepUtils();

        TestBean tb = new TestBean();
        tb.setMyInt(100);
        tb.setMyString("1000");
        TestBean tbClone = deepUtils.deepClone(tb, 0, null);
        assertNotNull(tbClone);
        assertEquals(tb.getMyInt(), tbClone.getMyInt());
        assertEquals(tb.getMyString(), tbClone.getMyString());

        // test skipping values
        tbClone = deepUtils.deepClone(tb, 0, new String[] {"myInt"});
        assertNotNull(tbClone);
        assertTrue(tb.getMyInt() != tbClone.getMyInt());
        assertEquals(tb.getMyString(), tbClone.getMyString());

        tbClone = deepUtils.deepClone(tb, 5, null);
        assertNotNull(tbClone);
        assertEquals(tb.getMyInt(), tbClone.getMyInt());
        assertEquals(tb.getMyString(), tbClone.getMyString());

        // test cloning maps
        Map<String, Object> sm = new HashMap<String, Object>();
        sm.put("A", "AZ");
        sm.put("B", "BZ");
        sm.put("C", "CZ");
        Map<String, Object> cm = deepUtils.deepClone(sm, 1, null);
        assertNotNull(cm);
        assertEquals(3, cm.size());
        assertEquals("AZ", cm.get("A"));
        assertEquals("BZ", cm.get("B"));
        assertEquals("CZ", cm.get("C"));

        Map<String, Object> cm2 = deepUtils.deepClone(sm, 1, new String[] {"A","C"});
        assertNotNull(cm2);
        assertEquals(1, cm2.size());
        assertEquals("BZ", cm2.get("B"));

        // test cloning collections
        List<String> l = new ArrayList<String>();
        l.add("A");
        l.add("B");
        l.add("C");
        List<String> cl = deepUtils.deepClone(l, 1, null);
        assertNotNull(cl);
        assertEquals(3, cl.size());

        List<TestPea> ltp = new ArrayList<TestPea>();
        ltp.add( new TestPea("A","AZ") );
        ltp.add( new TestPea("B","BZ") );
        List<TestPea> cltp = deepUtils.deepClone(ltp, 1, null);
        assertNotNull(cltp);
        assertEquals(2, cltp.size());
        assertNotSame(ltp.get(0), cltp.get(0)); // object was cloned
        assertEquals(ltp.get(0).id, cltp.get(0).id); // values are same though
        assertEquals(ltp.get(0).entityId, cltp.get(0).entityId);

        // test cloning nested objects
        TestNesting tn = new TestNesting(123, "123", new String[] {"1","2","3"});
        TestNesting ctn = deepUtils.deepClone(tn, 3, null);
        assertNotNull(ctn);
        assertNotSame(ctn, tn);
        assertEquals(ctn.getExtra(), tn.getExtra());
        assertEquals(ctn.getId(), tn.getId());
        assertEquals(ctn.getTitle(), tn.getTitle());
        assertEquals(ctn.getTestBean(), tn.getTestBean());
        assertEquals(ctn.getTestEntity().getId(), tn.getTestEntity().getId());
        assertEquals(ctn.getTestEntity().getBool(), tn.getTestEntity().getBool());

    }

    /**
     * Test method for {@link org.sakaiproject.ReflectUtils.util.reflect.ReflectUtil#deepCopy(java.lang.Object, java.lang.Object, int, java.lang.String[], boolean)}.
     */
    public void testCopy() {
        DeepUtils deepUtils = new DeepUtils();

        TestBean orig = new TestBean();
        orig.setMyInt(100);
        orig.setMyString("1000");
        TestBean dest = new TestBean();
        assertNotSame(orig.getMyInt(), dest.getMyInt());
        assertNotSame(orig.getMyString(), dest.getMyString());
        deepUtils.deepCopy(orig, dest, 0, null, false);
        assertNotNull(dest);
        assertEquals(orig.getMyInt(), dest.getMyInt());
        assertEquals(orig.getMyString(), dest.getMyString());

        dest = new TestBean();
        deepUtils.deepCopy(orig, dest, 0, new String[] {"myInt"}, false);
        assertNotNull(dest);
        assertNotSame(orig.getMyInt(), dest.getMyInt());
        assertEquals(orig.getMyString(), dest.getMyString());

        dest = new TestBean();
        deepUtils.deepCopy(orig, dest, 5, null, true);
        assertNotNull(dest);
        assertEquals(orig.getMyInt(), dest.getMyInt());
        assertEquals(orig.getMyString(), dest.getMyString());
    }

    public void testSimpleCopy() {
        DeepUtils deepUtils = new DeepUtils();

        // copy to immutable fails
        try {
            deepUtils.deepCopy(new TestBean(), "AZ", 0, null, true);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // immutable copy should fail
        String az = "AZ";
        String bz = "BZ";
        try {
            deepUtils.deepCopy(az, bz, 0, null, true);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        // test array copying
        String[] array = new String[3];
        deepUtils.deepCopy(new String[] {"A","B","C"}, array, 0, null, true);
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("B", array[1]);
        assertEquals("C", array[2]);

        // test scalar to array conversion
        array = new String[3];
        deepUtils.deepCopy("A,B,C", array, 0, null, true);
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("A", array[0]);
        assertEquals("B", array[1]);
        assertEquals("C", array[2]);
    }

    public void testPopulate() {
        DeepUtils deepUtils = new DeepUtils();
        List<String> results = null;
        Map<String, Object> properties = new HashMap<String, Object>();

        // empty should be ok and should not change anything
        TestBean target = new TestBean();
        results = deepUtils.populate(target, properties);
        assertNotNull(results);
        assertEquals(0, results.size());
        assertNotNull(target);
        assertEquals(0, target.getMyInt());
        assertEquals("woot", target.getMyString());

        // non matching fields should be ok
        properties.put("xxxxxxx", "xxxxxx");
        properties.put("yyyyyyy", 1000000);
        results = deepUtils.populate(target, properties);
        assertNotNull(results);
        assertEquals(0, results.size());
        assertNotNull(target);
        assertEquals(0, target.getMyInt());
        assertEquals("woot", target.getMyString());

        // strings should be ok
        properties.put("myInt", "100");
        properties.put("myString", "NEW");
        results = deepUtils.populate(target, properties);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(target);
        assertEquals(100, target.getMyInt());
        assertEquals("NEW", target.getMyString());

        // string arrays should be ok also
        properties.put("myInt", new String[] {"1000"});
        properties.put("myString", new String[] {"OLD","BLUE"});
        results = deepUtils.populate(target, properties);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(target);
        assertEquals(1000, target.getMyInt());
        assertEquals("OLD,BLUE", target.getMyString());

        // objects
        properties.put("myInt", new Long(222));
        properties.put("myString", 55555);
        results = deepUtils.populate(target, properties);
        assertNotNull(results);
        assertEquals(2, results.size());
        assertNotNull(target);
        assertEquals(222, target.getMyInt());
        assertEquals("55555", target.getMyString());
    }

    public void testPopulateFromParams() {
        DeepUtils deepUtils = new DeepUtils();
        List<String> results = null;
        Map<String, String[]> properties = new HashMap<String, String[]>();

        TestEntity target = new TestEntity();

        properties.put("id", new String[] {"1000"});
        properties.put("extra", new String[] {"OLD"});
        properties.put("sArray", new String[] {"AA","BB","CC"});
        results = deepUtils.populateFromParams(target, properties);
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
        DeepUtils deepUtils = new DeepUtils();
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

        results = deepUtils.populateFromParams(target, properties);
        assertNotNull(results);
        assertEquals(4, results.size());
        assertNotNull(target);
        assertEquals("1000", target.id);
        assertEquals(nowLong, target.longing);
        assertEquals(now, target.date);
        assertEquals(nowCal, target.calendar);
    }

    @SuppressWarnings("unchecked")
    public void testObjectToMap() {
        DeepUtils deepUtils = new DeepUtils();
        Map<String,Object> map = null;
        Map<String,Object> m = null;

        map = deepUtils.deepMap(new TestBean(), 0, null, false, false, null);
        assertNotNull(map);
        assertEquals(0, map.get("myInt"));
        assertEquals("woot", map.get("myString"));

        map = deepUtils.deepMap(new TestPea(), 0, null, false, false, null);
        assertNotNull(map);
        assertEquals("id", map.get("id"));
        assertEquals("EID", map.get("entityId"));

        map = deepUtils.deepMap(new TestNesting(), 10, null, false, false, null);
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

        map = deepUtils.deepMap(new TestUltraNested("az",new TestNesting()), 10, null, false, false, null);
        assertNotNull(map);
        assertFalse(map.containsKey("id"));
        assertEquals("az", map.get("title"));
        m = (Map<String, Object>) map.get("testNesting"); 
        assertNotNull(m);
        assertEquals(9, m.size());

        // test ignore nulls
        map = deepUtils.deepMap(new TestNesting(), 10, null, true, false, null);
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
        map = deepUtils.deepMap( new String[] {"A","B","C"} , 1, null, false, false, null);
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
        map = deepUtils.deepMap(l, 1, null, false, false, null);
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
        map = deepUtils.deepMap(sm, 1, null, false, false, null);
        assertNotNull(map);
        assertEquals(3, map.size());
        assertEquals("AZ", map.get("A"));
        assertEquals("BZ", map.get("B"));
        assertEquals("CZ", map.get("C"));
        
        
        // test handling of simple things like strings and the like
        map = deepUtils.deepMap("AZ", 1, null, false, false, null);
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("AZ", map.get("data"));
    }

    public void testPopulateFromNestedMap() {
        DeepUtils deepUtils = new DeepUtils();

        Map<String, Object> m = new ArrayOrderedMap<String, Object>();
        m.put("A", "AZ");
        m.put("id", "999");
        m.put("entityId", "AZ");

        TestPea tp = new TestPea();
        deepUtils.populate(tp, m);
        assertEquals("999", tp.id);
        assertEquals("AZ", tp.entityId);

        TestCompound tc = new TestCompound();
        m.put("myField", "F1");
        m.put("fieldInt", "123");
        m.put("myInt", "234");
        m.put("myString", "string");
        deepUtils.populate(tc, m);
        assertEquals("F1", tc.myField);
        assertEquals(new Integer(123), tc.fieldInt);
        assertEquals(234, tc.getMyInt());
        assertEquals("string", tc.getMyString());

        Map<String,String> sm = new HashMap<String, String>();
        sm.put("A", "AZ");
        sm.put("B", "BZ");
        sm.put("C", "CZ");

        List<String> l = new ArrayList<String>();
        l.add("A");
        l.add("B");
        l.add("C");

        String[] sa = new String[] {"A","B","C"};

        Set<String> set = new HashSet<String>();
        set.add("A");
        set.add("B");
        set.add("C");

        TestCollections tcoll = new TestCollections(false);
        m.clear();
        m.put("map", sm);
        m.put("list", l);
        m.put("array", sa);
        m.put("set", set);
        deepUtils.populate(tcoll, m);
        assertNotNull(tcoll.array);
        assertNotNull(tcoll.list);
        assertNotNull(tcoll.map);
        assertNotNull(tcoll.set);
        assertEquals(3, tcoll.array.length);
        assertEquals(3, tcoll.list.size());
        assertEquals(3, tcoll.map.size());
        assertEquals(3, tcoll.set.size());

        tcoll = new TestCollections(true);
        deepUtils.populate(tcoll, m);
        assertNotNull(tcoll.array);
        assertNotNull(tcoll.list);
        assertNotNull(tcoll.map);
        assertNotNull(tcoll.set);
        assertEquals(10, tcoll.array.length);
        assertEquals(3, tcoll.list.size());
        assertEquals(3, tcoll.map.size());
        assertEquals(3, tcoll.set.size());
    }

}
