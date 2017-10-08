package com.colonolnutty.module.shareddata;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/17/2017
 * Time: 11:37 AM
 */
public abstract class CNUtils {
    public static boolean contains(String name, String[] names) {
        if(name == null || names == null) {
            return false;
        }
        boolean contains = false;
        for(int i = 0; i < names.length; i++) {
            String value = names[i];
            if(name.equals(value)) {
                contains = true;
                i = names.length;
            }
        }
        return contains;
    }

    public static boolean fileEndsWith(String filePath, ArrayList<String> values) {
        boolean hasExtension = false;
        for(int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if(filePath.endsWith(value)) {
                hasExtension = true;
                i = values.size();
            }
        }
        return hasExtension;
    }

    public static boolean fileStartsWith(String filePath, String[] values) {
        boolean hasExtension = false;
        for(int i = 0; i < values.length; i++) {
            String value = values[i];
            File valueFilePath = new File(value);
            if(filePath.startsWith(valueFilePath.getAbsolutePath())) {
                hasExtension = true;
                i = values.length;
            }
        }
        return hasExtension;
    }

    public static boolean isValueType(JsonNode node) {
        return node.isDouble()
                || node.isInt()
                || node.isBoolean()
                || node.isTextual();
    }

    public static String toCommaSeparated(String[] values) {
        if(values.length == 0) {
            return "";
        }
        if(values.length == 1) {
            return values[0];
        }
        String commaSeparated = "";
        for(int i = 0; i < values.length; i++) {
            String value = values[i];
            if(value == null || value.isEmpty()) {
                continue;
            }
            commaSeparated += values[i].trim();
            if(i + 1 < values.length) {
                commaSeparated += ", ";
            }
        }
        return commaSeparated;
    }

    public static String[] fromCommaSeparated(String value) {
        if(value == null || value.isEmpty()) {
            return null;
        }
        ArrayList<String> values = new ArrayList<String>();
        String[] split = value.split(",\\s*");
        for (int i = 0; i < split.length; i++) {
            String val = split[i].trim();
            if(!val.isEmpty()) {
                values.add(val);
            }
        }
        String[] vals = new String[values.size()];
        for(int i = 0; i < values.size(); i++) {
            vals[i] = values.get(i);
        }
        return split;
    }

    public static String createIndent(int numberOfSpaces) {
        String indent = "";
        for(int i = 0; i < numberOfSpaces; i++) {
            indent += " ";
        }
        return indent;
    }

    public static String escapeString(String str) {
        return str.replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}