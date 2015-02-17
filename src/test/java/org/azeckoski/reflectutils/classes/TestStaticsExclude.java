/**
 * $Id: TestStaticsExclude.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestStaticsExclude.java $
 * TestStaticsExclude.java - genericdao - Sep 21, 2008 2:06:21 PM - azeckoski
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
 * Testing exclusion of statics
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class TestStaticsExclude {

    public static String sname;
    public String name;

    private static int snum;
    private int num;
    private static boolean sbool;

    public static int getSnum() {
        return snum;
    }

    public static void setSnum(int snum) {
        TestStaticsExclude.snum = snum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isSbool() {
        return sbool;
    }
    
    public void setSbool(boolean sbool) {
        TestStaticsExclude.sbool = sbool;
    }

}
