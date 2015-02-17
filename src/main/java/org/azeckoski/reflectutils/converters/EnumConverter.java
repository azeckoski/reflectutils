/**
 * $Id: EnumConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/EnumConverter.java $
 * EnumConverter.java - genericdao - Sep 11, 2008 5:35:21 PM - azeckoski
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

import java.util.List;

import org.azeckoski.reflectutils.ClassFields;
import org.azeckoski.reflectutils.FieldUtils;
import org.azeckoski.reflectutils.converters.api.InterfaceConverter;


/**
 * Handles conversions to enums (this is fairly limited to simply converting strings to enums)
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@SuppressWarnings("unchecked")
public class EnumConverter implements InterfaceConverter<Enum> {

    public Enum convert(Object value) {
        // no default so we cannot handle this case
        throw new IllegalArgumentException("Cannot convert to an enum without having the enum type available");
    }

    protected FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }

    public Enum convertInterface(Object value, Class<? extends Enum> implementationType) {
        Enum convert = null;
        String name = value.toString(); // can only deal with strings
        // now we try to find the enum field in this enum class
        ClassFields<Enum> cf = (ClassFields<Enum>) getFieldUtils().analyzeClass(implementationType);
        List<Enum> enumConstants = cf.getClassData().getEnumConstants();
        for (Enum e : enumConstants) {
            if (e.name().equals(name)) {
                // found the matching enum
                convert = e;
                break;
            }
        }
        if (convert == null) {
            throw new UnsupportedOperationException("Failure attempting to create enum for name ("+name+") in ("+implementationType+")");
        }
        return convert;
    }

}
