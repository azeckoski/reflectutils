/**
 * $Id: TestFilter.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestFilter.java $
 * TestFilter.java - genericdao - Sep 11, 2008 4:11:42 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.classes;

import org.azeckoski.reflectutils.ClassFields.FieldsFilter;


/**
 * Class that is made to test {@link FieldsFilter} <br/>
 * {@link FieldsFilter#ALL}: {@link #field},{@link #fin},{@link #property},{@link #read},{@link #trans},{@link #write} <br/>
 * {@link FieldsFilter#COMPLETE}: {@link #field},{@link #property},{@link #trans} <br/>
 * {@link FieldsFilter#READABLE}: {@link #field},{@link #fin},{@link #property},{@link #read},{@link #trans} <br/>
 * {@link FieldsFilter#SERIALIZABLE}: {@link #field},{@link #fin},{@link #property},{@link #read} <br/>
 * {@link FieldsFilter#WRITEABLE}: {@link #field},{@link #property},{@link #trans},{@link #write} <br/>
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@SuppressWarnings("unused")
public class TestFilter {

    public String field = "field";
    public transient String trans = "transient";
    public final String fin = "final";

    private String read = null;
    private String write = null;
    private String property = "property";
    private String unused = "unused";

    public String getRead() {
        return read;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public void setProperty(String property) {
        this.property = property;
    }
    public String getProperty() {
        return property;
    }

}
