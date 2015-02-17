/**
 * $Id: ObjectEncoder.java 1000 Apr 5, 2010 3:18:22 PM azeckoski $
 * $URL: https://source.sakaiproject.org/contrib $
 * ObjectEncoder.java - reflectutils - Apr 5, 2010 3:18:22 PM - azeckoski
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


/**
 * Special handler which allows an extra encoder to be placed into the transcoder pipeline which
 * will be called when objects are being encoded using whichever transcoder this is handed to,
 * the implementation of this must be threadsafe
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface ObjectEncoder {

    /**
     * This will ensure that no objects that are known to be impossible to serialize properly will
     * cause problems with the transcoders by allowing them to go into loops
     * 
     * @param object
     * @return a null if the current object is not special, an empty string to indicate the 
     * object should be skipped over with no output, and any string value to indicate the
     * return value to use instead of attempting to encode the object
     */
    public String encodeObject(Object object);

}
