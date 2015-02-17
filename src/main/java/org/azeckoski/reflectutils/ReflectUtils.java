/**
 * $Id: ReflectUtils.java 128 2014-03-18 22:27:33Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ReflectUtils.java $
 * ReflectUtil.java - entity-broker - 24 Aug 2007 6:43:14 PM - azeckoski
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

import org.azeckoski.reflectutils.ClassFields.FieldFindMode;
import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.beanutils.Resolver;
import org.azeckoski.reflectutils.converters.api.Converter;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Reflection utilities and utilities related to working with classes and their fields<br/>
 * <br/>
 * These are built to be compatible with <a href="http://commons.apache.org/beanutils/">Apache Commons BeanUtils</a>
 * and the nesting structure works the same, refer to the apache BeanUtils project docs for details<br/>
 * <br/>
 * Handles field operations for properties (getters and setters), partial properties (only getter or only setter), and fields.
 * This improves upon the BeanUtils limitation of handling only properties or the Google utilities limitation of handling
 * only fields.<br/>
 * <br/>
 * Getting and setting fields supports simple, nested, indexed, and mapped values<br/>
 * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
 * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
 * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
 * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
 * <br/>
 * Includes support for dealing with annotations and working with field which have annotations on them. Methods
 * for finding fields with an annotation and finding all annotations in a class or on a fields are included.
 * <br/>
 * Includes support for deep cloning, deep copying, and populating objects using auto-conversion. Also
 * includes support for fuzzy copies where object data can be copied from one object to another without
 * the objects being the same type.<br/>
 * <br/>
 * Also includes an extendable conversion system for converting between java types. This system also handles
 * conversions between arrays, maps, collections, and scalars and improves upon the apache system by
 * handling more types and handling object holders.<br/>
 * <br/>
 * Support for construction of any class and a set of utilities for determining what types of objects
 * you are working with are also included. A method for executing a specific constructor can be used if more control if needed.
 * <br/>
 * The utilities cache reflection data for high performance operation but uses weak/soft caching to avoid holding open
 * ClassLoaders and causing the caches to exist in memory permanently. The ability to override the caching mechanism
 * with your own is supported.<br/>
 * <br/>
 * The utilities are modular and are meant to be extendable and overridable. All methods are protected or public so
 * that the various utility classes can be easily overridden if needed.
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class ReflectUtils {

    // CONSTRUCTORS

    /**
     * default constructor - protected since it should not really be used
     */
    protected ReflectUtils() {
        this( null, null, null, null );
    }

    /**
     * Create a new copy of the reflection utilities so you can control various things like the field
     * finding mode and the caching<br/>
     * <b>WARNING</b>: This is expensive and recreating it over and over is not a good idea,
     * you should use the static instance if you are not prepared to cache this object somewhere<br/>
     * Use {@link ReflectUtils#getInstance()} to avoid recreating this over and over
     * 
     * @param fieldFindMode (optional) see {@link FieldFindMode} enum for details, null for default
     * @param reflectionCache (optional) a cache for holding class cache data (implements map), null to use the default internal cache
     * @param resolver (optional) the field path name resolver to use when resolving EL style paths, null for default
     * @param converters (optional) a map of converters to add to the default set, null to use the default set
     */
    @SuppressWarnings({"unchecked", "SameParameterValue"})
    public ReflectUtils(FieldFindMode fieldFindMode, Map<Class<?>, ClassFields> reflectionCache, Resolver resolver, Map<Class<?>, Converter<?>> converters) {
        setFieldFindMode(fieldFindMode);
        setReflectionCache(reflectionCache);
        setResolver(resolver);
        setConverters(converters);

        ReflectUtils.setInstance(this);
    }

    // setter passthroughs

    /**
     * Set the mode used to find fields on classes (default {@link FieldFindMode#HYBRID}) <br/>
     * <b>WARNING</b>: changing modes will clear the existing cache
     * 
     * @param fieldFindMode see {@link FieldFindMode} enum for details, null for default
     * @see FieldFindMode
     */
    public void setFieldFindMode(FieldFindMode fieldFindMode) {
        getClassDataCacher().setFieldFindMode(fieldFindMode);
    }

    /**
     * Set the cache to be used for holding the reflection data, 
     * this allows control over where the reflection caches are stored,
     * this should store the data in a way that it will not hold open the classloader the class comes from <br/>
     * Note that you can set this to a map implementation which does not store anything to disable caching if you like
     * 
     * @param reflectionCache a cache for holding class cache data (implements map), null to use the default internal cache
     */
    @SuppressWarnings("unchecked")
    public void setReflectionCache(Map<Class<?>, ClassFields> reflectionCache) {
        getClassDataCacher().setReflectionCache(reflectionCache);
    }

    /**
     * Set the field name path resolver to use
     * @param resolver the field path name resolver to use when resolving EL style paths, null for default
     * @see FieldUtils#setResolver(Resolver)
     */
    public void setResolver(Resolver resolver) {
        getFieldUtils().setResolver(resolver);
    }

    /**
     * Set the object converters to add to the default converters
     * @param converters a map of converters to add to the default set, null to just use the default set
     */
    public void setConverters(Map<Class<?>, Converter<?>> converters) {
        getConversionUtils().setConverters(converters);
    }

    /**
     * Add a converter to the default set which will convert objects to the supplied type
     * @param type the type this converter will convert objects to
     * @param converter the converter
     */
    public void addConverter(Class<?> type, Converter<?> converter) {
        getConversionUtils().addConverter(type, converter);
    }

    /**
     * Set the option to include the "class" field when reflecting over objects,
     * the default for this is false (do not include)
     * @param includeClassField if true then include the value from the "getClass()" method as "class" when encoding beans and maps
     */
    public void setIncludeClassField(boolean includeClassField) {
        getClassDataCacher().setIncludeClassField(includeClassField);
    }

    /* 
     * Everything below is a pass-through to the methods in the other utilities classes
     * (sometimes with a little extra tie together logic),
     * reflect utils is just a convenient way to access the various methods and allows
     * a developer to override any of the methods as they are all happy and public and non-final
     */

    /**
     * Analyze a class and produce an object which contains information about it and its fields
     * @param <T>
     * @param beanClass any object class
     * @return the ClassFields analysis object which contains the information about this object class
     * @throws IllegalArgumentException if class is null or primitive
     */
    public <T> ClassFields<T> analyzeClass(Class<T> beanClass) {
        return getFieldUtils().analyzeClass(beanClass);
    }

    /**
     * Construct an object for the class of the given type regardless of whether it has a default constructor,
     * this will construct anything which has a valid class type including primitives,
     * arrays, collections and even classes without default constructors,
     * this will attempt to use the default constructor first if available though.
     * It must be possible to construct the class without knowing something about it beforehand,
     * (i.e. classes with only constructors which require non-null arguments will not be able
     * to be constructed)
     * 
     * @param <T>
     * @param beanClass any object class
     * @return the newly constructed object of the given class type 
     * (if primitive then a wrapped object will be returned which java will unwrap automatically)
     */
    public <T> T constructClass(Class<T> beanClass) {
        return getConstructorUtils().constructClass(beanClass);
    }

    /**
     * Get the value of a field or property (getter method) from an object<br/>
     * Getting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param fieldName the name of the field (property) to get the value of or the getter method without the "get" and lowercase first char
     * @throws FieldnameNotFoundException if this fieldName does not exist on the object
     * @throws IllegalArgumentException if a failure occurs while getting the field value
     */
    public Object getFieldValue(Object object, String fieldName) {
        return getFieldUtils().getFieldValue(object, fieldName);
    }

    /**
     * Get the value of a field or getter method from an object allowing an annotation to override<br/>
     * Getting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param fieldName the name of the field (property) to get the value of or the getter method without the "get" and lowercase first char
     * @param annotationClass if the annotation class is set then we will attempt to get the value from the annotated field or getter method first
     * @return the value of the field OR null if the value is null
     * @throws FieldnameNotFoundException if this fieldName does not exist on the object and no annotation was found
     * @throws IllegalArgumentException if a failure occurs while getting the field value
     */
    public Object getFieldValue(Object object, String fieldName, Class<? extends Annotation> annotationClass) {
        Object value;
        Class<?> elementClass = object.getClass();
        if (annotationClass != null) {
            // try to find annotation first
            String annotatedField = getFieldNameWithAnnotation(elementClass, annotationClass);
            if (annotatedField != null) {
                fieldName = annotatedField;
            }
        }
        value = getFieldValue(object, fieldName);
        return value;
    }

    /**
     * Get the string value of a field or getter method from an object allowing an annotation to override<br/>
     * Getting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param fieldName the name of the field (property) to get the value of or the getter method without the "get" and lowercase first char
     * @param annotationClass if the annotation class is set then we will attempt to get the value from the annotated field or getter method first
     * @return the string value of the field OR null if the value is null
     * @throws FieldnameNotFoundException if this fieldName does not exist on the object and no annotation was found
     * @throws IllegalArgumentException if a failure occurs while getting the field value
     */
    public String getFieldValueAsString(Object object, String fieldName, Class<? extends Annotation> annotationClass) {
        String sValue = null;
        Object value = getFieldValue(object, fieldName, annotationClass);
        if (value != null) {
            sValue = getConversionUtils().convert(value, String.class); //value.toString();
        }
        return sValue;
    }

    /**
     * Set the value on the object field or setter method, will convert if needed<br/>
     * Setting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param fieldName the name of the field (property) to set the value of or the setter method without the "set" and lowercase first char
     * @param value the value to set on this field, must match the type in the object (will not attempt to covert)
     * @throws FieldnameNotFoundException if the fieldName could not be found in this object
     * @throws UnsupportedOperationException if the value could not be converted to the field type
     * @throws IllegalArgumentException if the params are null or other failures occur setting the value
     */
    public void setFieldValue(Object object, String fieldName, Object value) {
        getFieldUtils().setFieldValue(object, fieldName, value);
    }

    /**
     * Sets a value on the field of an object and will attempt to convert the property if configured to do so<br/>
     * Setting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param fieldName the name of the field (property) to set the value of or the setter method without the "set" and lowercase first char
     * @param value the value to set on this field, does not have to match the type in the object,
     * the type will be determined and then we will attempt to convert this value to the type in the field
     * @param autoConvert if true then automatically try to convert the value to the type of the field being set,
     * otherwise only set the field if the value matches the type
     * @throws FieldnameNotFoundException if the fieldName could not be found in this object
     * @throws IllegalArgumentException if the value type does not match the field type or the type could not be converted to the field type
     * @throws UnsupportedOperationException if the value could not be converted to the field type
     */
    public void setFieldValue(Object object, String fieldName, Object value, boolean autoConvert) {
        getFieldUtils().setFieldValue(object, fieldName, value, autoConvert);
    }

    /**
     * Get the types of the fields of a specific class type,
     * returns the method names without the "get"/"is" part and camelCased
     * @param type any class
     * @return a map of field name -> class type
     */
    public Map<String, Class<?>> getFieldTypes(Class<?> type) {
        return getFieldTypes(type, null);
    }

    /**
     * Get the types of the fields of a specific class type,
     * returns the method names without the "get"/"is" part and camelCased
     * @param type any class
     * @param filter (optional) indicates the fields to return the types for, can be null for defaults
     * @return a map of field name -> class type
     */
    @SuppressWarnings("SameParameterValue")
    public Map<String, Class<?>> getFieldTypes(Class<?> type, FieldsFilter filter) {
        Map<String, Class<?>> types = getFieldUtils().getFieldTypes(type, filter);
        return types;
    }

    /**
     * Get the type of a field from a class
     * @param type any class
     * @param fieldName the name of the field (property) or a getter method converted to a field name
     * @return the type of object stored in the field
     * @throws FieldnameNotFoundException if the fieldName could not be found in this object
     */
    public Class<?> getFieldType(Class<?> type, String fieldName) {
        return getFieldUtils().getFieldType(type, fieldName);
    }

    /**
     * Get a map of all fieldName -> value and all getterMethodName -> value without the word "get"
     * where the method takes no arguments, in other words, all values available from an object (readable values)
     * @param object any object
     * @return a map of name -> value
     * @throws IllegalArgumentException if failures occur
     */
    public Map<String, Object> getObjectValues(Object object) {
        return getObjectValues(object, FieldsFilter.READABLE, false);
    }

    /**
     * Get a map of all fieldName -> value and all getterMethodName -> value without the word "get"
     * where the method takes no arguments, in other words, all values available from an object (readable values)
     * @param object any object
     * @param filter (optional) indicates the fields to return the values for, can be null for defaults
     * @param includeClassField if true then the value from the "getClass()" method is returned as part of the 
     * set of object values with a type of {@link Class} and a field name of "class"
     * @return a map of name -> value
     * @throws IllegalArgumentException if failures occur
     */
    public Map<String, Object> getObjectValues(Object object, FieldsFilter filter, boolean includeClassField) {
        return getFieldUtils().getFieldValues(object, filter, includeClassField);
    }

    /**
     * Find the getter field on a class which has the given annotation
     * @param elementClass any class
     * @param annotationClass the annotation type which is expected to be on the field
     * @return the name of the field or null if no fields are found with the indicated annotation
     * @throws IllegalArgumentException if the annotation class is null
     */
    public String getFieldNameWithAnnotation(Class<?> elementClass, Class<? extends Annotation> annotationClass) {
        String fieldName;
        if (annotationClass == null) {
            throw new IllegalArgumentException("the annotationClass must not be null");
        }
        ClassFields<?> cf = getFieldUtils().analyzeClass(elementClass);
        fieldName = cf.getFieldNameByAnnotation(annotationClass);
        return fieldName;
    }

    /**
     * Deep clone an object and all the values in it into a brand new object of the same type,
     * this will traverse the bean and will make new objects for all non-null values contained in the object
     * 
     * @param <T>
     * @param object any java object, this can be a list, map, array, or any simple
     * object, it does not have to be a custom object or even a java bean,
     * also works with DynaBeans
     * @param maxDepth the number of objects to follow when traveling through the object and copying
     * the values from it, 0 means to only copy the simple values in the object, any objects will
     * be ignored and will end up as nulls, 1 means to follow the first objects found and copy all
     * of their simple values as well, and so forth
     * @param fieldNamesToSkip the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @return the cloned object
     */
    public <T> T clone(T object, int maxDepth, String[] fieldNamesToSkip) {
        return getDeepUtils().deepClone(object, maxDepth, fieldNamesToSkip);
    }

    /**
     * Deep copies one object into another, this is primarily for copying between identical types of objects but
     * it can also handle copying between objects which are quite different, 
     * this does not just do a reference copy of the values but actually creates new objects in the current classloader
     * 
     * @param orig the original object to copy from
     * @param dest the object to copy the values to (must have the same fields with the same types)
     * @param maxDepth the number of objects to follow when traveling through the object and copying
     * the values from it, 0 means to only copy the simple values in the object, any objects will
     * be ignored and will end up as nulls, 1 means to follow the first objects found and copy all
     * of their simple values as well, and so forth
     * @param fieldNamesToSkip the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @param ignoreNulls if true then nulls are not copied and the destination retains the value it has,
     * if false then nulls are copied and the destination value will become a null if the original value is a null
     * @throws IllegalArgumentException if the copy cannot be completed because the objects to copy do not have matching fields or types
     */
    public void copy(Object orig, Object dest, int maxDepth, String[] fieldNamesToSkip, boolean ignoreNulls) {
        getDeepUtils().deepCopy(orig, dest, maxDepth, fieldNamesToSkip, ignoreNulls);
    }

    /**
     * This handles the cloning of objects to maps, it is recursive and is a deep operation which
     * traverses the entire object and clones every part of it, when converting to a map this will ensure
     * that there are no objects which are not part of java.lang or java.util in the new map<br/>
     * NOTE: This can handle simple objects (non-maps and non-beans) but will have to make up the initial map key
     * in the returned map, "data" will be used as the key<br/>
     * NOTE: Nulls are allowed to pass through this method (i.e. passing in a null object results in a null output)
     * 
     * @param object any java object
     * @param maxDepth the number of objects to follow when traveling through the object and copying
     * the values from it, 0 means to only copy the simple values in the object, any objects will
     * be ignored and will end up as nulls, 1 means to follow the first objects found and copy all
     * of their simple values as well, and so forth
     * @param fieldNamesToSkip the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @param ignoreNulls if true then nulls are not copied and the destination retains the value it has,
     * if false then nulls are copied and the destination value will become a null if the original value is a null
     * @param ignoreTransient if true then all transient fields will be skipped, useful when serializing
     * @param initialKey (optional) the initial key to use when converting simpler objects to maps
     * @return the resulting map which contains the cloned data from the object
     */
    public Map<String, Object> map(Object object, int maxDepth, String[] fieldNamesToSkip, boolean ignoreNulls, boolean ignoreTransient, String initialKey) {
        return getDeepUtils().deepMap(object, maxDepth, fieldNamesToSkip, ignoreNulls, ignoreTransient, initialKey);
    }

    /**
     * Populates an object with the values in the properties map,
     * this will not fail if the fieldName in the map is not a property on the
     * object or the fieldName cannot be written to with the value in the object.
     * This will attempt to convert the provided object values into the right values
     * to place in the object<br/>
     * <b>NOTE:</b> simple types like numbers and strings can almost always be converted from
     * just about anything though they will probably end up as 0 or ""<br/>
     * Setting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param properties a map of fieldNames -> Object
     * @return the list of fieldNames which were successfully written to the object
     * @throws IllegalArgumentException if the arguments are invalid
     */
    public List<String> populate(Object object, Map<String, Object> properties) {
        return getDeepUtils().populate(object, properties);
    }

    /**
     * Populates an object with the String array values in the params map,
     * this will not fail if the fieldName in the map is not a property on the
     * object or the fieldName cannot be written to with the value in the object<br/>
     * Arrays which are length 1 will be converted to a string before they are set on the target field <br/>
     * Setting fields supports simple, nested, indexed, and mapped values:<br/>
     * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
     * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
     * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
     * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
     * 
     * @param object any object
     * @param params a map of fieldNames -> String[] (normally from an http request)
     * @return the list of fieldNames which were successfully written to the object
     * @throws IllegalArgumentException if the arguments are invalid
     */
    public List<String> populateFromParams(Object object, Map<String, String[]> params) {
        return getDeepUtils().populateFromParams(object, params);
    }

    /**
     * Converts an object to any other object if possible
     * @see ConversionUtils#convert(Object, Class)
     * 
     * @param <T>
     * @param object any object
     * @param type any class type that you want to try to convert the object to
     * @return the converted value (allows null to pass through except in the case of primitives which become the primitive default)
     * @throws UnsupportedOperationException if the conversion cannot be completed
     */
    public <T> T convert(Object object, Class<T> type) {
        T convert = getConversionUtils().convert(object, type);
        return convert;
    }


    // STATIC methods

    /**
     * @param methodName a getter or setter style method name (e.g. getThing, setStuff, isType)
     * @return the fieldName equivalent (thing, stuff, type)
     */
    public static String makeFieldNameFromMethod(String methodName) {
        return ClassFields.makeFieldNameFromMethod(methodName);
    }

    /**
     * Capitalize a string
     * @param input any string
     * @return the string capitalized (e.g. myString -> MyString)
     */
    public static String capitalize(String input) {
        return ClassFields.capitalize(input);
    }

    /**
     * undo string capitalization
     * @param input any string
     * @return the string uncapitalized (e.g. MyString -> myString)
     */
    public static String unCapitalize(String input) {
        return ClassFields.unCapitalize(input);
    }


    /**
     * @return a list of all superclasses and implemented interfaces by the supplied class,
     * recursively to the base, up to but excluding Object.class. These will be listed in order from
     * the supplied class, all concrete superclasses in ascending order, and then finally all
     * interfaces in recursive ascending order.<br/>
     * This will include duplicates if any superclasses implement the same classes 
     */
    public static List<Class<?>> getSuperclasses(Class<?> clazz) {
        return ClassLoaderUtils.getSuperclasses(clazz);
    }


    /**
     * Finds a class type that is in the containing collection,
     * will always return something (failsafe to Object.class)
     * @param collection
     * @return the class type contained in this collecion
     */
    @SuppressWarnings("unchecked")
    public static Class<?> getClassFromCollection(Collection collection) {
        return ClassLoaderUtils.getClassFromCollection(collection);
    }

    /**
     * Checks to see if an array contains a value,
     * will return false if a null value is supplied
     * 
     * @param <T>
     * @param array any array of objects
     * @param value the value to check for
     * @return true if the value is found, false otherwise
     */
    public static <T> boolean contains(T[] array, T value) {
        return ArrayUtils.contains(array, value);
    }

    /**
     * Append an item to the end of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to append to the end of the new array
     * @return a new array with value in the last spot
     */
    public static <T> T[] appendArray(T[] array, T value) {
        return ArrayUtils.appendArray(array, value);
    }

    /**
     * Take an array of anything and turn it into a string
     * 
     * @param array any array
     * @return a string representing that array
     */
    public static String arrayToString(Object[] array) {
        return ArrayUtils.arrayToString(array);
    }

    /**
     * @param text string to make MD5 hash from
     * @param maxLength
     * @return an MD5 hash no longer than maxLength
     */
    public static String makeMD5(String text, int maxLength) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Stupid java sucks for MD5", e);
        }
        md.update(text.getBytes());

        // convert the binary md5 hash into hex
        StringBuilder sb = new StringBuilder();
        byte[] b_arr = md.digest();

        for (byte aB_arr : b_arr) {
            // convert the high nibble
            byte b = aB_arr;
            b >>>= 4;
            b &= 0x0f; // this clears the top half of the byte (intentional)
            sb.append(Integer.toHexString(b));

            // convert the low nibble
            b = aB_arr;
            b &= 0x0F;
            sb.append(Integer.toHexString(b));
        }
        String md5 = sb.toString();
        if (maxLength > 0 && md5.length() > maxLength) {
            md5 = md5.substring(0, maxLength);
        }
        return md5;
    }

    /**
     * Finds a map value for a key (or set of keys) if it exists in the map and returns the string value of it
     * @param map any map with strings as keys
     * @param keys an array of keys to try to find in order
     * @return the string value OR null if it could not be found for any of the given keys
     */
    public static String findMapStringValue(Map<String, ?> map, String[] keys) {
        if (map == null || keys == null) {
            return null;
        }
        String value = null;
        try {
            for (String key : keys) {
                if (map.containsKey(key)) {
                    Object oVal = map.get(key);
                    if (oVal != null) {
                        value = oVal.toString();
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            // in case the given map is not actually of the right types at runtime
            value = null;
        }
        return value;
    }


    /**
     * This is necessary because of the stupid capitalization rules used by Sun
     * @param name a property descriptor name to compare to a fieldname
     * @param fieldName a fieldName
     * @return true if they are almost the same or actually the same
     */
    public static boolean pdNameCompare(String name, String fieldName) {
        // AZ try tweaking the name slightly (uncap the first char) and try again, this fixes the sThing to SThing issue
        boolean match = false;
        if (name == null || fieldName == null) {
            match = false;
        } else if (name.length() == 0 || fieldName.length() == 0) {
            match = false;
        } else if (name.equals(fieldName)) {
            match = true;
        } else if (ReflectUtils.unCapitalize(name).equals(fieldName)) {
            match = true;
        } else if (ReflectUtils.capitalize(name).equals(fieldName)) {
            match = true;
        }
        return match;
    }

    @Override
    public String toString() {
        return "Reflect::c="+ReflectUtils.timesCreated+":s="+singleton+":" + getClassDataCacher() + ":" + getFieldUtils() + ":" + getConversionUtils();
    }

    // STATIC access

    protected static SoftReference<ReflectUtils> instanceStorage;
    //    protected static Map<ClassLoader, ReflectUtil> utilByClassLoader = 
    //        new ReferenceMap<ClassLoader, ReflectUtil>(ReferenceType.WEAK, ReferenceType.STRONG);
    /**
     * Get a singleton instance of this class to work with (stored statically) <br/>
     * <b>WARNING</b>: do not hold onto this object or cache it yourself, call this method again if you need it again
     * @return a singleton instance of this class
     */
    public static ReflectUtils getInstance() {
        ReflectUtils instance = (instanceStorage == null ? null : instanceStorage.get());
        if (instance == null) {
            instance = ReflectUtils.setInstance(null);
        }
        return instance;
    }
    /**
     * Set the singleton instance of the class which will be stored statically
     * @param newInstance the instance to use as the singleton instance
     */
    public static ReflectUtils setInstance(ReflectUtils newInstance) {
        ReflectUtils instance = newInstance;
        if (instance == null) {
            instance = new ReflectUtils();
            instance.singleton = true;
        }
        ReflectUtils.timesCreated++;
        instanceStorage = new SoftReference<ReflectUtils>(instance);
        return instance;
    }
    public static void clearInstance() {
        instanceStorage.clear();
    }

    private static int timesCreated = 0;
    public static int getTimesCreated() {
        return timesCreated;
    }

    private boolean singleton = false;
    /**
     * @return true if this is a singleton instance
     */
    public boolean isSingleton() {
        return singleton;
    }

    // utils getters

    protected FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }

    protected ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    protected ClassDataCacher getClassDataCacher() {
        return ClassDataCacher.getInstance();
    }

    protected ConversionUtils getConversionUtils() {
        return ConversionUtils.getInstance();
    }

    protected DeepUtils getDeepUtils() {
        return DeepUtils.getInstance();
    }

}
