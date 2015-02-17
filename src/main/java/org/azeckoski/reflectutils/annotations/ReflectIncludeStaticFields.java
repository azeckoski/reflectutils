/**
 * $Id: ReflectIncludeStaticFields.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/annotations/ReflectIncludeStaticFields.java $
 * ReflectIgnoreClassFields.java - genericdao - Sep 21, 2008 12:45:51 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicate that all static fields on a class should be included when performing class
 * field reflection operations (default is to ignore static fields) <br/>
 * Example: <br/>
 * A class has 1 static field: String phone; <br/>
 * It also has 1 other field: String name; <br/>
 * Normally only the name field would be including when performing field reflection operations.
 * If this annotation is added to the class then both fields would be included in the 
 * list of fields for a class. Also, any static fields on any extended classes or interfaces
 * would also be included.
 *  
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReflectIncludeStaticFields { }
