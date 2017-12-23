package com.colonolnutty.module.shareddata;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 12:33 PM
 */
public class NodeProvider extends MapperWrapper {

    public ObjectNode createTestInverseNode(String pathName) {
        ObjectNode node = createTestNode(pathName);
        node.put("inverse", true);
        return node;
    }

    public ArrayNode createTestAddArrayNode(String pathName) {
        ArrayNode nodeArray = createArrayNode();
        ObjectNode testNode = createTestInverseNode(pathName);
        nodeArray.add(testNode);
        ObjectNode node = createAddNode(pathName);
        ArrayNode arrNode = node.putArray("value");
        arrNode.add(_mapper.createArrayNode());
        nodeArray.add(node);
        return nodeArray;
    }

    public ArrayNode createTestAddStringNode(String pathName) {
        ArrayNode nodeArray = createArrayNode();
        ObjectNode testNode = createTestInverseNode(pathName);
        nodeArray.add(testNode);
        ObjectNode node = createAddNode(pathName);
        node.put("value", "");
        nodeArray.add(node);
        return nodeArray;
    }

    public ArrayNode createTestAddDoubleNode(String pathName) {
        ArrayNode nodeArray = createArrayNode();
        ObjectNode testNode = createTestInverseNode(pathName);
        nodeArray.add(testNode);
        ObjectNode node = createAddNode(pathName);
        node.put("value", 0.0);
        nodeArray.add(node);
        return nodeArray;
    }

    public ArrayNode createTestAddIntegerNode(String pathName) {
        ArrayNode nodeArray = createArrayNode();
        ObjectNode testNode = createTestInverseNode(pathName);
        nodeArray.add(testNode);
        ObjectNode node = createAddNode(pathName);
        node.put("value", 0);
        nodeArray.add(node);
        return nodeArray;
    }

    public ArrayNode createTestRemoveNodes(String pathName, Double value) {
        ArrayNode nodeArray = createArrayNode();
        ObjectNode testNode = createTestNode(pathName);
        testNode.put("value",  value);
        nodeArray.add(testNode);
        ObjectNode node = createObjectNode();
        node.put("op", "remove");
        node.put("path", "/" + pathName);
        nodeArray.add(node);
        return nodeArray;
    }

    public ObjectNode createReplaceArrayNode(String pathName, ArrayNode value) {
        if(value == null) {
            return null;
        }
        ObjectNode node = createReplaceNode(pathName);
        ArrayNode arrNode = node.putArray("value");
        for(int i = 0; i < value.size(); i++) {
            arrNode.add(value.get(i));
        }
        return node;
    }

    public ObjectNode createReplaceDoubleNode(String pathName, Double value) {
        ObjectNode node = createReplaceNode(pathName);
        node.put("value", value);
        return node;
    }

    public ObjectNode createReplaceStringNode(String pathName, String value) {
        ObjectNode node = createReplaceNode(pathName);
        node.put("value", value);
        return node;
    }

    public ObjectNode createReplaceIntegerNode(String pathName, Integer value) {
        ObjectNode node = createReplaceNode(pathName);
        node.put("value", value);
        return node;
    }

    public ObjectNode createAddStringNode(String pathName, String value) {
        ObjectNode node = createAddNode(pathName);
        node.put("value", value);
        return node;
    }

    public ArrayNode createArrayNode() {
        return _mapper.createArrayNode();
    }

    public ObjectNode createObjectNode() {
        return _mapper.createObjectNode();
    }

    private ObjectNode createAddNode(String pathName) {
        return createOperationNode("add", pathName);
    }

    private ObjectNode createReplaceNode(String pathName) {
        return createOperationNode("replace", pathName);
    }

    private ObjectNode createTestNode(String pathName) {
        return createOperationNode("test", pathName);
    }

    private ObjectNode createOperationNode(String operation, String pathName) {
        if(!pathName.startsWith("/")) {
            pathName = "/" + pathName;
        }
        ObjectNode node = createObjectNode();
        node.put("op", operation);
        node.put("path", pathName);
        return node;
    }
}
