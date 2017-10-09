package com.colonolnutty.module.shareddata;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;

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
}
