/**
 * $Id: ClassFieldsTest.java 129 2014-03-18 23:25:36Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/ClassFieldsTest.java $
 * ClassFieldsTest.java - genericdao - May 18, 2008 10:07:01 PM - azeckoski
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

import junit.framework.TestCase;
import org.azeckoski.reflectutils.ClassFields.FieldFindMode;
import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.annotations.*;
import org.azeckoski.reflectutils.classes.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the various methods used to analyze a class and cache the information
 * about fields and annotations
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
@SuppressWarnings("unchecked")
public class ClassFieldsTest extends TestCase {

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#ClassFields(java.lang.Class, java.lang.reflect.Method[], java.lang.reflect.Method[], java.lang.reflect.Field[])}.
     */
    public void testClassFieldsClassOfQMethodArrayMethodArrayFieldArray() {
        ClassFields<?> cf = null;

        Method[] getters = null;
        Method[] setters = null;
        try {
            Method g1 = TestBean.class.getMethod("getMyInt", new Class<?>[] {});
            Method s1 = TestBean.class.getMethod("setMyInt", new Class<?>[] {int.class});
            Method g2 = TestBean.class.getMethod("getMyString", new Class<?>[] {});
            Method s2 = TestBean.class.getMethod("setMyString", new Class<?>[] {String.class});
            getters = new Method[] { g1, g2 };
            setters = new Method[] { s1, s2 };
        } catch (Exception e) {
            fail("Failed to find method to test");
        }
        assertNotNull(getters);
        assertNotNull(setters);
        Field[] fields = TestCompound.class.getFields();
        cf = new ClassFields<TestCompound>(TestCompound.class, getters, setters, fields);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );

        cf = new ClassFields<TestCompound>(TestCompound.class, getters, setters, new Field[] {});
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        // test invalid inputs cause failure
        try {
            cf = new ClassFields(null, getters, setters, null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        getters = new Method[] {};
        try {
            cf = new ClassFields<TestCompound>(TestCompound.class, getters, setters, null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#ClassFields(java.lang.Class, java.beans.PropertyDescriptor[], java.lang.reflect.Field[])}.
     */
    public void testClassFieldsClassOfQPropertyDescriptorArrayFieldArray() {
        ClassFields<?> cf = null;

        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(TestBean.class);
        } catch (IntrospectionException e) {
            fail(e.getMessage());
        }
        assertNotNull(beanInfo);
        if (beanInfo == null) { throw new NullPointerException(); } // lame but avoids the warnings
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        cf = new ClassFields(TestBean.class, pds, new Field[] {});
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(TestCompound.class);
        } catch (IntrospectionException e) {
            fail(e.getMessage());
        }
        assertNotNull(beanInfo);
        if (beanInfo == null) { throw new NullPointerException(); } // lame but avoids the warnings
        pds = beanInfo.getPropertyDescriptors();
        Field[] fields = TestCompound.class.getFields();
        cf = new ClassFields(TestBean.class, pds, fields);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );

        // test invalid inputs cause failure
        try {
            cf = new ClassFields(null, pds, fields);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#ClassFields(java.lang.Class, boolean)}.
     */
    public void testClassFieldsClassOfQBoolean() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class, FieldFindMode.HYBRID, true, false);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        cf = new ClassFields(TestCompound.class, FieldFindMode.HYBRID, true, false);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );

        // test invalid inputs cause failure
        try {
            cf = new ClassFields(null, FieldFindMode.HYBRID, true, false);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    public void testClassFieldsModes() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        cf = new ClassFields(TestBean.class, FieldFindMode.FIELD);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        cf = new ClassFields(TestBean.class, FieldFindMode.PROPERTY);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        cf = new ClassFields(TestCompound.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );

        cf = new ClassFields(TestCompound.class, FieldFindMode.FIELD);
        assertNotNull(cf);
        assertEquals(5, cf.size());
        assertTrue( cf.isFieldNameValid("prot1") );
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );

        cf = new ClassFields(TestCompound.class, FieldFindMode.PROPERTY);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        cf = new ClassFields(TestEntity.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(6, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("entityId") );
        assertTrue( cf.isFieldNameValid("extra") );
        assertTrue( cf.isFieldNameValid("bool") );
        assertTrue( cf.isFieldNameValid("sArray") );

        cf = new ClassFields(TestImplFour.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(1, cf.size());
        assertTrue( cf.isFieldNameValid("thing") );

        cf = new ClassFields(TestImplOne.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("nullVal") );

        cf = new ClassFields(TestNesting.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(9, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("title") );
        assertTrue( cf.isFieldNameValid("extra") );
        assertTrue( cf.isFieldNameValid("myArray") );
        assertTrue( cf.isFieldNameValid("sList") );
        assertTrue( cf.isFieldNameValid("sMap") );
        assertTrue( cf.isFieldNameValid("testPea") );
        assertTrue( cf.isFieldNameValid("testBean") );
        assertTrue( cf.isFieldNameValid("testEntity") );

        cf = new ClassFields(TestNone.class, FieldFindMode.HYBRID);
        assertNotNull(cf);
        assertEquals(0, cf.size());

        cf = new ClassFields(TestNone.class, FieldFindMode.FIELD);
        assertNotNull(cf);
        assertEquals(0, cf.size());

        cf = new ClassFields(TestNone.class, FieldFindMode.PROPERTY);
        assertNotNull(cf);
        assertEquals(0, cf.size());

        // test invalid inputs cause failure
        try {
            cf = new ClassFields(null, FieldFindMode.HYBRID);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#ClassFields(java.lang.Class)}.
     */
    public void testClassFieldsClassOfQ() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(6, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("entityId") );
        assertTrue( cf.isFieldNameValid("extra") );
        assertTrue( cf.isFieldNameValid("bool") );
        assertTrue( cf.isFieldNameValid("sArray") );
        assertTrue( cf.isFieldNameValid("fieldOnly") );

        cf = new ClassFields(TestImplFour.class);
        assertNotNull(cf);
        assertEquals(1, cf.size());
        assertTrue( cf.isFieldNameValid("thing") );

        cf = new ClassFields(TestImplOne.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("nullVal") );

        cf = new ClassFields(TestNesting.class);
        assertNotNull(cf);
        assertEquals(9, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("title") );
        assertTrue( cf.isFieldNameValid("extra") );
        assertTrue( cf.isFieldNameValid("myArray") );
        assertTrue( cf.isFieldNameValid("sList") );
        assertTrue( cf.isFieldNameValid("sMap") );
        assertTrue( cf.isFieldNameValid("testPea") );
        assertTrue( cf.isFieldNameValid("testBean") );
        assertTrue( cf.isFieldNameValid("testEntity") );

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(0, cf.size());

        cf = new ClassFields(TestUltraNested.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("title") );
        assertTrue( cf.isFieldNameValid("testNesting") );

        cf = new ClassFields(TestExtendBean.class);
        assertNotNull(cf);
        assertEquals(3, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("anotherThing") );

        // test invalid inputs cause failure
        try {
            cf = new ClassFields(null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldClass()}.
     */
    public void testGetFieldClass() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(TestBean.class, cf.getFieldClass());

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(TestPea.class, cf.getFieldClass());

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(TestNone.class, cf.getFieldClass());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(TestCompound.class, cf.getFieldClass());

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(TestEntity.class, cf.getFieldClass());
        assertEquals(6, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("entityId") );
        assertTrue( cf.isFieldNameValid("extra") );
        assertTrue( cf.isFieldNameValid("bool") );
        assertTrue( cf.isFieldNameValid("sArray") );
        assertTrue( cf.isFieldNameValid("fieldOnly") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldNames()}.
     */
    public void testGetFieldNames() {
        ClassFields<?> cf = null;
        List<String> names = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(2, cf.getFieldNames().size());
        names = cf.getFieldNames();
        assertTrue( names.contains("myInt") );
        assertTrue( names.contains("myString") );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(2, cf.getFieldNames().size());
        names = cf.getFieldNames();
        assertTrue( names.contains("id") );
        assertTrue( names.contains("entityId") );

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(0, cf.getFieldNames().size());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(4, cf.getFieldNames().size());
        names = cf.getFieldNames();
        assertTrue( names.contains("myInt") );
        assertTrue( names.contains("myString") );
        assertTrue( names.contains("myField") );
        assertTrue( names.contains("fieldInt") );

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(TestEntity.class, cf.getFieldClass());
        assertEquals(6, cf.getFieldNames().size());
        names = cf.getFieldNames();
        assertTrue( names.contains("id") );
        assertTrue( names.contains("entityId") );
        assertTrue( names.contains("extra") );
        assertTrue( names.contains("bool") );
        assertTrue( names.contains("sArray") );
        assertTrue( names.contains("fieldOnly") );

        cf = new ClassFields(TestGettersOnly.class);
        assertNotNull(cf);
        assertEquals(0, cf.getFieldNames().size());

        cf = new ClassFields(TestSettersOnly.class);
        assertNotNull(cf);
        assertEquals(0, cf.getFieldNames().size());

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        assertEquals(2, cf.getFieldNames().size());
        names = cf.getFieldNames();
        assertTrue( names.contains("myInt") );
        assertTrue( names.contains("myString") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getAllFieldNames()}.
     */
    public void testGetAllFieldNames() {
        ClassFields<?> cf = null;
        List<String> names = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(2, cf.getFieldNames(FieldsFilter.ALL).size());
        names = cf.getFieldNames(FieldsFilter.ALL);
        assertTrue( names.contains("myInt") );
        assertTrue( names.contains("myString") );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(2, cf.getFieldNames(FieldsFilter.ALL).size());
        names = cf.getFieldNames(FieldsFilter.ALL);
        assertTrue( names.contains("id") );
        assertTrue( names.contains("entityId") );

        cf = new ClassFields(TestGettersOnly.class);
        assertNotNull(cf);
        assertEquals(3, cf.getFieldNames(FieldsFilter.ALL).size());
        names = cf.getFieldNames(FieldsFilter.ALL);
        assertTrue( names.contains("int1") );
        assertTrue( names.contains("str2") );
        assertTrue( names.contains("bool3") );

        cf = new ClassFields(TestSettersOnly.class);
        assertNotNull(cf);
        assertEquals(3, cf.getFieldNames(FieldsFilter.ALL).size());
        names = cf.getFieldNames(FieldsFilter.ALL);
        assertTrue( names.contains("int1") );
        assertTrue( names.contains("str2") );
        assertTrue( names.contains("bool3") );

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        assertEquals(5, cf.getFieldNames(FieldsFilter.ALL).size());
        names = cf.getFieldNames(FieldsFilter.ALL);
        assertTrue( names.contains("myInt") );
        assertTrue( names.contains("myString") );
        assertTrue( names.contains("int1") );
        assertTrue( names.contains("str2") );
        assertTrue( names.contains("bool3") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldTypes()}.
     */
    public void testGetFieldTypes() {
        ClassFields<?> cf = null;
        Map<String, Class<?>> types = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(2, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(2, types.size());
        assertEquals(String.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(0, types.size());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(4, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));
        assertEquals(String.class, types.get("myField"));
        assertEquals(Integer.class, types.get("fieldInt"));

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(6, types.size());
        assertEquals(Long.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));
        assertEquals(String.class, types.get("extra"));
        assertEquals(Boolean.class, types.get("bool"));
        assertEquals(String[].class, types.get("sArray"));
        assertEquals(String.class, types.get("fieldOnly"));

        cf = new ClassFields(TestGettersOnly.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(0, types.size());

        cf = new ClassFields(TestSettersOnly.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(0, types.size());

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(2, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));

        cf = new ClassFields(TestPeaAndSetters.class);
        assertNotNull(cf);
        types = cf.getFieldTypes();
        assertEquals(2, types.size());
        assertEquals(String.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));
    }


    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldTypes(FieldsFilter.WRITEABLE)}.
     */
    public void testGetAllSettableFieldTypes() {
        ClassFields<?> cf = null;
        Map<String, Class<?>> types = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(2, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(2, types.size());
        assertEquals(String.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(0, types.size());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(4, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));
        assertEquals(String.class, types.get("myField"));
        assertEquals(Integer.class, types.get("fieldInt"));

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(6, types.size());
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(Long.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));
        assertEquals(String.class, types.get("extra"));
        assertEquals(Boolean.class, types.get("bool"));
        assertEquals(String[].class, types.get("sArray"));
        assertEquals(String.class, types.get("fieldOnly"));

        cf = new ClassFields(TestGettersOnly.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(0, types.size());

        cf = new ClassFields(TestSettersOnly.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(3, types.size());
        assertEquals(int.class, types.get("int1"));
        assertEquals(String.class, types.get("str2"));
        assertEquals(boolean.class, types.get("bool3"));

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(2, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));

        cf = new ClassFields(TestPeaAndSetters.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(5, types.size());
        assertEquals(String.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));
        assertEquals(int.class, types.get("int1"));
        assertEquals(String.class, types.get("str2"));
        assertEquals(boolean.class, types.get("bool3"));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getAllGettableFieldTypes()}.
     */
    public void testGetAllGettableFieldTypes() {
        ClassFields<?> cf = null;
        Map<String, Class<?>> types = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(2, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(2, types.size());
        assertEquals(String.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(0, types.size());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(4, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));
        assertEquals(String.class, types.get("myField"));
        assertEquals(Integer.class, types.get("fieldInt"));

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(8, types.size());
        assertEquals(Long.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));
        assertEquals(String.class, types.get("extra"));
        assertEquals(Boolean.class, types.get("bool"));
        assertEquals(String[].class, types.get("sArray"));
        assertEquals(String.class, types.get("prefix"));
        assertEquals(String.class, types.get("fieldOnly"));
        assertEquals(String.class, types.get("transStr"));

        cf = new ClassFields(TestGettersOnly.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(3, types.size());
        assertEquals(int.class, types.get("int1"));
        assertEquals(String.class, types.get("str2"));
        assertEquals(boolean.class, types.get("bool3"));

        cf = new ClassFields(TestSettersOnly.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(0, types.size());

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(5, types.size());
        assertEquals(int.class, types.get("myInt"));
        assertEquals(String.class, types.get("myString"));
        assertEquals(int.class, types.get("int1"));
        assertEquals(String.class, types.get("str2"));
        assertEquals(boolean.class, types.get("bool3"));

        cf = new ClassFields(TestPeaAndSetters.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(2, types.size());
        assertEquals(String.class, types.get("id"));
        assertEquals(String.class, types.get("entityId"));
    }

    public void testGetFieldTypesFilter() {
        ClassFields<?> cf = null;
        Map<String, Class<?>> types = null;

        cf = new ClassFields(TestFilter.class);
        assertNotNull(cf);
        types = cf.getFieldTypes(FieldsFilter.ALL);
        assertEquals(6, types.size());
        assertEquals(String.class, types.get("field"));
        assertEquals(String.class, types.get("fin"));
        assertEquals(String.class, types.get("property"));
        assertEquals(String.class, types.get("read"));
        assertEquals(String.class, types.get("trans"));
        assertEquals(String.class, types.get("write"));

        types = cf.getFieldTypes(FieldsFilter.COMPLETE);
        assertEquals(3, types.size());
        assertEquals(String.class, types.get("field"));
        assertEquals(String.class, types.get("property"));
        assertEquals(String.class, types.get("trans"));

        types = cf.getFieldTypes(FieldsFilter.READABLE);
        assertEquals(5, types.size());
        assertEquals(String.class, types.get("field"));
        assertEquals(String.class, types.get("fin"));
        assertEquals(String.class, types.get("property"));
        assertEquals(String.class, types.get("read"));
        assertEquals(String.class, types.get("trans"));

        types = cf.getFieldTypes(FieldsFilter.SERIALIZABLE);
        assertEquals(4, types.size());
        assertEquals(String.class, types.get("field"));
        assertEquals(String.class, types.get("fin"));
        assertEquals(String.class, types.get("property"));
        assertEquals(String.class, types.get("read"));

        types = cf.getFieldTypes(FieldsFilter.WRITEABLE);
        assertEquals(4, types.size());
        assertEquals(String.class, types.get("field"));
        assertEquals(String.class, types.get("property"));
        assertEquals(String.class, types.get("trans"));
        assertEquals(String.class, types.get("write"));

    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#size()}.
     */
    public void testSize() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(0, cf.size());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(4, cf.size());

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(6, cf.size());
    }

    public void testSizeFilter() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(4, cf.size(FieldsFilter.ALL));
        assertEquals(4, cf.size(FieldsFilter.COMPLETE));
        assertEquals(4, cf.size(FieldsFilter.READABLE));
        assertEquals(4, cf.size(FieldsFilter.SERIALIZABLE));
        assertEquals(4, cf.size(FieldsFilter.WRITEABLE));

        cf = new ClassFields(TestTransientFinal.class);
        assertNotNull(cf);
        assertEquals(8, cf.size(FieldsFilter.ALL));
        assertEquals(4, cf.size(FieldsFilter.COMPLETE));
        assertEquals(8, cf.size(FieldsFilter.READABLE));
        assertEquals(5, cf.size(FieldsFilter.SERIALIZABLE));
        assertEquals(4, cf.size(FieldsFilter.SERIALIZABLE_FIELDS));
        assertEquals(4, cf.size(FieldsFilter.WRITEABLE));

        cf = new ClassFields(TestFilter.class);
        assertNotNull(cf);
        assertEquals(6, cf.size(FieldsFilter.ALL));
        assertEquals(3, cf.size(FieldsFilter.COMPLETE));
        assertEquals(5, cf.size(FieldsFilter.READABLE));
        assertEquals(4, cf.size(FieldsFilter.SERIALIZABLE));
        assertEquals(5, cf.size(FieldsFilter.SERIALIZABLE_FIELDS));
        assertEquals(4, cf.size(FieldsFilter.WRITEABLE));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#isEmpty()}.
     */
    public void testIsEmpty() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertFalse(cf.isEmpty());

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertFalse(cf.isEmpty());

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertTrue(cf.isEmpty());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertFalse(cf.isEmpty());

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertFalse(cf.isEmpty());
    }

    public void testIsEmptyFilter() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertFalse( cf.isEmpty(FieldsFilter.ALL));
        assertFalse( cf.isEmpty(FieldsFilter.COMPLETE));
        assertFalse( cf.isEmpty(FieldsFilter.READABLE));
        assertFalse( cf.isEmpty(FieldsFilter.SERIALIZABLE));
        assertFalse( cf.isEmpty(FieldsFilter.WRITEABLE));

        cf = new ClassFields(TestTransientFinal.class);
        assertNotNull(cf);
        assertFalse( cf.isEmpty(FieldsFilter.ALL));
        assertFalse( cf.isEmpty(FieldsFilter.COMPLETE));
        assertFalse( cf.isEmpty(FieldsFilter.READABLE));
        assertFalse( cf.isEmpty(FieldsFilter.SERIALIZABLE));
        assertFalse( cf.isEmpty(FieldsFilter.WRITEABLE));

        cf = new ClassFields(TestFilter.class);
        assertNotNull(cf);
        assertFalse( cf.isEmpty(FieldsFilter.ALL));
        assertFalse( cf.isEmpty(FieldsFilter.COMPLETE));
        assertFalse( cf.isEmpty(FieldsFilter.READABLE));
        assertFalse( cf.isEmpty(FieldsFilter.SERIALIZABLE));
        assertFalse( cf.isEmpty(FieldsFilter.WRITEABLE));
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#isFieldNameValid(java.lang.String)}.
     */
    public void testIsFieldNameValid() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertFalse( cf.isFieldNameValid("myint") );
        assertFalse( cf.isFieldNameValid("mystring") );
        assertFalse( cf.isFieldNameValid("XXXXXX") );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("entityId") );
        assertFalse( cf.isFieldNameValid("ID") );
        assertFalse( cf.isFieldNameValid("mystring") );
        assertFalse( cf.isFieldNameValid("XXXXXX") );

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertTrue( cf.isFieldNameValid("myField") );
        assertTrue( cf.isFieldNameValid("fieldInt") );
        assertFalse( cf.isFieldNameValid("myint") );
        assertFalse( cf.isFieldNameValid("mystring") );
        assertFalse( cf.isFieldNameValid("XXXXXX") );

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(6, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("entityId") );
        assertTrue( cf.isFieldNameValid("extra") );
        assertTrue( cf.isFieldNameValid("bool") );
        assertTrue( cf.isFieldNameValid("sArray") );
        assertFalse( cf.isFieldNameValid("entityid") );
        assertFalse( cf.isFieldNameValid("sarray") );
        assertFalse( cf.isFieldNameValid("XXXXXX") );

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(0, cf.size());
        assertFalse( cf.isFieldNameValid("XXXXXX") );
        assertFalse( cf.isFieldNameValid("") );
        assertFalse( cf.isFieldNameValid(null) );

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt") );
        assertTrue( cf.isFieldNameValid("myString") );
        assertFalse( cf.isFieldNameValid("int1") );
        assertFalse( cf.isFieldNameValid("str2") );
        assertFalse( cf.isFieldNameValid("bool3") );
        assertFalse( cf.isFieldNameValid("myint") );
        assertFalse( cf.isFieldNameValid("mystring") );
        assertFalse( cf.isFieldNameValid("XXXXXX") );

        cf = new ClassFields(TestPeaAndSetters.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("id") );
        assertTrue( cf.isFieldNameValid("entityId") );
        assertFalse( cf.isFieldNameValid("int1") );
        assertFalse( cf.isFieldNameValid("str2") );
        assertFalse( cf.isFieldNameValid("bool3") );
        assertFalse( cf.isFieldNameValid("ID") );
        assertFalse( cf.isFieldNameValid("mystring") );
        assertFalse( cf.isFieldNameValid("XXXXXX") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#isFieldNameValid(java.lang.String)}.
     */
    public void testIsAnyFieldNameValid() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("myString", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("myint", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("mystring", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("id", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("entityId", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("ID", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("mystring", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(4, cf.size());
        assertTrue( cf.isFieldNameValid("myInt", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("myString", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("myField", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("fieldInt", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("myint", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("mystring", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(6, cf.size());
        assertTrue( cf.isFieldNameValid("id", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("entityId", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("extra", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("bool", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("sArray", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("entityid", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("sarray", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(0, cf.size());
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid(null) );

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("myInt", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("myString", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("int1", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("str2", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("bool3", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("myint", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("mystring", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );

        cf = new ClassFields(TestPeaAndSetters.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertTrue( cf.isFieldNameValid("id", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("entityId", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("int1", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("str2", FieldsFilter.ALL) );
        assertTrue( cf.isFieldNameValid("bool3", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("ID", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("mystring", FieldsFilter.ALL) );
        assertFalse( cf.isFieldNameValid("XXXXXX", FieldsFilter.ALL) );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldType(java.lang.String)}.
     */
    public void testGetFieldType() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(int.class, cf.getFieldType("myInt"));
        assertEquals(String.class, cf.getFieldType("myString"));
        assertEquals(String.class, cf.getFieldType("myField"));
        assertEquals(Integer.class, cf.getFieldType("fieldInt"));

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(Long.class, cf.getFieldType("id"));
        assertEquals(String.class, cf.getFieldType("entityId"));
        assertEquals(String.class, cf.getFieldType("extra"));
        assertEquals(Boolean.class, cf.getFieldType("bool"));
        assertEquals(String[].class, cf.getFieldType("sArray"));

        cf = new ClassFields(TestNesting.class);
        assertNotNull(cf);
        assertEquals(int.class, cf.getFieldType("id") );
        assertEquals(String.class, cf.getFieldType("title") );
        assertEquals(String.class, cf.getFieldType("extra") );
        assertEquals(String[].class, cf.getFieldType("myArray") );
        assertEquals(List.class, cf.getFieldType("sList") );
        assertEquals(Map.class, cf.getFieldType("sMap") );
        assertEquals(TestBean.class, cf.getFieldType("testBean") );
        assertEquals(TestEntity.class, cf.getFieldType("testEntity") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getClassProperty(java.lang.String)}.
     */
    public void testGetFieldProperty() {
        ClassFields<?> cf = null;
        ClassProperty cp = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("myInt");
        assertNotNull(cp);
        assertEquals("myInt", cp.getFieldName());
        assertEquals(int.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("id");
        assertNotNull(cp);
        assertEquals("id", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertTrue( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("myInt");
        assertNotNull(cp);
        assertEquals("myInt", cp.getFieldName());
        assertEquals(int.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("myField");
        assertNotNull(cp);
        assertEquals("myField", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertTrue( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cf = new ClassFields(TestNesting.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("id");
        assertNotNull(cp);
        assertEquals("id", cp.getFieldName());
        assertEquals(int.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("myArray");
        assertNotNull(cp);
        assertEquals("myArray", cp.getFieldName());
        assertEquals(String[].class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertTrue( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertTrue( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("sList");
        assertNotNull(cp);
        assertEquals("sList", cp.getFieldName());
        assertEquals(List.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertTrue( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("sMap");
        assertNotNull(cp);
        assertEquals("sMap", cp.getFieldName());
        assertEquals(Map.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertTrue( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

    }

    public void testPartialComplete() {
        ClassFields<?> cf = null;
        ClassProperty cp = null;

        cf = new ClassFields(TestBeanAndGetters.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("myInt");
        assertNotNull(cp);
        assertEquals("myInt", cp.getFieldName());
        assertEquals(int.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertTrue( cp.isField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertFalse( cp.isPublicField() );
        assertTrue( cp.isPublicGettable() );
        assertTrue( cp.isPublicSettable() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("int1");
        assertNotNull(cp);
        assertEquals("int1", cp.getFieldName());
        assertEquals(int.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertFalse( cp.isComplete() );
        assertTrue( cp.isField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertTrue( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertFalse( cp.isPublicField() );
        assertTrue( cp.isPublicGettable() );
        assertFalse( cp.isPublicSettable() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cf = new ClassFields(TestPeaAndSetters.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("id");
        assertNotNull(cp);
        assertEquals("id", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertTrue( cp.isField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertTrue( cp.isPublicField() );
        assertTrue( cp.isPublicGettable() );
        assertTrue( cp.isPublicSettable() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("str2");
        assertNotNull(cp);
        assertEquals("str2", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertFalse( cp.isComplete() );
        assertTrue( cp.isField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertTrue( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isPublicGettable() );
        assertTrue( cp.isPublicSettable() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );
    }


    public void testTransientFinal() {
        ClassFields<?> cf = null;
        ClassProperty cp = null;

        cf = new ClassFields(TestTransientFinal.class);
        assertNotNull(cf);
        cp = cf.getClassProperty("field1");
        assertNotNull(cp);
        assertEquals("field1", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertTrue( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("transField2");
        assertNotNull(cp);
        assertEquals("transField2", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertTrue( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertTrue( cp.isTransient() );

        cp = cf.getClassProperty("finalStr3");
        assertNotNull(cp);
        assertEquals("finalStr3", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNull(cp.getGetter());
        assertNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertFalse( cp.isComplete() );
        assertTrue( cp.isPublicField() );
        assertTrue( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertTrue( cp.isPartial() );
        assertFalse( cp.isProperty() );
        assertFalse( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("item1");
        assertNotNull(cp);
        assertEquals("item1", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertFalse( cp.isTransient() );

        cp = cf.getClassProperty("item2");
        assertNotNull(cp);
        assertEquals("item2", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertTrue( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertFalse( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertFalse( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertTrue( cp.isSettable() );
        assertTrue( cp.isTransient() );

        cp = cf.getClassProperty("item3");
        assertNotNull(cp);
        assertEquals("item3", cp.getFieldName());
        assertEquals(String.class, cp.getType());
        assertNotNull(cp.getGetter());
        assertNotNull(cp.getSetter());
        assertNotNull(cp.getField());
        assertFalse( cp.isArray() );
        assertFalse( cp.isComplete() );
        assertFalse( cp.isPublicField() );
        assertTrue( cp.isFinal() );
        assertTrue( cp.isGettable() );
        assertFalse( cp.isIndexed() );
        assertFalse( cp.isMapped() );
        assertTrue( cp.isPartial() );
        assertTrue( cp.isProperty() );
        assertFalse( cp.isSettable() );
        assertFalse( cp.isTransient() );
    }

    public void testGetAllClassProperties() {
        ClassFields<?> cf = null;
        Map<String, ClassProperty> m = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        m = cf.getAllClassProperties();
        assertNotNull(m);
        assertEquals(2, m.size());

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        m = cf.getAllClassProperties();
        assertNotNull(m);
        assertEquals(2, m.size());

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        m = cf.getAllClassProperties();
        assertNotNull(m);
        assertEquals(0, m.size());

        cf = new ClassFields(TestTransientFinal.class);
        assertNotNull(cf);
        m = cf.getAllClassProperties();
        assertNotNull(m);
        assertEquals(8, m.size());
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getClassAnnotations()}.
     */
    public void testGetClassAnnotations() {
        ClassFields<?> cf = null;
        Set<Annotation> s = null;
        Annotation a = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(1, cf.getClassAnnotations().size());
        s = cf.getClassAnnotations();
        a = s.iterator().next();
        assertEquals(TestAnnoteClass1.class, a.annotationType());

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(1, cf.getClassAnnotations().size());
        s = cf.getClassAnnotations();
        a = s.iterator().next();
        assertEquals(TestAnnoteClass2.class, a.annotationType());
        assertEquals("TEST", ((TestAnnoteClass2)a).value());

        cf = new ClassFields(TestNone.class);
        assertNotNull(cf);
        assertEquals(0, cf.getClassAnnotations().size());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(2, cf.getClassAnnotations().size());
        s = cf.getClassAnnotations();
        for (Annotation an : s) {
            assertTrue(an.annotationType().equals(TestAnnoteClass1.class) || an.annotationType().equals(TestAnnoteClass2.class));
        }

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(0, cf.getClassAnnotations().size());

        cf = new ClassFields(TestExtendBean.class);
        assertNotNull(cf);
        assertEquals(1, cf.getClassAnnotations().size());
        s = cf.getClassAnnotations();
        for (Annotation an : s) {
            an.annotationType().equals(TestAnnoteClass2.class);
        }
    }

    /**
     * Test getting a single annotation from a class by type
     */
    public void testGetClassAnnotation() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        TestAnnoteClass1 tac1 = cf.getClassAnnotation(TestAnnoteClass1.class);
        assertNotNull(tac1);

        TestAnnoteClass2 tac2 = cf.getClassAnnotation(TestAnnoteClass2.class);
        assertNull(tac2);

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        tac1 = cf.getClassAnnotation(TestAnnoteClass1.class);
        assertNull(tac1);

        tac2 = cf.getClassAnnotation(TestAnnoteClass2.class);
        assertNotNull(tac2);

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        tac1 = cf.getClassAnnotation(TestAnnoteClass1.class);
        assertNotNull(tac1);

        tac2 = cf.getClassAnnotation(TestAnnoteClass2.class);
        assertNotNull(tac2);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldAnnotations(java.lang.String)}.
     */
    public void testGetFieldAnnotations() {
        ClassFields<?> cf = null;
        Set<Annotation> s = null;
        Annotation a = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertEquals(1, cf.getFieldAnnotations("myInt").size());
        s = cf.getFieldAnnotations("myInt");
        a = s.iterator().next();
        assertEquals(TestAnnote.class, a.annotationType());
        assertEquals(1, cf.getFieldAnnotations("myString").size());
        s = cf.getFieldAnnotations("myString");
        a = s.iterator().next();
        assertEquals(TestAnnoteField1.class, a.annotationType());

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals(0, cf.getFieldAnnotations("id").size());
        assertEquals(1, cf.getFieldAnnotations("entityId").size());
        s = cf.getFieldAnnotations("entityId");
        a = s.iterator().next();
        assertEquals(TestAnnote.class, a.annotationType());

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals(0, cf.getFieldAnnotations("fieldInt").size());
        assertEquals(1, cf.getFieldAnnotations("myString").size());
        s = cf.getFieldAnnotations("myString");
        a = s.iterator().next();
        assertEquals(TestAnnoteField1.class, a.annotationType());
        assertEquals(2, cf.getFieldAnnotations("myInt").size());
        assertEquals(2, cf.getFieldAnnotations("myField").size());

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertEquals(0, cf.getFieldAnnotations("id").size());
        assertEquals(1, cf.getFieldAnnotations("entityId").size());
        s = cf.getFieldAnnotations("entityId");
        a = s.iterator().next();
        assertEquals(TestAnnote.class, a.annotationType());
        assertEquals(3, cf.getFieldAnnotations("extra").size());
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldAnnotation(java.lang.Class, java.lang.String)}.
     */
    public void testGetFieldAnnotation() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertNotNull(cf);
        assertNotNull( cf.getFieldAnnotation(TestAnnote.class, "myInt") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField1.class, "myInt") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField1.class, "myString") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField2.class, "myString") );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertNull( cf.getFieldAnnotation(TestAnnoteField1.class, "id") );
        assertNotNull( cf.getFieldAnnotation(TestAnnote.class, "entityId") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField1.class, "entityId") );

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertNotNull( cf.getFieldAnnotation(TestAnnote.class, "myInt") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField1.class, "myInt") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField2.class, "myInt") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField1.class, "myString") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField2.class, "myString") );
        assertNull( cf.getFieldAnnotation(TestAnnote.class, "fieldInt") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField1.class, "fieldInt") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField2.class, "fieldInt") );
        assertNull( cf.getFieldAnnotation(TestAnnote.class, "myField") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField1.class, "myField") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField2.class, "myField") );

        cf = new ClassFields(TestEntity.class);
        assertNotNull(cf);
        assertNull( cf.getFieldAnnotation(TestAnnote.class, "id") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField1.class, "id") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField2.class, "id") );
        assertNotNull( cf.getFieldAnnotation(TestAnnote.class, "entityId") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField1.class, "entityId") );
        assertNull( cf.getFieldAnnotation(TestAnnoteField2.class, "entityId") );
        assertNotNull( cf.getFieldAnnotation(TestAnnote.class, "extra") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField1.class, "extra") );
        assertNotNull( cf.getFieldAnnotation(TestAnnoteField2.class, "extra") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#getFieldNameByAnnotation(java.lang.Class)}.
     */
    public void testGetFieldNameByAnnotation() {
        ClassFields<?> cf = null;

        cf = new ClassFields(TestBean.class);
        assertEquals("myInt", cf.getFieldNameByAnnotation(TestAnnote.class) );
        assertEquals("myString", cf.getFieldNameByAnnotation(TestAnnoteField1.class) );
        assertEquals(null, cf.getFieldNameByAnnotation(TestAnnoteField2.class) );

        cf = new ClassFields(TestPea.class);
        assertNotNull(cf);
        assertEquals("entityId", cf.getFieldNameByAnnotation(TestAnnote.class) );
        assertEquals(null, cf.getFieldNameByAnnotation(TestAnnoteField1.class) );
        assertEquals(null, cf.getFieldNameByAnnotation(TestAnnoteField2.class) );

        cf = new ClassFields(TestCompound.class);
        assertNotNull(cf);
        assertEquals("myInt", cf.getFieldNameByAnnotation(TestAnnote.class) );
        // cannot tell because this annotation is on multiple fields
        //assertEquals(null, cf.getFieldNameByAnnotation(TestAnnoteField1.class) );
        assertEquals("myField", cf.getFieldNameByAnnotation(TestAnnoteField2.class) );

        cf = new ClassFields(TestExtendBean.class);
        assertNotNull(cf);
        assertEquals("myInt", cf.getFieldNameByAnnotation(TestAnnote.class) );
    }


    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#makeFieldNameFromMethod(java.lang.String)}.
     */
    public void testMakeFieldNameFromMethod() {
        String name = null;

        name = ClassFields.makeFieldNameFromMethod("getStuff");
        assertEquals("stuff", name);

        name = ClassFields.makeFieldNameFromMethod("getSomeStuff");
        assertEquals("someStuff", name);

        name = ClassFields.makeFieldNameFromMethod("setStuff");
        assertEquals("stuff", name);

        name = ClassFields.makeFieldNameFromMethod("isStuff");
        assertEquals("stuff", name);

        name = ClassFields.makeFieldNameFromMethod("stuff");
        assertEquals("stuff", name);
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#capitalize(java.lang.String)}.
     */
    public void testCapitalize() {
        assertTrue( ClassFields.capitalize("lower").equals("Lower") );
        assertTrue( ClassFields.capitalize("UPPER").equals("UPPER") );
        assertTrue( ClassFields.capitalize("myStuff").equals("MyStuff") );
        assertTrue( ClassFields.capitalize("MyStuff").equals("MyStuff") );
        assertTrue( ClassFields.capitalize("").equals("") );
        assertTrue( ClassFields.capitalize("m").equals("M") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#unCapitalize(java.lang.String)}.
     */
    public void testUnCapitalize() {
        assertTrue( ClassFields.unCapitalize("lower").equals("lower") );
        assertTrue( ClassFields.unCapitalize("UPPER").equals("uPPER") );
        assertTrue( ClassFields.unCapitalize("MyStuff").equals("myStuff") );
        assertTrue( ClassFields.unCapitalize("myStuff").equals("myStuff") );
        assertTrue( ClassFields.unCapitalize("").equals("") );
        assertTrue( ClassFields.unCapitalize("M").equals("m") );
    }

    /**
     * Test method for {@link org.azeckoski.reflectutils.ClassFields#isGetClassMethod(java.lang.reflect.Method)}.
     */
    public void testIsGetClassMethod() {
        Method m = null;

        try {
            m = TestBean.class.getMethod("getClass", new Class<?>[] {});
            assertTrue( ClassFields.isGetClassMethod(m) );
        } catch (Exception e) {
            fail("Failed to find method to test");
        }

        try {
            m = TestBean.class.getMethod("getMyString", new Class<?>[] {});
            assertFalse( ClassFields.isGetClassMethod(m) );
        } catch (Exception e) {
            fail("Failed to find method to test");
        }
    }

    public void testExcludeFields() {
        ClassFields<?> cf = null;

        cf = new ClassFields<TestExcludeFields>(TestExcludeFields.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertEquals(2, cf.size(FieldsFilter.ALL));
        assertTrue(cf.isFieldNameValid("name"));
        assertTrue(cf.isFieldNameValid("email"));
        assertFalse(cf.isFieldNameValid("password"));
        assertEquals(2, cf.getFieldNames().size());

    }

    public void testExcludeStaticFields() {
        ClassFields<?> cf = null;

        cf = new ClassFields<TestStaticsExclude>(TestStaticsExclude.class);
        assertNotNull(cf);
        assertEquals(2, cf.size());
        assertEquals(2, cf.size(FieldsFilter.ALL));
        assertTrue(cf.isFieldNameValid("name"));
        assertTrue(cf.isFieldNameValid("num"));
        assertFalse(cf.isFieldNameValid("sname"));
        assertFalse(cf.isFieldNameValid("snum"));
        assertFalse(cf.isFieldNameValid("sbool"));
        assertEquals(2, cf.getFieldNames().size());
    }

    public void testIncludeStaticFields() {
        ClassFields<?> cf = null;

        cf = new ClassFields<TestStaticsInclude>(TestStaticsInclude.class);
        assertNotNull(cf);
        assertEquals(5, cf.size());
        assertEquals(5, cf.size(FieldsFilter.ALL));
        assertTrue(cf.isFieldNameValid("name"));
        assertTrue(cf.isFieldNameValid("num"));
        assertTrue(cf.isFieldNameValid("sname"));
        assertTrue(cf.isFieldNameValid("snum"));
        assertTrue(cf.isFieldNameValid("sbool"));
        assertEquals(5, cf.getFieldNames().size());

    }

}
