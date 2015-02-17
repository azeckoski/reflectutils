/**
 * $Id: TestExtendBean.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestExtendBean.java $
 * TestExtendBean.java - genericdao - May 19, 2008 2:41:38 PM - azeckoski
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

import org.azeckoski.reflectutils.annotations.TestAnnoteClass2;
import org.azeckoski.reflectutils.annotations.TestAnnoteField1;


/**
 * This is a bean which extends another bean so we can test whether the
 * fields of both beans are visible and whether the annotations can be found as well<br/>
 * String anotherThing; ({@link TestAnnoteField1})<br/>
 */
@TestAnnoteClass2("TEST")
public class TestExtendBean extends TestBean {
    @TestAnnoteField1
    private String anotherThing;

    public String getAnotherThing() {
        return anotherThing;
    }   
    public void setAnotherThing(String anotherThing) {
        this.anotherThing = anotherThing;
    }
    public TestExtendBean() {}
    public TestExtendBean(int myInt) {
        super(myInt);
    }
}
