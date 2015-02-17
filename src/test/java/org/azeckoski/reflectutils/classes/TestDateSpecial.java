/**
 * $Id: TestDateSpecial.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestDateSpecial.java $
 * TestPea.java - genericdao - May 18, 2008 10:18:18 PM - azeckoski
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

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * A class for testing out handling of dates and other special types
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestDateSpecial {
    public String id;
    public long longing;
    public Date date;
    public Date time;
    public Calendar calendar;
    public Timestamp timestamp;
    public Float floating;
    public byte[] bytes;
    public BigInteger bigInteger;
}