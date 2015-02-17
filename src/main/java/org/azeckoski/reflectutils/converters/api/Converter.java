/**
 * $Id: Converter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/api/Converter.java $
 * Converter.java - genericdao - May 19, 2008 10:10:15 PM - azeckoski
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
 * General converter interface written to specifically be compatible with the commons beanutils one <br/>
 * Allows for conversion between types that is more complete than the current ones<br/>
 * To use this, implement the interface with the type that you care about and then register it with the
 * {@link ConversionUtils} by calling {@link ConversionUtils#addConverter(Class, Converter)}
 * or you can use the constructor (the method is recommended though)
 */
public interface Converter<T> extends BaseConverter {

    /**
     * Convert the specified input object into an output object of the type for this converter implementation
     *
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @return the converted value (can be null if desired)
     * @throws UnsupportedOperationException if conversion cannot be performed successfully
     */
    public T convert(Object value);

}
