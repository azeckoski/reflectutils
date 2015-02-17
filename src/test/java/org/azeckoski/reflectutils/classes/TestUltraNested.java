/**
 * $Id: TestUltraNested.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestUltraNested.java $
 * TestUltraNested.java - genericdao - May 18, 2008 10:24:05 PM - azeckoski
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
 * String title <br/>
 * TestNesting testNesting <br/>
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class TestUltraNested {
   private String title;
   private TestNesting testNesting;
   public TestUltraNested(String title, TestNesting testNesting) {
      this.title = title;
      this.testNesting = testNesting;
   }
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public TestNesting getTestNesting() {
      return testNesting;
   }
   public void setTestNesting(TestNesting testNesting) {
      this.testNesting = testNesting;
   }
}
