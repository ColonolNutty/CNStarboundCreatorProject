package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 12/30/2017
 * Time: 1:00 PM
 */
public class NodeAvailability {
    public String PathName;
    public JsonNode TestNode;
    public ArrayList<JsonNode> NonTestNodes;

    public NodeAvailability(String pathName) {
        PathName = pathName;
    }

    public boolean hasNonTestNodes() {
        return NonTestNodes != null && NonTestNodes.size() > 0;
    }
}
