/**
 * $Id: InterfaceConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/api/InterfaceConverter.java $
 * InterfaceConverter.java - genericdao - Sep 9, 2008 1:50:44 PM - azeckoski
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
 * Special converter which is used for converting to an interface when more control
 * over the type of implementation to create under the interface is desired<br/>
 * @see Converter for more details about the core converter interface and converters in general
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface InterfaceConverter<T> extends Converter<T> {

    /**
     * Convert the value into the implementationType for the interface T
     * 
     * @param value the input value to be converted, this will never be null or the type which you are converting to
     * since these simple cases are already handled in the {@link ConversionUtils}
     * @param implementationType
     * @return the converted value (can be null if desired)
     * @throws UnsupportedOperationException if conversion cannot be performed successfully
     */
    public T convertInterface(Object value, Class<? extends T> implementationType);

}
