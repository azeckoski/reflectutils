/**
 * $Id: SQLDateConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/SQLDateConverter.java $
 * SQLDateConverter.java - genericdao - Sep 8, 2008 2:31:38 PM - azeckoski
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

import java.sql.Date;

import org.azeckoski.reflectutils.converters.api.Converter;



/**
 * Passthrough to {@link DateConverter} for {@link Date}
 * @see DateConverter
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class SQLDateConverter implements Converter<Date> {

    public Date convert(Object value) {
        return DateConverter.convertToType(Date.class, value);
    }

}
