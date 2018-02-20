package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.DefaultNodeProvider;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:15 PM
 */
public class FoodValueHandler extends DefaultNodeProvider implements IJsonHandler {
    public static final String PATH_NAME = "/foodValue";

    public FoodValueHandler() {
        super();
    }

    @Override
    public JsonNode createTestNode(Ingredient ingredient) {
        Double foodValue = ingredient.getFoodValue();
        if(foodValue == null || foodValue < 0.0) {
            return null;
        }
        return _nodeProvider.createTestAddDoubleNode(PATH_NAME);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        Double foodValue = ingredient.getFoodValue();
        if(foodValue == null || foodValue < 0.0) {
            return null;
        }
        return _nodeProvider.createReplaceDoubleNode(PATH_NAME, foodValue);
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
        Double foodValue = ingredient.getFoodValue();
        boolean ingredientHasValue = foodValue != null && foodValue >= 0.0;
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
        return !nodeVal.equals(foodValue);
    }

    @Override
    public String getShortStringValue(Ingredient ingredient) {
        if(ingredient.hasFoodValue()) {
            return "fv: " + ingredient.getFoodValue();
        }
        return null;
    }
}
