package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.DefaultNodeProvider;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:15 PM
 */
public class DescriptionHandler extends DefaultNodeProvider implements IJsonHandler {
    public static final String PATH_NAME = "/description";

    @Override
    public JsonNode createTestNode(Ingredient ingredient) {
        if(CNStringUtils.isNullOrWhitespace(ingredient.description)) {
            return null;
        }
        return _nodeProvider.createTestAddStringNode(PATH_NAME);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        if(CNStringUtils.isNullOrWhitespace(ingredient.description)) {
            return null;
        }
        return _nodeProvider.createReplaceStringNode(PATH_NAME, ingredient.description);
    }

    @Override
    public boolean canHandle(String pathName) {
        return pathName.equals(PATH_NAME);
    }

    @Override
    public boolean needsUpdate(JsonNode node, Ingredient ingredient) {
        if(ingredient == null) {
            return false;
        }
        boolean ingredientHasDescription = !CNStringUtils.isNullOrWhitespace(ingredient.description);
        if(node == null) {
            return ingredientHasDescription;
        }

        if(node.isArray()) {
            return false;
        }

        if(!node.has("value")) {
            return ingredientHasDescription;
        }

        JsonNode descriptionNode = node.get("value");
        if(!descriptionNode.isTextual()) {
            return ingredientHasDescription;
        }

        String nodeVal = descriptionNode.asText();
        return !nodeVal.equals(ingredient.description);
    }

    @Override
    public String getShortStringValue(Ingredient ingredient) {
        if(ingredient.hasDescription()) {
            return "desc: " + ingredient.description;
        }
        return null;
    }
}
