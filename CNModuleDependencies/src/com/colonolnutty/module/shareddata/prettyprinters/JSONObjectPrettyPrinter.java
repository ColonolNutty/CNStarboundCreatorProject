package com.colonolnutty.module.shareddata.prettyprinters;

import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 12:38 PM
 */
public class JSONObjectPrettyPrinter extends BasePrettyPrinter {

    @Override
    public String makePretty(Object object, int indentSize) throws JSONException {
        if(!canPrettyPrint(object)) {
            return null;
        }
        return formatObject((JSONObject) object, indentSize, false);
    }

    public String formatObject(JSONObject obj, int indentSize, boolean includeIndent) throws JSONException {
        ArrayList<String> objProperties = CNJsonUtils.getPropertyNames(obj);
        if(objProperties.size() == 0) {
            return "{ }";
        }
        StringBuilder builder = new StringBuilder();
        if(includeIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        if(objProperties.size() == 1) {
            String firstProperty = objProperties.get(0);
            try {
                String result = formatAsIntended(obj.get(firstProperty), 0, false);
                if(result == null)
                {
                    return "{ }";
                }
                builder.append("{ \"" + firstProperty + "\" : " + result + " }");
            }
            catch(JSONException e) {
                throw new JSONException("Single property obj, for property: " + firstProperty, e);
            }
            return builder.toString();
        }
        ArrayList<String> sortedProperties = sortProperties(_propertiesInOrder, objProperties);
        builder.append("{" + NEW_LINE);
        int currentLevelIndent = indentSize + INDENT_SIZE;
        for(int i = 0; i < sortedProperties.size(); i++) {
            String propertyName = sortedProperties.get(i);
            Object propertyObj = obj.get(propertyName);
            String result;
            try {
                result = formatAsIntended(propertyObj, currentLevelIndent, false);
            }
            catch(JSONException e) {
                throw new JSONException("For property: " + propertyName, e);
            }
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

    public String formatArray(JSONArray array, int indentSize, boolean includeIndent) throws JSONException {
        int arrayLength = array.length();
        if(arrayLength == 0) {
            return "[ ]";
        }
        Object one = array.get(0);
        if(arrayLength == 1 && one instanceof JSONArray && ((JSONArray)one).length() == 0) {
            return "[[ ]]";
        }
        boolean containsValueTypes = CNJsonUtils.isValueType(one);
        StringBuilder builder = new StringBuilder();
        if(includeIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        builder.append("[");
        if(containsValueTypes) {
            builder.append(" ");
        }
        for(int i = 0; i < arrayLength; i++) {
            if(array.isNull(i)) {
                continue;
            }
            Object val = array.get(i);
            String result = null;
            try {
                result = formatAsIntended(val, indentSize + INDENT_SIZE, !containsValueTypes);
            }
            catch(JSONException e) {
                throw new JSONException("For index: " + i, e);
            }
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

    public String formatAsIntended(Object val, int indentSize, boolean includeIndent) throws JSONException {
        if(val == null || val.toString().equals("null")) {
            return null;
        }
        if(val instanceof Double || val instanceof Integer || val instanceof Boolean) {
            return val.toString();
        }
        if(val instanceof String) {
            return "\"" + CNStringUtils.escapeString(val.toString()) + "\"";
        }

        if(val instanceof JSONObject) {
            return formatObject((JSONObject) val, indentSize, includeIndent);
        }
        if(val instanceof JSONArray) {
            return formatArray((JSONArray) val, indentSize, includeIndent);
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
    public boolean canPrettyPrint(Object obj) {
        return obj instanceof JSONObject;
    }
}
