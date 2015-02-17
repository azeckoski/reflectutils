/**
 * $Id: ConversionUtils.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ConversionUtils.java $
 * ConversionUtils.java - genericdao - May 19, 2008 10:10:15 PM - azeckoski
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

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.azeckoski.reflectutils.converters.ArrayConverter;
import org.azeckoski.reflectutils.converters.BigDecimalConverter;
import org.azeckoski.reflectutils.converters.BigIntegerConverter;
import org.azeckoski.reflectutils.converters.BooleanConverter;
import org.azeckoski.reflectutils.converters.ByteConverter;
import org.azeckoski.reflectutils.converters.CalendarConverter;
import org.azeckoski.reflectutils.converters.CharacterConverter;
import org.azeckoski.reflectutils.converters.ClassConverter;
import org.azeckoski.reflectutils.converters.CollectionConverter;
import org.azeckoski.reflectutils.converters.DateConverter;
import org.azeckoski.reflectutils.converters.DoubleConverter;
import org.azeckoski.reflectutils.converters.EnumConverter;
import org.azeckoski.reflectutils.converters.FileConverter;
import org.azeckoski.reflectutils.converters.FloatConverter;
import org.azeckoski.reflectutils.converters.IntegerConverter;
import org.azeckoski.reflectutils.converters.LongConverter;
import org.azeckoski.reflectutils.converters.MapConverter;
import org.azeckoski.reflectutils.converters.NumberConverter;
import org.azeckoski.reflectutils.converters.SQLDateConverter;
import org.azeckoski.reflectutils.converters.SQLTimeConverter;
import org.azeckoski.reflectutils.converters.ScalarConverter;
import org.azeckoski.reflectutils.converters.ShortConverter;
import org.azeckoski.reflectutils.converters.StringConverter;
import org.azeckoski.reflectutils.converters.TimestampConverter;
import org.azeckoski.reflectutils.converters.URLConverter;
import org.azeckoski.reflectutils.converters.api.Converter;
import org.azeckoski.reflectutils.converters.api.InterfaceConverter;
import org.azeckoski.reflectutils.converters.api.VariableConverter;
import org.azeckoski.reflectutils.refmap.ReferenceMap;
import org.azeckoski.reflectutils.refmap.ReferenceType;

/**
 * Class which provides methods for converting between object types,
 * can be extended with various converters
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ConversionUtils {

    /**
     * Empty constructor
     */
    protected ConversionUtils() {
        this(null);
    }

    /**
     * Constructor which allows adding to the initial set of converters
     * <br/>
     * <b>WARNING:</b> if you don't need this control then just use the {@link #getInstance()} method to get this
     * 
     * @param converters a map of converters to add to the default set
     */
    public ConversionUtils(Map<Class<?>, Converter<?>> converters) {
        // populate the converters
        setConverters(converters);
        setVariableConverters(null);

        ConversionUtils.setInstance(this);
    }

    protected Map<Class<?>, Converter<?>> converters = null;
    /**
     * Set the object converters to add to the default converters
     * @param converters a map of converters to add to the default set
     */
    public void setConverters(Map<Class<?>, Converter<?>> converters) {
        loadDefaultConverters();
        if (converters != null) {
            for (Entry<Class<?>, Converter<?>> entry : converters.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    this.converters.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    protected Map<Class<?>, Converter<?>> getConverters() {
        if (converters == null) {
            loadDefaultConverters();
        }
        return converters;
    }

    /**
     * this loads up all the default converters
     */
    protected void loadDefaultConverters() {
        if (converters == null) {
            converters = new ReferenceMap<Class<?>, Converter<?>>(ReferenceType.WEAK, ReferenceType.STRONG);
        } else {
            converters.clear();
        }
        // order is not important here but maintain alpha order for readability
        converters.put(BigDecimal.class, new BigDecimalConverter());
        converters.put(BigInteger.class, new BigIntegerConverter());
        converters.put(Boolean.class, new BooleanConverter());
        converters.put(Byte.class, new ByteConverter());
        converters.put(Calendar.class, new CalendarConverter());
        converters.put(Character.class, new CharacterConverter());
        converters.put(Class.class, new ClassConverter());
        converters.put(Collection.class, new CollectionConverter());
        converters.put(Date.class, new DateConverter());
        converters.put(Double.class, new DoubleConverter());
        converters.put(Enum.class, new EnumConverter());
        converters.put(File.class, new FileConverter());
        converters.put(Float.class, new FloatConverter());
        converters.put(Integer.class, new IntegerConverter());
        converters.put(Long.class, new LongConverter());
        converters.put(Map.class, new MapConverter());
        converters.put(Number.class, new NumberConverter());
        converters.put(Short.class, new ShortConverter());
        converters.put(String.class, new StringConverter());
        converters.put(java.sql.Date.class, new SQLDateConverter());
        converters.put(java.sql.Time.class, new SQLTimeConverter());
        converters.put(java.sql.Timestamp.class, new TimestampConverter());
        converters.put(URL.class, new URLConverter());
    }

    /**
     * Add a converter to the default set which will convert objects to the supplied type
     * @param type the type this converter will convert objects to
     * @param converter the converter
     */
    public void addConverter(Class<?> type, Converter<?> converter) {
        if (type == null || converter == null) {
            throw new IllegalArgumentException("You must specify a type and a converter in order to add a converter (no nulls)");
        }
        getConverters().put(type, converter);
    }

    protected List<VariableConverter> variableConverters = null;
    /**
     * Replace the current or default variable converters with a new set,
     * this will remove the default variable converters and will not add them back in
     * @param variableConverters the variable object converters
     */
    public void setVariableConverters(List<VariableConverter> variableConverters) {
        loadDefaultVariableConverters();
        if (variableConverters != null) {
            for (VariableConverter variableConverter : variableConverters) {
                if (variableConverter != null) {
                    this.variableConverters.add(variableConverter);
                }
            }
        }
    }
    protected List<VariableConverter> getVariableConverters() {
        if (variableConverters == null) {
            loadDefaultVariableConverters();
        }
        return variableConverters;
    }

    protected void loadDefaultVariableConverters() {
        if (variableConverters == null) {
            variableConverters = new Vector<VariableConverter>();
        } else {
            clearVariableConverters();
        }
        variableConverters.add( new ArrayConverter() );
        variableConverters.add( new ScalarConverter() );
    }

    /**
     * Adds a variable converter to the end of the list of default variable converters
     * @param variableConverter
     */
    public void addVariableConverter(VariableConverter variableConverter) {
        if (variableConverter == null) {
            throw new IllegalArgumentException("You must specify a variableConverter in order to add it (no nulls)");
        }
        getVariableConverters().add(variableConverter);
    }

    /**
     * Resets and removes all variable converters including the defaults,
     * use this when you want to override the existing variable converters
     */
    public void clearVariableConverters() {
        if (variableConverters != null) {
            variableConverters.clear();
        }
    }


    /**
     * Added for apache commons beanutils compatibility,
     * you should probably use {@link #convert(Object, Class)}<br/>
     * Convert the specified value into a String.  If the specified value
     * is an array, the first element (converted to a String) will be
     * returned.  The registered {@link Converter} for the
     * <code>java.lang.String</code> class will be used, which allows
     * applications to customize Object->String conversions (the default
     * implementation simply uses toString()).
     *  
     * @param object any object
     * @return the string OR null if one cannot be found
     */
    public String convertToString(Object object) {
        // code here is basically from ConvertUtilsBeans 1.8.0
        String convert = null;
        if (object == null) {
            convert = null;
        } else if (object.getClass().isArray()) {
            if (Array.getLength(object) < 1) {
                convert = null;
            } else {
                Object value = Array.get(object, 0);
                if (value == null) {
                    convert = null;
                } else {
                    Converter<String> converter = getConverter(String.class);
                    return converter.convert(value);
                }
            }
        } else {
            Converter<String> converter = getConverter(String.class);
            return converter.convert(object);
        }
        return convert;
    }

    /**
     * Added for apache commons beanutils compatibility,
     * you should probably use {@link #convert(Object, Class)}<br/>
     * Convert the string value to an object of the specified class (if
     * possible).  Otherwise, return a String representation of the value.
     *
     * @param value the string value to be converted
     * @param type any class type that you want to try to convert the object to
     * @return the converted value
     * @throws UnsupportedOperationException if the conversion cannot be completed
     */
    public Object convertString(String value, Class<?> type) {
        Object convert = null;
        try {
            convert = convert(value, type);
        } catch (UnsupportedOperationException e) {
            convert = value;
        }
        return convert;
    }

    /**
     * Converts an object to any other object if possible using the current set of converters,
     * will allow nulls to pass through unless there is a converter which will handle them <br/>
     * Includes special handling for primitives, arrays, and collections 
     * (will take the first value when converting to scalar)
     * 
     * @param <T>
     * @param value any object
     * @param type any class type that you want to try to convert the object to
     * @return the converted value (allows null to pass through except in the case of primitives which become the primitive default)
     * @throws UnsupportedOperationException if the conversion cannot be completed
     */
    @SuppressWarnings("unchecked")
    public <T> T convert(Object value, Class<T> type) {
        T convert = null;
        Object toConvert = value;
        if (toConvert != null) {
            Class<?> fromType = toConvert.getClass();
            // first we check to see if we even need to do the conversion
            if ( ConstructorUtils.classAssignable(fromType, type) ) {
                // this is already equivalent so no reason to convert
                convert = (T) value;
            } else {
                // needs to be converted
                try {
                    convert = convertWithConverter(toConvert, type);
                } catch (RuntimeException e) {
                    throw new UnsupportedOperationException("Could not convert object ("+toConvert+") from type ("+fromType+") to type ("+type+"): " + e.getMessage(), e);
                }
            }
        } else {
            // object is null but type requested may be primitive
            if ( ConstructorUtils.isClassPrimitive(type) ) {
                // for primitives we return the default value
                if (ConstructorUtils.getPrimitiveDefaults().containsKey(type)) {
                    convert = (T) ConstructorUtils.getPrimitiveDefaults().get(type);
                }
            }
        }
        return convert;
    }

    /**
     * Use the converters to convert the value to the provided type,
     * this simply finds the converter and does the conversion,
     * will convert interface types automatically <br/>
     * WARNING: you should use {@link #convert(Object, Class)} unless you have a special need
     * for this, it is primarily for reducing code complexity
     * 
     * @param <T>
     * @param value any object to be converted
     * @param type the type to convert to
     * @return the converted object (may be null)
     * @throws UnsupportedOperationException is the conversion could not be completed
     */
    protected <T> T convertWithConverter(Object value, Class<T> type) {
        T convert = null;
        // check for a variable converter that says it will handle the conversion first
        VariableConverter variableConverter = getVariableConverter(value, type);
        if (variableConverter != null) {
            // use the variable converter
            convert = variableConverter.convert(value, type);
        } else {
            // use a converter
            Converter<T> converter = getConverterOrFail(type);
            if (InterfaceConverter.class.isAssignableFrom(converter.getClass())) {
                convert = ((InterfaceConverter<T>)converter).convertInterface(value, type);
            } else {
                // standard converter
                convert = converter.convert(value);
            }
        }
        return convert;
    }

    /**
     * Get the converter or throw exception
     * @param <T>
     * @param type type to convert to
     * @return the converter or die
     * @throws UnsupportedOperationException if the converter cannot be found
     */
    protected <T> Converter<T> getConverterOrFail(Class<T> type) {
        Converter<T> converter = getConverter(type);
        if (converter == null) {
            throw new UnsupportedOperationException("Conversion failure: No converter available to handle conversions to type ("+type+")");
        }
        return converter;
    }

    /**
     * Get the converter for the given type if there is one
     * @param <T>
     * @param type the type to convert to
     * @return the converter for this type OR null if there is not one
     */
    @SuppressWarnings("unchecked")
    protected <T> Converter<T> getConverter(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Cannot get a converter for nulls");
        }
        // first make sure we are using an actual wrapper class and not the primitive class (int.class)
        Class<?> toType = ConstructorUtils.getWrapper(type);
        Converter<T> converter = (Converter<T>) getConverters().get(toType);
        if (converter == null) {
            // none found so try not using the interface
            toType = ConstructorUtils.getClassFromInterface(toType);
            converter = (Converter<T>) getConverters().get(toType);
            if (converter == null) {
                // still no converter found so try the interfaces
                for (Class<?> iface : ConstructorUtils.getExtendAndInterfacesForClass(toType)) {
                    converter = (Converter<T>) getConverters().get(iface);
                    if (converter != null) {
                        // found a converter
                        break;
                    }
                }
            }
        }
        return converter;
    }

    /**
     * Get the variable converter for this value and type if there is one,
     * returns null if no converter is available
     * @param value the value to convert
     * @param type the type to convert to
     * @return the variable converter if there is one OR null if none exists
     */
    protected VariableConverter getVariableConverter(Object value, Class<?> type) {
        VariableConverter converter = null;
        Class<?> toType = ConstructorUtils.getWrapper(type);
        for (VariableConverter variableConverter : getVariableConverters()) {
            if (variableConverter.canConvert(value, toType)) {
                converter = variableConverter;
                break;
            }
        }
        return converter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("converters=");
        sb.append(getConverters().size());
        sb.append(":");
        for (Entry<Class<?>, Converter<?>> entry : getConverters().entrySet()) {
            sb.append("[");
            sb.append(entry.getKey().getName());
            sb.append("=>");
            sb.append(entry.getValue().getClass().getName());
            sb.append("]");
        }
        sb.append(":variable=");
        sb.append(getVariableConverters().size());
        sb.append(":");
        for (VariableConverter variableConverter : getVariableConverters()) {
            sb.append("(");
            sb.append(variableConverter.getClass().getName());
            sb.append(")");
        }        
        return "Convert::c="+ConversionUtils.timesCreated+":s="+singleton+":" + sb.toString();
    }

    // STATIC access

    protected static SoftReference<ConversionUtils> instanceStorage;
    /**
     * Get a singleton instance of this class to work with (stored statically) <br/>
     * <b>WARNING</b>: do not hold onto this object or cache it yourself, call this method again if you need it again
     * @return a singleton instance of this class
     */
    public static ConversionUtils getInstance() {
        ConversionUtils instance = (instanceStorage == null ? null : instanceStorage.get());
        if (instance == null) {
            instance = ConversionUtils.setInstance(null);
        }
        return instance;
    }
    /**
     * Set the singleton instance of the class which will be stored statically
     * @param instance the instance to use as the singleton instance
     */
    public static ConversionUtils setInstance(ConversionUtils newInstance) {
        ConversionUtils instance = newInstance;
        if (instance == null) {
            instance = new ConversionUtils();
            instance.singleton = true;
        }
        ConversionUtils.timesCreated++;
        instanceStorage = new SoftReference<ConversionUtils>(instance);
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

    protected ConstructorUtils getConstructorUtils() {
        return ConstructorUtils.getInstance();
    }

    protected FieldUtils getFieldUtils() {
        return FieldUtils.getInstance();
    }

}
