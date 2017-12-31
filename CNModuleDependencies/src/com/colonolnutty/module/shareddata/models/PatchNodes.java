package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 12/30/2017
 * Time: 1:00 PM
 */
public class PatchNodes {
    public ArrayList<JsonNode> TestNodes;
    public ArrayList<JsonNode> NonTestNodes;

    public PatchNodes(ArrayList<JsonNode> testNodes, ArrayList<JsonNode> nonTestNodes) {
        TestNodes = testNodes;
        NonTestNodes = nonTestNodes;
    }
}
