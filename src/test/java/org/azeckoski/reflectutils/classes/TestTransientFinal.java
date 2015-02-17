/**
 * $Id: TestTransientFinal.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestTransientFinal.java $
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

import org.azeckoski.reflectutils.annotations.ReflectTransient;

/**
 * For testing transient and final fields and methods<br/>
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestTransientFinal {
    public String field1;
    public transient String transField2;
    public final String finalStr3 = "ADZ";

    private String item1;
    private transient String item2;
    private final String item3 = "AZ";

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        // nothing, tricksy huh? this should not appear though
    }

    public String getItem4() {
        return "";
    }

    @ReflectTransient
    public String getItem5() {
        return "";
    }

}