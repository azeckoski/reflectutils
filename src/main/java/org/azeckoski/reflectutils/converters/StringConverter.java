/**
 * $Id: StringConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/StringConverter.java $
 * StringConverter.java - genericdao - Sep 8, 2008 2:34:47 PM - azeckoski
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

import org.azeckoski.reflectutils.converters.api.Converter;



/**
 * Converter to handle anything to string (this handles it in the way you would think)
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class StringConverter implements Converter<String> {

    public String convert(Object value) {
        String convert = "";
        if (value != null) {
            convert = value.toString();
        }
        return convert;
    }

}
