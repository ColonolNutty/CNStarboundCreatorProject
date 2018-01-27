package com.colonolnutty.module.shareddata.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * User: Jack's Computer
 * Date: 10/09/2017
 * Time: 11:12 AM
 */
public abstract class CNCollectionUtils {
    public static boolean isEmpty(ArrayNode node) {
        return node == null || node.size() == 0;
    }

    public static <T> boolean isEmpty(T[] arr) {
        return arr == null || arr.length == 0;
    }

    public static <T> boolean hasCount(ArrayList<T> arr, int count) {
        return arr != null && arr.size() == count;
    }

    public static <T> boolean hasMultiple(ArrayList<T> arr) {
        return arr != null && arr.size() > 1;
    }

    public static <T> boolean hasMultiple(T[] arr) {
        return arr != null && arr.length > 1;
    }

    public static <T> T getSingleOrNull(T[] values) {
        if(values.length == 0 || hasMultiple(values)) {
            return null;
        }
        return values[0];
    }

    public static <T> T getSingleOrNull(ArrayList<T> values) {
        if(values.isEmpty() || hasMultiple(values)) {
           return null;
        }
        return values.get(0);
    }

    public static <T> ArrayList<T> combine(ArrayList<T>... values) {
        ArrayList<T> result = new ArrayList<T>();
        for(ArrayList<T> value : values) {
            for(T val : value) {
                result.add(val);
            }
        }
        return result;
    }

    public static <T> T[] combine(Class<T> classOfType, T[]... values) {
        int totalLength = 0;
        for(T[] value : values) {
            totalLength += value.length;
        }
        T[] result = (T[]) Array.newInstance(classOfType, totalLength);
        int currentIdx = 0;
        for(T[] value : values) {
            for (T val : value) {
                result[currentIdx] = val;
                currentIdx++;
            }
        }
        return result;
    }

    public static <T> boolean contains(ArrayList<T> arr, T val) {
        boolean found = false;
        for(T value : arr) {
            if(value.equals(val)) {
                found = true;
                break;
            }
        }
        return found;
    }

    public static <T> ArrayList<T> toArrayList(Enumeration<T> keys) {
        ArrayList<T> arr = new ArrayList<T>();
        while(keys.hasMoreElements()) {
            arr.add(keys.nextElement());
        }
        return arr;
    }

    public static <T> T[] toArray(Class<T> classOfType, Enumeration<T> keys) {
        return toArray(classOfType, toArrayList(keys));
    }

    public static <T> ArrayList<T> toArrayList(T[] values) {
        ArrayList<T> arr = new ArrayList<T>();
        for(int i = 0; i < values.length; i++) {
            arr.add(values[i]);
        }
        return arr;
    }

    public static <T> T[] toArray(Class<T> classOfType, ArrayList<T> values) {
        T[] arr =  (T[])Array.newInstance(classOfType, values.size());
        for(int i = 0; i < values.size(); i++) {
            arr[i] = values.get(i);
        }
        return arr;
    }
}
