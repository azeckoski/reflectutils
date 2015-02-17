/**
 * $Id: ConstructorUtils.java 61 2009-09-25 11:14:16Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ConstructorUtils.java $
 * FieldUtils.java - genericdao - May 19, 2008 10:10:15 PM - azeckoski
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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.azeckoski.reflectutils.map.ArrayOrderedMap;

/**
 * Class which provides methods for dealing with class constructors,
 * also provides access to all the public constructors for a class
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ConstructorUtils {

    /**
     * Empty constructor
     * <br/>
     * <b>WARNING:</b> use the {@link #getInstance()} method to get this rather than recreating it over and over
     */
    public ConstructorUtils() {
        ConstructorUtils.setInstance(this);
    }

    protected ClassDataCacher getClassDataCacher() {
        return ClassDataCacher.getInstance();
    }

    // init all the maps when class inits
    static {
        makeImmutableDefaultsMap();
        makePrimitiveDefaultsMap();
        makePrimitiveWrapperMap();
        makeWTPMap();
    }

    /* 
     * Some code below derived from BeanCloner
     * http://www.coderslog.com/Main_Page
     * Copyright 2007 CodersLog.com 
     *   Licensed under the Apache License, Version 2.0 (the "License");
     */
    private static Set<Class<?>> immutableTypes = null;
    /**
     * @return a set of all known immutable types
     */
    public static synchronized Set<Class<?>> getImmutableTypes() {
        if (immutableTypes == null 
                || immutableTypes.isEmpty()) {
            makeDefaultImmuatableSet();
        }
        return immutableTypes;
    }
    private static void makeDefaultImmuatableSet() {
        immutableTypes = getImmutableDefaults().keySet();
    }

    private static Map<Class<?>, Object> immutableDefaults = null;
    /**
     * @return the map of all immutable types -> the default values for those types
     */
    public static synchronized Map<Class<?>, Object> getImmutableDefaults() {
        if (immutableDefaults == null ||
                immutableDefaults.isEmpty()) {
            makeImmutableDefaultsMap();
        }
        return immutableDefaults;
    }
    private static void makeImmutableDefaultsMap() {
        immutableDefaults = new HashMap<Class<?>, Object>();
        immutableDefaults.put(BigDecimal.class, new BigDecimal(0));
        immutableDefaults.put(BigInteger.class, BigInteger.valueOf(0l));
        immutableDefaults.put(Boolean.class, Boolean.FALSE);
        immutableDefaults.put(Byte.class, Byte.valueOf((byte)0));
        immutableDefaults.put(Character.class, (Character)c);
        immutableDefaults.put(Date.class, new Date(0));
        immutableDefaults.put(Double.class, Double.valueOf(0));
        immutableDefaults.put(Float.class, Float.valueOf(0));
        immutableDefaults.put(Long.class, Long.valueOf(0));
        immutableDefaults.put(Integer.class, Integer.valueOf(0));
        immutableDefaults.put(String.class, "");
        immutableDefaults.put(Short.class, Short.valueOf((short)0));
        immutableDefaults.put(Timestamp.class, new Timestamp(0));
    }

    private static Map<Class<?>, Class<?>> primitiveToWrapper = null;
    /**
     * @return the map of all primitive types -> wrapper types
     */
    public static synchronized Map<Class<?>, Class<?>> getPrimitiveToWrapper() {
        if (primitiveToWrapper == null ||
                primitiveToWrapper.isEmpty()) {
            makePrimitiveWrapperMap();
        }
        return primitiveToWrapper;
    }
    private static void makePrimitiveWrapperMap() {
        primitiveToWrapper = new HashMap<Class<?>, Class<?>>();
        primitiveToWrapper.put(boolean.class, Boolean.class);
        primitiveToWrapper.put(byte.class, Byte.class);
        primitiveToWrapper.put(char.class, Character.class);
        primitiveToWrapper.put(double.class, Double.class);
        primitiveToWrapper.put(float.class, Float.class);
        primitiveToWrapper.put(int.class, Integer.class);
        primitiveToWrapper.put(long.class, Long.class);
        primitiveToWrapper.put(short.class, Short.class);
        primitiveToWrapper.put(boolean[].class, Boolean[].class);
        primitiveToWrapper.put(byte[].class, Byte[].class);
        primitiveToWrapper.put(char[].class, Character[].class);
        primitiveToWrapper.put(double[].class, Double[].class);
        primitiveToWrapper.put(float[].class, Float[].class);
        primitiveToWrapper.put(int[].class, Integer[].class);
        primitiveToWrapper.put(long[].class, Long[].class);
        primitiveToWrapper.put(short[].class, Short[].class);
    }

    private static Map<Class<?>, Class<?>> wrapperToPrimitive = null;
    /**
     * @return the map of all wrapper types -> primitive types
     */
    public static synchronized Map<Class<?>, Class<?>> getWrapperToPrimitive() {
        if (wrapperToPrimitive == null ||
                wrapperToPrimitive.isEmpty()) {
            makeWTPMap();
        }
        return wrapperToPrimitive;
    }
    private static void makeWTPMap() {
        wrapperToPrimitive = new HashMap<Class<?>, Class<?>>();
        wrapperToPrimitive.put(Boolean.class, boolean.class);
        wrapperToPrimitive.put(Byte.class, byte.class);
        wrapperToPrimitive.put(Character.class, char.class);
        wrapperToPrimitive.put(Double.class, double.class);
        wrapperToPrimitive.put(Float.class, float.class);
        wrapperToPrimitive.put(Integer.class, int.class);
        wrapperToPrimitive.put(Long.class, long.class);
        wrapperToPrimitive.put(Short.class, short.class);
        wrapperToPrimitive.put(Boolean[].class, boolean[].class);
        wrapperToPrimitive.put(Byte[].class, byte[].class);
        wrapperToPrimitive.put(Character[].class, char[].class);
        wrapperToPrimitive.put(Double[].class, double[].class);
        wrapperToPrimitive.put(Float[].class, float[].class);
        wrapperToPrimitive.put(Integer[].class, int[].class);
        wrapperToPrimitive.put(Long[].class, long[].class);
        wrapperToPrimitive.put(Short[].class, short[].class);
    }

    private static Map<Class<?>, Object> primitiveDefaults = null;
    /**
     * @return the map of all primitive types -> the default values for those types
     */
    public static synchronized Map<Class<?>, Object> getPrimitiveDefaults() {
        if (primitiveDefaults == null ||
                primitiveDefaults.isEmpty()) {
            makePrimitiveDefaultsMap();
        }
        return primitiveDefaults;
    }
    private static char c;
    private static void makePrimitiveDefaultsMap() {
        primitiveDefaults = new HashMap<Class<?>, Object>();
        primitiveDefaults.put(boolean.class, (Boolean)false);
        primitiveDefaults.put(byte.class, (Byte)(byte)0);
        primitiveDefaults.put(char.class, (Character)c);
        primitiveDefaults.put(double.class, (Double)0.0D);
        primitiveDefaults.put(float.class, (Float)0.0F);
        primitiveDefaults.put(int.class, (Integer)0);
        primitiveDefaults.put(long.class, (Long)0L);
        primitiveDefaults.put(short.class, (Short)(short)0);
    }

    private static void checkNull(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("class type cannot be null to check the type");
        }
    }

    /**
     * Get the default value for for a type if one is available OR null if there is no default (since null sorta is the default)
     * @param <T>
     * @param type any class type including primitives
     * @return the default value OR null if there is no default
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDefaultValue(Class<T> type) {
        T val = null;
        if (getPrimitiveDefaults().containsKey(type)) {
            val = (T) getPrimitiveDefaults().get(type);
        } else if (getImmutableDefaults().containsKey(type)) {
            val = (T) getImmutableDefaults().get(type);
        }
        return val;
    }

    /**
     * @param type any class
     * @return true if this class is a primitive or other simple class (like String or immutable)
     */
    public static boolean isClassSimple(Class<?> type) {
        checkNull(type);
        boolean simple = false;
        if ( isClassPrimitive(type) 
                || getImmutableTypes().contains(type)) {
            simple = true;
        }
        return simple;
    }

    /**
     * Indicates that this class is a special type which we should not attempt to reflect over,
     * especially which doing deep copies or clones,
     * reflection over special types is generally slow or extremely costly or unpredictable
     * 
     * @param type any class
     * @return true if this is a special type which is non-reflectable
     */
    public static boolean isClassSpecial(Class<?> type) {
        boolean special = false;
        if (Class.class.isAssignableFrom(type)) {
            special = true;
        } else if (Type.class.isAssignableFrom(type)) {
            special = true;
        } else if (Package.class.isAssignableFrom(type)) {
            special = true;
        } else if (ClassLoader.class.isAssignableFrom(type)) {
            special = true;
        } else if (InputStream.class.isAssignableFrom(type)) {
            special = true;
        } else if (OutputStream.class.isAssignableFrom(type)) {
            special = true;
        } else if (InputStream.class.isAssignableFrom(type)) {
            special = true;
        } else if (Writer.class.isAssignableFrom(type)) {
            special = true;
        } else if (Reader.class.isAssignableFrom(type)) {
            special = true;
        }
        return special;
    }

    /**
     * @param type any class
     * @return true if this class is a bean of some kind (i.e. not primitive, immutable, or a holder like a map)
     */
    public static boolean isClassBean(Class<?> type) {
        checkNull(type);
        boolean bean = true;
        if ( isClassSimple(type) || isClassObjectHolder(type) ) {
            bean = false;
        }
        return bean;
    }

    /**
     * @param type any class
     * @return true if this class is an array (e.g. int[].class, {@link Integer}[] )
     */
    public static boolean isClassArray(Class<?> type) {
        checkNull(type);
        boolean array = false;
        if (type.isArray()) {
            array = true;
        }
        return array;
    }

    /**
     * @param type any class
     * @return true if this class is a primitive (e.g. int.class, boolean.class)
     */
    public static boolean isClassPrimitive(Class<?> type) {
        checkNull(type);
        boolean primitive = false;
        if (type.isPrimitive()) {
            primitive = true;
        }
        return primitive;
    }

    /**
     * @param type any class
     * @return true if this class is a list (e.g. {@link List}, {@link ArrayList})
     */
    public static boolean isClassList(Class<?> type) {
        checkNull(type);
        boolean list = false;
        if (List.class.isAssignableFrom(type)) {
            list = true;
        }
        return list;
    }

    /**
     * @param type any class
     * @return true if this class is a collection (e.g. {@link Collection}, {@link HashSet}, {@link Vector})
     */
    public static boolean isClassCollection(Class<?> type) {
        checkNull(type);
        boolean collection = false;
        if (Collection.class.isAssignableFrom(type)) {
            collection = true;
        }
        return collection;
    }

    /**
     * @param type any class
     * @return true if this class is a map (e.g. {@link Map}, {@link HashMap})
     */
    public static boolean isClassMap(Class<?> type) {
        checkNull(type);
        boolean collection = false;
        if (Map.class.isAssignableFrom(type)) {
            collection = true;
        }
        return collection;
    }

    /**
     * @param type any class
     * @return true if this is a collection, map, or array, 
     * something that holds a bunch of objects (e.g. {@link Map}, {@link Set}, {@link List}, array)
     */
    public static boolean isClassObjectHolder(Class<?> type) {
        checkNull(type);
        boolean holder = false;
        if ( isClassArray(type) || isClassCollection(type) || isClassMap(type) ) {
            holder = true;
        }
        return holder;
    }

    /**
     * @param type any class
     * @return the type of the array elements if this is an array or just the type if it is not an array
     */
    public static Class<?> getTypeFromArray(Class<?> type) {
        Class<?> toType = type;
        if (type.isArray()) {
            toType = type.getComponentType();
        }
        return toType;
    }

    /**
     * Checks for the special cases of the inner collections in {@link Collections} and {@link Arrays}
     * @param type any class type
     * @return the equivalent of the inner collection type or the original type if this is not one
     */
    public static Class<?> getTypeFromInnerCollection(Class<?> type) {
        // check for the special cases of collections which cannot be constructed
        if (type != null) {
            Class<?> parent = type.getEnclosingClass();
            if (parent != null) {
                if (Collections.class.equals(parent)) {
                    // unmodifiable collections
                    List<Class<?>> l = getInterfacesForClass(type);
                    if (l.size() > 0) {
                        for (Class<?> iface : l) {
                            if (Collection.class.isAssignableFrom(iface)) {
                                if ( List.class.isAssignableFrom(iface) 
                                        || Set.class.isAssignableFrom(iface)) {
                                    type = iface;
                                } else {
                                    type = Collection.class;
                                }
                                break;
                            } else if (Map.class.isAssignableFrom(iface)) {
                                type = iface;
                                break;
                            }
                        }
                    } else {
                        type = Collection.class;
                    }
                } else if (Arrays.class.equals(parent)) {
                    // Arrays#ArrayList special case
                    type = List.class;
                }
            }
        }
        return type;
    }

    /**
     * Gets a valid class which can be constructed from an interface or special cases which cannot be constructed
     * @param <T>
     * @param type any class
     * @return the type which implements this interface if one can be found
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassFromInterface(Class<T> type) {
        Class<T> toType = type;
        // check for the special cases of collections which cannot be constructed
        type = (Class<T>) ConstructorUtils.getTypeFromInnerCollection(type);
        // now check for interfaces
        if (type.isInterface()) {
            if (SortedSet.class.isAssignableFrom(type)) {
                toType = (Class<T>) TreeSet.class;
            } else if (SortedMap.class.isAssignableFrom(type)) {
                toType = (Class<T>) TreeMap.class;
            } else if ( isClassList(type) ) {
                // we use the thread safe version of list by default
                toType = (Class<T>) Vector.class;
            } else if (Set.class.isAssignableFrom(type)) {
                toType = (Class<T>) HashSet.class;
            } else if ( isClassMap(type) ) {
                toType = (Class<T>) ArrayOrderedMap.class;
            } else if ( isClassCollection(type) ) {
                toType = (Class<T>) Vector.class;
            // Serializable should stay at the end
            } else if (Serializable.class.isAssignableFrom(type)) {
                // if it is serializable then it is probably a string right?
                toType = (Class<T>) String.class;
            } else {
                // TODO try to find the interface implementation in the ClassLoader (not actually possible without real hackery)
            }
        }
        return toType;
    }

    /**
     * A simple but efficient method for getting the interfaces for a class type,
     * this has some shortcuts for the common types like maps, lists, etc.<br/>
     * Only returns the interfaces for the current type and not for all nested types
     * 
     * @param type any class type
     * @return the list of interfaces (empty if none)
     */
    public static List<Class<?>> getInterfacesForClass(Class<?> type) {
        ArrayList<Class<?>> interfaces = new ArrayList<Class<?>>();
        // find the actual interfaces from the class itself
        for (Class<?> iface : type.getInterfaces()) {
            interfaces.add(iface);
        }
        // add in the collection interface if this is a collection
        if ( isClassCollection(type) ) {
            if ( isClassList(type) ) {
                interfaces.add(List.class);
            } else if ( Set.class.isAssignableFrom(type)) {
                interfaces.add(Set.class);
            }
            interfaces.add(Collection.class);
        } else if ( isClassMap(type) ) {
            interfaces.add(Map.class);
        }
        return interfaces;
    }

    /**
     * Adds the class which this class extends (if there is one) to the list of interfaces
     * @see #getInterfacesForClass(Class)
     * @param type any class type
     * @return the list of interfaces and the class this extends (empty if none)
     */
    public static List<Class<?>> getExtendAndInterfacesForClass(Class<?> type) {
        ArrayList<Class<?>> l = new ArrayList<Class<?>>();
        Class<?> superClass = type.getSuperclass();
        if (superClass != null) {
            l.add(superClass);
        }
        l.addAll( getInterfacesForClass(type) );
        return l;
    }

    /**
     * Get the wrapper class for this class if there is one
     * @param beanClass any class
     * @return the wrapper class if there is one OR just returns the given class
     */
    public static Class<?> getWrapper(final Class<?> beanClass) {
        Class<?> wrapper = null;
        if (beanClass != null) {
            if ( isClassPrimitive(beanClass) ) {
                wrapper = getPrimitiveToWrapper().get(beanClass);
            } else if ( isClassArray(beanClass) && beanClass.getComponentType().isPrimitive()) {
                wrapper = getPrimitiveToWrapper().get(beanClass);
            } else {
                wrapper = beanClass;
            }
            if (wrapper == null) {
                wrapper = beanClass;
            }
        }
        return wrapper;
    }

    /**
     * Will compare 2 classes for equality which will make a friendly comparison of types
     * and will happily compare primitive types with wrappers and say they are equal
     * @param c1 any class
     * @param c2 any class
     * @return true if the classes are equivalent, false otherwise
     */
    public static boolean classEquals(final Class<?> c1, final Class<?> c2) {
        boolean equals = false;
        if (c1 == null || c2 == null) {
            equals = false;
        } else {
            if (c1.isArray() && c2.isArray()) {
                // both arrays
                if (c1.getComponentType().isPrimitive() == c2.getComponentType().isPrimitive()) {
                    equals = c1.equals(c2);
                } else {
                    // mixed primitive/wrappers so make all wrappers
                    Class<?> c1W = getWrapper(c1);
                    Class<?> c2W = getWrapper(c2);
                    equals = c1W.equals(c2W);
                }
            } else {
                if (c1.isArray() || c2.isArray()) {
                    // one array and the other is not so cannot be equals
                    equals = false;
                } else if (c1.isPrimitive() == c2.isPrimitive()) {
                    equals = c1.equals(c2);
                } else {
                    // mixed primitive/wrappers so make all wrappers
                    Class<?> c1W = getWrapper(c1);
                    Class<?> c2W = getWrapper(c2);
                    equals = c1W.equals(c2W);
                }
            }
        }
        return equals;
    }

    /**
     * Checks if assignFrom is assignable to assignTo (i.e. this is OK: assignFrom b; assignTo a = (assignTo) b;) <br/>
     * An example of this is: Integer b; Object a = (Object) b; <br/>
     * Another example of this is: ExtendedThing b; Thing a = (Thing) b; <br/>
     * This works like {@link #classEquals(Class, Class)} and will convert primitive class types for comparison automatically
     * 
     * @param assignFrom any class
     * @param assignTo any class
     * @return true if the class is assignable or equal OR false otherwise
     */
    public static boolean classAssignable(final Class<?> assignFrom, final Class<?> assignTo) {
        boolean assignable = false;
        if (assignTo == null || assignFrom == null) {
            assignable = false;
        } else {
            if ( Object.class.equals(assignTo) ) {
                // anything can assign to an object
                assignable = true;
            } else if ( classEquals(assignTo, assignFrom) ) {
                assignable = true;
            } else {
                if (assignTo.isAssignableFrom(assignFrom)) {
                    assignable = true;
                } else {
                    // make everything wrappers
                    Class<?> c1W = getWrapper(assignTo);
                    Class<?> c2W = getWrapper(assignFrom);
                    assignable = c1W.isAssignableFrom(c2W);
                }
            }
        }
        return assignable;
    }

    /* 
     * Some code below derived from BeanUtilsBean and PropertyUtilsbean
     * http://commons.apache.org/beanutils/
     *   Licensed under the Apache License, Version 2.0 (the "License");
     */

    /**
     * Construct an object for the class of the given type regardless of whether it has a default constructor,
     * this will construct anything which has a valid class type including primitives,
     * arrays, collections and even classes without default constructors,
     * this will attempt to use the default constructor first if available though, 
     * It must be possible to construct the class without knowing something about it beforehand,
     * (i.e. classes with only constructors which require non-null arguments will not be able
     * to be constructed)
     * 
     * @param <T>
     * @param type any object class
     * @return the newly constructed object of the given class type 
     * (if primitive then a wrapped object will be returned which java will unwrap automatically)
     * @throws IllegalArgumentException if the class is null or the class cannot be constructed
     */
    @SuppressWarnings("unchecked")
    public <T> T constructClass(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Cannot construct class when beanClass is null");
        }
        // make sure we are not trying to construct an interface
        type = ConstructorUtils.getClassFromInterface(type);
        T newC = null;
        if ( isClassPrimitive(type) ) {
            if (getPrimitiveDefaults().containsKey(type)) {
                newC = (T) getPrimitiveDefaults().get(type);
            }
        } else if ( isClassArray(type) ) {
            Class<?> componentType = getTypeFromArray(type);
            try {
                newC = (T) Array.newInstance(componentType, 0);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Could not construct array of type: " + componentType + " for: " + type.getName());
            }
        }
        if (newC == null) {
            try {
                // this should work 99% of the time
                newC = (T) type.newInstance();
            } catch (Exception e) {
                // now we will try to use the various constructors by giving them null values to construct the object
                List<Constructor<T>> constructors = null;
                if ( ConstructorUtils.isClassBean(type) ) {
                    // get bean constructors
                    constructors = getClassDataCacher().getClassData(type).getConstructors();
                } else {
                    // simpler type
                    try {
                        Constructor<?>[] c = type.getConstructors();
                        constructors = Arrays.asList((Constructor<T>[])c);
                    } catch (SecurityException e1) {
                        throw new IllegalArgumentException("Could not construct object for class (" + type.getName() + "): " + e1.getMessage(), e1);
                    }
                }
                for (Constructor<T> constructor : constructors) {
                    Object[] params = new Object[ constructor.getParameterTypes().length ];
                    try {
                        newC = (T) constructor.newInstance(params);
                        break;
                    } catch (IllegalArgumentException e1) {
                        // oh well
                    } catch (InstantiationException e1) {
                        // tough cookies
                    } catch (IllegalAccessException e1) {
                        // them's the breaks
                    } catch (InvocationTargetException e1) {
                        // life's tough
                    } catch (ExceptionInInitializerError e1) {
                        // meh
                    }
                    // ignore any exceptions and keep trying
                }
                if (newC == null) {
                    // all attempts failed
                    throw new IllegalArgumentException("Could not construct object for class (" + type.getName() + ") using newInstance or using any of the constructors: " + e.getMessage(), e);
                }
            }
        }
        return newC;
    }

    /**
     * Construct an object for the class of the given type with the given params (arguments),
     * arguments must match or the construction will fail
     * 
     * @param <T>
     * @param type any object class
     * @param params the parameters (args) for the constructor
     * @return the newly constructed object of the given class type OR fails if the params cannot be matched
     * @throws IllegalArgumentException if the class is null or the class cannot be constructed
     */
    @SuppressWarnings("unchecked")
    public <T> T constructClass(Class<T> type, Object[] params) {
        if (type == null) {
            throw new IllegalArgumentException("beanClass cannot be null");
        }
        T newC = null;
        if (params == null || params.length == 0) {
            newC = constructClass(type);
        } else {
            int paramsCount = params.length;
            // get all the constructors
            List<Constructor<T>> constructors = null;
            try {
                constructors = getClassDataCacher().getClassData(type).getConstructors();
            } catch (IllegalArgumentException e) {
                try {
                    Constructor<?>[] c = type.getConstructors();
                    constructors = Arrays.asList((Constructor<T>[])c);
                } catch (SecurityException e1) {
                    throw new IllegalArgumentException("Could not construct object for class (" + type.getName() + ")", e1);
                }
            }
            // make a list of the param type arrays
            List<Class<?>[]> constParamTypesList = new ArrayList<Class<?>[]>();
            for (Constructor<T> constructor : constructors) {
                constParamTypesList.add( constructor.getParameterTypes() );
            }
            // make an array of the input params types
            Class<?>[] paramTypes = new Class<?>[params.length];
            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    // handle nulls as any object
                    paramTypes[i] = Object.class;
                } else {
                    Class<?> c = params[i].getClass();
                    paramTypes[i] = c;
                }
            }
            // now see if any are a match
            Constructor<T> matched = null;
            Object[] args = null;
            // try to find exact match by size and order 
            for (int i = 0; i < constParamTypesList.size(); i++) {
                Class<?>[] cParamTypes = constParamTypesList.get(i);
                if (cParamTypes.length == paramsCount) {
                    // found matching number of params
                    boolean matching = false;
                    for (int j = 0; j < cParamTypes.length; j++) {
                        if ( classEquals(paramTypes[j], cParamTypes[j]) ) {
                            matching = true;
                        } else {
                            matching = false;
                            break;
                        }
                    }
                    if (matching) {
                        // found exact match
                        matched = constructors.get(i);
                        args = params;
                        break;
                    }
                }
            }
            // try to find exact match by size and order (but not the exact class types, just assignable) 
            if (matched == null) {
                for (int i = 0; i < constParamTypesList.size(); i++) {
                    Class<?>[] cParamTypes = constParamTypesList.get(i);
                    if (cParamTypes.length == paramsCount) {
                        // found matching number of params
                        boolean matching = false;
                        for (int j = 0; j < cParamTypes.length; j++) {
                            if ( classAssignable(cParamTypes[j], paramTypes[j]) ) {
                                // assignable (near) match
                                matching = true;
                            } else {
                                matching = false;
                                break;
                            }
                        }
                        if (matching) {
                            // found nearly exact match
                            matched = constructors.get(i);
                            args = params;
                            break;
                        }
                    }
                }
            }
            // TODO try to find near match by size (same number and types)
            // TODO try to find near match by order (same order but with extra junk nulls on the end)
            // TODO try to make any possible match
            // now try to construct if we got a match
            if (matched != null) {
                try {
                    newC = matched.newInstance(args);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Failure constructing object for class (" + type.getName() + ") with the given params: " + ArrayUtils.arrayToString(params), e);
                }
            }
        }
        if (newC == null) {
            throw new IllegalArgumentException("Could not construct object for class (" + type.getName() + ") with the given params: " + ArrayUtils.arrayToString(params));
        }
        return newC;
    }

    @Override
    public String toString() {
        return "Construct:"+getClassDataCacher();
    }

    // STATIC access

    protected static SoftReference<ConstructorUtils> instanceStorage;
    /**
     * Get a singleton instance of this class to work with (stored statically) <br/>
     * <b>WARNING</b>: do not hold onto this object or cache it yourself, call this method again if you need it again
     * @return a singleton instance of this class
     */
    public static ConstructorUtils getInstance() {
        ConstructorUtils instance = (instanceStorage == null ? null : instanceStorage.get());
        if (instance == null) {
            instance = ConstructorUtils.setInstance(null);
        }
        return instance;
    }
    /**
     * Set the singleton instance of the class which will be stored statically
     * @param instance the instance to use as the singleton instance
     */
    public static ConstructorUtils setInstance(ConstructorUtils newInstance) {
        ConstructorUtils instance = newInstance;
        if (instance == null) {
            instance = new ConstructorUtils();
            instance.singleton = true;
        }
        ConstructorUtils.timesCreated++;
        instanceStorage = new SoftReference<ConstructorUtils>(instance);
        return instance;
    }

    private static int timesCreated = 0;
    public static int getTimesCreated() {
        return timesCreated;
    }

    private boolean singleton = false;
    /**
     * @return true if this object is the singleton
     */
    public boolean isSingleton() {
        return singleton;
    }

}
