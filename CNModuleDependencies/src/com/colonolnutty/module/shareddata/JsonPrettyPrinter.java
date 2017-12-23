package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
        String prettyJson = CNStringUtils.createIndent(indentSize) + "{\r\n";
        ArrayList<String> foundProperties = new ArrayList<String>();
        for(int i = 0; i < _propertyOrder.length; i++) {
            String propertyName = _propertyOrder[i];
            if(obj.has(propertyName) && !foundProperties.contains(propertyName)) {
                foundProperties.add(propertyName);
            }
        }
        for(int i = 0; i < foundProperties.size(); i++) {
            String propertyName = foundProperties.get(i);
            if(obj.isNull(propertyName)) {
                continue;
            }
            prettyJson += CNStringUtils.createIndent(indentSize + 2) + "\"" + propertyName + "\" : " + formatAsIntended(obj.get(propertyName), indentSize + 2);
            if (i + 1 < foundProperties.size()) {
                prettyJson += ",\r\n";
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
                    prettyJson += ",\r\n";
                    appendedComma = true;
                }
                prettyJson += CNStringUtils.createIndent(indentSize + 2) + "\"" + propertyName + "\" : " + formatAsIntended(obj.get(propertyName), indentSize + 2);
                if(propertyNames.hasNext()) {
                    appendedComma = false;
                }
            }
        }
        prettyJson += "\r\n" + CNStringUtils.createIndent(indentSize) + "}";
        return prettyJson;
    }

    public String formatArray(JSONArray array, int indentSize) throws JSONException {
        if(array.length() == 0) {
            return "[ ]";
        }
        int arrayLength = array.length();
        String prettyJson = "[ ";
        for(int i = 0; i < arrayLength; i++) {
            if(array.isNull(i)) {
                continue;
            }
            Object val = array.get(i);
            if(val instanceof JSONObject) {
                prettyJson += "\r\n";
            }
            prettyJson += formatAsIntended(val, indentSize + 2);
            if((i + 1) < arrayLength) {
                if(val instanceof JSONObject) {
                    prettyJson += ",\r\n";
                }
                else if(val instanceof JSONArray) {
                    prettyJson += ",";
                }
                else {
                    prettyJson += ", ";
                }
            }
        }
        prettyJson += " ]";
        return prettyJson;
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
    public String makePretty(ArrayNode node, int indentSize) {
        if(node.isArray() && node.size() == 0) {
            return CNStringUtils.createIndent(indentSize) + "[]";
        }
        String prettyJson = CNStringUtils.createIndent(indentSize) + "[\r\n";
        for(int i = 0; i < node.size(); i++) {
            JsonNode subNode = node.get(i);
            String result = formatAsIntended(subNode, indentSize + 2);
            if(result == null) {
                continue;
            }
            prettyJson += result;
            if(i + 1 < node.size()) {
                prettyJson += ",\r\n";
            }
        }
        prettyJson += "\r\n" + CNStringUtils.createIndent(indentSize) + "]";
        return prettyJson;
    }

    public String formatAsIntended(JsonNode node, int indentSize) {
        if(node.isNull()) {
            return null;
        }
        if(CNJsonUtils.isValueType(node)) {
            String result = CNStringUtils.createIndent(indentSize);
            if (node.isDouble()) {
                return result + node.asDouble();
            }
            if (node.isInt()) {
                return result + node.asInt();
            }
            if (node.isBoolean()) {
                return result + node.asBoolean();
            }
            if (node.isTextual()) {
                return result + "\"" + CNStringUtils.escapeString(node.asText()) + "\"";
            }
        }

        if(node.isArray()) {
            return makePretty((ArrayNode)node, indentSize);
        }
        if(node.isObject()) {
            return formatAsObject(node, indentSize);
        }
        return null;
    }

    private String formatAsObject(JsonNode node, int indentSize) throws JSONException {
        boolean hasValue = false;
        String prettyJson = CNStringUtils.createIndent(indentSize) + "{\r\n";
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
            prettyJson += CNStringUtils.createIndent(indentSize + 2) + "\"" + fieldName + "\" : " + result.trim();
            if(fieldNames.hasNext()) {
                prettyJson += ",\r\n";
            }
        }
        if(!hasValue) {
            return "{}";
        }
        prettyJson += "\r\n" + CNStringUtils.createIndent(indentSize) + "}";
        return prettyJson;
    }
}
