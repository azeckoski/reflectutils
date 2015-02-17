/**
 * $Id: ClassProperty.java 35 2008-11-05 17:19:05Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ClassProperty.java $
 * ClassFields.java - genericdao - May 5, 2008 2:16:35 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2
 * 
 * A copy of the Apache License, Version 2 has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski@gmail.com) (aaronz@vt.edu) (aaron@caret.cam.ac.uk)
 */

package org.azeckoski.reflectutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.azeckoski.reflectutils.annotations.ReflectTransient;
import org.azeckoski.reflectutils.map.ArrayOrderedMap;
import org.azeckoski.reflectutils.map.OrderedMap;


/**
 * Simple class for holding the values we care about for a single field,
 * if it uses getters and setters then they must be like this:<br/>
 * If the fieldName is "thing" then: <br/>
 * <b>getter:</b> Object getThing() and 
 * <b>setter:</b> void setThing(Object value)
 * <br/>
 * Warning: Note that the values in this object may be garbage collected at any time,
 * do not store this object, it is meant for immediate short term use only
 */
public class ClassProperty {

    private final String fieldName;
    private Class<?> type;
    private Field field;
    private Method getter;
    private Method setter;

    protected boolean transientField = false;
    protected boolean staticField = false;
    protected boolean finalField = false;
    protected boolean publicField = false;
    protected int fieldModifiers = -1; // un-set value is -1

    protected boolean mapped = false;
    protected boolean indexed = false;
    protected boolean arrayed = false;

    private OrderedMap<Class<? extends Annotation>, Annotation> propertyAnnotations; // contains all annotations on this property
    protected void addAnnotation(Annotation annotation) {
        if (annotation != null) {
            if (propertyAnnotations == null) {
                propertyAnnotations = new ArrayOrderedMap<Class<? extends Annotation>, Annotation>();
            }
            Class<? extends Annotation> c = annotation.annotationType();
            if (! propertyAnnotations.containsKey(c)) {
                // only add an annotation in the first time it is encountered
                propertyAnnotations.put(c, annotation);
            }
            // note that we compare the simple name to avoid issues with cross classloader types
            if (ReflectTransient.class.getSimpleName().equals(c.getSimpleName())) {
                transientField = true; // pretend to be transient
            }
        }
    }
    protected Collection<Annotation> getAnnotationsCollection() {
        Collection<Annotation> ans = null;
        if (propertyAnnotations == null 
                || propertyAnnotations.isEmpty()) {
            ans = new ArrayList<Annotation>();
        } else {
            ans = propertyAnnotations.values();
        }
        return ans;
    }
    /**
     * @param annotationType the annotation type to look for on this field
     * @return the annotation of this type for this field OR null if none found
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        T a = null;
        if (propertyAnnotations != null) {
            a = (T) propertyAnnotations.get(annotationType);
        }
        return a;
    }
    /**
     * @return the annotations present on the property
     */
    public Annotation[] getAnnotations() {
        Collection<Annotation> c = getAnnotationsCollection();
        return c.toArray(new Annotation[c.size()]);
    }

    public ClassProperty(String fieldName) {
        this.fieldName = fieldName;
    }
    public ClassProperty(String fieldName, Method getter, Method setter) {
        this.fieldName = fieldName;
        if (getter != null) {
            setGetter(getter);
        }
        if (setter != null) {
            setSetter(setter);
        }
    }
    public ClassProperty(String fieldName, Field field) {
        this.fieldName = fieldName;
        setField(field);
    }

