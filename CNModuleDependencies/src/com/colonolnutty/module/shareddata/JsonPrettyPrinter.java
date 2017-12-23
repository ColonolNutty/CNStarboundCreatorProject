package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
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
    private String[] _propertyOrder;

    public JsonPrettyPrinter(String[] propertyOrder) {
        _propertyOrder = propertyOrder;
    }

    public void setPropertyOrder(String[] propertyOrder) {
        _propertyOrder = propertyOrder;
    }

    @Override
    public String makePretty(JSONObject obj, int indentSize) throws JSONException {
        ArrayList<String> foundProperties = new ArrayList<String>();
        for(int i = 0; i < _propertyOrder.length; i++) {
            String propertyName = _propertyOrder[i];
            if(obj.has(propertyName) && !foundProperties.contains(propertyName)) {
                foundProperties.add(propertyName);
            }
        }
        StringBuilder builder = new StringBuilder(CNStringUtils.createIndent(indentSize) + "{\r\n");
        for(int i = 0; i < foundProperties.size(); i++) {
            String propertyName = foundProperties.get(i);
            if(obj.isNull(propertyName)) {
                continue;
            }
            builder.append(CNStringUtils.createIndent(indentSize + 2) + "\"" + propertyName + "\" : " + formatAsIntended(obj.get(propertyName), indentSize + 2));
            if (i + 1 < foundProperties.size()) {
                builder.append(",\r\n");
            }
        }
        boolean appendedComma = foundProperties.isEmpty();
        Iterator<String> propertyNames = obj.keys();
        while(propertyNames.hasNext()) {
            String propertyName = propertyNames.next();
            if(!foundProperties.contains(propertyName)) {
                if(obj.isNull(propertyName)) {
                    continue;
                }
                if(!appendedComma) {
                    builder.append(",\r\n");
                    appendedComma = true;
                }
                builder.append(CNStringUtils.createIndent(indentSize + 2) + "\"" + propertyName + "\" : " + formatAsIntended(obj.get(propertyName), indentSize + 2));
                if(propertyNames.hasNext()) {
                    appendedComma = false;
                }
            }
        }
        builder.append("\r\n" + CNStringUtils.createIndent(indentSize) + "}");
        return builder.toString();
    }

    public String formatArray(JSONArray array, int indentSize) throws JSONException {
        if(array.length() == 0) {
            return "[ ]";
        }
        int arrayLength = array.length();
        StringBuilder builder = new StringBuilder("[ ");
        for(int i = 0; i < arrayLength; i++) {
            if(array.isNull(i)) {
                continue;
            }
            Object val = array.get(i);
            if(val instanceof JSONObject) {
                builder.append("\r\n");
            }
            builder.append(formatAsIntended(val, indentSize + 2));
            if((i + 1) < arrayLength) {
                if(val instanceof JSONObject) {
                    builder.append(",\r\n");
                }
                else if(val instanceof JSONArray) {
                    builder.append(",");
                }
                else {
                    builder.append(", ");
                }
            }
        }
        builder.append(" ]");
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
            return makePretty((JSONObject) val, indentSize);
        }
        if(val instanceof JSONArray) {
            return formatArray((JSONArray) val, indentSize);
        }
        throw new JSONException("Unknown object type: " + val);
    }

    @Override
    public String makePretty(JsonNode node, int indentSize) {
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
            String result = formatAsIntended(subNode, indentSize + 2);
            if(result == null) {
                continue;
            }
            if(!containsValueTypes) {
                builder.append("\r\n");
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
            else if(CNJsonUtils.isValueType(subNode)) {
                builder.append(" ");
            }
        }
        if(!containsValueTypes) {
            builder.append("\r\n" + CNStringUtils.createIndent(indentSize));
        }
        builder.append("]");
        return builder.toString();
    }

    public String formatObject(JsonNode node, int indentSize) throws JSONException {
        boolean hasValue = false;
        StringBuilder builder = new StringBuilder(CNStringUtils.createIndent(indentSize) + "{\r\n");
        Iterator<String> fieldNames = node.fieldNames();
        while(fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if(node.isNull()) {
                continue;
            }
            String result = formatAsIntended(node.get(fieldName), indentSize + 2);
            if(result == null) {
                continue;
            }
            hasValue = true;
            builder.append(CNStringUtils.createIndent(indentSize + 2) + "\"" + fieldName + "\" : " + result.trim());
            if(fieldNames.hasNext()) {
                builder.append(",\r\n");
            }
        }
        if(!hasValue) {
            return "{ }";
        }
        builder.append("\r\n" + CNStringUtils.createIndent(indentSize) + "}");
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
            return makePretty(node, indentSize);
        }
        if(node.isObject()) {
            return formatObject(node, indentSize);
        }
        return null;
    }
}
