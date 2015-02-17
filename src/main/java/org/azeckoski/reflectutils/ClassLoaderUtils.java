/**
 * $Id: ClassLoaderUtils.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ClassLoaderUtils.java $
 * ClassLoaderUtils.java - genericdao - May 8, 2008 5:39:34 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities to help with operations that deal in {@link ClassLoader} and {@link Class}
 * 
 * @author Aaron Zeckoski (azeckoski@gmail.com)
 */
public class ClassLoaderUtils {

    /**
     * @param className a fully qualified class name (e.g. org.dspace.MyClass)
     * @return the Class if it can be found in the context or current {@link ClassLoader} OR null if no class can be found
     */
    public static Class<?> getClassFromString(String className) {
        Class<?> c = null;
        try {
            ClassLoader cl = getCurrentClassLoader();
            c = Class.forName(className, true, cl);
        } catch (ClassNotFoundException e) {
            try {
                ClassLoader cl = ClassLoaderUtils.class.getClassLoader();
                c = Class.forName(className, true, cl);
            } catch (ClassNotFoundException e1) {
                try {
                    c = Class.forName(className);
                } catch (ClassNotFoundException e2) {
                    c = null;
                }
            }
        }
        return c;
    }

    /**
     * @return the current context {@link ClassLoader} or the nearest thing that can be found
     */
    public static ClassLoader getCurrentClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassLoaderUtils.class.getClassLoader();
        }
        return cl;
    }


    /**
     * Returns a list of all superclasses and implemented interfaces by the supplied class,
     * recursively to the base, up to but excluding Object.class. These will be listed in order from
     * the supplied class, all concrete superclasses in ascending order, and then finally all
     * interfaces in recursive ascending order.<br/>
     * This will include duplicates if any superclasses implement the same classes 
     * 
     * Taken from PonderUtilCore around version 1.2.2
     *  - Antranig Basman (antranig@caret.cam.ac.uk)
     */
    public static List<Class<?>> getSuperclasses(Class<?> clazz) {
        List<Class<?>> accumulate = new ArrayList<Class<?>>();
        while (clazz != Object.class) {
            accumulate.add(clazz);
            clazz = clazz.getSuperclass();
        }
        int supers = accumulate.size();
        for (int i = 0; i < supers; ++i) {
            appendSuperclasses(accumulate.get(i), accumulate);
        }
        return accumulate;
    }

    /**
     * Taken from PonderUtilCore around version 1.2.2
     *  - Antranig Basman (antranig@caret.cam.ac.uk)
     */
    private static void appendSuperclasses(Class<?> clazz, List<Class<?>> accrete) {
        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            accrete.add(interfaces[i]);
        }
        for (int i = 0; i < interfaces.length; ++i) {
            appendSuperclasses(interfaces[i], accrete);
        }
    }


    /**
     * Finds a class type that is in the containing collection,
     * will always return something (failsafe to Object.class)
     * @param collection any collection (List, Set, etc.)
     * @return the class type contained in this collecion
     */
    @SuppressWarnings("unchecked")
    public static Class<?> getClassFromCollection(Collection collection) {
        // try to get the type of entities out of this collection
        Class<?> c = Object.class;
        if (collection != null) {
            if (! collection.isEmpty()) {
                c = collection.iterator().next().getClass();
            } else {
                // this always gets Object.class -AZ
                //c = collection.toArray().getClass().getComponentType();
                c = Object.class;
            }
        }
        return c;
    }

}
