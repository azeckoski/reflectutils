/**
 * $Id: ArrayConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/ArrayConverter.java $
 * ArrayConverter.java - genericdao - Sep 10, 2008 1:36:28 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.converters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.azeckoski.reflectutils.ConstructorUtils;
import org.azeckoski.reflectutils.ConversionUtils;
import org.azeckoski.reflectutils.converters.api.VariableConverter;


/**
 * Handles conversions to arrays from various types (including other arrays),
 * also handles the special case of a comma separated list of strings which
 * it will attempt to convert into an array of strings or whatever was requested
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ArrayConverter implements VariableConverter {

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.converters.api.VariableConverter#canConvert(java.lang.Object, java.lang.Class)
     */
    public boolean canConvert(Object value, Class<?> toType) {
        if ( ConstructorUtils.isClassArray(toType)) {
            return true;
        }
        return false;
    }

    protected ConversionUtils getConversionUtils() {
        return ConversionUtils.getInstance();
    }

    /* (non-Javadoc)
     * @see org.azeckoski.reflectutils.converters.api.VariableConverter#convert(java.lang.Object, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public <T> T convert(Object value, Class<T> toType) {
        T convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;

        Class<?> componentType = toType.getComponentType();
        if ( ConstructorUtils.isClassArray(fromType) ) {
            // from array - to different type of array
            int length = Array.getLength(toConvert);
            convert = (T) Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Object object = Array.get(toConvert, i);
                object = getConversionUtils().convert(object, componentType); // convert each value
                Array.set(convert, i, object);
            }
        } else if ( ConstructorUtils.isClassCollection(fromType) ) {
            // from collection
            Collection collection = (Collection) toConvert;
            int length = collection.size();
            convert = (T) Array.newInstance(componentType, length);
            int i = 0;
            for (Object object : collection) {
                object = getConversionUtils().convert(object, componentType); // convert each value
                Array.set(convert, i, object);
                i++;
            }
        } else if ( ConstructorUtils.isClassMap(fromType) ) {
            // from map
            Map map = (Map) toConvert;
            int length = map.size();
            convert = (T) Array.newInstance(componentType, length);
            int i = 0;
            for (Object object : map.values()) {
                object = getConversionUtils().convert(object, componentType); // convert each value
                Array.set(convert, i, object);
                i++;
            }
        } else {
            // from scalar
            String valueString = toConvert.toString();
            if ("".equals(valueString)) {
                // empty string becomes empty array
                convert = (T) Array.newInstance(componentType, 0);
            } else if (valueString.indexOf(',') > 0) {
                // support comma separated string to array
                String[] parts = valueString.split(",");
                convert = (T) Array.newInstance(componentType, parts.length);
                for (int i = 0; i < parts.length; i++) {
                    Object object = getConversionUtils().convert(parts[i].trim(), componentType); // convert each value
                    Array.set(convert, i, object);
                }
            } else {
                // just put it in the array
                convert = (T) Array.newInstance(componentType, 1);
                Object object = getConversionUtils().convert(toConvert, componentType); // convert each value
                Array.set(convert, 0, object);
            }
        }
        return convert;
    }

}
