/**
 * $Id: ReflectTransientClassFields.java 23 2008-10-02 16:00:30Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/annotations/ReflectTransientClassFields.java $
 * ReflectTransientClassFields.java - genericdao - Sep 21, 2008 12:45:51 PM - azeckoski
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
 * Indicate fields on a class which should be marked as transient <br/>
 * This is primarily for indicating fields as transient in classes
 * which you are extending or constant fields in interfaces <br/>
 * For example: <br/>
 * Given a class with 3 String fields: name, email, phone <br/>
 * Adding the annotation like so to the class will cause the phone field to marked as transient for field operations:
 * <pre>
 *    &#064;ReflectTransientClassFields("phone")
 *    public class Person {
 *        String name;
 *        String email;
 *        String phone;
 *        ... 
 *    }
 * </pre>
 * And this annotation would cause the email and phone fields to be marked as transient:
 * <pre>
 *    &#064;ReflectTransientClassFields({"phone","email"})
 *    public class Person {
 *        String name;
 *        String email;
 *        String phone;
 *        ... 
 *    }
 * </pre>
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ReflectTransientClassFields {
    String[] value();
}
