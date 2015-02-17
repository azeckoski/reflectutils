/**
 * $Id: TestNoPubConstructor.java 36 2008-11-05 18:01:24Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestNoPubConstructor.java $
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
 * This class is here to allow testing constructor utils for protected constructors
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestNoPubConstructor {

    private String constructed = "no";
    public String getConstructed() {
        return constructed;
    }

    protected TestNoPubConstructor() {
        constructed = "prot";
    }

    public TestNoPubConstructor(String val) {
        if (val == null) {
            throw new IllegalArgumentException("DOH!");
        }
    }
}