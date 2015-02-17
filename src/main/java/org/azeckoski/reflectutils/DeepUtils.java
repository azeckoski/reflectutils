/**
 * $Id: DeepUtils.java 41 2008-11-12 17:36:24Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/DeepUtils.java $
 * FieldUtils.java - genericdao - May 19, 2008 10:10:15 PM - azeckoski
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

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.beanutils.FieldAdapter;
import org.azeckoski.reflectutils.exceptions.FieldGetValueException;
import org.azeckoski.reflectutils.exceptions.FieldSetValueException;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;

/**
 * Class which provides methods for handling deep operations: <br/>
 * deep copy - make a copy of one object into another which traverses the entire object and duplicates all data down to immutable/simple fields <br/>
 * deep clone - clone an object by traversing it and making a copy of every field <br/>
 * deep map - turn an object into a map with only simple objects in it, all contained objects become nested maps <br/>
 * populate - turn a map into an object, can handle params maps (from a servlet request) and nested or non-nested string->object maps
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class DeepUtils {

    /**
     * Empty constructor
     * <br/>
     * <b>WARNING:</b> use the {@link #getInstance()} method to get this rather than recreating it over and over
     */
    public DeepUtils() {
        DeepUtils.setInstance(this);
    }

    protected ClassDataCacher getClassDataCacher() {
        return ClassDataCacher.getInstance();
    }

    protected ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    protected ConversionUtils getConversionUtils() {
        return ConversionUtils.getInstance();
    }

    protected FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }

    protected FieldAdapter getFieldAdapter() {
        return getFieldUtils().getFieldAdapter();
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
     * @param fieldNamesToSkip (optional) the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @return the cloned object
     */
    @SuppressWarnings("unchecked")
    public <T> T deepClone(T object, int maxDepth, String[] fieldNamesToSkip) {
        Set<String> skip = ArrayUtils.makeSetFromArray(fieldNamesToSkip);
        T clone = (T) internalDeepClone(object, CopyDestination.ORIGINAL, maxDepth, skip, 0, true, false);
        return clone;
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
     * @param fieldNamesToSkip (optional) the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @param ignoreNulls if true then nulls are not copied and the destination retains the value it has,
     * if false then nulls are copied and the destination value will become a null if the original value is a null
     * @throws IllegalArgumentException if the copy cannot be completed because the objects to copy do not have matching fields or types
     */
    public void deepCopy(Object orig, Object dest, int maxDepth, String[] fieldNamesToSkip, boolean ignoreNulls) {
        Set<String> skip = ArrayUtils.makeSetFromArray(fieldNamesToSkip);
        internalDeepCopy(orig, dest, maxDepth, skip, ignoreNulls, true);
    }

    /**
     * This handles the copying of objects to maps, it is recursive and is a deep operation which
     * traverses the entire object and clones every part of it. When converting to a map this will ensure
     * that there are no objects which are not part of java.lang or java.util in the new map<br/>
     * NOTE: This can handle simple objects (non-maps and non-beans) but will have to make up the initial map key
     * in the returned map, "data" will be used as the key<br/>
     * NOTE: Nulls are allowed to pass through this method (i.e. passing in a null object results in a null output)
     * 
     * @param bean any java object
     * @param maxDepth the number of objects to follow when traveling through the object and copying
     * the values from it, 0 means to only copy the simple values in the object, any objects will
     * be ignored and will end up as nulls, 1 means to follow the first objects found and copy all
     * of their simple values as well, and so forth
     * @param fieldNamesToSkip (optional) the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @param ignoreNulls if true then nulls are not copied and the destination retains the value it has,
     * if false then nulls are copied and the destination value will become a null if the original value is a null
     * @param ignoreTransient if true then all transient fields will be skipped, useful when serializing
     * @param initialKey (optional) the initial key to use when simple objects are converted to maps, defaults to "data"
     * @return the resulting map which contains the cloned data from the object
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> deepMap(Object object, int maxDepth, String[] fieldNamesToSkip, boolean ignoreNulls, boolean ignoreTransient, String initialKey) {
        Set<String> skip = ArrayUtils.makeSetFromArray(fieldNamesToSkip);
        Object clone = internalDeepClone(object, CopyDestination.MAP, maxDepth, skip, 0, ignoreNulls, ignoreTransient);
        Map<String, Object> map = null;
        if (clone != null) {
            if ( ConstructorUtils.isClassMap(clone.getClass())) {
                map = (Map<String, Object>) clone;
            } else {
                // handle the case of non-map/bean by wrapping in a map
                if (initialKey == null) {
                    initialKey = "data";
                }
                map = new HashMap<String, Object>();
                map.put(initialKey, clone);
            }
        }
        return map;
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
        if (object == null || properties == null) {
            throw new IllegalArgumentException("object and properties cannot be null");
        }
        Map<String, Class<?>> fieldTypes = getFieldUtils().getFieldTypes(object.getClass(), FieldsFilter.ALL);
        List<String> writtenFields = new ArrayList<String>();
        for (Entry<String, Object> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName != null && ! "".equals(fieldName)) {
                Class<?> targetType = fieldTypes.get(fieldName);
                if (targetType != null) {
                    Object value = entry.getValue();
                    try {
                        if ( ConstructorUtils.isClassSimple(targetType) ) {
                            // this should write the value or fail
                            getFieldUtils().setFieldValue(object, fieldName, value, true);
                        } else {
                            // value is a bean so we will dig into it further
                            Object dest = getFieldUtils().getFieldValue(object, fieldName);
                            if (dest == null) {
                                // need to construct the destination
                                if (value.getClass().isArray() && targetType.isArray()) {
                                    // special case for array to array since the destination must match
                                    dest = ArrayUtils.template((Object[])value);
                                } else {
                                    dest = getConstructorUtils().constructClass(targetType);
                                }
                                getFieldUtils().setFieldValue(object, fieldName, dest, true);
                            }
                            internalDeepCopy(value, dest, 5, null, false, true);
                        }
                        writtenFields.add(fieldName); // record that the value was written successfully
                    } catch (RuntimeException e) {
                        // this is OK, it just means we did not write the value
                        continue;
                    }
                }
            }
        }
        return writtenFields;
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
        if (object == null || params == null) {
            throw new IllegalArgumentException("object and params cannot be null");
        }
        List<String> writtenFields = new ArrayList<String>();
        for (Entry<String, String[]> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName != null && ! "".equals(fieldName)) {
                String[] value = entry.getValue();
                try {
                    // this should write the value or fail
                    if (value != null) {
                        Object toSet = value;
                        if (value.length == 0) {
                            toSet = "";
                        } else if (value.length == 1) {
                            toSet = value[0];
                        }
                        getFieldUtils().setFieldValue(object, fieldName, toSet, true);
                        writtenFields.add(fieldName); // record that the value was written successfully
                    }
                } catch (RuntimeException e) {
                    // this is OK, it just means we did not write the value
                    continue;
                }
            }
        }
        return writtenFields;
    }


    public static enum CopyDestination {ORIGINAL, MAP};
    /**
     * This handles the cloning of objects to objects or maps, it is recursive and is a deep operation which
     * traverses the entire object and clones every part of it, when converting to a map this will ensure
     * that there are no objects which are not part of java.lang or java.util in the new map
     * @param bean any java object
     * @param dest the type of destination: ORIGINAL = an object of type matching bean, MAP = an ordered map
     * @param maxDepth the number of objects to follow when traveling through the object and copying
     * the values from it, 0 means to only copy the simple values in the object, any objects will
     * be ignored and will end up as nulls, 1 means to follow the first objects found and copy all
     * of their simple values as well, and so forth
     * @param fieldNamesToSkip the names of fields to skip while cloning this object,
     * this only has an effect on the bottom level of the object, any fields found
     * on child objects will always be copied (if the maxDepth allows)
     * @param currentDepth the current depth for recursion and halting when appropriate depth is reached
     * @param ignoreNulls if true then nulls are not copied and the destination retains the value it has,
     * if false then nulls are copied and the destination value will become a null if the original value is a null
     * @param ignoreTransient if true then all transient fields will be skipped, useful when serializing
     * @return the object or map which has been cloned
     */
    @SuppressWarnings("unchecked")
    protected Object internalDeepClone(Object bean, CopyDestination dest, int maxDepth, Set<String> fieldNamesToSkip, int currentDepth, boolean ignoreNulls, boolean ignoreTransient) {
        Object copy = null;
        if ( bean != null ) {
            Class<?> beanClass = bean.getClass();
            // always copy the simple types if possible
            if (ConstructorUtils.isClassSimple(beanClass) 
                    || ConstructorUtils.isClassSpecial(beanClass)) {
                // simple and special types are just ref copied
                copy = bean;
            } else {
                if (currentDepth <= maxDepth) {
                    currentDepth++;
                    try {
                        // now do the cloning based on the thing to clone
                        if (beanClass.isArray()) {
                            // special case, use array reflection
                            // make new array
                            int length = ArrayUtils.size((Object[]) bean);
                            Class<?> componentType = beanClass.getComponentType();
                            if ( ConstructorUtils.isClassBean(componentType) 
                                    && CopyDestination.MAP.equals(dest)) {
                                // special array component type for arrays of objects when map converting
                                componentType = ArrayOrderedMap.class;
                            }
                            copy = ArrayUtils.create(componentType, length);
                            // now copy the stuff into it
                            for (int i = 0; i < length; i++) {
                                Object value = Array.get(bean, i);
                                Object clone = internalDeepClone(value, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient);
                                Array.set(copy, i, clone);
                            }
                        } else if (Collection.class.isAssignableFrom(beanClass)) {
                            // special case, clone everything in the list
                            copy = getConstructorUtils().constructClass(beanClass); // make new list
                            for (Object element : (Collection) bean) {
                                Object clone = internalDeepClone(element, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient);
                                ((Collection) copy).add( clone );
                            }
                        } else if (Map.class.isAssignableFrom(beanClass)) {
                            // special case, clone everything in the map except keys
                            copy = getConstructorUtils().constructClass(beanClass); // make new map
                            for (Object key : ((Map) bean).keySet()) {
                                if ( fieldNamesToSkip != null
                                        && fieldNamesToSkip.contains(key) ) {
                                    continue; // skip to next
                                }
                                Object value = ((Map) bean).get(key);
                                if (value == null && ignoreNulls) {
                                    continue;
                                }
                                try {
                                    ((Map) copy).put(key, 
                                            internalDeepClone(value, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient) );
                                } catch (NullPointerException e) {
                                    // map does not support nulls so skip and keep going
                                    continue;
                                }                     
                            }
                        } else if (getFieldAdapter().isAdaptableClass(beanClass)) {
                            // special handling for dynabeans
                            if (CopyDestination.MAP.equals(dest)) {
                                copy = new ArrayOrderedMap<String, Object>();
                            } else {
                                copy = getFieldAdapter().newInstance(bean); // make new dynabean
                            }
                            List<String> propertyNames = getFieldAdapter().getPropertyNames(bean);
                            for (String name : propertyNames) {
                                if ( fieldNamesToSkip != null
                                        && fieldNamesToSkip.contains(name) ) {
                                    continue; // skip to next
                                }
                                try {
                                    Object value = getFieldAdapter().getSimpleValue(bean, name);
                                    if (value == null && ignoreNulls) {
                                        continue;
                                    }
                                    if (CopyDestination.MAP.equals(dest)) {
                                        ((Map<String, Object>) copy).put(name, 
                                                internalDeepClone(value, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient) );
                                    } else {
                                        getFieldUtils().setFieldValue(copy, name, 
                                                internalDeepClone(value, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient) );
                                    }
                                } catch (IllegalArgumentException e) {
                                    // this is ok, field might not be readable so we will not clone it, continue on
                                }
                            }
                        } else {
                            // regular javabean
                            FieldsFilter filter = FieldsFilter.COMPLETE;
                            if (CopyDestination.MAP.equals(dest)) {
                                copy = new ArrayOrderedMap<String, Object>();
                                // maps should pick up all readable fields
                                if (ignoreTransient) {
                                    filter = FieldsFilter.SERIALIZABLE;
                                } else {
                                    filter = FieldsFilter.READABLE;
                                }
                            } else {
                                copy = getConstructorUtils().constructClass(beanClass); // make new bean
                            }
                            ClassFields<?> cf = getFieldUtils().analyzeClass(beanClass);
                            Map<String, Class<?>> types = cf.getFieldTypes(filter);
                            for (String name : types.keySet()) {
                                if (ClassFields.FIELD_CLASS.equals(name)) {
                                    continue; // No point in trying to set/get an object's class
                                }
                                if ( fieldNamesToSkip != null
                                        && fieldNamesToSkip.contains(name) ) {
                                    continue; // skip to next
                                }
                                try {
                                    Object value = getFieldUtils().getFieldValue(bean, name);
                                    if (value == null && ignoreNulls) {
                                        continue;
                                    }
                                    if (CopyDestination.MAP.equals(dest)) {
                                        ((Map<String, Object>) copy).put(name, 
                                                internalDeepClone(value, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient) );                     
                                    } else {
                                        getFieldUtils().setFieldValue(copy, name, 
                                                internalDeepClone(value, dest, maxDepth, null, currentDepth, ignoreNulls, ignoreTransient) );
                                    }
                                } catch (FieldnameNotFoundException e) {
                                    // this is ok, field might not exist or might not be readable so we skip it
                                } catch (FieldGetValueException e) {
                                    // this is ok, field might not be readable (it should be though)
                                } catch (FieldSetValueException e) {
                                    // this is ok, field might not be writeable (should also not happen)
                                } catch (IllegalArgumentException e) {
                                    // this is ok, failure should not stop the clone
                                }
                            }
                        }
                    } catch (Exception e) {
                        // catch any possible thrown exception and translate (this is not a mistake so ignore findbugs)
                        throw new RuntimeException("Failure during deep cloning ("+beanClass+") maxDepth="+maxDepth+", currentDepth="+currentDepth+": " + e.getMessage(), e);
                    }
                }
            }
        }
        return copy;
    }

    /**
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
     * @param autoConvert if true the the original will be converted to the dest type if needed
     * @throws IllegalArgumentException if the copy cannot be completed because the objects to copy do not have matching fields or types
     */
    @SuppressWarnings("unchecked")
    protected void internalDeepCopy(Object orig, Object dest, int maxDepth, Set<String> fieldNamesToSkip, boolean ignoreNulls, boolean autoConvert) {
        if (orig == null || dest == null) {
            throw new IllegalArgumentException("original object and destination object must not be null, if you want to clone the object then use the clone method");
        }

        int currentDepth = 1;
        Class<?> origClass = orig.getClass();
        Class<?> destClass = dest.getClass();

        // check if copy is possible first
        if (ConstructorUtils.getImmutableTypes().contains(destClass)) {
            // cannot copy to an immutable object
            throw new IllegalArgumentException("Cannot copy to an immutable object ("+destClass+")");
        } else if (ConstructorUtils.isClassSpecial(origClass)) {
            // cannot copy special objects
            throw new IllegalArgumentException("Cannot copy a special object ("+origClass+")");
        }
        if (autoConvert && ! ConstructorUtils.isClassBean(origClass) ) {
            // attempt to convert non-beans into the type we are copying to, fail if it does not work
            // This has to be done because the copy will simply fail if we try to copy between non-beans which are not equivalent
            try {
                orig = getConversionUtils().convert(orig, destClass);
                origClass = destClass;
            } catch (UnsupportedOperationException e) {
                throw new IllegalArgumentException("Cannot copy from an immutable object to a complex object without converter support");
            }
        }
        // copy orig to dest
        try {
            if (origClass.isArray()) {
                // special case, copy and overwrite existing array values
                if (destClass.isArray()) {
                    // TODO if the dest array is empty then fail
                    try {
                        for (int i = 0; i < Array.getLength(orig); i++) {
                            Object value = Array.get(orig, i);
                            if (ignoreNulls && value == null) {
                                // don't copy this null over the existing value
                            } else {
                                Array.set(dest, i, 
                                        internalDeepClone(value, CopyDestination.ORIGINAL, maxDepth, null, currentDepth, ignoreNulls, false));
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        // partial copy is ok, continue on
                    }
                } else {
                    throw new IllegalArgumentException("Cannot copy a simple value to a complex object");                  
                }
            } else if (Collection.class.isAssignableFrom(origClass)) {
                // special case, copy everything from orig and add to dest
                for (Object value : (Collection) orig) {
                    if (ignoreNulls && value == null) {
                        // don't copy this null over the existing value
                    } else {
                        ((Collection) dest).add( 
                                internalDeepClone(value, CopyDestination.ORIGINAL, maxDepth, null, currentDepth, ignoreNulls, false));
                    }
                }
            } else if (Map.class.isAssignableFrom(origClass)) {
                // special case clone everything in the map except keys
                for (Object key : ((Map) orig).keySet()) {
                    if ( fieldNamesToSkip != null
                            && fieldNamesToSkip.contains(key) ) {
                        continue; // skip to next
                    }
                    Object value = ((Map) orig).get(key);
                    if (ignoreNulls && value == null) {
                        // don't copy this null over the existing value
                    } else {
                        ((Map) dest).put(key, 
                                internalDeepClone(value, CopyDestination.ORIGINAL, maxDepth, null, currentDepth, ignoreNulls, false));
                    }
                }
            } else if (getFieldAdapter().isAdaptableClass(origClass)) {
                List<String> propertyNames = getFieldAdapter().getPropertyNames(orig);
                for (String name : propertyNames) {
                    if ( fieldNamesToSkip != null
                            && fieldNamesToSkip.contains(name) ) {
                        continue; // skip to next
                    }
                    try {
                        Object value = getFieldAdapter().getSimpleValue(orig, name);
                        if (ignoreNulls && value == null) {
                            // don't copy this null over the existing value
                        } else {
                            getFieldUtils().setFieldValue(dest, name, 
                                    internalDeepClone(value, CopyDestination.ORIGINAL, maxDepth, null, currentDepth, ignoreNulls, false));
                        }
                    } catch (FieldnameNotFoundException e) {
                        // it is ok for the objects to not be the same
                    } catch (IllegalArgumentException e) {
                        // it is ok for the objects to not be the same
                    }
                }
            } else {
                // regular javabean
                Map<String, Object> values = getFieldUtils().getFieldValues(orig, FieldsFilter.READABLE, false);
                for (Entry<String, Object> entry : values.entrySet()) {
                    String name = entry.getKey();
                    if (ClassFields.FIELD_CLASS.equals(name)) {
                        continue; // No point in trying to set an object's class
                    }
                    if ( fieldNamesToSkip != null
                            && fieldNamesToSkip.contains(name) ) {
                        continue; // skip to next
                    }
                    try {
                        Object value = getFieldUtils().getFieldValue(orig, name);
                        if (ignoreNulls && value == null) {
                            // don't copy this null over the existing value
                        } else {
                            getFieldUtils().setFieldValue(dest, name, 
                                    internalDeepClone(value, CopyDestination.ORIGINAL, maxDepth, null, currentDepth, ignoreNulls, false));
                        }
                    } catch (FieldnameNotFoundException e) {
                        // this is ok, field might not exist or might not be readable so we skip it
                    } catch (FieldGetValueException e) {
                        // this is ok, field might not be readable
                    } catch (FieldSetValueException e) {
                        // this is ok, field might not be writeable
                    } catch (IllegalArgumentException e) {
                        // this is ok, failure should not stop the clone
                    }
                }
            }
        } catch (Exception e) {
            // catch any possible thrown exception and translate (this is not a mistake so ignore findbugs)
            throw new IllegalArgumentException("Failure copying " + orig + " ("+origClass+") to " 
                    + dest + " ("+destClass+")" + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return "Deep:"+getClassDataCacher();
    }

    // STATIC access

    protected static SoftReference<DeepUtils> instanceStorage;
    /**
     * Get a singleton instance of this class to work with (stored statically) <br/>
     * <b>WARNING</b>: do not hold onto this object or cache it yourself, call this method again if you need it again
     * @return a singleton instance of this class
     */
    public static DeepUtils getInstance() {
        DeepUtils instance = (instanceStorage == null ? null : instanceStorage.get());
        if (instance == null) {
            instance = DeepUtils.setInstance(null);
        }
        return instance;
    }
    /**
     * Set the singleton instance of the class which will be stored statically
     * @param instance the instance to use as the singleton instance
     */
    public static DeepUtils setInstance(DeepUtils newInstance) {
        DeepUtils instance = newInstance;
        if (instance == null) {
            instance = new DeepUtils();
            instance.singleton = true;
        }
        DeepUtils.timesCreated++;
        instanceStorage = new SoftReference<DeepUtils>(instance);
        return instance;
    }

    private static int timesCreated = 0;
    public static int getTimesCreated() {
        return timesCreated;
    }

    private boolean singleton = false;
    /**
     * @return true if this object is the singleton
     */
    public boolean isSingleton() {
        return singleton;
    }

}
