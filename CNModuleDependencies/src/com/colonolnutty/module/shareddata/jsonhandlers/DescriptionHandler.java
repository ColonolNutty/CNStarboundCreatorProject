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
    private String _pathName;

    public DescriptionHandler() {
        super();
        _pathName = "description";
    }

    @Override
    public JsonNode createTestNode(Ingredient ingredient) {
        if(CNStringUtils.isNullOrWhitespace(ingredient.description)) {
            return null;
        }
        return _nodeProvider.createTestAddStringNode(_pathName);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        return _nodeProvider.createReplaceStringNode(_pathName, ingredient.description);
    }

    @Override
    public boolean canHandle(String pathName) {
        return pathName.equals(_pathName);
    }

    @Override
    public boolean needsUpdate(JsonNode node, Ingredient ingredient) {
        if(node == null || ingredient.description == null) {
            return false;
        }

        if(node.isArray()) {
            return false;
        }

        if(!node.has("value")) {
            return false;
        }

        Double nodeVal = node.get("value").asDouble();
        return !nodeVal.equals(ingredient.description);
    }
}
