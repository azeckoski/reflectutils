/**
 * $Id: DefaultFieldAdapter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/beanutils/DefaultFieldAdapter.java $
 * DefaultFieldAdapter.java - genericdao - Sep 20, 2008 10:38:59 AM - azeckoski
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


/**
 * Does nothing but implement with the defaults, used when the normal adapter is not available
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class DefaultFieldAdapter implements FieldAdapter {

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#isAdaptableObject(java.lang.Object)
     */
    public boolean isAdaptableObject(Object obj) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#isAdaptableClass(java.lang.Class)
     */
    public boolean isAdaptableClass(Class<?> beanClass) {
        return false;
    }

    // NOTE: nothing below here should ever get called

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getFieldType(java.lang.Object, java.lang.String)
     */
    public Class<?> getFieldType(Object obj, String name) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getFieldValues(java.lang.Object, org.azeckoski.reflectutils.ClassFields.FieldsFilter)
     */
    public Map<String, Object> getFieldValues(Object obj, FieldsFilter filter) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getIndexedValue(java.lang.Object, java.lang.String, int)
     */
    public Object getIndexedValue(Object obj, String name, int index) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getMappedValue(java.lang.Object, java.lang.String, java.lang.String)
     */
    public Object getMappedValue(Object obj, String name, String key) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getSimpleValue(java.lang.Object, java.lang.String)
     */
    public Object getSimpleValue(Object obj, String name) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setIndexedValue(java.lang.Object, java.lang.String, int, java.lang.Object)
     */
    public void setIndexedValue(Object obj, String name, int index, Object value) {
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setMappedValue(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object)
     */
    public void setMappedValue(Object obj, String name, String key, Object value) {
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setSimpleValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public void setSimpleValue(Object obj, String name, Object value) {
    }

    public List<String> getPropertyNames(Object bean) {
        return null;
    }

    public Object newInstance(Object bean) {
        return null;
    }

}
