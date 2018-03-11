package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.DefaultNodeProvider;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:15 PM
 */
public class EffectsHandler extends DefaultNodeProvider implements IJsonHandler {
    public static final String PATH_NAME = "/effects";

    public EffectsHandler() {
        super();
    }

    @Override
    public JsonNode createTestNode(Ingredient ingredient) {
        if(!ingredient.hasEffects()) {
            return null;
        }
        return _nodeProvider.createTestAddArrayNode(PATH_NAME);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        if(!ingredient.hasEffects()) {
            return null;
        }
        return _nodeProvider.createReplaceArrayNode(PATH_NAME, ingredient.getEffects());
    }

    @Override
    public boolean canHandle(String pathName) {
        return pathName.equals(PATH_NAME);
    }

    @Override
    public boolean needsUpdate(JsonNode node, Ingredient ingredient) {
        Hashtable<String, Integer> ingredientEffects = getEffects(ingredient.getEffects());

        JsonNode effects = getEffectsNodeFromNode(node);
        Hashtable<String, Integer> nodeEffects = getEffects(effects);

        if(nodeEffects == null && ingredientEffects == null) {
            return false;
        }
        if(nodeEffects == null || ingredientEffects == null) {
            return true;
        }
        return checkShouldUpdate(ingredientEffects, nodeEffects)
                || checkShouldUpdate(nodeEffects, ingredientEffects);
    }

    @Override
    public String getShortStringValue(Ingredient ingredient) {
        if(!ingredient.hasEffects()) {
            return null;
        }
        JsonNode effectsNode = ingredient.getEffects();
        if(effectsNode == null || !effectsNode.isArray() || effectsNode.size() == 0 || effectsNode.size() > 1) {
            return null;
        }
        JsonNode effectsSubNode = effectsNode.get(0);
        if(!effectsSubNode.isArray() || effectsSubNode.size() == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("effects: ");
        boolean hasEffects = false;
        for(int i = 0; i < effectsSubNode.size(); i++) {
            JsonNode subSubNode = effectsSubNode.get(i);
            if(!subSubNode.isObject()
                    || !subSubNode.has("effect")
                    || !subSubNode.has("duration")) {
                continue;
            }
            JsonNode effectNode = subSubNode.get("effect");
            JsonNode durationNode = subSubNode.get("duration");
            if(!effectNode.isTextual() || !durationNode.isInt()) {
                continue;
            }
            String name = effectNode.asText();
            Integer duration = durationNode.asInt();
            hasEffects = true;

            stringBuilder.append("{ n: \"");
            stringBuilder.append(name);
            stringBuilder.append("\" d: ");
            stringBuilder.append(duration);
            stringBuilder.append(" }");
            if((i + 1) < effectsSubNode.size()) {
                stringBuilder.append(", ");
            }
        }
        if(!hasEffects) {
            return null;
        }
        return stringBuilder.toString();
    }

    public JsonNode getEffectsNodeFromNode(JsonNode node) {
        if(node == null) {
            return null;
        }
        if(node.isArray()) {
            return node;
        }
        else if(node.isObject()) {
            if (!node.has("value")) {
                return null;
            }
            JsonNode subNode = node.get("value");
            if(subNode.isArray()) {
                return subNode;
            }
        }
        return null;
    }

    public Hashtable<String, Integer> getEffects(JsonNode effectsNode) {
        if(effectsNode == null || !effectsNode.isArray() || effectsNode.size() == 0 || effectsNode.size() > 1) {
            return null;
        }
        JsonNode effectsSubNode = effectsNode.get(0);
        if(!effectsSubNode.isArray() || effectsSubNode.size() == 0) {
            return null;
        }

        Hashtable<String, Integer> effects = new Hashtable<String, Integer>();
        for(int i = 0; i < effectsSubNode.size(); i++) {
            JsonNode subSubNode = effectsSubNode.get(i);
            if(!subSubNode.isObject()
                    || !subSubNode.has("effect")
                    || !subSubNode.has("duration")) {
                continue;
            }
            JsonNode effectNode = subSubNode.get("effect");
            JsonNode durationNode = subSubNode.get("duration");
            if(!effectNode.isTextual() || !durationNode.isInt()) {
                continue;
            }
            String name = effectNode.asText();
            Integer duration = durationNode.asInt();
            if(!effects.containsKey(name)) {
                effects.put(name, duration);
            }
        }
        if(effects.size() == 0) {
            return null;
        }
        return effects;
    }

    public boolean checkShouldUpdate(Hashtable<String, Integer> effectsOne,
                                     Hashtable<String, Integer> effectsTwo) {
        if(effectsOne.size() != effectsTwo.size()) {
            return true;
        }
        boolean shouldUpdate = false;
        Enumeration<String> keys = effectsOne.keys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            if(!effectsTwo.containsKey(key)) {
                shouldUpdate = true;
                break;
            }
            Integer valOne = effectsOne.get(key);
            Integer valTwo = effectsTwo.get(key);
            if(!valOne.equals(valTwo)) {
                shouldUpdate = true;
                break;
            }
        }
        return shouldUpdate;
    }
}
