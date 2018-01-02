package com.colonolnutty.module.shareddata.models.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:23 PM
 */
public class ArrayNodeWrapper implements IJsonArrayWrapper {
    private ArrayNode _arrNode;

    public ArrayNodeWrapper(ArrayNode node) {
        _arrNode = node;
    }

    @Override
    public IJsonWrapper get(int index) {
        if(_arrNode == null || size() == 0 || !_arrNode.has(index)) {
            return null;
        }
        JsonNode subNode = _arrNode.get(index);
        if(subNode.isNull()) {
            return null;
        }
        if(subNode.isArray()) {
            return new ArrayNodeWrapper((ArrayNode)subNode);
        }
        return new JsonNodeWrapper(subNode);
    }

    @Override
    public boolean firstItemIsValueType() {
        Object firstItem = get(0);
        if(firstItem == null) {
            return false;
        }
        if(!(firstItem instanceof IJsonWrapper)) {
            return false;
        }
        return ((IJsonWrapper) firstItem).isValueType();
    }

    @Override
    public int size() {
        if(_arrNode == null) {
            return 0;
        }
        return _arrNode.size();
    }

    @Override
    public boolean isValueType() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public String toString() {
        if(_arrNode == null) {
            return null;
        }
        return _arrNode.toString();
    }
}
