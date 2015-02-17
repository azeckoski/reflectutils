/**
 * $Id: TestHibernateLikeBean.java 45 2008-11-13 13:23:21Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestHibernateLikeBean.java $
 * TestHibernateLikeBean.java - reflectutils - Nov 13, 2008 1:03:31 PM - azeckoski
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;


/**
 * This is meant to simulate a crazy bean which is similar to the ones hibernate produces
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class TestHibernateLikeBean {

    private Long id = 0l;
    private String title = "AZ";
    private Class<?> persistentClass = TestBean.class;
    private ClassLoader cl = TestBean.class.getClassLoader();
    private InputStream input = new ByteArrayInputStream( ("This is an input stream").getBytes() );
    private OutputStream output = new ByteArrayOutputStream(100);
    private Reader reader = new StringReader("This is a reader");
    private Writer writer = new StringWriter(100);
    private Class<?>[] classes = new Class[] {TestBean.class, TestEntity.class, TestCompound.class};
    @SuppressWarnings("unused")
    private enum MyEnum { AAA, BBB, CCC}

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Class<?> getPersistentClass() {
        return persistentClass;
    }
    
    public void setPersistentClass(Class<?> persistentClass) {
        this.persistentClass = persistentClass;
    }
    
    public ClassLoader getCl() {
        return cl;
    }
    
    public void setCl(ClassLoader cl) {
        this.cl = cl;
    }
    
    public InputStream getInput() {
        return input;
    }
    
    public void setInput(InputStream input) {
        this.input = input;
    }
    
    public OutputStream getOutput() {
        return output;
    }
    
    public void setOutput(OutputStream output) {
        this.output = output;
    }
    
    public Reader getReader() {
        return reader;
    }
    
    public void setReader(Reader reader) {
        this.reader = reader;
    }
    
    public Writer getWriter() {
        return writer;
    }
    
    public void setWriter(Writer writer) {
        this.writer = writer;
    }
    
    public Class<?>[] getClasses() {
        return classes;
    }
    
    public void setClasses(Class<?>[] classes) {
        this.classes = classes;
    };

}
