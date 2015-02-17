/**
 * $Id: ClassDataCacher.java 129 2014-03-18 23:25:36Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ClassDataCacher.java $
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

import org.azeckoski.reflectutils.ClassFields.FieldFindMode;
import org.azeckoski.reflectutils.refmap.ReferenceMap;
import org.azeckoski.reflectutils.refmap.ReferenceType;

import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * Class which provides access to the analysis objects and the cached reflection data
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ClassDataCacher {

    // CONSTRUCTORS

    /**
     * default constructor - protected
     */
    protected ClassDataCacher() {
        this( null, null );
    }

    /**
     * Construct and specify a mode for looking up fields which does not match the default: {@link FieldFindMode#HYBRID}
     * @param fieldFindMode the mode when looking up fields in classes
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     */
    public ClassDataCacher(FieldFindMode fieldFindMode) {
        this( fieldFindMode, null );
    }

    /**
     * Construct and specify the field finding mode and your own cache when caching class data, must implement the standard map interface but
     * only the following methods are required:<br/>
     * {@link Map#clear()}, {@link Map#size()}, {@link Map#put(Object, Object)}, {@link Map#get(Object)} <br/>
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     * 
     * @param fieldFindMode the mode when looking up fields in classes (null for default of {@link FieldFindMode#HYBRID})
     * @param reflectionCache a map implementation to use as the cache mechanism (null to use internal)
     */
    @SuppressWarnings("unchecked")
    public ClassDataCacher(FieldFindMode fieldFindMode, Map<Class<?>, ClassFields> reflectionCache) {
        setFieldFindMode(fieldFindMode);
        setReflectionCache(reflectionCache);

        ClassDataCacher.setInstance(this);
    }

    // class fields

    protected FieldFindMode fieldFindMode = FieldFindMode.HYBRID;
    /**
     * Set the mode used to find fields on classes (default {@link FieldFindMode#HYBRID}) <br/>
     * <b>WARNING</b>: changing modes will clear the existing cache
     * 
     * @param fieldFindMode see FieldFindMode enum for details
     * @see FieldFindMode
     */
    public void setFieldFindMode(FieldFindMode fieldFindMode) {
        if (fieldFindMode == null) {
            fieldFindMode = FieldFindMode.HYBRID;
        }
        if (! this.fieldFindMode.equals(fieldFindMode)) {
            // need to clear the cache if we change the mode
            getReflectionCache().clear();
        }
        this.fieldFindMode = fieldFindMode;
    }
    public FieldFindMode getFieldFindMode() {
        return fieldFindMode;
    }

    protected boolean includeClassField = false;
    /**
     * Setting to determine if the result of "getClass()" should be included in the reflection data <br/>
     * <b>WARNING</b>: changing this will clear the existing cache
     * 
     * @param includeClassField if true then getClass() will be treated as a readable field called "class", default is false
     */    
    public void setIncludeClassField(boolean includeClassField) {
        if (this.includeClassField != includeClassField) {
            // need to clear the cache if we change this
            getReflectionCache().clear();
        }
        this.includeClassField = includeClassField;
    }
    public boolean isIncludeClassField() {
        return includeClassField;
    }


    public int lookups = 0;
    public int cacheHits = 0;
    public int cacheMisses = 0;

    @SuppressWarnings("unchecked")
    protected Map<Class<?>, ClassFields> reflectionCache = null;
    @SuppressWarnings("unchecked")
    protected Map<Class<?>, ClassFields> getReflectionCache() {
        if (reflectionCache == null) {
            // internally we are using the ReferenceMap (from the Guice codebase)
            // modeled after the groovy reflection caching (weak -> soft)
            reflectionCache = new ReferenceMap<Class<?>,ClassFields>(ReferenceType.WEAK, ReferenceType.SOFT);
        }
        return reflectionCache;
    }
    /**
     * Set the cache to be used for holding the reflection data, 
     * this allows control over where the reflection caches are stored,
     * this should store the data in a way that it will not hold open the classloader the class comes from <br/>
     * Note that you can set this to a map implementation which does not store anything to disable caching if you like
     * 
     * @param reflectionCache a cache for holding class cache data (implements map), null to use the default internal cache
     */
    @SuppressWarnings("unchecked")
    public void setReflectionCache(Map<Class<?>, ClassFields> reflectionCache) {
        if (reflectionCache != null) {
            this.reflectionCache.clear();
            this.reflectionCache = reflectionCache;
        } else {
            getReflectionCache();
        }
    }

    /**
     * Get the class fields analysis of a class which contains information about that class and its fields,
     * includes annotations, fields/properties, etc. packaged in a way which makes the data easy to get to,
     * use the {@link ClassData} object to get to the more raw data
     * 
     * @param <T>
     * @param cls any {@link Class}
     * @return the ClassFields analysis object which contains the information about this class
     */
    public <T> ClassFields<T> getClassFields(Class<T> cls) {
        return getClassFields(cls, this.fieldFindMode);
    }

    /**
     * Get the class fields analysis of a class which contains information about that class and its fields,
     * includes annotations, fields/properties, etc. packaged in a way which makes the data easy to get to,
     * use the {@link ClassData} object to get to the more raw data
     *
     * @param <T>
     * @param cls any {@link Class}
     * @param mode (optional) mode for searching the class for fields, default HYBRID
     * @return the ClassFields analysis object which contains the information about this class
     */
    @SuppressWarnings("unchecked")
    public <T> ClassFields<T> getClassFields(Class<T> cls, FieldFindMode mode) {
        if (cls == null) {
            throw new IllegalArgumentException("cls (type) cannot be null");
        }
        if (mode == null) {
            if (this.fieldFindMode == null) {
                mode = FieldFindMode.HYBRID; // default
            } else {
                mode = this.fieldFindMode;
            }
        }
        lookups++;
        ClassFields<T> cf = getReflectionCache().get(cls);
        if (cf != null && !mode.equals(cf.getFieldFindMode())) {
            cf = null;
        }
        if (cf == null) {
            // make new and put in cache
            cf = new ClassFields<T>(cls, mode, false, this.includeClassField);
            getReflectionCache().put(cls, cf);
            cacheMisses++;
        } else {
            cacheHits++;
        }
        return cf;
    }

    /**
     * Convenience Method: <br/>
     * Gets the class data object which contains information about this class,
     * will retrieve this from the class data cache if available or generate it if not<br/>
     * This is also available from the {@link ClassFields} object
     * 
     * @param <T>
     * @param cls any {@link Class}
     * @return the class data cache object (contains reflected data from this class)
     */
    public <T> ClassData<T> getClassData(Class<T> cls) {
        ClassFields<T> cf = getClassFields(cls);
        return cf.getClassData();
    }

    /**
     * Convenience Method: <br/>
     * Analyze an object and produce an object which contains information about it and its fields,
     * see {@link ClassDataCacher#getClassData(Class)}
     * 
     * @param <T>
     * @param obj any {@link Object}
     * @return the ClassFields analysis object which contains the information about this objects class
     * @throws IllegalArgumentException if obj is null
     */
    @SuppressWarnings("unchecked")
    public <T> ClassFields<T> getClassFieldsFromObject(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("obj cannot be null");
        }
        Class<T> cls = (Class<T>) obj.getClass();
        return getClassFields(cls);
    }

    /**
     * Convenience Method: <br/>
     * Gets the class data object which contains information about this objects class,
     * will retrieve this from the class data cache if available or generate it if not<br/>
     * This is also available from the {@link ClassFields} object
     * 
     * @param <T>
     * @param obj any {@link Object}
     * @return the raw ClassData cache object which contains reflection data about this objects class
     * @throws IllegalArgumentException if obj is null
     */
    public <T> ClassData<T> getClassData(Object obj) {
        ClassFields<T> cf = getClassFieldsFromObject(obj);
        return cf.getClassData();
    }

    /**
     * Clears all cached objects
     */
    public void clear() {
        getReflectionCache().clear(); 
    }

    /**
     * @return the size of the cache (number of cached {@link ClassFields} entries)
     */
    public int size() {
        return getReflectionCache().size();
    }

    @Override
    public String toString() {
        return "Cache::c="+ClassDataCacher.timesCreated+":s="+singleton+":fieldMode="+fieldFindMode+":lookups="+lookups+"::cache:hits="+cacheHits+":misses="+cacheMisses+":size="+size()+":singleton="+singleton;
    }

    // STATIC access

    protected static SoftReference<ClassDataCacher> instanceStorage;
    /**
     * Get a singleton instance of this class to work with (stored statically) <br/>
     * <b>WARNING</b>: do not hold onto this object or cache it yourself, call this method again if you need it again
     * @return a singleton instance of this class
     */
    public static ClassDataCacher getInstance() {
        ClassDataCacher instance = (instanceStorage == null ? null : instanceStorage.get());
        if (instance == null) {
            instance = ClassDataCacher.setInstance(null);
        }
        return instance;
    }
    /**
     * Set the singleton instance of the class which will be stored statically
     * @param newInstance the instance to use as the singleton instance
     */
    public static ClassDataCacher setInstance(ClassDataCacher newInstance) {
        ClassDataCacher instance = newInstance;
        if (instance == null) {
            instance = new ClassDataCacher();
            instance.singleton = true;
        }
        ClassDataCacher.timesCreated++;
        instanceStorage = new SoftReference<ClassDataCacher>(instance);
        return instance;
    }
    public static void clearInstance() {
        instanceStorage.clear();
    }

    private static int timesCreated = 0;
    public static int getTimesCreated() {
        return timesCreated;
    }

    private boolean singleton = false;
    public final boolean isSingleton() {
        return singleton;
    }

}
