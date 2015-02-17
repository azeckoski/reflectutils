/**
 * $Id: FieldGetValueException.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/exceptions/FieldGetValueException.java $
 * FieldGetValueException.java - genericdao - May 21, 2008 4:49:44 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.exceptions;


/**
 * Indicates that there was a failure attempting to get a field value,
 * probably caused by a security exception or obscure java failure reading or writing the value
 * (this indicates the arguments were correct and the field was found but there was a failure at the time of the operation)
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class FieldGetValueException extends RuntimeException {

   /**
    * the field name we were trying to get the value from 
    */
   public String fieldName;
   /**
    * the object with the field we were trying to get a value from
    */
   public Object object;

   public FieldGetValueException(String fieldName, Object object, Throwable cause) {
      super("Failed to get field ("+fieldName+") value from object ("+object+"), cause=" + cause, cause);
      this.fieldName = fieldName;
      this.object = object;
   }
   public FieldGetValueException(String message, String fieldName, Object object, Throwable cause) {
      super(message, cause);
      this.fieldName = fieldName;
      this.object = object;
   }
   
}
