package com.colonolnutty.module.shareddata.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: Jack's Computer
 * Date: 09/17/2017
 * Time: 11:37 AM
 */
public abstract class CNJsonUtils {

    public static boolean isValueType(JsonNode node) {
        return node.isDouble()
                || node.isInt()
                || node.isBoolean()
                || node.isTextual();
    }

    public static boolean isValueType(Object obj) {
        return obj instanceof Double
                || obj instanceof Integer
                || obj instanceof Boolean
                || obj instanceof String;
    }

    public static boolean hasTestNode(JsonNode node) {
        if(!node.isArray()) {
            return node.has("op")
                    && node.get("op").asText().equals("test");
        }
        boolean foundTestNode = false;
        for(int i = 0; i < node.size(); i++) {
            if(hasTestNode(node.get(i))) {
                foundTestNode = true;
                i = node.size();
            }
        }
        return foundTestNode;
    }

    public static boolean hasPathName(JsonNode node, String pathName) {
        return node.has("path")
                && node.get("path").asText().equals("/" + pathName);
    }

    public static String getNodePath(JsonNode node) {
        String pathName = null;
        if(node == null) {
            return null;
        }
        if(node.isArray() && node.size() > 0) {
            for(JsonNode subNode : node) {
                pathName = getNodePath(subNode);
                if(pathName != null) {
                    break;
                }
            }
        }
        else if(!node.isArray()
                && node.has("path")) {
            pathName = node.get("path").asText();
        }
        return pathName;
    }

    public static ArrayList<String> getPropertyNames(JSONObject obj) {
        ArrayList<String> properties = new ArrayList<String>();
        Iterator<String> keys = obj.keys();
        while(keys.hasNext()) {
            properties.add(keys.next());
        }
        return properties;
    }

    public static ArrayList<String> getPropertyNames(JsonNode node) {
        ArrayList<String> properties = new ArrayList<String>();
        Iterator<String> keys = node.fieldNames();
        while(keys.hasNext()) {
            String next = keys.next();
            if(next == null) {
                continue;
            }
            properties.add(next);
        }
        return properties;
    }
}
