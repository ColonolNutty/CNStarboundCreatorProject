package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.DefaultNodeProvider;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        Double price = ingredient.getPrice();
        if(price == null || price < 0.0) {
            return null;
        }
        return _nodeProvider.createTestAddDoubleNode(PATH_NAME);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        Double price = ingredient.getPrice();
        if(price == null || price < 0.0) {
            return null;
        }
        return _nodeProvider.createReplaceDoubleNode(PATH_NAME, price);
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
        Double price = ingredient.getPrice();
        boolean ingredientHasValue = price != null && price >= 0.0;
        if(node == null) {
            return ingredientHasValue;
        }

        if(node.isArray()) {
            return false;
        }

        if(!node.has("value")) {
            return ingredientHasValue;
        }

        JsonNode value = node.get("value");
        if(!value.isDouble()) {
            return ingredientHasValue;
        }

        Double nodeVal = value.asDouble();
        return !nodeVal.equals(price);
    }

    @Override
    public String getShortStringValue(Ingredient ingredient) {
        if(ingredient.hasPrice()) {
            return "p: " + ingredient.getPrice();
        }
        return null;
    }
}
