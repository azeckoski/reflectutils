/**
 * $Id: TestNesting.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestNesting.java $
 * TestNesting.java - genericdao - May 18, 2008 10:23:17 PM - azeckoski
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.azeckoski.reflectutils.annotations.TestAnnoteClass2;
import org.azeckoski.reflectutils.annotations.TestAnnoteField1;

/**
 * int id = 5 ({@link TestAnnoteField1} on private field) <br/>
 * String title = "55" ({@link TestAnnoteField1} on private field) <br/>
 * String extra = null ({@link TestAnnoteField1} on private field) <br/>
 * String[] myArray = new String[] {"A","B"} <br/>
 * List<String> sList = [A,B] <br/>
 * Map<String, String> sMap = [A1=ONE, B2=TWO] <br/>
 * TestBean testBean = null <br/>
 * - int myInt = 0 <br/>
 * - String myString = "woot" <br/>
 * TestEntity testEntity <br/>
 * - Long id = 3 <br/>
 * - String entityId = "33" (EntityId) <br/>
 * - String extra = null <br/>
 * - String[] sArray = {"1","2"} <br/>
 */
@TestAnnoteClass2("TEST")
public class TestNesting {
   @TestAnnoteField1
   private int id = 5;
   @TestAnnoteField1
   private String title = "55";
   @TestAnnoteField1
   private String extra = null;
   private String[] myArray = new String[] {"A","B"};
   private List<String> sList = new ArrayList<String>();
   private Map<String, String> sMap = new HashMap<String, String>();
   public TestPea testPea = null;
   private TestBean testBean = null;
   private TestEntity testEntity;
   public TestNesting() {
      testEntity = new TestEntity();
      sMap.put("A1", "ONE");
      sMap.put("B2", "TWO");
      sList.add("A");
      sList.add("B");
   }
   public TestNesting(int id, String title, String[] list) {
      this();
      this.id = id;
      this.title = title;
      sList = Arrays.asList(list);
   }
   public int getId() {
      return id;
   }
   public void setId(int id) {
      this.id = id;
   }
   public String getTitle() {
      return title;
   }
   public void setTitle(String title) {
      this.title = title;
   }
   public String getExtra() {
      return extra;
   }
   public void setExtra(String extra) {
      this.extra = extra;
   }
   public List<String> getSList() {
      return sList;
   }
   public void setSList(List<String> list) {
      sList = list;
   }
   public TestBean getTestBean() {
      return testBean;
   }
   public void setTestBean(TestBean testBean) {
      this.testBean = testBean;
   }
   public TestEntity getTestEntity() {
      return testEntity;
   }
   public void setTestEntity(TestEntity testEntity) {
      this.testEntity = testEntity;
   }
   public Map<String, String> getSMap() {
      return sMap;
   }
   public void setSMap(Map<String, String> map) {
      sMap = map;
   }
   public String[] getMyArray() {
      return myArray;
   }
   public void setMyArray(String[] myArray) {
      this.myArray = myArray;
   }
}