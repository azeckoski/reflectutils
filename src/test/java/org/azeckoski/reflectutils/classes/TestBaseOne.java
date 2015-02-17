/**
 * $Id: TestBaseOne.java 22 2008-10-02 15:32:02Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestBaseOne.java $
 * TestBaseOne.java - reflectutils - Oct 2, 2008 2:43:30 PM - azeckoski
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


/**
 * This is testing our ability to deal with stupid classes that would infinitely loop
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class TestBaseOne {

    public String name = "AZ";
    private int number = 10;
    public int getNumber() {
        return number;
    }

    public TestLoopOne getContained() {
        return new TestLoopOne(1);
    }

}
