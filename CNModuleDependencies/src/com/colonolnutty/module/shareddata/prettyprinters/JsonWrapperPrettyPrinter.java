package com.colonolnutty.module.shareddata.prettyprinters;

import com.colonolnutty.module.shareddata.models.json.*;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:30 PM
 */
public class JsonWrapperPrettyPrinter extends BasePrettyPrinter {
    @Override
    public String makePretty(Object obj, int indentSize) throws JSONException {
        if(!canPrettyPrint(obj)) {
            return null;
        }
        if(obj instanceof JSONObject) {
            return formatObject(new JSONObjectWrapper((JSONObject) obj), indentSize, false);
        }
        if(obj instanceof JSONArray) {
            return formatArray(new JSONArrayWrapper((JSONArray) obj), indentSize, false);
        }
        if(!(obj instanceof JsonNode)) {
            return null;
        }
        JsonNode node = (JsonNode) obj;
        if(node.isNull()) {
            return null;
        }
        if(node.isObject()) {
            return formatObject(new JsonNodeWrapper(node), indentSize, false);
        }
        if(node.isArray()) {
            return formatArray(new ArrayNodeWrapper((ArrayNode) node), indentSize, false);
        }
        if(node.isTextual()) {
            if(node.asText().equals("null")) {
                return null;
            }
            return "\"" + node.asText() + "\"";
        }
        if(CNJsonUtils.isValueType(node)) {
            return node.asText();
        }
        return null;
    }

    public String formatObject(IJsonObjectWrapper obj, int indentSize, boolean shouldIndent) {
        ArrayList<String> objProperties = obj.fieldNames();
        if(objProperties.size() == 0) {
            return "{ }";
        }
        StringBuilder builder = new StringBuilder();
        if(shouldIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        if(objProperties.size() == 1) {
            String firstProperty = objProperties.get(0);
            IJsonWrapper firstObj = obj.get(firstProperty);
            return formatSinglePropertyObject(firstProperty, firstObj, indentSize, shouldIndent);
        }
        ArrayList<String> sortedProperties = sortProperties(_propertiesInOrder, objProperties);
        builder.append("{" + NEW_LINE);
        int currentLevelIndent = indentSize + INDENT_SIZE;
        for(int i = 0; i < sortedProperties.size(); i++) {
            String propertyName = sortedProperties.get(i);
            IJsonWrapper propertyObj = obj.get(propertyName);
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

    public String formatArray(IJsonArrayWrapper arr, int indentSize, boolean shouldIndent) {
        int arrSize = arr.size();
        if(arrSize == 0) {
            return "[ ]";
        }
        StringBuilder builder = new StringBuilder();
        if(shouldIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        Object objOne = arr.get(0);
        if(objOne == null || !(objOne instanceof IJsonWrapper)) {
            return null;
        }
        IJsonWrapper objOneWrapped = (IJsonWrapper) objOne;
        if(arrSize == 1) {
            if(objOneWrapped.isArray()
                    && ((IJsonArrayWrapper) objOne).size() == 0) {
                builder.append("[[ ]]");
                return builder.toString();
            }
            else if(!objOneWrapped.isValueType()) {
                return formatSingleItemArray(objOneWrapped, indentSize, shouldIndent);
            }
        }
        boolean containsValueTypes = objOneWrapped.isValueType();
        builder.append("[");
        if(containsValueTypes) {
            builder.append(" ");
        }
        for(int i = 0; i < arrSize; i++) {
            String result = formatAsIntended(arr.get(i), indentSize + INDENT_SIZE, !containsValueTypes);
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

    public String formatSingleItemArray(IJsonWrapper item, int indentSize, boolean shouldIndent) {
        StringBuilder builder = new StringBuilder();
        if(shouldIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        int subIndent = indentSize + INDENT_SIZE;
        if(item.isArray()) {
            subIndent = indentSize;
        }
        String result = formatAsIntended(item, subIndent, true);
        if(result == null) {
            builder.append("[ ]");
            return builder.toString();
        }
        builder.append("[");
        if(item.isArray()) {
            builder.append(result.trim());
            builder.append("]");
            return builder.toString();
        }
        boolean isMultilineObject = result.contains(NEW_LINE);
        if(isMultilineObject) {
            builder.append(NEW_LINE);
        }
        else {
            builder.append(" ");
            result = result.trim();
        }
        builder.append(result);
        if(isMultilineObject) {
            builder.append(NEW_LINE);
            if(shouldIndent) {
                builder.append(CNStringUtils.createIndent(indentSize));
            }
        }
        else {
            builder.append(" ");
        }
        builder.append("]");
        return builder.toString();
    }

    public String formatSinglePropertyObject(String propertyName, IJsonWrapper obj, int indentSize, boolean shouldIndent) {
        StringBuilder builder = new StringBuilder();
        if(shouldIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        int subIndent = indentSize + INDENT_SIZE;
        String result = formatAsIntended(obj, subIndent, true);
        if(result == null) {
            builder.append("{ }");
            return builder.toString();
        }
        builder.append("{");
        boolean isMultilineObject = result.contains(NEW_LINE);
        result = result.trim();
        if(isMultilineObject) {
            builder.append(NEW_LINE);
            builder.append(CNStringUtils.createIndent(subIndent));
        }
        else {
            builder.append(" ");
        }
        builder.append("\"" + propertyName + "\" : " + result);
        if(isMultilineObject) {
            builder.append(NEW_LINE);
            if(shouldIndent) {
                builder.append(CNStringUtils.createIndent(indentSize));
            }
        }
        else {
            builder.append(" ");
        }
        builder.append("}");
        return builder.toString();
    }

    public String formatAsIntended(IJsonWrapper obj, int indentSize, boolean shouldIndent) {
        if(obj == null) {
            return null;
        }
        String objStr = obj.toString();
        if(objStr == null || objStr.equals("null")) {
            return null;
        }
        if(obj.isArray()) {
            return formatArray((IJsonArrayWrapper) obj, indentSize, shouldIndent);
        }
        if(obj.isObject()) {
            return formatObject((IJsonObjectWrapper) obj, indentSize, shouldIndent);
        }
        return objStr;
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
        return obj instanceof JSONObject
                || obj instanceof JSONArray
                || obj instanceof JsonNode;
    }
}
