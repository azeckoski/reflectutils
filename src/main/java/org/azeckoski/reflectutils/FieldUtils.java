/**
 * $Id: FieldUtils.java 129 2014-03-18 23:25:36Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/FieldUtils.java $
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

import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.ClassProperty.IndexedProperty;
import org.azeckoski.reflectutils.ClassProperty.MappedProperty;
import org.azeckoski.reflectutils.beanutils.DefaultResolver;
import org.azeckoski.reflectutils.beanutils.FieldAdapter;
import org.azeckoski.reflectutils.beanutils.FieldAdapterManager;
import org.azeckoski.reflectutils.beanutils.Resolver;
import org.azeckoski.reflectutils.exceptions.FieldGetValueException;
import org.azeckoski.reflectutils.exceptions.FieldSetValueException;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class which provides methods for dealing with the fields in objects and classes,
 * this provides the core functionality for the reflect util class<br/>
 * <br/>
 * Setting and getting fields supports simple, nested, indexed, and mapped values:<br/>
 * <b>Simple:</b> Get/set a field in a bean (or map), Example: "title", "id"<br/>
 * <b>Nested:</b> Get/set a field in a bean which is contained in another bean, Example: "someBean.title", "someBean.id"<br/>
 * <b>Indexed:</b> Get/set a list/array item by index in a bean, Example: "myList[1]", "anArray[2]"<br/>
 * <b>Mapped:</b> Get/set a map entry by key in a bean, Example: "myMap(key)", "someMap(thing)"<br/>
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class FieldUtils {

    /**
     * Empty constructor - protected
     */
    protected FieldUtils() {
        this( null );
    }

    /**
     * Constructor which allows the field path name resolver to be set specifically
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     * 
     * @param resolver controls the resolution of indexed, nested, and mapped field paths
     */
    @SuppressWarnings("SameParameterValue")
    public FieldUtils(Resolver resolver) {
        setResolver(resolver);

        fieldAdapterManager = new FieldAdapterManager();

        FieldUtils.setInstance(this);
    }


    protected ClassDataCacher getClassDataCacher() {
        return ClassDataCacher.getInstance();
    }

    public ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    public ConversionUtils getConversionUtils() {
        return ConversionUtils.getInstance();
    }

    protected Resolver nameResolver = null;
    public void setResolver(Resolver resolver) {
        if (resolver != null) {
            this.nameResolver = resolver;
        } else {
            getResolver();
        }
    }
    protected Resolver getResolver() {
        if (nameResolver == null) {
            nameResolver = new DefaultResolver();
        }
        return nameResolver;
    }


    protected FieldAdapterManager fieldAdapterManager;
    /**
     * INTERNAL USAGE
     * @return the field adapter being used by this set of field utils
     */
    public FieldAdapter getFieldAdapter() {
        return fieldAdapterManager.getFieldAdapter();
    }


    /**
     * Analyze a class and produce an object which contains information about it and its fields
     * @param <T>
     * @param cls any class
     * @return the ClassFields analysis object which contains the information about this object class
     * @throws IllegalArgumentException if class is null or primitive
     */
    public <T> ClassFields<T> analyzeClass(Class<T> cls) {
        ClassFields<T> cf = getClassDataCacher().getClassFields(cls);
        return cf;
    }

    public <T> ClassFields<T> analyzeClass(Class<T> cls, ClassFields.FieldFindMode mode) {
        ClassFields<T> cf = getClassDataCacher().getClassFields(cls, mode);
        return cf;
    }

    /**
     * Analyze an object and produce an object which contains information about it and its fields
     * @param obj any object
     * @return the ClassFields analysis object which contains the information about this object class
     * @throws IllegalArgumentException if obj is null
     */
    @SuppressWarnings("unchecked")
    public <T> ClassFields<T> analyzeObject(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (Class.class.equals(obj)) {
            // this is a class so we should pass it over to the other method
            return analyzeClass((Class)obj);
        }
        Class<T> cls = (Class<T>) obj.getClass();
        return analyzeClass(cls);
    }

    /**
     * Finds the type for a field based on the given class and the field name
     * @param type any class type
     * @param name the name of a field in this class (can be nested, indexed, mapped, etc.)
     * @return the type of the field (will be {@link Object} if the type is indeterminate)
     */
    public Class<?> getFieldType(Class<?> type, String name) {
        if (type == null || name == null) {
            throw new IllegalArgumentException("type and name must not be null");
        }
        // get the nested class or return Object.class as a cop out
        while (getResolver().hasNested(name)) {
            String next = getResolver().next(name);
            Class<?> nestedClass;
            if (Object.class.equals(type)
                    || Map.class.isAssignableFrom(type)
                    || getResolver().isMapped(next)
                    || getResolver().isIndexed(next) ) {
                // these can contain objects or it is Object so we bail out
                return Object.class;
            } else {
                // a real class, hooray, analyze it
                ClassFields<?> cf = analyzeClass(type);
                nestedClass = cf.getFieldType(name);
            }
            type = nestedClass;
            name = getResolver().remove(name);
        }
        String targetName = getResolver().getProperty(name); // simple name of target field
        // get the type
        Class<?> fieldType;
        if ( ConstructorUtils.isClassObjectHolder(type) 
                || Object.class.equals(type) ) {
            // special handling for the holder types, needed because attempting to analyze a map or other container will cause a failure
            fieldType = Object.class;
        } else {
            // normal object
            ClassFields<?> cf = analyzeClass(type);
            try {
                fieldType = cf.getFieldType(targetName);
            } catch (FieldnameNotFoundException fnfe) {
                // could not find this as a standard field so handle as internal lookup
                ClassData<?> cd = cf.getClassData();
                Field field = getFieldIfPossible(cd, name);
                if (field == null) {
                    throw new FieldnameNotFoundException("Could not find field with name ("+name+") in class (" + type + ") after extended look into non-visible fields", fnfe);
                }
                fieldType = field.getType();
            }
        }
        // special handling for indexed and mapped names
        if (getResolver().isIndexed(name) || getResolver().isMapped(name)) {
            if ( ConstructorUtils.isClassArray(fieldType) ) {
                // use the array component type
                fieldType = type.getComponentType();
            } else {
                // default for contained type of holders
                fieldType = Object.class;
            }
        }
        return fieldType;
    }

    /**
     * Finds the type for a field based on the containing object and the field name
     * @param obj any object
     * @param name the name of a field in this object (can be nested, indexed, mapped, etc.)
     * @return the type of the field (will be {@link Object} if the type is indeterminate)
     * @throws FieldnameNotFoundException if the name is invalid for this obj
     * @throws IllegalArgumentException if the params are null
     */
    @SuppressWarnings("unchecked")
    public Class<?> getFieldType(Object obj, String name) {
        if (obj == null || name == null) {
            throw new IllegalArgumentException("obj and name must not be null");
        }
        if (Class.class.equals(obj)) {
            // this is a class so we should pass it over to the other method
            return getFieldType((Class<?>)obj, name); // EXIT
        }
        if ( Object.class.equals(obj.getClass()) ) {
            return Object.class; // EXIT
        }
        // get the nested object or die
        while (getResolver().hasNested(name)) {
            String next = getResolver().next(name);
            Object nestedBean;
            if (Map.class.isAssignableFrom(obj.getClass())) {
                nestedBean = getValueOfMap((Map) obj, next);
            } else if (getResolver().isMapped(next)) {
                nestedBean = getMappedValue(obj, next);
            } else if (getResolver().isIndexed(next)) {
                nestedBean = getIndexedValue(obj, next);
            } else {
                nestedBean = getSimpleValue(obj, next);
            }
            if (nestedBean == null) {
                // no auto create so we have to fail here
                throw new NullPointerException("Nested traversal failure: null field value for name (" 
                        + name + ") on object class (" + obj.getClass() + ") for object: " + obj);
            }
            obj = nestedBean;
            name = getResolver().remove(name);
        }
        String targetName = getResolver().getProperty(name); // simple name of target field
        // get the type
        Class<?> fieldType;
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            fieldType = fieldAdapterManager.getFieldAdapter().getFieldType(obj, targetName);
        } else if ( ConstructorUtils.isClassObjectHolder(obj.getClass()) 
                || Object.class.equals(obj.getClass()) ) {
            // special handling for the holder types, needed because attempting to analyze a map or other container will cause a failure
            fieldType = Object.class;
        } else {
            // normal object
            ClassFields<?> cf = analyzeObject(obj);
            try {
                ClassProperty cp = cf.getClassProperty(targetName);
                fieldType = cp.getType();
            } catch (FieldnameNotFoundException fnfe) {
                // could not find this as a standard field so handle as internal lookup
                ClassData<?> cd = cf.getClassData();
                Field field = getFieldIfPossible(cd, name);
                if (field == null) {
                    throw new FieldnameNotFoundException("Could not find field with name ("+name+") on object (" + obj + ") after extended look into non-visible fields", fnfe);
                }
                fieldType = field.getType();
            }
        }
        // special handling for indexed and mapped names
        if (getResolver().isIndexed(name) || getResolver().isMapped(name)) {
            if ( ConstructorUtils.isClassArray(fieldType) ) {
                // use the array component type
                fieldType = fieldType.getComponentType();
            } else {
                // default for contained type of holders
                fieldType = Object.class;
            }
        }
        return fieldType;
    }

    /**
     * Get the types of the fields of a specific class type <br/>
     * returns the method names as fields (without the "get"/"is" part and camelCased)
     * @param type any class
     * @param filter (optional) indicates the fields to return the types for, can be null for defaults
     * @return a map of field name -> class type
     */
    public Map<String, Class<?>> getFieldTypes(Class<?> type, FieldsFilter filter) {
        ClassFields<?> cf = analyzeClass(type, findFieldFindMode(filter));
        Map<String, Class<?>> types = cf.getFieldTypes(filter);
        return types;
    }

    private ClassFields.FieldFindMode findFieldFindMode(FieldsFilter filter) {
        ClassFields.FieldFindMode mode = ClassFields.FieldFindMode.HYBRID; // default
        if (FieldsFilter.ALL.equals(filter)) {
            mode = ClassFields.FieldFindMode.ALL;
        } else if (FieldsFilter.SERIALIZABLE_FIELDS.equals(filter)) {
            mode = ClassFields.FieldFindMode.FIELD;
        }
        return mode;
    }

    /**
     * Get the names of all fields in a class
     * @param cls any class
     * @return a list of the field names
     */
    public <T> List<String> getFieldNames(Class<T> cls) {
        ClassFields<T> cf = analyzeClass(cls);
        return cf.getFieldNames();
    }

    public <T> List<String> getFieldNames(Class<T> cls, FieldsFilter filter) {
        ClassFields<T> cf = analyzeClass(cls, findFieldFindMode(filter));
        return cf.getFieldNames();
    }

    /**
     * Get the values of all readable fields on an object (may not all be writeable)
     * @param obj any object
     * @return a map of field name -> value
     * @throws IllegalArgumentException if the obj is null
     */
    public Map<String, Object> getFieldValues(Object obj) {
        return getFieldValues(obj, FieldsFilter.READABLE, false);
    }

    /**
     * Get the values of all fields on an object but optionally filter the fields used
     * @param obj any object
     * @param filter (optional) indicates the fields to return the values for, can be null for defaults <br/>
     * WARNING: getting the field values from settable only fields works as expected (i.e. you will an empty map)
     * @param includeClassField if true then the value from the "getClass()" method is returned as part of the 
     * set of object values with a type of {@link Class} and a field name of "class"
     * @return a map of field name -> value
     * @throws IllegalArgumentException if the obj is null
     */
    public Map<String, Object> getFieldValues(Object obj, FieldsFilter filter, boolean includeClassField) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        Map<String, Object> values = new ArrayOrderedMap<String, Object>();
        if (includeClassField) {
            // add as the first field
            values.put(ClassFields.FIELD_CLASS, obj.getClass());
        }
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            values.putAll( fieldAdapterManager.getFieldAdapter().getFieldValues(obj, filter) );
        } else {
            Map<String, Class<?>> types = getFieldTypes(obj.getClass(), filter);
            if (FieldsFilter.WRITEABLE.equals(filter)) {
                types.clear();
            }
            for (String name : types.keySet()) {
                try {
                    Object o = getFieldValue(obj, name);
                    values.put(name, o);
                } catch (RuntimeException e) {
                    // failed to get the value so we will skip this one
                    continue;
                }
            }
        }
        return values;
    }

    /**
     * Get the value of a field on an object,
     * name can be nested, indexed, or mapped
     * @param obj any object
     * @param name the name of a field on this object
     * @return the value of the field
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws FieldGetValueException if the field is not readable or not visible
     * @throws IllegalArgumentException if there is a failure getting the value
     */
    @SuppressWarnings("unchecked")
    public Object getFieldValue(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        // Resolve nested references
        Holder holder = unpackNestedName(name, obj, false);
        name = holder.getName();
        obj = holder.getObject();

        Object value;
        if (Map.class.isAssignableFrom(obj.getClass())) {
            value = getValueOfMap((Map) obj, name);
        } else if (getResolver().isMapped(name)) {
            value = getMappedValue(obj, name);
        } else if (getResolver().isIndexed(name)) {
            value = getIndexedValue(obj, name);
        } else {
            value = getSimpleValue(obj, name);
        }
        return value;
    }

    /**
     * Get the value of a field on an object as a specific type,
     * name can be nested, indexed, or mapped
     * @param obj any object
     * @param name the name of a field on this object
     * @param asType the type to return the value as (converts as needed)
     * @return the value in the field as the type requested
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws FieldGetValueException if the field is not readable or not visible
     * @throws UnsupportedOperationException if the value cannot be converted to the type requested
     * @throws IllegalArgumentException if there is a failure getting the value
     */
    public <T> T getFieldValue(Object obj, String name, Class<T> asType) {
        Object o = getFieldValue(obj, name);
        T value = getConversionUtils().convert(o, asType);
        return value;
    }

    /**
     * Set the value of a field on an object (automatically auto converts),
     * name can be nested, indexed, or mapped
     * 
     * @param obj any object
     * @param name the name of a field on this object
     * @param value the value to set the field to (must match target exactly)
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws FieldSetValueException if the field is not writeable or visible
     * @throws IllegalArgumentException if there is a general failure setting the value
     */
    public void setFieldValue(Object obj, String name, Object value) {
        setFieldValue(obj, name, value, true);
    }

    /**
     * Set the value of a field on an object (optionally convert the value to the field type),
     * name can be nested, indexed, or mapped
     * 
     * @param obj any object
     * @param name the name of a field on this object
     * @param value the value to set the field to
     * @param autoConvert if true then the value will be converted to the target value type if it is possible,
     * otherwise the value must match the target type exactly
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws UnsupportedOperationException if this value cannot be auto converted to the type specified
     * @throws FieldSetValueException if the field is not writeable or visible
     * @throws IllegalArgumentException if there is a general failure setting the value
     */
    @SuppressWarnings("unchecked")
    public void setFieldValue(Object obj, String name, Object value, boolean autoConvert) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        // Resolve nested references
        Holder holder = unpackNestedName(name, obj, true);
        name = holder.getName();
        obj = holder.getObject();

        if (autoConvert) {
            // auto convert the value to the target type if possible
            //            String targetName = getResolver().getProperty(name); // simple name of target field
            // attempt to convert the value into the target type
            Class<?> type = getFieldType(obj, name);
            value = getConversionUtils().convert(value, type);
        }

        // set the value
        if (Map.class.isAssignableFrom(obj.getClass())) {
            setValueOfMap((Map) obj, name, value);
        } else if (getResolver().isMapped(name)) {
            setMappedValue(obj, name, value);
        } else if (getResolver().isIndexed(name)) {
            setIndexedValue(obj, name, value);
        } else {
            setSimpleValue(obj, name, value);
        }
    }

    /**
     * For setting an indexed value on an indexed object directly,
     * indexed objects are lists and arrays <br/>
     * NOTE: If the supplied index is invalid for the array then this will fail
     * 
     * @param indexedObject any array or list
     * @param index the index to put the value into (will append to the end of the list if index < 0), must be within the bounds of the array
     * @param value any value, will be converted to the correct type for the array automatically
     * @throws IllegalArgumentException if there is a failure because of an invalid index or null arguments
     * @throws FieldSetValueException if the field is not writeable or visible
     */
    @SuppressWarnings("unchecked")
    public void setIndexedValue(Object indexedObject, int index, Object value) {
        if (indexedObject == null) {
            throw new IllegalArgumentException("Invalid indexedObject, cannot be null");
        }
        if ( ConstructorUtils.isClassArray(indexedObject.getClass()) ) {
            // this is an array
            try {
                // set the value on the array
                // NOTE: cannot automatically expand the array
                Class<?> componentType = ArrayUtils.type((Object[])indexedObject);
                Object convert = ReflectUtils.getInstance().convert(value, componentType);
                Array.set(indexedObject, index, convert);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to set index ("+index+") for array of size ("+Array.getLength(indexedObject)+") to value: " + value, e);
            }
        } else if ( ConstructorUtils.isClassList(indexedObject.getClass()) ) {
            // this is a list
            List l = (List) indexedObject;
            try {
                // set value on list
                if (index < 0) {
                    l.add(value);
                } else {
                    if (index >= l.size()) {
                        // automatically expand the list
                        int start = l.size();
                        for (int i = start; i < (index+1); i++) {
                            l.add(i, null);
                        }
                    }
                    l.set(index, value);
                }
            } catch (Exception e) {
                // catching the general exception is correct here, translate the exception
                throw new IllegalArgumentException("Failed to set index ("+index+") for list of size ("+l.size()+") to value: " + value, e);
            }
        } else {
            // invalid
            //noinspection ConstantConditions
            throw new IllegalArgumentException("Object does not appear to be indexed (not an array or a list): "
                    + (indexedObject == null ? "NULL" : indexedObject.getClass()) );
        }
    }


    // INTERNAL - specific methods which are not really for general use

    /**
     * Traverses the nested name path to get to the requested name and object
     * @param fullName the full path name (e.g. thing.field1.field2.stuff)
     * @param object the object to traverse
     * @param autoCreate if true then create the nested objects to force successful traversal, else will throw NPE
     * @return a holder with the nested name (e.g. stuff) and the nested object
     * @throws NullPointerException if the path cannot be traversed
     * @throws IllegalArgumentException if the path is invalid
     */
    @SuppressWarnings("unchecked")
    protected Holder unpackNestedName(final String fullName, final Object object, boolean autoCreate) {
        String name = fullName;
        Object obj = object;
        Class<?> cls = object.getClass();
        try {
            // Resolve nested references
            while (getResolver().hasNested(name)) {
                String next = getResolver().next(name);
                Object nestedBean;
                if (Map.class.isAssignableFrom(obj.getClass())) {
                    nestedBean = getValueOfMap((Map) obj, next);
                } else if (getResolver().isMapped(next)) {
                    nestedBean = getMappedValue(obj, next);
                } else if (getResolver().isIndexed(next)) {
                    nestedBean = getIndexedValue(obj, next);
                } else {
                    nestedBean = getSimpleValue(obj, next);
                }
                if (nestedBean == null) {
                    // could not get the nested bean because it is unset
                    if (autoCreate) {
                        // create the nested bean
                        try {
                            Class<?> type = getFieldType(obj, next);
                            if (Object.class.equals(type)) {
                                // indeterminate type so we will make a map
                                type = ArrayOrderedMap.class;
                            }
                            nestedBean = getConstructorUtils().constructClass(type);
                            setFieldValue(obj, next, nestedBean, false); // need to put this new object into the parent
                        } catch (RuntimeException e) {
                            throw new IllegalArgumentException("Nested path failure: Could not create nested object ("
                                    +cls.getName()+") in path ("+fullName+"): " + e.getMessage(), e);
                        }
                    } else {
                        // no auto create so we have to fail here
                        throw new NullPointerException("Nested traversal failure: null field value for name (" 
                                + name + ") in nestedName ("+fullName+") on object class (" + cls + ") for object: " + obj);
                    }
                }
                obj = nestedBean;
                name = getResolver().remove(name);
            }
        } catch (FieldnameNotFoundException e) {
            // convert field name failure into illegal argument
            throw new IllegalArgumentException("Nested path failure: Invalid path name ("
                    +fullName+") contains invalid field names: " + e.getMessage(), e);
        }
        return new Holder(name, obj);
    }

    /**
     * For getting a value out of a bean based on a field name
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     */
    protected Object getSimpleValue(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        Object value;
        // Handle DynaBean instances specially
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            value = fieldAdapterManager.getFieldAdapter().getSimpleValue(obj, name);
        } else {
            // normal bean
            ClassFields<?> cf = analyzeObject(obj);
            try {
                // use the class property
                ClassProperty cp = cf.getClassProperty(name);
                value = findFieldValue(obj, cp);
            } catch (FieldnameNotFoundException fnfe) {
                // could not find this as a standard field so handle as internal lookup
                ClassData<?> cd = cf.getClassData();
                Field field = getFieldIfPossible(cd, name);
                if (field == null) {
                    throw new FieldnameNotFoundException("Could not find field with name ("+name+") on object (" + obj + ") after extended look into non-visible fields", fnfe);
                }
                try {
                    value = field.get(obj);
                } catch (Exception e) {
                    // catching the general exception is correct here, translate the exception
                    throw new FieldGetValueException("Field get failure getting value for field ("+name+") from non-visible field in object: " + obj, name, obj, e);
                }
            }
        }
        return value;
    }

    /**
     * For getting an indexed value out of an object based on field name,
     * name must be like: fieldname[index],
     * If the object is an array or a list then name can be the index only:
     * "[1]" or "[0]" (brackets must be included)
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is a failure
     * @throws FieldGetValueException if there is an internal failure getting the field
     */
    @SuppressWarnings("unchecked")
    protected Object getIndexedValue(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        Object value = null;
        Resolver resolver = getResolver();

        // get the index from the indexed name
        int index;
        try {
            index = resolver.getIndex(name);
            if (index < 0) {
                throw new IllegalArgumentException("Could not find index in name (" + name + ")");
            }
            // get the fieldname from the indexed name
            name = resolver.getProperty(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed field (" + name + ") on type (" + obj.getClass() + ")", e);
        }

        boolean indexedProperty = false;
        // Handle DynaBean instances specially
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            value = fieldAdapterManager.getFieldAdapter().getIndexedValue(obj, name, index);
        } else {
            boolean isArray = false;
            Object indexedObject = null;
            if (obj.getClass().isArray()) {
                indexedObject = obj;
                isArray = true;
            } else if (List.class.isAssignableFrom(obj.getClass())) {
                indexedObject = obj;
            } else {
                // normal bean
                ClassFields cf = analyzeObject(obj);
                ClassProperty cp = cf.getClassProperty(name);
                if (! cp.isIndexed()) {
                    throw new IllegalArgumentException("This field ("+name+") is not an indexed field");
                }
                isArray = cp.isArray();
                // try to get the indexed getter and use that first
                if (cp instanceof IndexedProperty) {
                    indexedProperty = true;
                    IndexedProperty icp = (IndexedProperty) cp;
                    try {
                        Method getter = icp.getIndexGetter();
                        //noinspection RedundantArrayCreation
                        value = getter.invoke(obj, new Object[] {index});
                    } catch (Exception e) {
                        // catching the general exception is correct here, translate the exception
                        throw new FieldGetValueException("Indexed getter method failure getting indexed ("
                                +index+") value for name ("+cp.getFieldName()+") from: " + obj, cp.getFieldName(), obj, e);
                    }
                } else {
                    indexedObject = findFieldValue(obj, cp);
                }
            }
            if (!indexedProperty) {
                // now get the indexed value if possible
                if (indexedObject != null) {
                    if (isArray) {
                        // this is an array
                        try {
                            // get value from array
                            value = Array.get(indexedObject, index);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            throw new IllegalArgumentException("Index ("+index+") is out of bounds ("+Array.getLength(indexedObject)+") for the array: " + indexedObject, e);
                        }
                    } else {
                        // this better be a list
                        if (! List.class.isAssignableFrom(indexedObject.getClass())) {
                            throw new IllegalArgumentException("Field (" + name + ") does not appear to be indexed (not an array or a list)");
                        } else {
                            // get value from list
                            try {
                                value = ((List)indexedObject).get(index);
                            } catch (IndexOutOfBoundsException e) {
                                throw new IllegalArgumentException("Index ("+index+") is out of bounds ("+((List)indexedObject).size()+") for the list: " + indexedObject, e);
                            }
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Indexed object is null, cannot retrieve index ("+index+") value from field ("+name+")");
                }
            }
        }
        return value;
    }

    /**
     * For getting a mapped value out of an object based on field name,
     * name must be like: fieldname[index]
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there are invalid arguments
     * @throws FieldGetValueException if there is an internal failure getting the field
     */
    @SuppressWarnings("unchecked")
    protected Object getMappedValue(Object obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        Object value = null;
        Resolver resolver = getResolver();

        // get the key from the mapped name
        String key;
        try {
            key = resolver.getKey(name);
            if (key == null) {
                throw new IllegalArgumentException("Could not find key in name (" + name + ")");
            }
            // get the fieldname from the mapped name
            name = resolver.getProperty(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mapped field (" + name + ") on type (" + obj.getClass() + ")", e);
        }

        boolean mappedProperty = false;
        // Handle DynaBean instances specially
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            value = fieldAdapterManager.getFieldAdapter().getMappedValue(obj, name, key);
        } else {
            Map map = null;
            if (Map.class.isAssignableFrom(obj.getClass())) {
                map = (Map) obj;
            } else {
                // normal bean
                ClassFields cf = analyzeObject(obj);
                ClassProperty cp = cf.getClassProperty(name);
                if (! cp.isMapped()) {
                    throw new IllegalArgumentException("This field ("+name+") is not an mapped field");
                }
                // try to get the mapped getter and use that first
                if (cp instanceof MappedProperty) {
                    mappedProperty = true;
                    MappedProperty mcp = (MappedProperty) cp;
                    try {
                        Method getter = mcp.getMapGetter();
                        //noinspection RedundantArrayCreation
                        value = getter.invoke(obj, new Object[] {key});
                    } catch (Exception e) {
                        // catching the general exception is correct here, translate the exception
                        throw new FieldGetValueException("Mapped getter method failure getting mapped ("
                                +key+") value for name ("+cp.getFieldName()+") from: " + obj, cp.getFieldName(), obj, e);
                    }
                } else {
                    Object o = findFieldValue(obj, cp);
                    if (! Map.class.isAssignableFrom(o.getClass())) {
                        throw new IllegalArgumentException("Field (" + name + ") does not appear to be a map (not instance of Map)");
                    }
                    map = (Map) o;
                }
            }
            // get the value from the map
            if (!mappedProperty) {
                if (map != null) {
                    try {
                        value = map.get(key);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Key ("+key+") is invalid ("+map.size()+") for the map: " + map, e);
                    }
                } else {
                    throw new IllegalArgumentException("Mapped object is null, cannot retrieve key ("+key+") value from field ("+name+")");
                }
            }
        }
        return value;
    }

    /**
     * For getting a value out of a map based on a field name which has a key in it,
     * name is expected to be the key for the map only: e.g. "mykey",
     * if it happens to be of the form: thing[mykey] then the key will be extracted
     * <br/>
     * <b>WARNING: Cannot handle a nested name or mapped/indexed key</b>
     * @return the value in the map with a key matching this name OR null if no key found
     */
    @SuppressWarnings("unchecked")
    protected Object getValueOfMap(Map map, String name) {
        Resolver resolver = getResolver();
        if (resolver.isMapped(name)) {
            String propName = resolver.getProperty(name);
            if (propName == null || propName.length() == 0) {
                name = resolver.getKey(name);
            }
        }

        Object value = map.get(name);
        return value;
    }

    /**
     * This will get the value from a field,
     * for internal use only,
     * Reduce code duplication
     * @param obj any object
     * @param cp the analysis object which must match the given object (defines the field)
     * @return the value for the field
     * @throws IllegalArgumentException if inputs are invalid (null)
     * @throws FieldGetValueException if there is an internal failure getting the field
     */
    protected Object findFieldValue(Object obj, ClassProperty cp) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (cp == null) {
            throw new IllegalArgumentException("ClassProperty cannot be null");
        }

        Object value;
        if (cp.isPublicField()) {
            Field field = cp.getField();
            try {
                value = field.get(obj);
            } catch (Exception e) {
                // catching the general exception is correct here, translate the exception
                throw new FieldGetValueException("Field get failure getting value for name ("+cp.getFieldName()+") from: " + obj, cp.getFieldName(), obj, e);
            }
        } else {
            // must be a property then
            Method getter = cp.getGetter();
            try {
                //noinspection RedundantArrayCreation
                value = getter.invoke(obj, new Object[0]);
            } catch (Exception e) {
                // catching the general exception is correct here, translate the exception
                throw new FieldGetValueException("Getter method failure getting value for name ("+cp.getFieldName()+") from: " + obj, cp.getFieldName(), obj, e);
            }
        }
        return value;
    }


    /**
     * Set a value on a field of an object, the types must match and the name must be identical
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     * @throws FieldSetValueException if there is an internal failure setting the field
     */
    protected void setSimpleValue(Object obj, String name, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        // Handle DynaBean instances specially
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            fieldAdapterManager.getFieldAdapter().setSimpleValue(obj, name, value);
        } else {
            // normal bean
            ClassFields<?> cf = analyzeObject(obj);
            try {
                ClassProperty cp = cf.getClassProperty(name);
                assignFieldValue(obj, cp, value);
            } catch (FieldnameNotFoundException fnfe) {
                // could not find this as a standard field so handle as internal lookup
                ClassData<?> cd = cf.getClassData();
                Field field = getFieldIfPossible(cd, name);
                if (field == null) {
                    throw new FieldnameNotFoundException("Could not find field with name ("+name+") on object (" + obj + ") after extended look into non-visible fields", fnfe);
                }
                try {
                    value = getConversionUtils().convert(value, field.getType());
                    field.set(obj, value);
                } catch (Exception e) {
                    // catching the general exception is correct here, translate the exception
                    throw new FieldSetValueException("Field set failure setting value ("+value+") for field ("+name+") from non-visible field in object: " + obj, name, obj, e);
                }
            }
        }
    }

    /**
     * For setting an indexed value on an object based on field name,
     * name must be like: fieldname[index]
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is a failure
     * @throws FieldSetValueException if there is an internal failure setting the field
     */
    @SuppressWarnings("unchecked")
    protected void setIndexedValue(Object obj, String name, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        Resolver resolver = getResolver();
        // get the index from the indexed name
        int index;
        try {
            index = resolver.getIndex(name);
            if (index < 0) {
                throw new IllegalArgumentException("Could not find index in name (" + name + ")");
            }
            // get the fieldname from the indexed name
            name = resolver.getProperty(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid indexed field (" + name + ") on type (" + obj.getClass() + ")", e);
        }

        boolean indexedProperty = false;
        // Handle DynaBean instances specially
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            fieldAdapterManager.getFieldAdapter().setIndexedValue(obj, name, index, value);
        } else {
            boolean isArray = false;
            Object indexedObject = null;
            if ( ConstructorUtils.isClassArray(obj.getClass()) ) {
                indexedObject = obj;
                isArray = true;
            } else if ( ConstructorUtils.isClassList(obj.getClass()) ) {
                indexedObject = obj;
            } else {
                // normal bean
                ClassFields cf = analyzeObject(obj);
                ClassProperty cp = cf.getClassProperty(name);
                if (! cp.isIndexed()) {
                    throw new IllegalArgumentException("This field ("+name+") is not an indexed field");
                }
                isArray = cp.isArray();
                // try to get the indexed setter and use that first
                if (cp instanceof IndexedProperty) {
                    indexedProperty = true;
                    IndexedProperty icp = (IndexedProperty) cp;
                    try {
                        Method setter = icp.getIndexSetter();
                        //noinspection RedundantArrayCreation
                        setter.invoke(obj, new Object[] {index, value});
                    } catch (Exception e) {
                        // catching the general exception is correct here, translate the exception
                        throw new FieldSetValueException("Indexed setter method failure setting indexed ("
                                +index+") value for name ("+cp.getFieldName()+") on: " + obj, cp.getFieldName(), obj, e);
                    }
                } else {
                    // get the field value out and work with it directly
                    indexedObject = findFieldValue(obj, cp);
                    if (indexedObject == null) {
                        // handle nulls by creating if possible
                        try {
                            if (isArray) {
                                // create the array if it is null
                                Class<?> type = value.getClass();
                                indexedObject = ArrayUtils.create(type, index+1);
                            } else { // List
                                // create the list if it is null, back-fill it, and assign it back to the object
                                Class<?> type = cp.getType();
                                if (type.isInterface()) {
                                    indexedObject = new ArrayList(index+1);
                                } else {
                                    indexedObject = type.newInstance();
                                }
                            }
                            setSimpleValue(obj, name, indexedObject);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Indexed object is null, attempt to create list failed, cannot set value for index ("+index+") on field ("+name+")", e);
                        }
                    }
                }
            }
            if (!indexedProperty) {
                // set the indexed value
                if (isArray) {
                    // this is an array
                    try {
                        // set the value on the array
                        int length = ArrayUtils.size((Object[])indexedObject);
                        if (index >= length) {
                            // automatically expand the array
                            indexedObject = ArrayUtils.resize((Object[])indexedObject, index+1);
                            setSimpleValue(obj, name, indexedObject); // need to put the array back into the object
                        }
                        // convert this value to the type for the array
                        Class<?> componentType = ArrayUtils.type((Object[])indexedObject);
                        Object convert = ReflectUtils.getInstance().convert(value, componentType);
                        Array.set(indexedObject, index, convert);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Failed to set index ("+index+") for array of size ("+Array.getLength(indexedObject)+") to value: " + value, e);
                    }
                } else {
                    // this better be a list
                    if (indexedObject == null 
                            || ! List.class.isAssignableFrom(indexedObject.getClass())) {
                        throw new IllegalArgumentException("Field (" + name + ") does not appear to be indexed (not an array or a list): " 
                                + (indexedObject == null ? "NULL" : indexedObject.getClass()) );
                    } else {
                        // this is a list
                        List l = (List) indexedObject;
                        try {
                            // set value on list
                            if (index < 0) {
                                l.add(value);
                            } else {
                                if (index >= l.size()) {
                                    // automatically expand the list
                                    int start = l.size();
                                    for (int i = start; i < (index+1); i++) {
                                        l.add(i, null);
                                    }
                                }
                                l.set(index, value);
                            }
                        } catch (Exception e) {
                            // catching the general exception is correct here, translate the exception
                            throw new IllegalArgumentException("Failed to set index ("+index+") for list of size ("+l.size()+") to value: " + value, e);
                        }
                    }
                }
            }
        }
    }

    /**
     * For getting a mapped value out of an object based on field name,
     * name must be like: fieldname[index]
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     * @throws FieldSetValueException if there is an internal failure setting the field
     */
    @SuppressWarnings("unchecked")
    protected void setMappedValue(Object obj, String name, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("field name cannot be null or blank");
        }

        Resolver resolver = getResolver();
        // get the key from the mapped name
        String key;
        try {
            key = resolver.getKey(name);
            if (key == null) {
                throw new IllegalArgumentException("Could not find key in name (" + name + ")");
            }
            // get the fieldname from the mapped name
            name = resolver.getProperty(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid mapped field (" + name + ") on type (" + obj.getClass() + ")", e);
        }

        boolean mappedProperty = false;
        // Handle DynaBean instances specially
        if (fieldAdapterManager.isAdaptableObject(obj)) {
            fieldAdapterManager.getFieldAdapter().setMappedValue(obj, name, key, value);
        } else {
            Map map = null;
            if (Map.class.isAssignableFrom(obj.getClass())) {
                map = (Map) obj;
            } else {
                // normal bean
                ClassFields cf = analyzeObject(obj);
                ClassProperty cp = cf.getClassProperty(name);
                if (! cp.isMapped()) {
                    throw new IllegalArgumentException("This field ("+name+") is not an mapped field");
                }
                // try to get the mapped setter and use that first
                if (cp instanceof MappedProperty) {
                    mappedProperty = true;
                    MappedProperty mcp = (MappedProperty) cp;
                    try {
                        Method setter = mcp.getMapSetter();
                        //noinspection RedundantArrayCreation
                        value = setter.invoke(obj, new Object[] {key, value});
                    } catch (Exception e) {
                        // catching the general exception is correct here, translate the exception
                        throw new FieldSetValueException("Mapped setter method failure setting mapped ("
                                +key+") value for name ("+cp.getFieldName()+") on: " + obj, cp.getFieldName(), obj, e);
                    }
                } else {
                    Object o = findFieldValue(obj, cp);
                    if (o == null) {
                        // create the map if it is null and assign it back to the object
                        try {
                            Class<?> type = cp.getType();
                            if (type.isInterface()) {
                                map = new ArrayOrderedMap(5);
                            } else {
                                map = (Map) type.newInstance();
                            }
                            setSimpleValue(obj, name, map);
                        } catch (Exception e) {
                            // catching the general exception is correct here, translate the exception
                            throw new IllegalArgumentException("Mapped object is null, attempt to create map failed, cannot set value for key ("+key+") on field ("+name+")", e);
                        }
                    } else {
                        if (! Map.class.isAssignableFrom(o.getClass())) {
                            throw new IllegalArgumentException("Field (" + name + ") does not appear to be a map (not instance of Map)");
                        }
                        map = (Map) o;
                    }
                }
            }
            if (!mappedProperty) {
                // set value in map
                if (map == null) {
                    throw new IllegalArgumentException("Mapped object is null, cannot set value for key ("+key+") on field ("+name+")");
                }
                // set value on map
                try {
                    map.put(key, value);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Value ("+value+") cannot be put for key ("+key+") for the map: " + map, e);
                }
            }
        }
    }

    /**
     * Set a value on a map using the name as the key,
     * name is expected to be the key for the map only: e.g. "mykey",
     * if it happens to be of the form: thing[mykey] then the key will be extracted
     * <br/>
     * <b>WARNING: Cannot handle a nested name or mapped/indexed key</b>
     */
    @SuppressWarnings("unchecked")
    protected void setValueOfMap(Map map, String name, Object value) {
        Resolver resolver = getResolver();
        if (resolver.isMapped(name)) {
            String propName = resolver.getProperty(name);
            if (propName == null || propName.length() == 0) {
                name = resolver.getKey(name);
            }
        }

        map.put(name, value);
    }

    /**
     * This will set the value on a field, types must match,
     * for internal use only,
     * Reduce code duplication
     * @param obj any object
     * @param cp the analysis object which must match the given name and object
     * @param value the value for the field
     * @throws FieldSetValueException if the field is not writeable or visible
     * @throws IllegalArgumentException if inputs are invalid (null)
     */
    protected void assignFieldValue(Object obj, ClassProperty cp, Object value) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (cp == null) {
            throw new IllegalArgumentException("ClassProperty cannot be null");
        }
        if (cp.isPublicField()) {
            Field field = cp.getField();
            try {
                field.set(obj, value);
            } catch (Exception e) {
                // catching the general exception is correct here, translate the exception
                throw new FieldSetValueException("Field set failure setting value ("+value+") for name ("+cp.getFieldName()+") on: " + obj, 
                        cp.getFieldName(), value, obj, e);
            }
        } else {
            // must be a property then
            Method setter = cp.getSetter();
            try {
                //noinspection RedundantArrayCreation
                setter.invoke(obj, new Object[] {value});
            } catch (Exception e) {
                throw new FieldSetValueException("Setter method failure setting value ("+value+") for name ("+cp.getFieldName()+") on: " + obj, 
                        cp.getFieldName(), value, obj, e);
            }
        }
    }

    /**
     * Get the field if it exists for this class
     * @param cd the class data cache object
     * @param name the name of the field
     * @return the field if found OR null if not
     */
    protected Field getFieldIfPossible(ClassData<?> cd, String name) {
        Field f = null;
        List<Field> fields = cd.getFields();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                f = field;
                break;
            }
        }
        return f;
    }

    public static final class Holder {
        public String name;
        public Object object;
        public Holder(String name, Object object) {
            this.name = name;
            this.object = object;
        }
        public String getName() {
            return name;
        }
        public Object getObject() {
            return object;
        }
    }

    @Override
    public String toString() {
        return "Field::c="+FieldUtils.timesCreated+":s="+singleton+":resolver=" + getResolver().getClass().getName();
    }

    // STATIC access

    protected static SoftReference<FieldUtils> instanceStorage;
    /**
     * Get a singleton instance of this class to work with (stored statically) <br/>
     * <b>WARNING</b>: do not hold onto this object or cache it yourself, call this method again if you need it again
     * @return a singleton instance of this class
     */
    public static FieldUtils getInstance() {
        FieldUtils instance = (instanceStorage == null ? null : instanceStorage.get());
        if (instance == null) {
            instance = FieldUtils.setInstance(null);
        }
        return instance;
    }
    /**
     * Set the singleton instance of the class which will be stored statically
     * @param newInstance the instance to use as the singleton instance
     */
    public static FieldUtils setInstance(FieldUtils newInstance) {
        FieldUtils instance = newInstance;
        if (instance == null) {
            instance = new FieldUtils();
            instance.singleton = true;
        }
        FieldUtils.timesCreated++;
        instanceStorage = new SoftReference<FieldUtils>(instance);
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
    public boolean isSingleton() {
        return singleton;
    }

}
