/**
 * $Id: TestSettersOnly.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestSettersOnly.java $
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
 * No annotations, setters only <br/>
 *  int int1; <br/>
 *  String str2; <br/>
 *  boolean bool3; <br/>
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestSettersOnly {
    private int int1;
    private String str2;
    private boolean bool3;

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public void setBool3(boolean bool3) {
        this.bool3 = bool3;
    }

    public String makeThing() {
        String ok = int1 + str2 + bool3;
        return ok;
    }
}