    /**
     * @return true if this field is complete (value can be set and retrieved via public methods/fields)
     */
    public boolean isComplete() {
        return (isPublicGettable() && isPublicSettable());
    }
    /**
     * @return true if this field is partial (value can be set OR retrieved but not both)
     */
    public boolean isPartial() {
        return (! isComplete());
    }
    /**
     * @return true if there is a getter and setter method available for this field (may not be public)
     */
    public boolean isProperty() {
        return (getGetter() != null && getSetter() != null);
    }
    /**
     * @return true if the associated field object is set
     */
    public boolean isField() {
        return (getField() != null);
    }
    /**
     * @return true if the associated field object is set and public
     */
    public boolean isPublicField() {
        return (publicField && isField());
    }
    /**
     * @return true if the associated field object is static
     */
    public boolean isStatic() {
        return staticField;
    }
    /**
     * @return true if this field value can be retrieved
     */
    public boolean isGettable() {
        return (isField() || getGetter() != null);
    }
    /**
     * @return true if this field value can be retrieved and is public (either the getter or the field)
     */
    public boolean isPublicGettable() {
        return (isPublicField() || getGetter() != null);
    }
    /**
     * @return true if this field value can be set
     */
    public boolean isSettable() {
        boolean settable = false;
        if (! finalField) { // no setting finals
            if (isField() || getSetter() != null) {
                settable = true;
            }
        }
        return settable;
    }
    /**
     * @return true if this field value can be set and is public (either the setter or the field)
     */
    public boolean isPublicSettable() {
        boolean settable = false;
        if (! finalField) { // no setting finals
            if (isPublicField() || getSetter() != null) {
                settable = true;
            }
        }
        return settable;
    }
    /**
     * @return the name of this field
     */
    public String getFieldName() {
        return fieldName;
    }
    public Method getGetter() {
        return getter;
    }
    protected void setGetter(Method getter) {
        this.getter = getter;
        makeType(); // getter type always wins
    }
    public Method getSetter() {
        return setter;
    }
    protected void setSetter(Method setter) {
        this.setter = setter;
        if (type == null) {
            makeType();
        }
    }
    public Field getField() {
        return field;
    }
    protected void setField(Field field) {
        this.field = field;
        // only the field knows the true fieldModifiers
        setModifiers(field);
        if (type == null) {
            makeType();
        }
    }
    /**
     * @return the type of this field
     */
    public Class<?> getType() {
        return type;
    }
    protected void setType(Class<?> type) {
        this.type = type;
    }
    /**
     * @return true if this field is final
     */
    public boolean isFinal() {
        return finalField;
    }
    /**
     * @return true if this field is transient
     */
    public boolean isTransient() {
        return transientField;
    }
    /**
     * @return true if this field is a map of some kind
     */
    public boolean isMapped() {
        return mapped;
    }
    /**
     * @return true if this field is indexed (typically a list)
     */
    public boolean isIndexed() {
        return indexed;
    }      
    /**
     * @return true if this field is an array
     */
    public boolean isArray() {
        return arrayed;
    }
    /**
     * @return the fieldModifiers code for this field (see {@link Modifier} to decode this)
     */
    public int getModifiers() {
        return fieldModifiers;
    }
    /**
     * Sets the fieldModifiers based on this field,
     * will not reset them once they are set except to clear them if a null field is set
     */
    protected void setModifiers(Field field) {
        if (field == null) {
            fieldModifiers = -1;
            transientField = false;
            finalField = false;
            publicField = false;
            staticField = false;
        } else {
            if (fieldModifiers < 0) {
                fieldModifiers = field.getModifiers();
                transientField = Modifier.isTransient(fieldModifiers);
                finalField = Modifier.isFinal(fieldModifiers);
                publicField = Modifier.isPublic(fieldModifiers);
                staticField = Modifier.isStatic(fieldModifiers);
            }
        }
    }
    private void makeType() {
        if (getGetter() != null) {
            Method m = getGetter();
            setType( m.getReturnType() );
        } else if (getSetter() != null) {
            Method m = getSetter();
            Class<?>[] params = m.getParameterTypes();
            if (params != null) {
                if (params.length == 1) {
                    setType( params[0] ); // normal setter
                } else if (params.length == 2) {
                    setType( params[1] ); // indexed/mapped setter
                }
            }
        } else {
            Field f = getField();
            setType( f.getType() );
        }
        indexed = false;
        arrayed = false;
        mapped = false;
        Class<?> type = getType();
        if (type != null) {
            if (type.isArray()) {
                indexed = true;
                arrayed = true;
            }
            if (Map.class.isAssignableFrom(type)) {
                mapped = true;
            }
            if (List.class.isAssignableFrom(type)) {
                indexed = true;
            }
        }
    }
    @Override
    public String toString() {
        return fieldName + "(" + (type == null ? "" : type.getSimpleName()) + ")["
        +(field == null?"-":"F")+(getter == null?"-":"G")+(setter == null?"-":"S")+":"
        +(mapped?"M":"-")+(indexed?"I":"-")+(arrayed?"A":"-")+":"
        +(finalField?"F":"-")+(transientField?"T":"-")+(publicField?"P":"-")+(staticField?"S":"-")+"]";
    }

