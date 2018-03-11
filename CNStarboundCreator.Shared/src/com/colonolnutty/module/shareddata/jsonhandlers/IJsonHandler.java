package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:15 PM
 */
public interface IJsonHandler {
    String getPathName();
    JsonNode createTestNode(Ingredient ingredient);
    JsonNode createReplaceNode(Ingredient ingredient);
    boolean canHandle(String pathName);
    boolean canHandle(Ingredient ingredient);
    boolean needsUpdate(JsonNode node, Ingredient ingredient);
    String getShortStringValue(Ingredient ingredient);
}
