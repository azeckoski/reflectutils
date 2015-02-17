/**
 * $Id: TestImplOne.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestImplOne.java $
 * TestImplOne.java - genericdao - May 18, 2008 10:13:32 PM - azeckoski
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

import org.azeckoski.reflectutils.interfaces.TestInterfaceOne;

/**
 * Simple class which implements interface one<br/>
 * (f) String id = "identity"<br/>
 * (f) Long nullVal = null<br/>
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestImplOne implements TestInterfaceOne {
   public String id = "identity";
   public Long nullVal = null;
}
