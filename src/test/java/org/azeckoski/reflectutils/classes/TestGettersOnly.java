/**
 * $Id: TestGettersOnly.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestGettersOnly.java $
 * TestBean.java - genericdao - May 18, 2008 10:20:20 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.classes;

/**
 * No annotations <br/>
 *  int int1; <br/>
 *  String str2; <br/>
 *  boolean bool3; <br/>
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestGettersOnly {
    private int int1;
    private String str2;
    private boolean bool3;

    public int getInt1() {
        return int1;
    }

    public String getStr2() {
        return str2;
    }

    public boolean isBool3() {
        return bool3;
    }
}