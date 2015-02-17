/**
 * $Id: MapConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/MapConverter.java $
 * MapConverter.java - genericdao - Sep 9, 2008 2:04:53 PM - azeckoski
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
import org.azeckoski.reflectutils.FieldUtils;
import org.azeckoski.reflectutils.ClassFields.FieldsFilter;
import org.azeckoski.reflectutils.converters.api.InterfaceConverter;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;


/**
 * Map converter to handle converting things into maps,
 * Can handle simple cases by stuffing them into the map with the key "data"
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@SuppressWarnings("unchecked")
public class MapConverter implements InterfaceConverter<Map> {

    public Map convert(Object value) {
        return convertInterface(value, ArrayOrderedMap.class);
    }

    protected ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    protected FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }

    public Map convertInterface(Object value, Class<? extends Map> implementationType) {
        Map convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;
        if (implementationType == null || implementationType.isInterface()) {
            implementationType = ArrayOrderedMap.class;
        }
        convert = (Map<String, Object>) getConstructorUtils().constructClass(implementationType);
        if ( ConstructorUtils.isClassArray(fromType) ) {
            // from array
            int length = Array.getLength(toConvert);
            for (int i = 0; i < length; i++) {
                Object aVal = Array.get(toConvert, i);
                convert.put(i+"", aVal);
            }
        } else if ( ConstructorUtils.isClassCollection(fromType) ) {
            // from collection
            int i = 0;
            for (Object object : (Collection) value) {
                convert.put(i+"", object);
                i++;
            }
        } else if ( ConstructorUtils.isClassMap(fromType) ) {
            // from map - this is a case where we are going from one map type to another
            convert.putAll((Map)value);
        } else {
            // from scalar
            if (ConstructorUtils.isClassSimple(fromType)) {
                convert.put("data", toConvert);
            } else {
                // convert bean to map
                convert = getFieldUtils().getFieldValues(value, FieldsFilter.COMPLETE, false);
            }
        }
        return convert;
    }

}
