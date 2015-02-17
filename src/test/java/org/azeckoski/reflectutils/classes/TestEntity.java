/**
 * $Id: TestEntity.java 128 2014-03-18 22:27:33Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestEntity.java $
 * TestEntity.java - genericdao - May 18, 2008 10:22:09 PM - azeckoski
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
import org.azeckoski.reflectutils.annotations.TestAnnoteField1;
import org.azeckoski.reflectutils.annotations.TestAnnoteField2;

/**
 * Long id = 3 <br/>
 * String entityId = "33" ({@link TestAnnote} on getter) <br/>
 * String extra = null (all 3 test annotations, field, getter, setter) <br/>
 * Boolean bool = null <br/>
 * String[] sArray = {"1","2"} <br/>
 * transient String transStr = "transient" (only getter) <br/>
 * public String fieldOnly (no getters or setters) <br/>
 * private String privateFieldOnly (no getters or setters) <br/>
 * String prefix - getter without fields <br/>
 */
public class TestEntity {
   private Long id = (long) 3;
   private String entityId = "33";
   @TestAnnote
   private String extra = null;
   private Boolean bool = null;
   private String[] sArray = {"1","2"};
   private transient String transStr = "transient";
   public String fieldOnly = "fieldOnly";
   private String privateFieldOnly = "privateFieldOnly";
   public String getPrefix() {
      return "crud";
   }
   public String createEntity(Object entity) {
      return "1";
   }
   @TestAnnote
   public String getEntityId() {
      return entityId;
   }
   public void setEntityId(String entityId) {
      this.entityId = entityId;
   }
   public Long getId() {
      return id;
   }
   public void setId(Long id) {
      this.id = id;
   }
   @TestAnnoteField1
   public String getExtra() {
      return extra;
   }
   @TestAnnoteField2("TEST")
   public void setExtra(String extra) {
      this.extra = extra;
   }
   public String[] getSArray() {
      return sArray;
   }
   public void setSArray(String[] array) {
      sArray = array;
   }
   public Boolean getBool() {
      return bool;
   }
   public void setBool(Boolean bool) {
      this.bool = bool;
   }
   public String getTransStr() {
      return transStr;
   }
}