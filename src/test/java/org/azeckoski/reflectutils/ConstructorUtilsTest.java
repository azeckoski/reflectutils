/**
 * $Id: ConstructorUtilsTest.java 41 2008-11-12 17:36:24Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/ConstructorUtilsTest.java $
 * ConstructorUtilsTest.java - genericdao - Aug 31, 2008 12:06:11 PM - azeckoski
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.TestCase;

import org.azeckoski.reflectutils.classes.TestBean;
import org.azeckoski.reflectutils.classes.TestExtendBean;
import org.azeckoski.reflectutils.classes.TestNesting;
import org.azeckoski.reflectutils.classes.TestNoPubConstructor;
import org.azeckoski.reflectutils.classes.TestPea;
import org.azeckoski.reflectutils.classes.TestUltraNested;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;


/**
 * Testing the constructor utils
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ConstructorUtilsTest extends TestCase {

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassArray(java.lang.Class)}.
     */
    public void testIsClassArray() {
        assertFalse(ConstructorUtils.isClassArray(String.class));
        assertFalse(ConstructorUtils.isClassArray(int.class));
        assertFalse(ConstructorUtils.isClassArray(List.class));
        assertFalse(ConstructorUtils.isClassArray(Set.class));
        assertFalse(ConstructorUtils.isClassArray(Map.class));
        assertFalse(ConstructorUtils.isClassArray(Object.class));
        assertTrue(ConstructorUtils.isClassArray(String[].class));
        assertTrue(ConstructorUtils.isClassArray(int[].class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassPrimitive(java.lang.Class)}.
     */
    public void testIsClassPrimitive() {
        assertFalse(ConstructorUtils.isClassPrimitive(String.class));
        assertTrue(ConstructorUtils.isClassPrimitive(int.class));
        assertFalse(ConstructorUtils.isClassPrimitive(List.class));
        assertFalse(ConstructorUtils.isClassPrimitive(Set.class));
        assertFalse(ConstructorUtils.isClassPrimitive(Map.class));
        assertFalse(ConstructorUtils.isClassPrimitive(Object.class));
        assertFalse(ConstructorUtils.isClassPrimitive(String[].class));
        assertFalse(ConstructorUtils.isClassPrimitive(int[].class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassList(java.lang.Class)}.
     */
    public void testIsClassList() {
        assertFalse(ConstructorUtils.isClassList(String.class));
        assertFalse(ConstructorUtils.isClassList(int.class));
        assertTrue(ConstructorUtils.isClassList(List.class));
        assertFalse(ConstructorUtils.isClassList(Set.class));
        assertFalse(ConstructorUtils.isClassList(Map.class));
        assertFalse(ConstructorUtils.isClassList(Object.class));
        assertFalse(ConstructorUtils.isClassList(String[].class));
        assertFalse(ConstructorUtils.isClassList(int[].class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassCollection(java.lang.Class)}.
     */
    public void testIsClassCollection() {
        assertFalse(ConstructorUtils.isClassCollection(String.class));
        assertFalse(ConstructorUtils.isClassCollection(int.class));
        assertTrue(ConstructorUtils.isClassCollection(List.class));
        assertTrue(ConstructorUtils.isClassCollection(Set.class));
        assertFalse(ConstructorUtils.isClassCollection(Map.class));
        assertFalse(ConstructorUtils.isClassCollection(Object.class));
        assertFalse(ConstructorUtils.isClassCollection(String[].class));
        assertFalse(ConstructorUtils.isClassCollection(int[].class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassMap(java.lang.Class)}.
     */
    public void testIsClassMap() {
        assertFalse(ConstructorUtils.isClassMap(String.class));
        assertFalse(ConstructorUtils.isClassMap(int.class));
        assertFalse(ConstructorUtils.isClassMap(List.class));
        assertFalse(ConstructorUtils.isClassMap(Set.class));
        assertTrue(ConstructorUtils.isClassMap(Map.class));
        assertFalse(ConstructorUtils.isClassMap(Object.class));
        assertFalse(ConstructorUtils.isClassMap(String[].class));
        assertFalse(ConstructorUtils.isClassMap(int[].class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassObjectHolder(java.lang.Class)}.
     */
    public void testIsClassObjectHolder() {
        assertFalse(ConstructorUtils.isClassObjectHolder(String.class));
        assertFalse(ConstructorUtils.isClassObjectHolder(int.class));
        assertTrue(ConstructorUtils.isClassObjectHolder(List.class));
        assertTrue(ConstructorUtils.isClassObjectHolder(Set.class));
        assertTrue(ConstructorUtils.isClassObjectHolder(Map.class));
        assertFalse(ConstructorUtils.isClassObjectHolder(Object.class));
        assertTrue(ConstructorUtils.isClassObjectHolder(String[].class));
        assertTrue(ConstructorUtils.isClassObjectHolder(int[].class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getTypeFromArray(java.lang.Class)}.
     */
    public void testGetTypeFromArray() {
        assertEquals(String.class, ConstructorUtils.getTypeFromArray(String[].class));
        assertEquals(int.class, ConstructorUtils.getTypeFromArray(int[].class));
        assertEquals(String.class, ConstructorUtils.getTypeFromArray(String.class));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getClassFromInterface(java.lang.Class)}.
     */
    @SuppressWarnings("unchecked")
    public void testGetClassFromInterface() {
        assertEquals(Vector.class, ConstructorUtils.getClassFromInterface(List.class));
        assertEquals(HashSet.class, ConstructorUtils.getClassFromInterface(Set.class));
        assertEquals(ArrayOrderedMap.class, ConstructorUtils.getClassFromInterface(Map.class));
        assertEquals(Vector.class, ConstructorUtils.getClassFromInterface(Collection.class));
        assertEquals(Long.class, ConstructorUtils.getClassFromInterface(Long.class));
        assertEquals(Integer.class, ConstructorUtils.getClassFromInterface(Integer.class));
        assertEquals(String.class, ConstructorUtils.getClassFromInterface(String.class));
        assertEquals(String[].class, ConstructorUtils.getClassFromInterface(String[].class));
        assertEquals(int.class, ConstructorUtils.getClassFromInterface(int.class));

        // test special cases
        assertEquals(Vector.class, ConstructorUtils.getClassFromInterface(Arrays.asList(new ArrayList<String>()).getClass()));
        assertEquals(Vector.class, ConstructorUtils.getClassFromInterface(Collections.synchronizedCollection(new ArrayList<String>()).getClass()));
        assertEquals(Vector.class, ConstructorUtils.getClassFromInterface(Collections.synchronizedList(new ArrayList<String>()).getClass()));
        assertEquals(HashSet.class, ConstructorUtils.getClassFromInterface(Collections.synchronizedSet(new HashSet<String>()).getClass()));
        assertEquals(ArrayOrderedMap.class, ConstructorUtils.getClassFromInterface(Collections.synchronizedMap(new HashMap<String,String>()).getClass()));
        assertEquals(TreeSet.class, ConstructorUtils.getClassFromInterface(Collections.synchronizedSortedSet(new TreeSet<String>()).getClass()));
        assertEquals(TreeMap.class, ConstructorUtils.getClassFromInterface(Collections.synchronizedSortedMap(new TreeMap<String,String>()).getClass()));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getImmutableTypes()}.
     */
    public void testGetImmutableTypes() {
        Set<Class<?>> s = ConstructorUtils.getImmutableTypes();
        assertNotNull(s);
        assertTrue(s.size() > 0);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getPrimitiveToWrapper()}.
     */
    public void testGetPrimitiveToWrapper() {
        Map<Class<?>, Class<?>> m = ConstructorUtils.getPrimitiveToWrapper();
        assertNotNull(m);
        assertTrue(m.size() > 0);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getPrimitiveDefaults()}.
     */
    public void testGetPrimitiveDefaults() {
        Map<Class<?>, Object> m = ConstructorUtils.getPrimitiveDefaults();
        assertNotNull(m);
        assertTrue(m.size() > 0);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#isClassSimple(java.lang.Class)}.
     */
    public void testIsClassSimple() {
        assertTrue( ConstructorUtils.isClassSimple(int.class) );
        assertTrue( ConstructorUtils.isClassSimple(String.class) );
        assertTrue( ConstructorUtils.isClassSimple(Date.class) );
        assertTrue( ConstructorUtils.isClassSimple(Boolean.class) );

        assertFalse( ConstructorUtils.isClassSimple(TestBean.class) );
        assertFalse( ConstructorUtils.isClassSimple(TestPea.class) );
        assertFalse( ConstructorUtils.isClassSimple(TestUltraNested.class) );
    }


    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getWrapperToPrimitive()}.
     */
    public void testGetWrapperToPrimitive() {
        Map<Class<?>, Class<?>> m = ConstructorUtils.getWrapperToPrimitive();
        assertNotNull(m);
        assertTrue(m.size() > 0);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#getWrapper(java.lang.Class)}.
     */
    public void testGetWrapper() {
        assertEquals(Integer.class, ConstructorUtils.getWrapper(int.class));
        assertEquals(Integer.class, ConstructorUtils.getWrapper(Integer.class));
        assertEquals(String.class, ConstructorUtils.getWrapper(String.class));
        assertEquals(Boolean.class, ConstructorUtils.getWrapper(boolean.class));
        assertEquals(Boolean.class, ConstructorUtils.getWrapper(Boolean.class));
        assertEquals(Integer[].class, ConstructorUtils.getWrapper(int[].class));
        assertEquals(Integer[].class, ConstructorUtils.getWrapper(Integer[].class));
        assertEquals(null, ConstructorUtils.getWrapper(null));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#classEquals(java.lang.Class, java.lang.Class)}.
     */
    public void testClassEquals() {
        assertTrue( ConstructorUtils.classEquals(int.class, int.class) );
        assertTrue( ConstructorUtils.classEquals(Integer.class, Integer.class) );
        assertTrue( ConstructorUtils.classEquals(int.class, Integer.class) );

        assertTrue( ConstructorUtils.classEquals(int[].class, int[].class) );
        assertTrue( ConstructorUtils.classEquals(Integer[].class, Integer[].class) );
        assertTrue( ConstructorUtils.classEquals(int[].class, Integer[].class) );

        assertTrue( ConstructorUtils.classEquals(TestBean.class, TestBean.class) );
        assertTrue( ConstructorUtils.classEquals(TestExtendBean.class, TestExtendBean.class) );

        assertFalse( ConstructorUtils.classEquals(Object.class, Integer.class) );
        assertFalse( ConstructorUtils.classEquals(Object[].class, Integer.class) );
        assertFalse( ConstructorUtils.classEquals(int.class, boolean.class) );
        assertFalse( ConstructorUtils.classEquals(Integer.class, Boolean.class) );
        assertFalse( ConstructorUtils.classEquals(int[].class, boolean[].class) );
        assertFalse( ConstructorUtils.classEquals(Integer[].class, Boolean[].class) );
        assertFalse( ConstructorUtils.classEquals(int.class, null) );
        assertFalse( ConstructorUtils.classEquals(null, boolean.class) );
        assertFalse( ConstructorUtils.classEquals(TestBean.class, TestExtendBean.class) );
        assertFalse( ConstructorUtils.classEquals(TestExtendBean.class, TestBean.class) );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#classAssignable(java.lang.Class, java.lang.Class)}.
     */
    public void testClassAssignable() {
        assertTrue( ConstructorUtils.classAssignable(int.class, int.class) );
        assertTrue( ConstructorUtils.classAssignable(Integer.class, Integer.class) );
        assertTrue( ConstructorUtils.classAssignable(Integer.class, int.class) );

        assertTrue( ConstructorUtils.classAssignable(int[].class, int[].class) );
        assertTrue( ConstructorUtils.classAssignable(Integer[].class, Integer[].class) );
        assertTrue( ConstructorUtils.classAssignable(Integer[].class, int[].class) );

        assertTrue( ConstructorUtils.classAssignable(TestBean.class, TestBean.class) );
        assertTrue( ConstructorUtils.classAssignable(TestExtendBean.class, TestExtendBean.class) );

        assertTrue( ConstructorUtils.classAssignable(TestExtendBean.class, TestBean.class) );
        assertTrue( ConstructorUtils.classAssignable(Integer.class, Object.class) );
        assertTrue( ConstructorUtils.classAssignable(Integer[].class, Object.class) );
        assertTrue( ConstructorUtils.classAssignable(Integer[].class, Object[].class) );
        assertTrue( ConstructorUtils.classAssignable(Integer.class, Object.class) );

        assertFalse( ConstructorUtils.classAssignable(TestBean.class, TestExtendBean.class) );
        assertFalse( ConstructorUtils.classAssignable(Integer.class, Object[].class) );
        assertFalse( ConstructorUtils.classAssignable(Object[].class, Integer[].class) );
        assertFalse( ConstructorUtils.classAssignable(Object.class, Integer[].class) );
        assertFalse( ConstructorUtils.classAssignable(Object.class, Integer.class) );
        assertFalse( ConstructorUtils.classAssignable(Object[].class, Integer[].class) );
        assertFalse( ConstructorUtils.classAssignable(boolean.class, int.class) );
        assertFalse( ConstructorUtils.classAssignable(Boolean.class, Integer.class) );
        assertFalse( ConstructorUtils.classAssignable(boolean[].class, int[].class) );
        assertFalse( ConstructorUtils.classAssignable(Boolean[].class, Integer[].class) );
        assertFalse( ConstructorUtils.classAssignable(null, int.class) );
        assertFalse( ConstructorUtils.classAssignable(boolean.class, null) );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#constructClass(java.lang.Class)}.
     */
    @SuppressWarnings("unchecked")
    public void testConstructClassClassOfT() {
        ConstructorUtils constructUtils = new ConstructorUtils();

        TestBean testBean = constructUtils.constructClass(TestBean.class);
        assertNotNull(testBean);

        TestNesting testNesting = constructUtils.constructClass(TestNesting.class);
        assertNotNull(testNesting);

        TestUltraNested ultraNested = constructUtils.constructClass(TestUltraNested.class);
        assertNotNull(ultraNested);

        // test constructing the various collections
        ArrayList arrayList = constructUtils.constructClass(ArrayList.class);
        assertNotNull(arrayList);

        HashSet hashSet = constructUtils.constructClass(HashSet.class);
        assertNotNull(hashSet);

        HashMap hashMap = constructUtils.constructClass(HashMap.class);
        assertNotNull(hashMap);

        // test constructing the various collections by interface
        List list = constructUtils.constructClass(List.class);
        assertNotNull(list);

        Set set = constructUtils.constructClass(Set.class);
        assertNotNull(set);

        Map map = constructUtils.constructClass(Map.class);
        assertNotNull(map);

        // now test constructing some simple objects
        String string = constructUtils.constructClass(String.class);
        assertNotNull(string);

        Boolean bool = constructUtils.constructClass(Boolean.class);
        assertNotNull(bool);

        // now for the really simple ones
        int i = constructUtils.constructClass(int.class);
        assertEquals(0, i);

        boolean b = constructUtils.constructClass(boolean.class);
        assertEquals(false, b);

        // arrays
        Integer[] integers = constructUtils.constructClass(Integer[].class);
        assertNotNull(integers);

        TestBean[] testBeans = constructUtils.constructClass(TestBean[].class);
        assertNotNull(testBeans);

        boolean[] bools = constructUtils.constructClass(boolean[].class);
        assertNotNull(bools);

        try {
            constructUtils.constructClass(null);
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ConstructorUtils#constructClass(java.lang.Class, java.lang.Object[])}.
     */
    public void testConstructClassClassOfTObjectArray() {
        ConstructorUtils constructUtils = new ConstructorUtils();

        TestBean testBean = constructUtils.constructClass(TestBean.class, null);
        assertNotNull(testBean);
        assertEquals(0, testBean.getMyInt());
        assertEquals("woot", testBean.getMyString());

        testBean = constructUtils.constructClass(TestBean.class, new Object[] {5});
        assertNotNull(testBean);
        assertEquals(5, testBean.getMyInt());
        assertEquals("woot", testBean.getMyString());

        testBean = constructUtils.constructClass(TestBean.class, new Object[] {5, "AZ"});
        assertNotNull(testBean);
        assertEquals(5, testBean.getMyInt());
        assertEquals("AZ", testBean.getMyString());

        try {
            testBean = constructUtils.constructClass(TestBean.class, new Object[] {"ASD", "QWE"});
            fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testConstructNoPublic() {
        ConstructorUtils constructUtils = new ConstructorUtils();

        TestNoPubConstructor t1 = constructUtils.constructClass(TestNoPubConstructor.class);
        assertNotNull(t1);
        assertEquals("prot", t1.getConstructed());
    }

    public void testSpecialCheck() {
        assertTrue( ConstructorUtils.isClassSpecial(Class.class) );
        assertTrue( ConstructorUtils.isClassSpecial(ClassLoader.class) );
        assertTrue( ConstructorUtils.isClassSpecial(InputStream.class) );

        assertFalse( ConstructorUtils.isClassSpecial(String.class) );
        assertFalse( ConstructorUtils.isClassSpecial(int.class) );
        assertFalse( ConstructorUtils.isClassSpecial(TestBean.class) );
    }
}
