/**
 * $Id: TestCompound.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestCompound.java $
 * TestCompound.java - genericdao - May 18, 2008 10:59:07 PM - azeckoski
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

import org.azeckoski.reflectutils.annotations.TestAnnote;
import org.azeckoski.reflectutils.annotations.TestAnnoteClass1;
import org.azeckoski.reflectutils.annotations.TestAnnoteClass2;
import org.azeckoski.reflectutils.annotations.TestAnnoteField1;
import org.azeckoski.reflectutils.annotations.TestAnnoteField2;

/**
 * Class with methods and fields ({@link TestAnnoteClass1}, {@link TestAnnoteClass2}) <br/>
 * (f) String myField; ({@link TestAnnoteField1}, {@link TestAnnoteField2}) <br/>
 * (f) Integer fieldInt = 5; <br/>
 * int myInt = 8; ({@link TestAnnote}, {@link TestAnnoteField1}) <br/>
 * String myString; ({@link TestAnnoteField1} on setter) <br/>
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
@TestAnnoteClass1
@TestAnnoteClass2("TEST")
public class TestCompound {
   protected String prot1 = "ONE";
   @TestAnnoteField1
   @TestAnnoteField2("TEST")
   public String myField;
   public Integer fieldInt = 5;
   @TestAnnote
   private int myInt = 8;
   private String myString;

   @TestAnnoteField1
   public int getMyInt() {
      return myInt;
   }
   public void setMyInt(int myInt) {
      this.myInt = myInt;
   }
   public String getMyString() {
      return myString;
   }
   @TestAnnoteField1
   public void setMyString(String myString) {
      this.myString = myString;
   }
}
