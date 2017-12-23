package com.colonolnutty.module.shareddata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONObject;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 11:35 AM
 */
public interface IPrettyPrinter {
    String makePretty(JSONObject obj, int indentSize);
    String makePretty(JsonNode node, int indentSize);
}
