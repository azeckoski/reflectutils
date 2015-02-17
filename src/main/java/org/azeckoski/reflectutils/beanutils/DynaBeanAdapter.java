/**
 * $Id: DynaBeanAdapter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/beanutils/DynaBeanAdapter.java $
 * DynaBeanAdapter.java - genericdao - Sep 20, 2008 10:08:01 AM - azeckoski
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.exceptions.FieldnameNotFoundException;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;


/**
 * This allows dynabeans to work with the field utils,
 * should only be loaded by reflection if the DynaBean class can be found
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class DynaBeanAdapter implements FieldAdapter {

    public boolean isAdaptableObject(Object obj) {
        boolean adaptable = false;
        if (obj instanceof DynaBean) {
            adaptable = true;
        }
        return adaptable;
    }

    public boolean isAdaptableClass(Class<?> beanClass) {
        boolean adaptable = false;
        if (DynaBean.class.isAssignableFrom(beanClass)) {
            adaptable = true;
        }
        return adaptable;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getFieldType(java.lang.Object, java.lang.String)
     */
    public Class<?> getFieldType(Object obj, String name) {
        DynaClass dynaClass = ((DynaBean) obj).getDynaClass();
        DynaProperty dynaProperty = dynaClass.getDynaProperty(name);
        if (dynaProperty == null) {
            throw new FieldnameNotFoundException("DynaBean: Could not find this fieldName ("+name+") on the target object: " + obj, name, null);
        }
        return dynaProperty.getType();
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getFieldValues(java.lang.Object, org.azeckoski.reflectutils.ClassFields.FieldsFilter)
     */
    public Map<String, Object> getFieldValues(Object obj, FieldsFilter filter) {
        Map<String, Object> values = new HashMap<String, Object>();
        DynaProperty[] descriptors =
            ((DynaBean) obj).getDynaClass().getDynaProperties();
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            // cannot filter the values for dynabeans -AZ
            Object o = getSimpleValue(obj, name);
            values.put(name, o);
        }
        return values;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getSimpleValue(java.lang.Object, java.lang.String)
     */
    public Object getSimpleValue(Object obj, String name) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new FieldnameNotFoundException(name);
        }
        Object value = (((DynaBean) obj).get(name));
        return value;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getIndexedValue(java.lang.Object, java.lang.String, int)
     */
    public Object getIndexedValue(Object obj, String name, int index) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new FieldnameNotFoundException(name);
        }
        Object value = ((DynaBean) obj).get(name, index);
        return value;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#getMappedValue(java.lang.Object, java.lang.String, java.lang.String)
     */
    public Object getMappedValue(Object obj, String name, String key) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new FieldnameNotFoundException(name);
        }
        Object value = ((DynaBean) obj).get(name, key);
        return value;
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setIndexedValue(java.lang.Object, java.lang.String, int, java.lang.Object)
     */
    public void setIndexedValue(Object obj, String name, int index, Object value) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new FieldnameNotFoundException(name);
        }
        ((DynaBean) obj).set(name, index, value);
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setMappedValue(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object)
     */
    public void setMappedValue(Object obj, String name, String key, Object value) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new FieldnameNotFoundException(name);
        }
        ((DynaBean) obj).set(name, key, value);
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.beanutils.FieldAdapter#setSimpleValue(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public void setSimpleValue(Object obj, String name, Object value) {
        DynaProperty descriptor =
            ((DynaBean) obj).getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new FieldnameNotFoundException(name);
        }
        ((DynaBean) obj).set(name, value);
    }

    public Object newInstance(Object bean) {
        try {
            return ((DynaBean) bean).getDynaClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate DynaBean: " + bean, e);
        } // make new dynabean
    }

    public List<String> getPropertyNames(Object bean) {
        List<String> names = new ArrayList<String>();
        DynaProperty origDescriptors[] =
            ((DynaBean) bean).getDynaClass().getDynaProperties();
        for (DynaProperty dynaProperty : origDescriptors) {
            String name = dynaProperty.getName();
            names.add(name);
        }
        return names;
    }

}
