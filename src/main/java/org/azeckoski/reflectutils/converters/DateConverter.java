/**
 * $Id: DateConverter.java 65 2010-04-05 14:53:55Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/DateConverter.java $
 * DateConverter.java - genericdao - Sep 8, 2008 12:52:47 PM - azeckoski
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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.azeckoski.reflectutils.ConstructorUtils;
import org.azeckoski.reflectutils.converters.api.Converter;


/**
 * Handles conversions from objects into {@link Date},
 * generally the best way to handle this is to simply do conversions based on longs<br/>
 * Allows control over the conversion formats which are used by settings the patterns or the formats<br/>
 * <br/>
 * Based on code from commons bean utils but updated for generics and removed complexity and (mis)usage of Exception<br/>
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class DateConverter extends BaseDateFormatHolder implements Converter<Date> {

    public Date convert(Object value) {
        return DateConverter.convertToType(Date.class, value, getDateFormats());
    }

    // CONSTRUCTORS
    public DateConverter() {
        super();
    }

    public DateConverter(String[] patterns) {
        super(patterns);
    }

    public DateConverter(DateFormat[] formats) {
        super(formats);
    }

    
    /**
     * Convert an object to one of the following date types<br/>
     * <ul>
     *     <li><code>java.util.Date</code></li>
     *     <li><code>java.util.Calendar</code></li>
     *     <li><code>java.sql.Date</code></li>
     *     <li><code>java.sql.Time</code></li>
     *     <li><code>java.sql.Timestamp</code></li>
     * </ul>
     * 
     * @param <T>
     * @param targetType the type to convert to (should be one of the given date types)
     * @param value any object
     * @return the converted value
     */
    public static <T> T convertToType(Class<T> targetType, Object value) {
        return convertToType(targetType, value, null);
    }

    /**
     * @param <T>
     * @param targetType the type to convert to (should be one of the given date types)
     * @param value any object
     * @param dateFormats include date formats to use during the string -> date conversion
     * @return the converted value
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToType(Class<T> targetType, Object value, DateFormat[] dateFormats) {
        if (value == null) {
            throw new IllegalArgumentException("value to convert cannot be null");
        }

        // Handle java.sql.Timestamp
        if (value instanceof java.sql.Timestamp) {
            long timeInMillis = ((Timestamp)value).getTime();
            // ---------------------- JDK 1.3 Fix ----------------------
            // N.B. Prior to JDK 1.4 the Timestamp's getTime() method
            //      didn't include the milliseconds. The following code
            //      ensures it works consistently accross JDK versions
//            java.sql.Timestamp timestamp = (java.sql.Timestamp)value;
//            long timeInMillis = ((timestamp.getTime() / 1000) * 1000);
//            timeInMillis += timestamp.getNanos() / 1000000;
            // ---------------------- JDK 1.3 Fix ----------------------
            return (T) toDate(targetType, timeInMillis);
        }

        // Handle Date (includes java.sql.Date & java.sql.Time)
        if (value instanceof Date) {
            long time = ((Date)value).getTime();
            return (T) toDate(targetType, time);
        }

        // Handle Calendar
        if (value instanceof Calendar) {
            long time = ((Calendar)value).getTimeInMillis();
            return (T) toDate(targetType, time);
        }

        // Handle numbers
        if (value instanceof Number) {
            long num = ((Number)value).longValue();
            return (T) toDate(targetType, num);
        }

        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            Object defaultValue = ConstructorUtils.getDefaultValue(targetType);
            if (defaultValue == null) {
                throw new UnsupportedOperationException("Date convert failure: Could not convert empty string to target ("+targetType+") or get a default value for it");
            }
            return (T) defaultValue;
        } else {
            // try to get the long value out of the string
            try {
                long num = new Long(stringValue);
                if (num > 30000000l) {
                    // must be a UTC time code, also, we only lost a week since 1970 so whatever
                    return (T) toDate(targetType, num);
                } else if (num > 10000l) {
                    // probably is a date like: YYYYMMDD so convert it
                    int date = (int) (num % 100);
                    int remain = (int) (num / 100);
                    int month = remain % 100;
                    int year = remain / 100;
                    if (date <= 31 && month <= 12) {
                        if (month > 0) { month -= 1; }
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.set(year, month, date);
                        long time = calendar.getTimeInMillis();
                        return (T) toDate(targetType, time);
                    }
                }
            } catch (NumberFormatException e) {
                // ok, not a long probably so try parsing
            }
        }

        // Parse the Date/Time
        Calendar calendar = parse(stringValue, dateFormats);
        if (calendar != null) {
            long time = ((Calendar)value).getTimeInMillis();
            return (T) toDate(targetType, time);
        }

        // Default String conversion
        return (T) toDate(targetType, stringValue);
    }

    /**
     * Convert a long value to the specified Date type for this
     * <i>Converter</i>.
     * <p>
     *
     * This method handles conversion to the following types:
     * <ul>
     *     <li><code>java.util.Date</code></li>
     *     <li><code>java.util.Calendar</code></li>
     *     <li><code>java.sql.Date</code></li>
     *     <li><code>java.sql.Time</code></li>
     *     <li><code>java.sql.Timestamp</code></li>
     * </ul>
     *
     * @param type The Date type to convert to
     * @param value The long value to convert.
     * @return The converted date value.
     */
    protected static Object toDate(Class<?> type, long value) {

        // java.util.Date
        if (type.equals(Date.class)) {
            return new Date(value);
        }

        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            return new java.sql.Date(value);
        }

        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            return new java.sql.Time(value);
        }

        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            return new java.sql.Timestamp(value);
        }

        // java.util.Calendar
        if (type.equals(Calendar.class)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(value);
//            calendar.setLenient(false);
            return calendar;
        }

        throw new UnsupportedOperationException("Date conversion failure: cannot convert long value ("+value+") to ("+type+")");
    }

    /**
     * Default String to Date conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li><code>java.sql.Date</code></li>
     *     <li><code>java.sql.Time</code></li>
     *     <li><code>java.sql.Timestamp</code></li>
     * </ul>
     * <p>
     * <strong>N.B.</strong> No default String conversion
     * mechanism is provided for <code>java.util.Date</code>
     * and <code>java.util.Calendar</code> type.
     *
     * @param type The Number type to convert to
     * @param value The String value to convert.
     * @return The converted Number value.
     */
    protected static Object toDate(Class<?> type, String value) {
        // java.sql.Date
        if (type.equals(java.sql.Date.class)) {
            try {
                return java.sql.Date.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException("Date conversion failure: " +
                "String must be in JDBC format [yyyy-MM-dd] to create a java.sql.Date");
            }
        }

        // java.sql.Time
        if (type.equals(java.sql.Time.class)) {
            try {
                return java.sql.Time.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException("Date conversion failure: " +
                "String must be in JDBC format [HH:mm:ss] to create a java.sql.Time");
            }
        }

        // java.sql.Timestamp
        if (type.equals(java.sql.Timestamp.class)) {
            try {
                return java.sql.Timestamp.valueOf(value);
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException("Date conversion failure: " +
                        "String must be in JDBC format [yyyy-MM-dd HH:mm:ss.fffffffff] " +
                "to create a java.sql.Timestamp");
            }
        }

        throw new UnsupportedOperationException("Date conversion failure: cannot convert string value ("+value+") to ("+type+")");
    }

    /**
     * Parse a String date value using the set of patterns.
     *
     * @param value The String date value.
     * @return The converted Date object.
     * @throws Exception if an error occurs parsing the date.
     */
    protected static Calendar parse(String value, DateFormat[] dateFormats) {
        Calendar calendar = null;
        if (dateFormats != null && dateFormats.length > 0) {
            for (DateFormat format : dateFormats) {
                calendar = parse(value, format);
                if (calendar != null) {
                    break;
                }
            }
        }
        if (calendar == null) {
            // just do the simple attempt now
            DateFormat format = new SimpleDateFormat();
            try {
                Date date = format.parse(value);
                if (date != null) {
                    calendar = format.getCalendar();
                }
            } catch (ParseException e) {
                calendar = null;
            }
        }
        return calendar;
    }

    /**
     * Parse a String into a <code>Calendar</code> object using the specified <code>DateFormat</code><br/>
     * Does exact matching (non-lenient) only on the given pattern<br/>
     *
     * @param value The String date value.
     * @param format The DateFormat to parse the String value.
     * @return The converted Calendar object OR null if it cannot be converted
     */
    protected static Calendar parse(String value, DateFormat format) {
        format.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        Date parsedDate = format.parse(value, pos); // ignore the result (use the Calendar)
        Calendar calendar = null;
        if (pos.getErrorIndex() >= 0 || pos.getIndex() != value.length() || parsedDate == null) {
            calendar = null;
        } else {
            calendar = format.getCalendar();
        }
        return calendar;
    }

}
