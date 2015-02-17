/**
 * $Id: ClassConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/ClassConverter.java $
 * ClassConverter.java - genericdao - Sep 8, 2008 2:47:07 PM - azeckoski
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

import org.azeckoski.reflectutils.ClassLoaderUtils;
import org.azeckoski.reflectutils.converters.api.Converter;


/**
 * Converts a string to a class (this is pretty much the only conversion supported)
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ClassConverter implements Converter<Class<?>> {

    public Class<?> convert(Object value) {
        String className = value.toString();
        Class<?> c = ClassLoaderUtils.getClassFromString(className);
        if (c == null) {
            throw new UnsupportedOperationException("Class convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a Class");
        }
        return c;
    }

}
