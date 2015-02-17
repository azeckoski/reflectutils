/**
 * $Id: URLConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/URLConverter.java $
 * URLConverter.java - genericdao - Sep 8, 2008 3:08:18 PM - azeckoski
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

import java.net.MalformedURLException;
import java.net.URL;

import org.azeckoski.reflectutils.converters.api.Converter;



/**
 * Converts strings into URLs
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class URLConverter implements Converter<URL> {

    public URL convert(Object value) {
        URL url = null;
        String s = value.toString();
        if (s != null && s.length() > 0) {
            try {
                url = new URL(s);
            } catch (MalformedURLException e) {
                throw new UnsupportedOperationException("URL convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a URL: " + e.getMessage());
            }
        }
        if (url == null) {
            throw new UnsupportedOperationException("URL convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a URL");
        }
        return url;
    }

}
