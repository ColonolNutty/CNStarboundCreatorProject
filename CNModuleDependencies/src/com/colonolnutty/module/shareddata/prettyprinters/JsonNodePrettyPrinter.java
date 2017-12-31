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
        if(node.isArray()) {
            return formatArray(node, indentSize);
        }
        if(node.isObject()) {
            return formatObject(node, indentSize);
        }
        if(CNJsonUtils.isValueType(node)) {
            return node.asText();
        }
        return obj.toString();
    }

    public String formatObject(JsonNode node, int indentSize) throws JSONException {
        ArrayList<String> objFields = CNJsonUtils.getPropertyNames(node);
        if(node.isNull() || objFields.size() == 0) {
            return "{ }";
        }
        if(objFields.size() == 1) {
            String propOne = objFields.get(0);
            JsonNode subNodeOne = node.get(propOne);
            return CNStringUtils.createIndent(indentSize) + "{ \"" + propOne + "\" : " + formatAsIntended(subNodeOne, 0) + " }";
        }
        boolean hasValue = false;
        StringBuilder builder = new StringBuilder(CNStringUtils.createIndent(indentSize) + "{" + NEW_LINE);
        for(int i = 0; i < objFields.size(); i++) {
            String fieldName = objFields.get(i);
            String result = formatAsIntended(node.get(fieldName), indentSize + INDENT_SIZE);
            if(result == null) {
                continue;
            }
            hasValue = true;
            builder.append(CNStringUtils.createIndent(indentSize + INDENT_SIZE) + "\"" + fieldName + "\" : " + result.trim());
            if((i + 1) < objFields.size()) {
                builder.append("," + NEW_LINE);
            }
        }
        if(!hasValue) {
            return "{ }";
        }
        builder.append(NEW_LINE + CNStringUtils.createIndent(indentSize) + "}");
        return builder.toString();
    }

    public String formatArray(JsonNode node, int indentSize) throws JSONException {
        if(!node.isArray()) {
            return formatObject(node, indentSize);
        }
        int nodeSize = node.size();
        if(nodeSize == 0) {
            return "[ ]";
        }
        JsonNode nodeOne = node.get(0);
        if(nodeSize == 1) {
            if(nodeOne.isArray() && nodeOne.size() == 0) {
                return "[[ ]]";
            }
            if(nodeOne.isObject()) {
                return CNStringUtils.createIndent(indentSize) + "[" + formatObject(nodeOne, 0) + "]";
            }
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
            if(containsValueTypes && (subNode.isObject() || subNode.isArray())) {
                result = result.trim();
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

    @Override
    public boolean canPrettyPrint(Object obj) {
        return obj instanceof JsonNode;
    }
}
