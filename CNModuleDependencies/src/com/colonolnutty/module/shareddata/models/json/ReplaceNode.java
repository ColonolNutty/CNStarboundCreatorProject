package com.colonolnutty.module.shareddata.models.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 1:57 PM
 */
public class ReplaceNode {
    public ArrayNode TestNode;
    public ObjectNode ReplaceNode;

    public ReplaceNode(ArrayNode testNode, ObjectNode replaceNode) {
        TestNode = testNode;
        ReplaceNode = replaceNode;
    }
}
