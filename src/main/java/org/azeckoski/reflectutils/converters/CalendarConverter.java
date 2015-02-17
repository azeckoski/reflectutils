/**
 * $Id: CalendarConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/CalendarConverter.java $
 * CalendarConverter.java - genericdao - Sep 8, 2008 12:45:51 PM - azeckoski
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

import java.text.DateFormat;
import java.util.Calendar;

import org.azeckoski.reflectutils.converters.api.Converter;



/**
 * Passthrough to {@link DateConverter} for {@link Calendar}
 * @see DateConverter
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class CalendarConverter extends BaseDateFormatHolder implements Converter<Calendar> {

    public Calendar convert(Object value) {
        return DateConverter.convertToType(Calendar.class, value, getDateFormats());
    }

    // CONSTRUCTORS
    public CalendarConverter() {
        super();
    }

    public CalendarConverter(String[] patterns) {
        super(patterns);
    }

    public CalendarConverter(DateFormat[] formats) {
        super(formats);
    }

}
