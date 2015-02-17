/**
 * $Id: FieldnameNotFoundException.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/exceptions/FieldnameNotFoundException.java $
 * FieldnameNotFoundException.java - genericdao - Apr 27, 2008 2:47:36 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2
 * 
 * A copy of the Apache License, Version 2 has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.exceptions;

/**
 * Indicates that the fieldname could not be found
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class FieldnameNotFoundException extends RuntimeException {

   public String fieldName;

   public FieldnameNotFoundException(String fieldName) {
      this(fieldName, null);
   }

   public FieldnameNotFoundException(String fieldName, Throwable cause) {
      this("Could not find fieldName ("+fieldName+") on object", fieldName, cause);
   }

   public FieldnameNotFoundException(String message, String fieldName, Throwable cause) {
      super(message, cause);
      this.fieldName = fieldName;
   }

}
