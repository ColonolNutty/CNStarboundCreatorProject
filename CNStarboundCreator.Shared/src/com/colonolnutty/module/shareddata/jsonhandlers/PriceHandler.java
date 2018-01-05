package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.DefaultNodeProvider;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:15 PM
 */
public class PriceHandler extends DefaultNodeProvider implements IJsonHandler {
    public static final String PATH_NAME = "/price";

    public PriceHandler() {
        super();
    }

    @Override
    public JsonNode createTestNode(Ingredient ingredient) {
        if(ingredient.price == null || ingredient.price < 0.0) {
            return null;
        }
        return _nodeProvider.createTestAddDoubleNode(PATH_NAME);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        if(ingredient.price == null || ingredient.price < 0.0) {
            return null;
        }
        return _nodeProvider.createReplaceDoubleNode(PATH_NAME, ingredient.price);
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
        boolean ingredientHasFoodValue = ingredient.price != null && ingredient.price >= 0.0;
        if(node == null) {
            return ingredientHasFoodValue;
        }

        if(node.isArray()) {
            return false;
        }

        if(!node.has("value")) {
            return ingredientHasFoodValue;
        }

        JsonNode foodValue = node.get("value");
        if(!foodValue.isDouble()) {
            return ingredientHasFoodValue;
        }

        Double nodeVal = foodValue.asDouble();
        return !nodeVal.equals(ingredient.price);
    }

    @Override
    public String getShortStringValue(Ingredient ingredient) {
        if(ingredient.hasPrice()) {
            return "p: " + ingredient.price;
        }
        return null;
    }
}