    /**
     * Slightly extended version with the extra indexed properties,
     * indexed getters and setters must be setup like so:<br/>
     * If the fieldName is "thing" then: <br/>
     * <b>getter:</b> Object getThing(int index) and 
     * <b>setter:</b> void setThing(int index, Object value)
     */
    public static class IndexedProperty extends ClassProperty {
        private Method indexGetter;
        private Method indexSetter;

        public IndexedProperty(String fieldName) {
            super(fieldName);
            indexed = true;
        }
        public IndexedProperty(String fieldName, Method getter, Method setter) {
            super(fieldName, getter, setter);
            indexed = true;
        }
        public IndexedProperty(String fieldName, Method getter, Method setter, Method indexGetter,
                Method indexSetter) {
            super(fieldName, getter, setter);
            setIndexGetter(indexGetter);
            setIndexSetter(indexSetter);
            indexed = true;
        }

        public Method getIndexGetter() {
            return indexGetter;
        }      
        protected void setIndexGetter(Method indexGetter) {
            this.indexGetter = indexGetter;
        }
        public Method getIndexSetter() {
            return indexSetter;
        }
        protected void setIndexSetter(Method indexSetter) {
            this.indexSetter = indexSetter;
        }
        @Override
        public boolean isGettable() {
            boolean gettable = false;
            if (getIndexGetter() != null || super.isGettable()) {
                gettable = true;
            }
            return gettable;
        }
        @Override
        public boolean isSettable() {
            boolean settable = false;
            if (getIndexSetter() != null || super.isSettable()) {
                settable = true;
            }
            return settable;
        }
    }

    /**
     * Slightly extended version with the extra mapped properties,
     * mapped getters and setters must be setup like so:<br/>
     * If the fieldName is "thing" then: <br/>
     * <b>getter:</b> Object getThing(String key) and 
     * <b>setter:</b> void setThing(String key, Object value)
     * <br/>
     * The keys MUST be Strings
     */
    public static class MappedProperty extends ClassProperty {
        private Method mapGetter;
        private Method mapSetter;

        public MappedProperty(String fieldName) {
            super(fieldName);
            mapped = true;
        }
        public MappedProperty(String fieldName, Method getter, Method setter) {
            super(fieldName, getter, setter);
            mapped = true;
        }
        public MappedProperty(String fieldName, Method getter, Method setter, Method mapGetter,
                Method mapSetter) {
            super(fieldName, getter, setter);
            setMapGetter(mapGetter);
            setMapSetter(mapSetter);
            mapped = true;
        }

        public Method getMapGetter() {
            return mapGetter;
        }      
        protected void setMapGetter(Method mapGetter) {
            this.mapGetter = mapGetter;
        }
        public Method getMapSetter() {
            return mapSetter;
        }
        protected void setMapSetter(Method mapSetter) {
            this.mapSetter = mapSetter;
        }
        @Override
        public boolean isGettable() {
            boolean gettable = false;
            if (getMapGetter() != null || super.isGettable()) {
                gettable = true;
            }
            return gettable;
        }
        @Override
        public boolean isSettable() {
            boolean settable = false;
            if (getMapSetter() != null || super.isSettable()) {
                settable = true;
            }
            return settable;
        }
    }

}
