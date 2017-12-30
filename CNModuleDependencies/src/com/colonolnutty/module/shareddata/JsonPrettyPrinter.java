package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: Jack's Computer
 * Date: 09/19/2017
 * Time: 12:13 PM
 */
public class JsonPrettyPrinter implements IPrettyPrinter {
    private String[] _propertiesInOrder;
    public static final String NEW_LINE = "\r\n";
    public static final int INDENT_SIZE = 2;

    public JsonPrettyPrinter(String[] propertyOrder) {
        setPropertyOrder(propertyOrder);
    }

    public void setPropertyOrder(String[] propertyOrder) {
        _propertiesInOrder = propertyOrder;
    }

    @Override
    public String formatObject(JSONObject obj, int indentSize) throws JSONException {
        ArrayList<String> objProperties = CNJsonUtils.getPropertyNames(obj);
        ArrayList<String> sortedProperties = sortProperties(_propertiesInOrder, objProperties);
        StringBuilder builder = new StringBuilder();
        builder.append(CNStringUtils.createIndent(indentSize) + "{" + NEW_LINE);
        int currentLevelIndent = indentSize + INDENT_SIZE;
        for(int i = 0; i < sortedProperties.size(); i++) {
            String propertyName = sortedProperties.get(i);
            if(obj.isNull(propertyName)) {
                continue;
            }
            Object propertyObj = obj.get(propertyName);
            builder.append(CNStringUtils.createIndent(currentLevelIndent) + "\"" + propertyName + "\" : " + formatAsIntended(propertyObj, currentLevelIndent).trim());
            if ((i + 1) < sortedProperties.size()) {
                builder.append("," + NEW_LINE);
            }
        }
        builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize) + "}");
        return builder.toString();
    }

    public String formatArray(JSONArray array, int indentSize) throws JSONException {
        int arrayLength = array.length();
        if(arrayLength == 0) {
            return "[ ]";
        }
        Object one = array.get(0);
        if(arrayLength == 1 && one instanceof JSONArray && ((JSONArray)one).length() == 0) {
            return "[[ ]]";
        }
        boolean containsValueTypes = CNJsonUtils.isValueType(one);
        StringBuilder builder = new StringBuilder(CNStringUtils.createIndent(indentSize) + "[");
        if(containsValueTypes) {
            builder.append(" ");
        }
        for(int i = 0; i < arrayLength; i++) {
            if(array.isNull(i)) {
                continue;
            }
            Object val = array.get(i);
            String result = formatAsIntended(val, indentSize + INDENT_SIZE);
            if(result == null) {
                continue;
            }
            if(!containsValueTypes) {
                builder.append(NEW_LINE);
            }
            builder.append(result);

            if((i + 1) < arrayLength) {
                if(!containsValueTypes) {
                    builder.append(",");
                }
                else {
                    builder.append(", ");
                }
            }
            else if(containsValueTypes) {
                builder.append(" ");
            }
        }
        if(!containsValueTypes) {
            builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize));
        }
        builder.append("]");
        return builder.toString();
    }

    public String formatAsIntended(Object val, int indentSize) throws JSONException {
        if(val instanceof Double || val instanceof Integer || val instanceof Boolean) {
            return val.toString();
        }
        if(val instanceof String) {
            return "\"" + CNStringUtils.escapeString(val.toString()) + "\"";
        }

        if(val instanceof JSONObject) {
            return formatObject((JSONObject) val, indentSize);
        }
        if(val instanceof JSONArray) {
            return formatArray((JSONArray) val, indentSize);
        }
        throw new JSONException("Unknown object type: " + val);
    }

    public ArrayList<String> sortProperties(String[] propertyNamesInOrder, ArrayList<String> properties) {
        ArrayList<String> sortedProperties = new ArrayList<String>();
        if(propertyNamesInOrder != null) {
            for (String propertyName : propertyNamesInOrder) {
                if (properties.contains(propertyName)
                        && !sortedProperties.contains(propertyName)) {
                    sortedProperties.add(propertyName);
                }
            }
        }
        for(String propertyName : properties) {
            if(!sortedProperties.contains(propertyName)) {
                sortedProperties.add(propertyName);
            }
        }
        return sortedProperties;
    }


    @Override
    public String formatArray(JsonNode node, int indentSize) {
        if(!node.isArray()) {
            return formatObject(node, indentSize);
        }
        int nodeSize = node.size();
        if(nodeSize == 0) {
            return "[ ]";
        }
        JsonNode nodeOne = node.get(0);
        if(nodeOne.isArray() && nodeOne.size() == 0 && nodeSize == 1) {
            return "[[ ]]";
        }
        boolean containsValueTypes = CNJsonUtils.isValueType(nodeOne);
        StringBuilder builder = new StringBuilder(CNStringUtils.createIndent(indentSize) + "[");
        if(containsValueTypes) {
            builder.append(" ");
        }
        for(int i = 0; i < nodeSize; i++) {
            JsonNode subNode = node.get(i);
            String result = formatAsIntended(subNode, indentSize + INDENT_SIZE);
            if(result == null) {
                continue;
            }
            if(!containsValueTypes) {
                builder.append(NEW_LINE);
            }
            builder.append(result);

            if((i + 1) < nodeSize) {
                if(!containsValueTypes) {
                    builder.append(",");
                }
                else {
                    builder.append(", ");
                }
            }
            else if(containsValueTypes) {
                builder.append(" ");
            }
        }
        if(!containsValueTypes) {
            builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize));
        }
        builder.append("]");
        return builder.toString();
    }

    public String formatObject(JsonNode node, int indentSize) throws JSONException {
        boolean hasValue = false;
        StringBuilder builder = new StringBuilder(CNStringUtils.createIndent(indentSize) + "{" + NEW_LINE);
        Iterator<String> fieldNames = node.fieldNames();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if(node.isNull()) {
                continue;
            }
            String result = formatAsIntended(node.get(fieldName), indentSize + INDENT_SIZE);
            if(result == null) {
                continue;
            }
            hasValue = true;
            builder.append(CNStringUtils.createIndent(indentSize + INDENT_SIZE) + "\"" + fieldName + "\" : " + result.trim());
            if(fieldNames.hasNext()) {
                builder.append("," + NEW_LINE);
            }
        }
        if(!hasValue) {
            return "{ }";
        }
        builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize) + "}");
        return builder.toString();
    }

    public String formatAsIntended(JsonNode node, int indentSize) {
        if(node.isNull()) {
            return null;
        }
        if(CNJsonUtils.isValueType(node)) {
            if (node.isDouble() || node.isInt() || node.isBoolean()) {
                return node.asText();
            }
            if (node.isTextual()) {
                return "\"" + CNStringUtils.escapeString(node.asText()) + "\"";
            }
        }

        if(node.isArray()) {
            return formatArray(node, indentSize);
        }
        if(node.isObject()) {
            return formatObject(node, indentSize);
        }
        return null;
    }
}
