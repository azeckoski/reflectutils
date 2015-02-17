/**
 * $Id: CollectionConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/CollectionConverter.java $
 * CollectionConverter.java - genericdao - Sep 9, 2008 4:52:53 PM - azeckoski
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
import java.util.Vector;

import org.azeckoski.reflectutils.ConstructorUtils;
import org.azeckoski.reflectutils.converters.api.InterfaceConverter;


/**
 * Converter for collections (primarily for converting to other types of collections),
 * can also from a scalar to a collection by placing the scalar value into the collection
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@SuppressWarnings("unchecked")
public class CollectionConverter implements InterfaceConverter<Collection> {

    public Collection convert(Object value) {
        return convertInterface(value, Vector.class);
    }

    protected ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    public Collection convertInterface(Object value, Class<? extends Collection> implementationType) {
        Collection convert = null;
        Class<?> fromType = value.getClass();
        Object toConvert = value;
        if (implementationType == null) {
            implementationType = Vector.class;
        } else if (implementationType.isInterface()) {
            implementationType = ConstructorUtils.getClassFromInterface(implementationType);
        }
        convert = getConstructorUtils().constructClass(implementationType);
        if ( ConstructorUtils.isClassArray(fromType) ) {
            // from array
            int length = Array.getLength(toConvert);
            for (int i = 0; i < length; i++) {
                Object aVal = Array.get(toConvert, i);
                convert.add( aVal );
            }
        } else if ( ConstructorUtils.isClassCollection(fromType) ) {
            // from collection - to other type of collection
            Collection<?> collection = (Collection) toConvert;
            convert.addAll(collection);
        } else if ( ConstructorUtils.isClassMap(fromType) ) {
            // from map
            Map map = (Map) toConvert;
            convert.addAll(map.values());
        } else {
            // from scalar
            convert.add(toConvert);
        }
        return convert;
    }

}
