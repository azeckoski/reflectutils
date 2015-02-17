/**
 * $Id: BigDecimalConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/BigDecimalConverter.java $
 * BigDecimalConverter.java - genericdao - Sep 8, 2008 11:22:47 AM - azeckoski
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

import org.azeckoski.reflectutils.converters.api.Converter;



/**
 * BigDecimal passthrough
 * @see NumberConverter for more details
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class BigDecimalConverter implements Converter<BigDecimal> {
    public BigDecimal convert(Object value) {
        return NumberConverter.convertToType(BigDecimal.class, value);
    }
}
