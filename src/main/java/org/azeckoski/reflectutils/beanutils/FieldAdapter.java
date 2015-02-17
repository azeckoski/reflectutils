/**
 * $Id: FieldAdapter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/beanutils/FieldAdapter.java $
 * FieldAdapter.java - genericdao - Sep 20, 2008 8:15:36 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.beanutils;

import java.util.List;
import java.util.Map;

import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.exceptions.FieldGetValueException;
import org.azeckoski.reflectutils.exceptions.FieldSetValueException;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;


/**
 * Interface that allows us to adapt to special class types like the apache dynabeans without
 * requiring them to be a dependency unless they happen to be in the ClassLoader
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface FieldAdapter {

    /**
     * @param obj any object
     * @return true if this object is part of the set this adapter is made to handle, false otherwise
     */
    public boolean isAdaptableObject(Object obj);

    /**
     * @param beanClass any class
     * @return true if this class is adaptable, false otherwise
     */
    public boolean isAdaptableClass(Class<?> beanClass);

    /**
     * @return a new instance of the adapted class type based on a sample
     */
    public Object newInstance(Object bean);

    /**
     * @param bean any bean of the adaptable class type
     * @return the list of property names for that bean
     */
    public List<String> getPropertyNames(Object bean);

    /**
     * Finds the type for a field based on the containing object and the field name
     * @param obj any object
     * @param name the name of a field in this object (can be nested, indexed, mapped, etc.)
     * @return the type of the field (will be {@link Object} if the type is indeterminate)
     * @throws FieldnameNotFoundException if the name is invalid for this obj
     * @throws IllegalArgumentException if the params are null
     */
    public Class<?> getFieldType(Object obj, String name);

    /**
     * Get the values of all fields on an object but optionally filter the fields used
     * @param obj any object
     * @param filter (optional) indicates the fields to return the values for, can be null for defaults <br/>
     * WARNING: getting the field values from settable only fields works as expected (i.e. you will an empty map)
     * @return a map of field name -> value
     * @throws IllegalArgumentException if the obj is null
     */
    public Map<String, Object> getFieldValues(Object obj, FieldsFilter filter);

    /**
     * For getting a value out of a bean based on a field name,
     * mostly for internal usage, use getFieldValue
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * 
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     */
    public Object getSimpleValue(Object obj, String name);

    /**
     * For getting an indexed value out of an object based on field name,
     * mostly for internal usage, use getFieldValue
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * 
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is a failure
     * @throws FieldGetValueException if there is an internal failure getting the field
     */
    public Object getIndexedValue(Object obj, String name, int index);

    /**
     * For getting a mapped value out of an object based on field name,
     * mostly for internal usage, use getFieldValue
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there are invalid arguments
     * @throws FieldGetValueException if there is an internal failure getting the field
     */
    public Object getMappedValue(Object obj, String name, String key);

    /**
     * Set a value on a field of an object, the types must match and the name must be identical
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     * @throws FieldSetValueException if there is an internal failure setting the field
     */
    public void setSimpleValue(Object obj, String name, Object value);

    /**
     * For getting an indexed value out of an object based on field name,
     * mostly for internal usage, use getFieldValue
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     * @throws FieldSetValueException if there is an internal failure setting the field
     */
    public void setIndexedValue(Object obj, String name, int index, Object value);

    /**
     * For getting a mapped value out of an object based on field name,
     * mostly for internal usage, use getFieldValue
     * <br/>
     * <b>WARNING: Cannot handle a nested/mapped/indexed name</b>
     * @throws FieldnameNotFoundException if this field name is invalid for this object
     * @throws IllegalArgumentException if there is failure
     * @throws FieldSetValueException if there is an internal failure setting the field
     */
    public void setMappedValue(Object obj, String name, String key, Object value);

}
