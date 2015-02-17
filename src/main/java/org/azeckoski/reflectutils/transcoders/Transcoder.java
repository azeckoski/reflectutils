/**
 * $Id: Transcoder.java 81 2011-12-19 17:03:40Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/transcoders/Transcoder.java $
 * Transcoder.java - entity-broker - Sep 16, 2008 3:20:14 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.transcoders;

import java.util.Map;

import org.azeckoski.reflectutils.ReflectUtils;


/**
 * The transcoder can convert from a java objects => a format => java objects (simple) <br/>
 * Note that conversion to simple objects is all that is generally supported,
 * conversions to complex objects require use of the {@link ReflectUtils} to handle
 * the conversion using {@link ReflectUtils#populate(Object, Map)} or {@link ReflectUtils#convert(Object, Class)}
 * <br/>
 * The ability to append optional properties is also supported <br/>
 * The various transcoders will also have optional configuration parameters which should be controlled
 * via the constructors. <br/>
 * Transcoders should be written to be created once and used many times without problems.
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface Transcoder {

    /**
     * default map key to use if the output data is not actually a map but is a list or array or whatever
     * when it is being decoded into a map
     */
    public static final String DATA_KEY = "data";

    /**
     * @return the handled format (xml, json, etc.)
     */
    public String getHandledFormat();

    /**
     * @param object any java object (should not be null)
     * @param name (optional) the name of the data, will be handled differently by the encoders (null for default: {@link #DATA_KEY})
     * @param properties (optional) additional properties which will be added to the encoding 
     * as if it were a property of the bean or map being encoded, if the object being encoded is not a bean
     * or a map then the properties are ignored
     * @return the object encoded into the handled format
     */
    public String encode(Object object, String name, Map<String, Object> properties);
    
    /**
     * @param object any java object (should not be null)
     * @param name (optional) the name of the data, will be handled differently by the encoders (null for default: {@link #DATA_KEY})
     * @param properties (optional) additional properties which will be added to the encoding
     * as if it were a property of the bean or map being encoded, if the object being encoded is not a bean
     * or a map then the properties are ignored
     * @param maxDepth the maximum traversal depth to use for this specific object
     * @return the object encoded into the handled format
     */
    public String encode(Object object, String name, Map<String, Object> properties, int maxDepth);

    /**
     * Decode the data string into a map of java objects (typically simple objects like String, Integer, etc.)
     * @param string a string in the handled format
     * @return the map of java objects based on the data string, this will not attempt to convert XML into beans
     * but will simply convert data into maps or lists or simple java objects (strings, etc.), if there
     * is only a single simple value then it will be placed into the map with the key {@value #DATA_KEY}
     */
    public Map<String, Object> decode(String string);

}
