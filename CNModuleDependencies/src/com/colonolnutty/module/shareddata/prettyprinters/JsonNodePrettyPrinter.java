package com.colonolnutty.module.shareddata.prettyprinters;

import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 12:28 PM
 */
public class JsonNodePrettyPrinter extends BasePrettyPrinter {

    @Override
    public String makePretty(Object obj, int indentSize) {
        if(!canPrettyPrint(obj)) {
            return null;
        }
        JsonNode node = (JsonNode) obj;
        return formatAsIntended(node, indentSize, false);
    }

    public String formatObject(JsonNode node, int indentSize, boolean includeIndent) throws JSONException {
        ArrayList<String> objProperties = CNJsonUtils.getPropertyNames(node);
        if(objProperties.size() == 0) {
            return "{ }";
        }
        StringBuilder builder = new StringBuilder();
        if(includeIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        if(objProperties.size() == 1) {
            String firstProperty = objProperties.get(0);
            JsonNode firstObj = node.get(firstProperty);
            String result = formatAsIntended(firstObj, 0, false);
            if(result == null) {
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
            JsonNode propertyObj = node.get(propertyName);
            String result = formatAsIntended(propertyObj, currentLevelIndent, false);
            if(result == null) {
                continue;
            }
            builder.append(CNStringUtils.createIndent(currentLevelIndent));
            builder.append("\"" + propertyName + "\" : " + result);
            if((i + 1) < sortedProperties.size()) {
                builder.append("," + NEW_LINE);
            }
        }
        builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize) + "}");
        return builder.toString();
    }

    public String formatArray(JsonNode node, int indentSize, boolean includeIndent) throws JSONException {
        if(!node.isArray()) {
            return formatObject(node, indentSize, includeIndent);
        }
        int nodeSize = node.size();
        if(nodeSize == 0) {
            return "[ ]";
        }
        StringBuilder builder = new StringBuilder();
        if(includeIndent) {
            builder.append(CNStringUtils.createIndent(indentSize));
        }
        JsonNode nodeOne = node.get(0);
        if(nodeSize == 1) {
            if(nodeOne.isArray() && nodeOne.size() == 0) {
                return "[[ ]]";
            }
            if(nodeOne.isObject()) {
                builder.append("[" + formatObject(nodeOne, 0, false) + "]");
                return builder.toString();
            }
        }
        boolean containsValueTypes = CNJsonUtils.isValueType(nodeOne);
        builder.append("[");
        if(containsValueTypes) {
            builder.append(" ");
        }
        for(int i = 0; i < nodeSize; i++) {
            JsonNode subNode = node.get(i);
            String result = formatAsIntended(subNode, indentSize + INDENT_SIZE, !containsValueTypes);
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

    public String formatAsIntended(JsonNode node, int indentSize, boolean includeIndent) {
        if(node == null || node.isNull() || (node.isTextual() && node.asText().equals("null"))) {
            return null;
        }

        if(node.isArray()) {
            return formatArray(node, indentSize, includeIndent);
        }
        if(node.isObject()) {
            return formatObject(node, indentSize, includeIndent);
        }

        if(CNJsonUtils.isValueType(node)) {
            if (node.isTextual()) {
                return "\"" + CNStringUtils.escapeString(node.asText()) + "\"";
            }
            return node.asText();
        }
        return null;
    }

    @Override
    public boolean canPrettyPrint(Object obj) {
        return obj instanceof JsonNode;
    }
}
