/**
 * $Id: NumberConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/NumberConverter.java $
 * NumberConverter.java - genericdao - Sep 8, 2008 10:48:42 AM - azeckoski
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.azeckoski.reflectutils.ConstructorUtils;
import org.azeckoski.reflectutils.converters.api.Converter;


/**
 * Handles all the number conversions in a single converter,
 * the other number converters are simply going to call through to this one<br/>
 * Special handling for {@link Boolean}, {@link Date}, {@link Calendar}, and {@link String}
 * <ul>
 *     <li><code>java.math.BigDecimal</code></li>
 *     <li><code>java.math.BigInteger</code></li>
 *     <li><code>java.lang.Byte</code></li>
 *     <li><code>java.lang.Double</code></li>
 *     <li><code>java.lang.Float</code></li>
 *     <li><code>java.lang.Integer</code></li>
 *     <li><code>java.lang.Long</code></li>
 *     <li><code>java.lang.Number</code></li>
 *     <li><code>java.lang.Short</code></li>
 * </ul>
 * <br/>
 * Based on code from commons bean utils but updated for generics and removed complexity and (mis)usage of Throwable<br/>
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class NumberConverter implements Converter<Number> {

    public Number convert(Object value) {
        return NumberConverter.convertToType(Number.class, value);
    }

    // Code derived from commons beanutils converters

    /**
     * Convert the input object into a Number object of the
     * specified type.
     *
     * @param targetType Data type to which this value should be converted.
     * @param value The input value to be converted.
     * @return The converted value.
     * @throws Throwable if an error occurs converting to the specified type
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertToType(Class<T> targetType, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("value to convert cannot be null");
        }
        Class<?> sourceType = value.getClass();

        // Handle Number
        if (value instanceof Number) {
            return (T) toNumber(sourceType, targetType, (Number)value);
        }

        // Handle Boolean
        if (value instanceof Boolean) {
            return (T) toNumber(sourceType, targetType, ((Boolean)value).booleanValue() ? 1 : 0);
        }

        // Handle Date
        if (value instanceof Date) {
            long time = ((Date)value).getTime();
            return (T) toNumber(Long.class, targetType, time);
        }

        // Handle Calendar
        if (value instanceof Calendar) {
            long time = ((Calendar)value).getTime().getTime();
            return (T) toNumber(Long.class, targetType, time);
        }

        // Convert all other types to String & handle
        String stringValue = value.toString().trim();
        if (stringValue.length() == 0) {
            Object defaultValue = ConstructorUtils.getDefaultValue(targetType);
            if (defaultValue == null) {
                throw new UnsupportedOperationException("Number convert failure: Could not convert empty string to target ("+targetType+") or get a default value for it");
            }
            return (T) defaultValue;
        }

        // Convert/Parse a String
        Number number = toNumber(sourceType, targetType, stringValue);
        return (T) number;
    }

    /**
     * Convert any Number object to the specified type for this <i>Converter</i>.
     * <p>
     * This method handles conversion to the following types:
     * <ul>
     *     <li><code>java.math.BigDecimal</code></li>
     *     <li><code>java.math.BigInteger</code></li>
     *     <li><code>java.lang.Byte</code></li>
     *     <li><code>java.lang.Double</code></li>
     *     <li><code>java.lang.Float</code></li>
     *     <li><code>java.lang.Integer</code></li>
     *     <li><code>java.lang.Long</code></li>
     *     <li><code>java.lang.Number</code></li>
     *     <li><code>java.lang.Short</code></li>
     * </ul>
     * @param sourceType The type being converted from
     * @param targetType The Number type to convert to
     * @param value The Number to convert.
     * @return The converted value.
     */
    private static Number toNumber(Class<?> sourceType, Class<?> targetType, Number value) {

        // Correct Number type already
        if ( ConstructorUtils.classEquals(targetType, value.getClass()) ) {
            return value;
        }

        // Byte
        if (targetType.equals(Byte.class)) {
            long longValue = value.longValue();
            if (longValue > Byte.MAX_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too large for target (" + targetType + ")");
            }
            if (longValue < Byte.MIN_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too small for target (" + targetType + ")");
            }
            return new Byte(value.byteValue());
        }

        // Short
        if (targetType.equals(Short.class)) {
            long longValue = value.longValue();
            if (longValue > Short.MAX_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too large for target (" + targetType + ")");
            }
            if (longValue < Short.MIN_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too small for target (" + targetType + ")");
            }
            return new Short(value.shortValue());
        }

        // Integer
        if (targetType.equals(Integer.class)) {
            long longValue = value.longValue();
            if (longValue > Integer.MAX_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too large for target (" + targetType + ")");
            }
            if (longValue < Integer.MIN_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too small for target (" + targetType + ")");
            }
            return new Integer(value.intValue());
        }

        // Long
        if (targetType.equals(Long.class)) {
            return new Long(value.longValue());
        }

        // Float
        if (targetType.equals(Float.class)) {
            if (value.doubleValue() > Float.MAX_VALUE) {
                throw new UnsupportedOperationException("source (" + sourceType + ") value (" 
                        + value + ") is too large for target (" + targetType + ")");
            }
            return new Float(value.floatValue());
        }

        // Double
        if (targetType.equals(Double.class)) {
            return new Double(value.doubleValue());
        }

        // BigDecimal
        if (targetType.equals(BigDecimal.class)) {
            if (value instanceof Float || value instanceof Double) {
                return new BigDecimal(value.toString());
            } else if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger)value);
            } else {
                return BigDecimal.valueOf(value.longValue());
            }
        }

        // BigInteger
        if (targetType.equals(BigInteger.class)) {
            if (value instanceof BigDecimal) {
                return ((BigDecimal)value).toBigInteger();
            } else {
                return BigInteger.valueOf(value.longValue());
            }
        }

        throw new UnsupportedOperationException("Number convert failure: cannot convert source ("+sourceType+") to target ("+targetType+")");
    }

    /**
     * Default String to Number conversion.
     * <p>
     * This method handles conversion from a String to the following types:
     * <ul>
     *     <li><code>java.lang.Byte</code></li>
     *     <li><code>java.lang.Short</code></li>
     *     <li><code>java.lang.Integer</code></li>
     *     <li><code>java.lang.Long</code></li>
     *     <li><code>java.lang.Float</code></li>
     *     <li><code>java.lang.Double</code></li>
     *     <li><code>java.math.BigDecimal</code></li>
     *     <li><code>java.math.BigInteger</code></li>
     * </ul>
     * @param sourceType The type being converted from
     * @param targetType The Number type to convert to
     * @param value The String value to convert.
     *
     * @return The converted Number value.
     */
    private static Number toNumber(Class<?> sourceType, Class<?> targetType, String value) {

        // Byte
        if (targetType.equals(Byte.class)) {
            return Byte.valueOf(value);
        }

        // Short
        if (targetType.equals(Short.class)) {
            return Short.valueOf(value);
        }

        // Integer
        if (targetType.equals(Integer.class)) {
            return Integer.valueOf(value);
        }

        // Long
        if (targetType.equals(Long.class)) {
            return Long.valueOf(value);
        }

        // Float
        if (targetType.equals(Float.class)) {
            return Float.valueOf(value);
        }

        // Double
        if (targetType.equals(Double.class)) {
            return Double.valueOf(value);
        }

        // BigDecimal
        if (targetType.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }

        // BigInteger
        if (targetType.equals(BigInteger.class)) {
            return new BigInteger(value);
        }

        throw new UnsupportedOperationException("Number convert failure: cannot convert string source ("+sourceType+") to target ("+targetType+")");
    }

}
