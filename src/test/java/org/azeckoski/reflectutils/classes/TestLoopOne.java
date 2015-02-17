/**
 * $Id: TestLoopOne.java 22 2008-10-02 15:32:02Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestLoopOne.java $
 * TestLoopOne.java - reflectutils - Oct 2, 2008 2:44:30 PM - azeckoski
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
 * 
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class TestLoopOne {

    public String name = "looper";
    public int id = 1000;
    public int count = 1;

    private TestLoopOne self = this;

    public TestLoopOne getSelf() {
        return self;
    }

    public TestLoopOne getContained() {
        return new TestLoopOne(count++);
    }

    public TestLoopOne(int count) {
        this.count = count;
    }
    
}
