package com.colonolnutty.module.shareddata.prettyprinters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 11:35 AM
 */
public interface IPrettyPrinter {
    void setPropertyOrder(String[] propertyOrder);
    String makePretty(Object obj, int indentSize) throws JSONException;
    boolean canPrettyPrint(Object obj);
}
