package com.colonolnutty.module.shareddata.jsonhandlers;

import com.colonolnutty.module.shareddata.DefaultNodeProvider;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:15 PM
 */
public class EffectsHandler extends DefaultNodeProvider implements IJsonHandler {
    private String _pathName;

    public EffectsHandler() {
        super();
        _pathName = "effects";
    }

    @Override
    public JsonNode createTestNode(Ingredient ingredient) {
        if(ingredient.effects == null) {
            return null;
        }
        return _nodeProvider.createTestAddArrayNode(_pathName);
    }

    @Override
    public JsonNode createReplaceNode(Ingredient ingredient) {
        return _nodeProvider.createReplaceArrayNode(_pathName, ingredient.effects);
    }

    @Override
    public boolean canHandle(String pathName) {
        return pathName.equals(_pathName);
    }

    @Override
    public boolean needsUpdate(JsonNode node, Ingredient ingredient) {
        if(node == null || node.isArray() || ingredient.effects == null) {
            return false;
        }
        Hashtable<String, Integer> ingredientEffects = new Hashtable<String, Integer>();
        for(int i = 0; i < ingredient.effects.size(); i++) {
            JsonNode subEffects = ingredient.effects.get(i);
            for(int j = 0; j < subEffects.size(); j++) {
                JsonNode effectNode = subEffects.get(j);
                String name = effectNode.get("effect").asText();
                Integer duration = effectNode.get("duration").asInt();
                if(!ingredientEffects.contains(name)) {
                    ingredientEffects.put(name, duration);
                }
            }
        }

        Hashtable<String, Integer> nodeEffects = new Hashtable<String, Integer>();
        if(!node.has("value")) {
            return false;
        }

        JsonNode effects = node.get("value");
        if(!effects.isArray() || effects.size() == 0 || effects.size() > 1) {
            return false;
        }
        JsonNode subEffectsNode = effects.get(0);
        if(!subEffectsNode.isArray() || subEffectsNode.size() == 0) {
            return false;
        }

        for(int j = 0; j < subEffectsNode.size(); j++) {
            JsonNode subSubNode = subEffectsNode.get(j);
            if(!subSubNode.has("effect")
                || !subSubNode.has("duration")) {
                continue;
            }
            String name = subSubNode.get("effect").asText();
            Integer duration = subSubNode.get("duration").asInt();
            if(!nodeEffects.contains(name)) {
                nodeEffects.put(name, duration);
            }
        }

        if(ingredientEffects.size() != nodeEffects.size()) {
            return true;
        }

        boolean needsUpdate = false;
        Enumeration<String> nodeKeys = nodeEffects.keys();
        while(nodeKeys.hasMoreElements()) {
            String key = nodeKeys.nextElement();
            if(!ingredientEffects.containsKey(key)) {
                needsUpdate = true;
                break;
            }
        }

        Enumeration<String> keys = ingredientEffects.keys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            if(!nodeEffects.contains(key)) {
                needsUpdate = true;
                break;
            }
            Integer ingredDuration = ingredientEffects.get(key);
            Integer nodeDuration = nodeEffects.get(key);
            if(ingredDuration != nodeDuration) {
                needsUpdate = true;
                break;
            }
        }

        return needsUpdate;
    }
}
