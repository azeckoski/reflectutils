/**
 * $Id: FileConverter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/converters/FileConverter.java $
 * FileConverter.java - genericdao - Sep 8, 2008 2:54:29 PM - azeckoski
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

import java.io.File;

import org.azeckoski.reflectutils.converters.api.Converter;


/**
 * Simple converter to make {@link File} from strings
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class FileConverter implements Converter<File> {

    public File convert(Object value) {
        File f = null;
        String fileName = value.toString();
        if (fileName != null && fileName.length() > 0) {
            f = new File(fileName);
        }
        if (f == null) {
            throw new UnsupportedOperationException("File convert failure: cannot convert source ("+value+") and type ("+value.getClass()+") to a File");
        }
        return f;
    }

}
