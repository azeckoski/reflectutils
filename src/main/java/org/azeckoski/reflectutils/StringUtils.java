/**
 * $Id: StringUtils.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/StringUtils.java $
 * StringUtils.java - genericdao - Sep 18, 2008 5:15:22 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * Some simple utils to assist with string operations
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class StringUtils {

    public static String makeStringFromInputStream(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to get data from stream: " + e.getMessage(), e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // oh well
            }
        }
        return sb.toString();
    }

    public static InputStream makeInputStreamFromString(String string) {
        InputStream stream = null;
        if (string != null) {
            try {
                stream = new ByteArrayInputStream(string.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // I know this is a valid encoding... -AZ
            }
        }
        return stream;
    }

}
