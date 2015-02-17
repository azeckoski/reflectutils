/**
 * $Id: TestCollections.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/test/java/org/azeckoski/reflectutils/classes/TestCollections.java $
 * TestCollections.java - genericdao - May 18, 2008 10:23:17 PM - azeckoski
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test various collections of data (map, list, array, set),
 * all initially null
 */
public class TestCollections {

    public Map<String, Object> map;
    public List<String> list;
    public String[] array;
    public Set<String> set;

    public TestCollections(boolean filled) {
        if (filled) {
            map = new HashMap<String, Object>(10);
            list = new ArrayList<String>(10);
            array = new String[10];
            set = new HashSet<String>(10);
        }
    }
    
}