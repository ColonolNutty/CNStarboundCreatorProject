package com.colonolnutty.module.shareddata;

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
public class JsonPrettyPrinter {
    private String[] _propertyOrder;
    private CNLog _log;

    public JsonPrettyPrinter(CNLog log, String[] propertyOrder) {
        _log = log;
        _propertyOrder = propertyOrder;
    }

    public String makePretty(JSONObject obj, int indentSize) {
        String prettyJson = CNUtils.createIndent(indentSize) + "{\r\n";
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
            prettyJson += CNUtils.createIndent(indentSize + 2) + "\"" + propertyName + "\" : " + formatAsIntended(obj, propertyName, indentSize + 2);
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
                prettyJson += CNUtils.createIndent(indentSize + 2) + "\"" + propertyName + "\" : " + formatAsIntended(obj, propertyName, indentSize + 2);
                if(propertyNames.hasNext()) {
                    appendedComma = false;
                }
            }
        }
        prettyJson += "\r\n" + CNUtils.createIndent(indentSize) + "}";
        return prettyJson;
    }

    private String formatArray(JSONArray array, int indentSize) {
        if(array.length() == 0) {
            return "[ ]";
        }
        String prettyJson = "[ ";
        for(int i = 0; i < array.length(); i++) {
            if(array.isNull(i)) {
                continue;
            }
            prettyJson += formatAsIntended(array, i, indentSize + 2);
        }
        prettyJson += " ]";
        return prettyJson;
    }

    private String formatAsIntended(JSONObject obj, String key, int indentSize) {
        try {
            try {
                JSONObject val = obj.getJSONObject(key);
                return makePretty(val, indentSize);
            } catch (JSONException e) {
                try {
                    JSONArray val = obj.getJSONArray(key);
                    return formatArray(val, indentSize);
                } catch (JSONException e1) {
                    try {
                        String result;
                        if (isDouble(obj, key)) {
                            Double val = obj.getDouble(key);
                            result = val.toString();
                        } else {
                            int val = obj.getInt(key);
                            result = val + "";
                        }
                        return result;
                    } catch (JSONException e2) {
                        try {
                            Boolean val = obj.getBoolean(key);
                            return val.toString();
                        } catch (JSONException e4) {
                            try {
                                return "\"" + CNUtils.escapeString(obj.getString(key)) + "\"";
                            }
                            catch(JSONException e5) {
                                _log.error("Unknown object type: " + key, e5);
                            }
                        }
                    }
                }
            }
            return null;
        }
        catch(JSONException e) {
            _log.error("When parsing: " + key, e);
        }
        return "";
    }

    private String formatAsIntended(JSONArray obj, int key, int indentSize) {
        try {
            boolean isNotLast = key + 1 < obj.length();
            try {
                JSONObject val = obj.getJSONObject(key);
                String result = "\r\n" + makePretty(val, indentSize);
                if(isNotLast) {
                    result += ",\r\n";
                }
                return result;
            }
            catch(JSONException e) {
                try {
                    JSONArray val = obj.getJSONArray(key);
                    String result = formatArray(val, indentSize);
                    if(isNotLast) {
                        result += ",";
                    }
                    return result;
                }
                catch(JSONException e1) {
                    try {
                        String result;
                        if(isDouble(obj, key)) {
                            Double val = obj.getDouble(key);
                            result = val.toString();
                        }
                        else {
                            int val = obj.getInt(key);
                            result = val + "";
                        }
                        if(isNotLast) {
                            result += ", ";
                        }
                        return result;
                    }
                    catch(JSONException e2) {
                        try {
                            Boolean val = obj.getBoolean(key);
                            String result = val.toString();
                            if(isNotLast) {
                                result += ", ";
                            }
                            return result;
                        }
                        catch(JSONException e4) {}
                    }
                }
            }
            String result = "\"" + CNUtils.escapeString(obj.getString(key)) + "\"";
            if(isNotLast) {
                result += ", ";
            }
            return result;
        }
        catch(JSONException e) {
            _log.error("Unknown object type: " + key, e);
        }
        return "";
    }

    private boolean isDouble(JSONObject obj, String key) {
        try {
            Object val = obj.get(key);
            return val instanceof Double;
        }
        catch(JSONException e) {
            return false;
        }
    }

    private boolean isDouble(JSONArray obj, int key) {
        try {
            Object val = obj.get(key);
            return val instanceof Double;
        }
        catch(JSONException e) {
            return false;
        }
    }

    public String makePretty(ArrayNode node, int indentSize) {
        if(node.isArray() && node.size() == 0) {
            return CNUtils.createIndent(indentSize) + "[]";
        }
        String prettyJson = CNUtils.createIndent(indentSize) + "[\r\n";
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
        prettyJson += "\r\n" + CNUtils.createIndent(indentSize) + "]";
        return prettyJson;
    }

    private String formatAsIntended(JsonNode node, int indentSize) {
        if(node.isNull()) {
            return null;
        }
        if(CNUtils.isValueType(node)) {
            if(node.isTextual()) {
                return CNUtils.createIndent(indentSize) + "\"" + node.asText() + "\"";
            }
            return CNUtils.createIndent(indentSize) + node.asText();
        }
        if(node.isArray()) {
            return makePretty((ArrayNode)node, indentSize);
        }
        if(node.isObject()) {
            return formatAsObject(node, indentSize);
        }
        return null;
    }

    private String formatAsObject(JsonNode node, int indentSize) {
        boolean hasValue = false;
        String prettyJson = CNUtils.createIndent(indentSize) + "{\r\n";
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
            prettyJson += CNUtils.createIndent(indentSize + 2) + "\"" + fieldName + "\" : " + result.trim();
            if(fieldNames.hasNext()) {
                prettyJson += ",\r\n";
            }
        }
        if(!hasValue) {
            return "{}";
        }
        prettyJson += "\r\n" + CNUtils.createIndent(indentSize) + "}";
        return prettyJson;
    }
}
