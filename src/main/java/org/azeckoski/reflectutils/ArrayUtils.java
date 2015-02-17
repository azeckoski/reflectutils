/**
 * $Id: ArrayUtils.java 2 2008-10-01 10:04:26Z azeckoski $
 * $URL: http://reflectutils.googlecode.com/svn/trunk/src/main/java/org/azeckoski/reflectutils/ArrayUtils.java $
 * ArrayUtils.java - genericdao - May 5, 2008 2:16:35 PM - azeckoski
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

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Utils for working with collections and arrays (these are basically convenience methods)
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public class ArrayUtils {

    /**
     * Resize an array and return the new one,
     * this will truncate an array or expand it depending on the size given
     * 
     * @param <T>
     * @param array any array
     * @param newSize the new size for the array
     * @return the resized array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] resize(T[] array, int newSize) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, newSize);
        int toCopy = Math.min(array.length, newArray.length);
        System.arraycopy( array, 0, newArray, 0, toCopy );
        return newArray;
    }

    /**
     * @param <T>
     * @param type any class type (including primitives)
     * @param newSize the initial size for the new array
     * @return the new array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] create(Class<T> type, int newSize) {
        T[] newArray = (T[]) Array.newInstance(type, newSize);
        return newArray;
    }

    /**
     * @param <T>
     * @param array any array
     * @return the size of the array
     */
    public static <T> int size(T[] array) {
        return Array.getLength(array);
    }

    /**
     * @param <T>
     * @param array any array
     * @return the component type of the items in the array
     */
    public static <T> Class<?> type(T[] array) {
        return array.getClass().getComponentType();
    }

    /**
     * Make a copy of an array
     * 
     * @param <T>
     * @param array an array of objects
     * @return a copy of the array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] array) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length);
        System.arraycopy( array, 0, newArray, 0, array.length );
        return newArray;
    }

    /**
     * Make a template copy of an array (empty copy which is the same size and type)
     * @param <T>
     * @param array any array
     * @return the template copy (empty but same size as input)
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] template(T[] array) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length);
        return newArray;
    }

    /**
     * Checks to see if an array contains a value,
     * will return false if a null value is supplied
     * 
     * @param <T>
     * @param array any array of objects
     * @param value the value to check for
     * @return true if the value is found, false otherwise
     */
    public static <T> boolean contains(T[] array, T value) {
        boolean foundValue = false;
        if (value != null) {
            for (int i = 0; i < array.length; i++) {
                if (value.equals(array[i])) {
                    foundValue = true;
                    break;
                }
            }
        }
        return foundValue;
    }

    /**
     * Checks to see if an array contains a value
     * 
     * @param array
     * @param value
     * @return true if the value is found, false otherwise
     */
    public static boolean contains(int[] array, int value) {
        boolean foundValue = false;
        for (int i = 0; i < array.length; i++) {
            if (value == array[i]) {
                foundValue = true;
                break;
            }
        }
        return foundValue;
    }

    /**
     * Append an item to the end of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to append to the end of the new array
     * @return a new array with value in the last spot
     */
    public static String[] appendArray(String[] array, String value) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy( array, 0, newArray, 0, array.length );
        newArray[newArray.length-1] = value;
        return newArray;
    }

    /**
     * Append an item to the end of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to append to the end of the new array
     * @return a new array with value in the last spot
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] appendArray(T[] array, T value) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy( array, 0, newArray, 0, array.length );
        newArray[newArray.length-1] = value;
        return newArray;
    }

    /**
     * Append an array to another array
     * 
     * @param array1 an array of items
     * @param array2 an array of items
     * @return a new array with array1 first and array2 appended on the end
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] appendArrays(T[] array1, T[] array2) {
        Class<?> type = array1.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array1.length + array2.length);
        System.arraycopy( array1, 0, newArray, 0, array1.length );
        System.arraycopy( array2, 0, newArray, array1.length, array2.length );
        return newArray;
    }

    /**
     * Append an item to the end of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to append to the end of the new array
     * @return a new array with value in the last spot
     */
    public static int[] appendArray(int[] array, int value) {
        int[] newArray = new int[array.length + 1];
        System.arraycopy( array, 0, newArray, 0, array.length );
        newArray[newArray.length-1] = value;
        return newArray;
    }

    /**
     * Prepend an item to the front of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to prepend to the front of the new array
     * @return a new array with value in the first spot
     */
    public static String[] prependArray(String[] array, String value) {
        String[] newArray = new String[array.length + 1];
        System.arraycopy( array, 0, newArray, 1, array.length );
        newArray[0] = value;
        return newArray;
    }

    /**
     * Prepend an item to the front of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to prepend to the front of the new array
     * @return a new array with value in the first spot
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] prependArray(T[] array, T value) {
        Class<?> type = array.getClass().getComponentType();
        T[] newArray = (T[]) Array.newInstance(type, array.length + 1);
        System.arraycopy( array, 0, newArray, 1, array.length );
        newArray[0] = value;
        return newArray;
    }

    /**
     * Prepend an item to the front of an array and return the new array
     * 
     * @param array an array of items
     * @param value the item to prepend to the front of the new array
     * @return a new array with value in the first spot
     */
    public static int[] prependArray(int[] array, int value) {
        int[] newArray = new int[array.length + 1];
        System.arraycopy( array, 0, newArray, 1, array.length );
        newArray[0] = value;
        return newArray;
    }

    /**
     * Take an array of anything and turn it into a string
     * 
     * @param array any array
     * @return a string representing that array
     */
    public static String arrayToString(Object[] array) {
        StringBuilder result = new StringBuilder();
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    result.append(",");
                }
                if (array[i] != null) {
                    result.append(array[i].toString());
                }
            }
        }
        return result.toString();
    }

    /**
     * Take a list of number objects and return an int[] array
     * @param list any list of {@link Number}
     * @return an array of int
     */
    public static int[] listToIntArray(List<Number> list) {
        int[] newArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            newArray[i] = list.get(i).intValue();
        }
        return newArray;
    }


    /**
     * Remove all duplicate objects from a list
     * 
     * @param <T>
     * @param list any list
     * @return the original list with the duplicate objects removed
     */
    public static <T> List<T> removeDuplicates(List<T> list) {
        Set<T> s = new HashSet<T>();
        for (Iterator<T> iter = list.iterator(); iter.hasNext();) {
            T element = (T) iter.next();
            if (! s.add(element)) {
                iter.remove();
            }
        }
        return list;
    }

    /**
     * Create a set from any array
     * 
     * @param array any array (including null or empty)
     * @return the set with the values from the array
     */
    public static <T> Set<T> makeSetFromArray(T[] array) {
        Set<T> set = new HashSet<T>();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    set.add(array[i]);
                }
            }
        }
        return set;
    }

}
