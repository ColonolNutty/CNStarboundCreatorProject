package com.colonolnutty.module.shareddata.prettyprinters;

import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 1:31 PM
 */
public class UnspecificPrettyPrinter extends BasePrettyPrinter {
    @Override
    public String makePretty(Object obj, int indentSize) throws JSONException {
        if(!canPrettyPrint(obj)) {
            return null;
        }
        if(obj instanceof JSONObject) {
            Hashtable<String, Object> properties = toTable((JSONObject)obj);
            return formatObject(properties, indentSize, false);
        }
        if(obj instanceof JsonNode) {
            JsonNode node = (JsonNode) obj;
            if(node.isObject()) {
                Hashtable<String, Object> properties = toTable(node);
                return formatObject(properties, indentSize, false);
            }
            if(node.isArray()) {
                ArrayList<Object> properties = toList(node);
                return formatArray(properties, indentSize, false);
            }
            if(node.isTextual()) {
                return "\"" + CNStringUtils.escapeString(node.asText()) + "\"";
            }
            else if(CNJsonUtils.isValueType(node)) {
                return node.asText();
            }
        }
        return null;
    }

    public String formatObject(Hashtable<String, Object> objTable, int indentSize, boolean shouldIndent) throws JSONException {
        ArrayList<String> objProperties = CNCollectionUtils.toArrayList(objTable.keys());
        if(objProperties.size() == 0) {
            return "{ }";
        }
        StringBuilder builder = new StringBuilder();
        if(shouldIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        if(objProperties.size() == 1) {
            String firstProperty = objProperties.get(0);
            Object firstObj = objTable.get(firstProperty);
            String result = formatAsIntended(firstObj, indentSize, true);
            if (result == null) {
                builder.append("{ }");
                return builder.toString();
            }
            if(result.contains(NEW_LINE)) {
                builder.append("{" + NEW_LINE + CNStringUtils.createIndent(indentSize + INDENT_SIZE));
                result = result.replace(NEW_LINE,  NEW_LINE + CNStringUtils.createIndent(indentSize + INDENT_SIZE));
            }
            else {
                builder.append("{ ");
            }
            builder.append("\"" + firstProperty + "\" : " + result);
            if(result.contains(NEW_LINE)) {
                builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize) + "}");
            }
            else {
                builder.append(" }");
            }
            return builder.toString();
        }
        builder.append("{" + NEW_LINE);
        ArrayList<String> sortedProperties = sortProperties(_propertiesInOrder, objProperties);
        int currentLevelIndent = indentSize + INDENT_SIZE;
        for(int i = 0; i < sortedProperties.size(); i++) {
            String propertyName = sortedProperties.get(i);
            Object propertyObj = objTable.get(propertyName);
            String result = formatAsIntended(propertyObj, currentLevelIndent, false);
            if(result == null) {
                continue;
            }
            builder.append(CNStringUtils.createIndent(currentLevelIndent));
            builder.append("\"" + propertyName + "\" : " + result);
            if ((i + 1) < sortedProperties.size()) {
                builder.append("," + NEW_LINE);
            }
        }
        builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize) + "}");
        return builder.toString();
    }

    public String formatArray(ArrayList<Object> arr, int indentSize, boolean shouldIndent) throws JSONException {
        int arrSize = arr.size();
        if(arrSize == 0) {
            return "[ ]";
        }
        StringBuilder builder = new StringBuilder();
        if(shouldIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        Object objOne = arr.get(0);
        if(arrSize == 1) {
            if(objOne instanceof ArrayList
                    && ((ArrayList<Object>) objOne).size() == 0) {
                return "[[ ]]";
            }
            if(objOne instanceof Hashtable) {
                builder.append("[" + formatObject(((Hashtable<String, Object>) objOne), 0, false) + "]");
                return builder.toString();
            }
        }
        boolean containsValueTypes = CNJsonUtils.isValueType(objOne);
        builder.append("[");
        if(containsValueTypes) {
            builder.append(" ");
        }
        for(int i = 0; i < arrSize; i++) {
            Object subObj = arr.get(i);
            String result = formatAsIntended(subObj, indentSize + INDENT_SIZE, !containsValueTypes);
            if(result == null) {
                continue;
            }
            if(!containsValueTypes) {
                builder.append(NEW_LINE);
            }
            builder.append(result);

            if((i + 1) < arrSize) {
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

    public String formatAsIntended(Object obj, int indentSize, boolean shouldIndent) throws JSONException {
        if(obj == null || obj.toString().equals("null")) {
            return null;
        }
        if(obj instanceof ArrayList) {
            return formatArray((ArrayList<Object>) obj, indentSize, shouldIndent);
        }
        if(obj instanceof Hashtable) {
            return formatObject((Hashtable<String, Object>) obj, indentSize, shouldIndent);
        }
        if(CNJsonUtils.isValueType(obj)) {
            if(obj instanceof String) {
                return "\"" + obj.toString() + "\"";
            }
            return obj.toString();
        }
        return null;
    }

    public ArrayList<Object> toList(JSONArray arr) {
        ArrayList<Object> objs = new ArrayList<Object>();
        for(int i = 0; i < arr.length(); i++) {
            Object result = handle(arr.get(i));
            if(result != null) {
                objs.add(result);
            }
        }
        return objs;
    }

    public Hashtable<String, Object> toTable(JSONObject obj) {
        Hashtable<String, Object> table = new Hashtable<String, Object>();
        Iterator<String> fieldNames = obj.keys();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            Object subObj = obj.get(fieldName);
            Object result = handle(subObj);
            if(result != null) {
                table.put(fieldName, result);
            }
        }
        return table;
    }

    private Object handle(Object obj) {
        if(obj instanceof JSONArray) {
            return toList((JSONArray) obj);
        }
        if(obj instanceof JSONObject) {
            return toTable((JSONObject) obj);
        }
        return obj;
    }


    public ArrayList<Object> toList(JsonNode node) {
        ArrayList<Object> objects = new ArrayList<Object>();
        for(JsonNode subNode : node) {
            Object result = handle(subNode);
            if(result != null) {
                objects.add(result);
            }
        }
        return objects;
    }

    public Hashtable<String, Object> toTable(JsonNode node) {
        Hashtable<String, Object> objFields = new Hashtable<String, Object>();
        Iterator<String> fieldNames = node.fieldNames();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode subNode = node.get(fieldName);
            Object result = handle(subNode);
            if(result != null) {
                objFields.put(fieldName, result);
            }
        }
        return objFields;
    }

    private Object handle(JsonNode node) {
        if(node.isBoolean()) {
            return node.asBoolean();
        }
        if(node.isDouble()) {
            return node.asDouble();
        }
        if (node.isInt()) {
            return node.asInt();
        }
        if(node.isTextual()) {
            return node.asText();
        }
        if(node.isArray()) {
            return toList(node);
        }
        if(node.isObject()) {
            return toTable(node);
        }
        return null;
    }

    @Override
    public boolean canPrettyPrint(Object obj) {
        return obj instanceof JsonNode || obj instanceof JSONObject;
    }
}
