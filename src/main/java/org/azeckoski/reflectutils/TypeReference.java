/**
 * $Id: TypeReference.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/TypeReference.java $
 * TypeReference.java - genericdao - May 31, 2008 11:47:30 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A class which will sort of let us cheat and get to generics info at runtime
 * if used correctly, derived from:
 * http://gafter.blogspot.com/2006/12/super-type-tokens.html
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public abstract class TypeReference<T> {

   private final Type type;

   protected TypeReference() {
      Type superclass = getClass().getGenericSuperclass();
      if (superclass instanceof Class) {
         throw new RuntimeException("Missing type parameter.");
      }
      this.type = ( (ParameterizedType)superclass ).getActualTypeArguments()[0];
   }

   public Type getType() {
      return this.type;
   }

   @SuppressWarnings("unchecked")
   public boolean equals(Object o) {
      return ( o instanceof TypeReference ) ? ( (TypeReference)o ).type.equals(this.type) : false;
   }

   public int hashCode() {
      return this.type.hashCode();
   }
}

