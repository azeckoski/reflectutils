/**
 * $Id: TranscoderUtils.java 65 2010-04-05 14:53:55Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/transcoders/TranscoderUtils.java $
 * TranscoderUtils.java - reflectutils - Nov 12, 2008 4:17:30 PM - azeckoski
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;


/**
 * This allows for special handling which is shared between the transcoders
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class TranscoderUtils {

    /**
     * This will handle the encoding of special and user specific objects,
     * this allows there to be added control over the way certain types of objects are encoded
     * 
     * @param object
     * @return a null if the current object is not special, an empty string to indicate the 
     * object should be skipped over with no output, and any string value to indicate the
     * return value to use instead of attempting to encode the object
     */
    public static String handleObjectEncoding(Object object, List<ObjectEncoder> encoders) {
        String encoded = null;
        if (encoders != null) {
            for (ObjectEncoder encoder : encoders) {
                try {
                    encoded = encoder.encodeObject(object);
                } catch (Exception e) {
                    // nothing to do here but skip to the next one
                    encoded = null;
                }
                if (encoded != null) {
                    break; // break out of the loop because we are done
                }
            }
        }
        if (encoded == null) {
            encoded = checkObjectSpecial(object);
        }
        return encoded;
    }

    /**
     * This will ensure that no objects that are known to be impossible to serialize properly will
     * cause problems with the transcoders by allowing them to go into loops
     * 
     * @param object
     * @return a null if the current object is not special, an empty string to indicate the 
     * object should be skipped over with no output, and any string value to indicate the
     * return value to use instead of attempting to encode the object
     */
    public static String checkObjectSpecial(Object object) {
        String special = null;
        if (object != null) {
            Class<?> type = object.getClass();
            if (Class.class.isAssignableFrom(type)) {
                // class objects are serialized as the full name
                special = ((Class<?>)object).getName();
            } else if (Type.class.isAssignableFrom(type)) {
                // type just does to string
                special = ((Type)object).toString();
            } else if (Package.class.isAssignableFrom(type)) {
                // package uses name only
                special = ((Package)object).getName();
            } else if (ClassLoader.class.isAssignableFrom(type)) {
                // classloaders are skipped over entirely
                special = "";
            } else if (InputStream.class.isAssignableFrom(type)) {
                // skip IS
                special = "";
            } else if (OutputStream.class.isAssignableFrom(type)) {
                // skip OS
                special = "";
            } else if (InputStream.class.isAssignableFrom(type)) {
                // skip IS
                special = "";
            } else if (Writer.class.isAssignableFrom(type)) {
                // skip writer
                special = "";
            } else if (Reader.class.isAssignableFrom(type)) {
                // turn reader into string
                Reader reader = ((Reader)object);
                StringBuilder sb = new StringBuilder();
                try {
                    while (reader.ready()) {
                        int c = reader.read();
                        if (c <= -1) {
                            break;
                        }
                        sb.append((char) c);
                    }
                    special = sb.toString();
                } catch (IOException e) {
                    special = "Could not read from Reader ("+reader.toString()+"): " + e.getMessage();
                }
            }
        }
        return special;
    }

}
