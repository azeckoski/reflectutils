/**
 * $Id: TestImplFour.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestImplFour.java $
 * TestImplFour.java - genericdao - May 18, 2008 10:15:00 PM - azeckoski
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

import java.io.IOException;
import java.nio.CharBuffer;

import org.azeckoski.reflectutils.interfaces.TestInterfaceFour;


/**
 * Test class that implements {@link TestInterfaceFour}<br/>
 * String thing
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestImplFour implements TestInterfaceFour {
   private String thing;
   public void setThing(String thing) {
      this.thing = thing;
   }
   public String getThing() {
      return thing;
   }
   public void run() {
      // nothing
   }
   public int read(CharBuffer cb) throws IOException {
      return 0;
   }
}
