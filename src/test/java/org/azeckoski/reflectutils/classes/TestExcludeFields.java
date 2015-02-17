/**
 * $Id: TestExcludeFields.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestExcludeFields.java $
 * TestExcludeFields.java - genericdao - Sep 21, 2008 2:12:28 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.classes;

import org.azeckoski.reflectutils.annotations.ReflectIgnoreClassFields;


/**
 * For testing out exclusion of fields by annotations
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@ReflectIgnoreClassFields("password")
public class TestExcludeFields {

    public String name;
    public String email;
    public String password;

}
