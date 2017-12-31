package com.colonolnutty.module.shareddata.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * User: Jack's Computer
 * Date: 10/09/2017
 * Time: 11:12 AM
 */
public abstract class CNCollectionUtils {
    public static ArrayList<String> toStringArrayList(String[] strs) {
        ArrayList<String> arr = new ArrayList<String>();
        for(String str : strs) {
            arr.add(str);
        }
        return arr;
    }

    public static String[] toStringArray(ArrayList<String> strs) {
        String[] arr = new String[strs.size()];
        for(int i = 0; i < strs.size(); i++) {
            arr[i] = strs.get(i);
        }
        return arr;
    }

    public static boolean isEmpty(ArrayNode node) {
        return node == null || node.size() == 0;
    }

    public static String[] combine(String[] one, String[] two) {
        String[] ingredientFileExtensions = new String[one.length + two.length];
        for(int i = 0; i < one.length; i++) {
            ingredientFileExtensions[i] = one[i];
        }
        for(int i = 0; i < two.length; i++) {
            ingredientFileExtensions[i + one.length] = two[i];
        }
        return ingredientFileExtensions;
    }

    public static boolean contains(ArrayList<String> arr, String val) {
        boolean found = false;
        for(int i = 0; i < arr.size(); i++) {
            if(arr.get(i).equals(val)) {
                found = true;
                i = arr.size();
            }
        }
        return found;
    }

    public static ArrayList<String> toArrayList(Enumeration<String> keys) {
        ArrayList<String> arr = new ArrayList<String>();
        while(keys.hasMoreElements()) {
            arr.add(keys.nextElement());
        }
        return arr;
    }
}
