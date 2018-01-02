package com.colonolnutty.module.shareddata.models.json;

import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:17 PM
 */
public class JsonNodeWrapper implements IJsonObjectWrapper {
    private JsonNode _node;
    private ArrayList<String> _fieldNames;
    private Boolean _isValueType;
    private Boolean _isObject;
    private Boolean _isArray;

    public JsonNodeWrapper(JsonNode node) {
        _node = node;
    }

    @Override
    public IJsonWrapper get(String fieldName) {
        if(_node == null || !_node.isObject() || !_node.has(fieldName)) {
            return null;
        }
        JsonNode subNode = _node.get(fieldName);
        if(subNode.isNull()) {
            return null;
        }
        if(subNode.isArray()) {
            return new ArrayNodeWrapper((ArrayNode) subNode);
        }
        return new JsonNodeWrapper(subNode);
    }

    @Override
    public ArrayList<String> fieldNames() {
        if(_fieldNames != null) {
            return _fieldNames;
        }
        if(_node == null) {
            _fieldNames = new ArrayList<String>();
        }
        else {
            _fieldNames = CNJsonUtils.getPropertyNames(_node);
        }
        return _fieldNames;
    }

    @Override
    public boolean isValueType() {
        if(_node == null) {
            return false;
        }
        if(_isValueType == null) {
            _isValueType = CNJsonUtils.isValueType(_node);
        }
        return _isValueType;
    }

    @Override
    public boolean isObject() {
        if(_node == null) {
            return false;
        }
        if(_isObject == null) {
            _isObject = _node.isObject();
        }
        return _isObject;
    }

    @Override
    public boolean isArray() {
        if(_node == null) {
            return false;
        }
        if(_isArray == null) {
            _isArray = _node.isArray();
        }
        return _isArray;
    }

    @Override
    public String toString() {
        if(_node == null || _node.isNull() || (_node.isTextual() && _node.asText().equals("null"))) {
            return null;
        }
        if(isObject() || isArray()) {
            return _node.toString();
        }
        if(_node.isTextual()) {
            return "\"" + CNStringUtils.escapeString(_node.asText()) + "\"";
        }
        return _node.asText();
    }
}
