/**
 * $Id: OrderedMap.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/map/OrderedMap.java $
 * OrderedMap.java - genericdao - May 5, 2008 2:16:35 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2
 * 
 * A copy of the Apache License, Version 2 has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils.map;

import java.util.List;
import java.util.Map;

/**
 * A map which keeps track of the order the entries are added 
 * and allows retrieval of entries in the order they were entered as well<br/>
 * Use {@link ArrayOrderedMap} or {@link ConcurrentOrderedMap} depending on your needs<br/>
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface OrderedMap<K, V> extends Map<K, V> {

    /**
     * @param name (optional) the name to use when encoding this map of data, may be null
     */
    public String getName();
    /**
     * @param name (optional) the name to use when encoding this map of data
     */
    public void setName(String name);

    /**
     * @return a list of all the keys in this map in the order they were entered,
     * this list is a copy and manipulating it has no effect on the map
     */
    public List<K> getKeys();

    /**
     * @return a list of all the values in this map in the order they were entered,
     * this list is a copy and manipulating it has no effect on the map
     */
    public List<V> getValues();

    /**
     * @return a list of all the entries in this map in the order they were created
     */
    public List<Entry<K, V>> getEntries();

    /**
     * Get an entry based on the position it is in the map (based on the order entries were created)
     * @param position the position in the map (must be less that the size)
     * @return the entry at that position
     * @throws IllegalArgumentException if the position is greater than the map size
     */
    public Entry<K, V> getEntry(int position);

}
