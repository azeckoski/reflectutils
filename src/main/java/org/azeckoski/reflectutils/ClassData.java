/**
 * $Id: ClassData.java 61 2009-09-25 11:14:16Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ClassData.java $
 * ClassData.java - genericdao - Sep 4, 2008 6:19:39 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @param <T> the class type
 * A class which retrieves and holds all the reflected class data related to a class 
 * (annotations, constructors, fields, methods), this is immutable<br/>
 * WARNING: this is NOT cheap and the results need to be cached badly,
 * you should get this from the {@link ClassDataCacher} rather than constructing it yourself<br/>
 */
public class ClassData<T> {

    private final Class<T> type;
    private final List<Annotation> annotations;
    private final List<Constructor<T>> constructors;
    private final List<Field> fields;
    private final List<Method> methods;
    private final List<Class<?>> interfaces;
    private final List<Class<?>> superclasses;
    private final List<T> enumConstants;

    public ClassData(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("ClassData requires a valid class, type is null");
        }
        if (type.isPrimitive() || type.isArray()) {
            throw new IllegalArgumentException("Invalid type to make reflection cache for ("+type.getName()+"), cannot reflect over primitives or arrays");
        }
        this.type = type;
        T[] ecs = type.getEnumConstants();
        if (ecs != null) {
            enumConstants = new ArrayList<T>(ecs.length);
            for (T t : ecs) {
                enumConstants.add(t);
            }
        } else {
            enumConstants = new ArrayList<T>(0);
        }
        annotations = new ArrayList<Annotation>(5);
        getAllAnnotations(type, annotations);
        constructors = new ArrayList<Constructor<T>>(5);
        fields = new ArrayList<Field>(5);
        methods = new ArrayList<Method>(5);
        interfaces = new ArrayList<Class<?>>(5);
        superclasses = new ArrayList<Class<?>>(5);
        getAllThings(type, fields, methods, constructors, interfaces, superclasses);
        // sort the fields,methods,constructors
        Collections.sort(fields, new MemberComparator());
        Collections.sort(methods, new MemberComparator());
        Collections.sort(constructors, new MemberComparator());
        // remove duplicates from the list of interfaces
        ArrayUtils.removeDuplicates(interfaces);
    }

    /**
     * @return only the public constructors in the class this data represents
     */
    public List<Constructor<T>> getPublicConstructors() {
        ArrayList<Constructor<T>> pubConstructors = new ArrayList<Constructor<T>>();
        for (Constructor<T> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers())) {
                pubConstructors.add(constructor);
            }
        }
        return pubConstructors;
    }

    /**
     * @return only the public fields in the class this data represents
     */
    public List<Field> getPublicFields() {
        ArrayList<Field> pubFields = new ArrayList<Field>();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers())) {
                pubFields.add(field);
            }
        }
        return pubFields;
    }

    /**
     * @return only the public methods for the class this data represents
     */
    public List<Method> getPublicMethods() {
        ArrayList<Method> pubMethods = new ArrayList<Method>();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                pubMethods.add(method);
            }
        }
        return pubMethods;
    }

    protected void getAllAnnotations(final Class<?> type, final List<Annotation> list) {
        // get only public annotations
        Annotation[] annotations = type.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation != null) {
                list.add(annotation);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void getAllThings(final Class<?> type, final List<Field> fList, final List<Method> mList, final List<Constructor<T>> cList, final List<Class<?>> iList, final List<Class<?>> sList) {
        // get the fields for the current class
        Field[] fields = type.getDeclaredFields();
        for (final Field field : fields) {
            if (field != null) {
                int modifiers = field.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    fList.add(field);
                } else {
                    try {
                        AccessController.doPrivileged(new PrivilegedAction<T>() {
                            public T run() {
                                field.setAccessible(true);
                                return null;
                            }
                        });
                        fList.add(field);
                    } catch (SecurityException e) {
                        // oh well, this does not get added then
                    }
                }
            }
        }
        Method[] methods = type.getDeclaredMethods();
        for (final Method method : methods) {
            if (method != null) {
                int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    mList.add(method);
                } else {
                    try {
                        AccessController.doPrivileged(new PrivilegedAction<T>() {
                            public T run() {
                                method.setAccessible(true);
                                return null;
                            }
                        });
                        mList.add(method);
                    } catch (SecurityException e) {
                        // oh well, this does not get added then
                    }
                }
            }
        }
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        for (final Constructor<?> constructor : constructors) {
            // need to avoid the Object constructor
            if (!Object.class.equals(type) && constructor != null) {
                int modifiers = constructor.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    cList.add((Constructor<T>)constructor);
                } else {
                    try {
                        AccessController.doPrivileged(new PrivilegedAction<T>() {
                            public T run() {
                                constructor.setAccessible(true);
                                return null;
                            }
                        });
                        cList.add((Constructor<T>)constructor);
                    } catch (SecurityException e) {
                        // oh well, this does not get added then
                    }
                }
            }
        }
        // now we recursively go through the interfaces and super classes
        Class<?>[] interfaces = type.getInterfaces();
        for (Class<?> iface : interfaces) {
            iList.add(iface); // add to the interfaces list
        }
        for (int i = 0; i < interfaces.length; i++) {
            getAllThings(interfaces[i], fList, mList, cList, iList, sList);
        }
        Class<?> superClass = type.getSuperclass();
        if (superClass != null) {
            sList.add(superClass); // add to superclasses list
            getAllThings(superClass, fList, mList, cList, iList, sList);
        }
    }

    /**
     * @return the type of the class this data represents
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * @return the annotations on the class this data represents
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * @return the constructors for the class this data represents
     */
    public List<Constructor<T>> getConstructors() {
        return constructors;
    }

    /**
     * @return all fields in the class this data represents
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * @return all methods for the class this data represents
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * @return all interfaces for the class this data represents
     */
    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    /**
     * @return all superclasses (extends) for the class this data represents
     */
    public List<Class<?>> getSuperclasses() {
        return superclasses;
    }

    /**
     * @return the list of all enum constants (these are the actual enum statics for this enum) OR empty if this is not an enum
     */
    public List<T> getEnumConstants() {
        return enumConstants;
    }

    /**
     * Sorts the members by visibility and name order
     */
    public static final class MemberComparator implements Comparator<Member>, Serializable {
        public static final long serialVersionUID = 1l;
        public int compare(Member o1, Member o2) {
            String c1 = getModifierPrefix(o1.getModifiers()) + o1.getName();
            String c2 = getModifierPrefix(o2.getModifiers()) + o2.getName();
            return c1.compareTo(c2);
        }
    }

    public static final String getModifierPrefix(int modifier) {
        String prefix = "0public-";
        if (Modifier.isProtected(modifier)) {
            prefix = "1protected-";
        } else if (Modifier.isPrivate(modifier)) {
            prefix = "2private-";
        }
        return prefix;
    }
    
}

