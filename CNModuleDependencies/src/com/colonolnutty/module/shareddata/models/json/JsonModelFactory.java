package com.colonolnutty.module.shareddata.models.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:02 PM
 */
public class JsonModelFactory {
    private JsonNode _node;
    private JSONObject _jsonObj;

    private JsonModelFactory() {}

    public static IJsonObjectWrapper createObjectWrapper(JSONObject obj) {
        return new JSONObjectWrapper(obj);
    }

    public static IJsonObjectWrapper createObjectWrapper(JsonNode node) {
        if(node.isObject()) {
            return new JsonNodeWrapper((ObjectNode)node);
        }
        return null;
    }

    public static IJsonArrayWrapper createArrayWrapper(JSONArray arr) {
        return new JSONArrayWrapper(arr);
    }
}
