/**
 * $Id: VariableConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/api/VariableConverter.java $
 * VariableConverter.java - genericdao - Sep 10, 2008 1:08:15 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.converters.api;

import org.azeckoski.reflectutils.ConversionUtils;


/**
 * Allows a converter to check to see if it can handle converting something before
 * any of the converters are actually called, these converters are called before
 * any of the other converters if they happen to return true, note that the order
 * of the variable converters will match the order they were registered
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface VariableConverter extends BaseConverter {

    /**
     * Check if this value can be converted to this type by this converter
     * 
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @param toType the type to convert this value to
     * @return true if it can handle the conversion, false otherwise
     */
    public boolean canConvert(Object value, Class<?> toType);

    /**
     * Convert the specified input object into an output object of the type specified
     *
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @param toType the type to convert this value to
     * @return the converted value (can be null if desired)
     * @throws UnsupportedOperationException if conversion cannot be performed successfully
     */
    public <T> T convert(Object value, Class<T> toType);

}
