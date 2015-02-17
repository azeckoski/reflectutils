/**
 * $Id: CharacterConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/CharacterConverter.java $
 * CharacterConverter.java - genericdao - Sep 8, 2008 2:37:03 PM - azeckoski
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
 * Converts objects to a character (will mostly truncate the string value of the object)
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class CharacterConverter implements Converter<Character> {

    public Character convert(Object value) {
        Character c = null;
        if (value instanceof char[]) {
            char[] ca = (char[]) value;
            if (ca.length > 0) {
                c = ca[0];
            }
        } else {
            String s = value.toString();
            if (s.length() > 0) {
                c = s.charAt(0);
            }
        }
        if (c == null) {
            throw new UnsupportedOperationException("Character convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a char");
        }
        return c;
    }

}
