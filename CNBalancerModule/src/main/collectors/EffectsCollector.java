package main.collectors;

import com.colonolnutty.module.shareddata.IRequireNodeProvider;
import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.locators.StatusEffectStore;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.IngredientProperty;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.settings.BalancerSettings;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 12:22 PM
 */
public class EffectsCollector extends BaseCollector implements ICollector, IRequireNodeProvider {
    private BalancerSettings _settings;
    private CNLog _log;
    private StatusEffectStore _statusEffectStore;
    private NodeProvider _nodeProvider;
    private boolean _isRawFood = false;
    private static Hashtable<String, ArrayList<String>> _mutuallyExclusiveEffects;

    private Hashtable<String, Integer> _effects;

    public EffectsCollector(BalancerSettings settings,
                            CNLog log,
                            StatusEffectStore statusEffectStore) {
        _settings = settings;
        _statusEffectStore = statusEffectStore;
        _log = log;
        _nodeProvider = new NodeProvider();
        _effects = new Hashtable<String, Integer>();
        _mutuallyExclusiveEffects = new Hashtable<>();
        ArrayList<String> foodPoisonMutuallyExclusiveEffects = new ArrayList<>();
        foodPoisonMutuallyExclusiveEffects.add("poisonblock");
        foodPoisonMutuallyExclusiveEffects.add("antidote");
        _mutuallyExclusiveEffects.put("foodpoison", foodPoisonMutuallyExclusiveEffects);
        ArrayList<String> weakPoisonEffects = new ArrayList<>();
        weakPoisonEffects.add("antidote");
        weakPoisonEffects.add("poisonblock");
        _mutuallyExclusiveEffects.put("weakpoison", weakPoisonEffects);
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount, Recipe recipe) {
        if(!ingredient.hasEffects()) {
            return;
        }
        _isRawFood = recipe.output.item.startsWith("raw");
        Hashtable<String, Integer> ingredientEffects = getEffects(ingredient, _isRawFood);
        Enumeration<String> effectNames = ingredientEffects.keys();
        while(effectNames.hasMoreElements()) {
            String effectName = effectNames.nextElement();
            Integer effectDuration = ingredientEffects.get(effectName);
            int duration = calculateValue(inputCount, (double)effectDuration, _settings.increasePercentage).intValue();
            addOrUpdateEffect(effectName, duration);
        }
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        if(!ingredient.hasEffects() && _effects.size() == 0) {
            return false;
        }
        if(!_settings.enableEffectsUpdate) {
            return false;
        }
        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        ArrayNode combinedEffects = toEffectsArrayNode(ingredient.getName(), _effects, outputCount);
        ArrayNode combined = _nodeProvider.createArrayNode();
        combined.add(combinedEffects);
        ingredient.update(IngredientProperty.Effects, getMutuallyExclusiveEffects(combined));
        if(ingredient.filePath != null && !ingredient.filePath.endsWith(".consumable")) {
            return false;
        }
        return !effectsAreEqual(ingredient.effects, ingredient.getEffects());
    }

    @Override
    public String getDescriptionOfUpdate(Ingredient ingredient) {
        boolean hasOldEffects = ingredient.hasEffects(ingredient.effects);
        boolean hasNewEffects = ingredient.hasEffects(ingredient.getEffects());
        if(!hasOldEffects && !hasNewEffects) {
            return "No Change";
        }
        if(!hasOldEffects && hasNewEffects) {
            return "Has New Effects, but no Existing Effects";
        }
        if(hasOldEffects && !hasNewEffects) {
            return "Has Existing Effects, but no New Effects";
        }
        Hashtable<String, Integer> oldEffects = getEffects(ingredient.effects, ingredient, _isRawFood);
        StringBuilder builder = new StringBuilder("Old Effects: ");
        Enumeration<String> oldEffectKeys = oldEffects.keys();
        while(oldEffectKeys.hasMoreElements()) {
            String effectName = oldEffectKeys.nextElement();
            Integer duration = oldEffects.get(effectName);
            builder.append("\n Effect: '" + effectName + "' Duration: " + duration);
        }
        Hashtable<String, Integer> newEffects = getEffects(ingredient.getEffects(), ingredient, _isRawFood);
        builder.append("\nNew Effects: ");
        Enumeration<String> newEffectKeys = oldEffects.keys();
        while(newEffectKeys.hasMoreElements()) {
            String effectName = newEffectKeys.nextElement();
            Integer duration = newEffects.get(effectName);
            builder.append("\n Effect: '" + effectName + "' Duration: " + duration);
        }
        return builder.toString();
    }

    @Override
    public String getName() {
        return "Effects";
    }

    public void addOrUpdateEffect(String name, int duration) {
        if(_effects.containsKey(name)) {
            int existingValue = _effects.get(name);
            _effects.put(name, existingValue + duration);
        }
        else {
            _effects.put(name, duration);
        }
    }

    public Hashtable<String, Integer> getEffects(Ingredient ingredient, boolean isRawFood) {
        ArrayNode ingredientEffects = ingredient.getEffects();
        return getEffects(ingredientEffects, ingredient, isRawFood);
    }

    public Hashtable<String, Integer> getEffects(ArrayNode ingredientEffects, Ingredient ingredient, boolean isRawFood) {
        Hashtable<String, Integer> effectValues = new Hashtable<String, Integer>();
        if(ingredientEffects == null) {
            return effectValues;
        }
        for(JsonNode baseNode : ingredientEffects){
            if(!baseNode.isArray()) {
                continue;
            }
            for(JsonNode subNode : baseNode) {
                if(subNode == null) {
                    continue;
                }

                String effectName = null;
                if(CNJsonUtils.isValueType(subNode)) {
                    effectName = subNode.asText();
                }
                else if(subNode.has("effect")) {
                    effectName = subNode.get("effect").asText();
                }

                if(CNStringUtils.isNullOrWhitespace(effectName)) {
                    _log.debug("Effect with no name found on ingredient: " + ingredient.getName(), 4);
                    continue;
                }

                int defaultDuration = _statusEffectStore.getDefaultStatusEffectDuration(effectName);
                int effectDuration = defaultDuration;
                if(subNode.has("duration")) {
                    effectDuration = subNode.get("duration").asInt(defaultDuration);
                }
                if(effectDuration <= 0) {
                    continue;
                }

                boolean isFoodPoisonEffect = effectName.equals("foodpoison");
                if(isFoodPoisonEffect && !isRawFood) {
                    continue;
                }

                boolean isFoodPoisonOnRawFood = isFoodPoisonEffect && isRawFood;
                boolean isExcludedEffect = CNStringUtils.contains(effectName, _settings.excludedEffects);
                if(!isFoodPoisonOnRawFood && isExcludedEffect) {
                    continue;
                }

                if(!effectValues.containsKey(effectName)) {
                    effectValues.put(effectName, effectDuration);
                }
                else {
                    int existingValue = effectValues.get(effectName);
                    effectValues.put(effectName, existingValue + effectDuration);
                }
                _log.debug(effectName + " with duration: " + effectDuration, 4);
            }
        }
        return effectValues;
    }

    public ArrayNode toEffectsArrayNode(String ingredientName, Hashtable<String, Integer> effects, Double outputCount) {
        ArrayNode arrayNode = _nodeProvider.createArrayNode();
        if(effects.isEmpty()) {
            return arrayNode;
        }
        _log.startSubBundle("New effects for " + ingredientName);

        Enumeration<String> effectKeys = effects.keys();
        while(effectKeys.hasMoreElements()) {
            String effectName = effectKeys.nextElement();
            int effectDuration = (int)effects.get(effectName);//(int)(effects.get(effectName)/outputCount);
            if(effectDuration == 0) {
                continue;
            }
            ObjectNode objNode = _nodeProvider.createObjectNode();
            objNode.put("effect", effectName);
            objNode.put("duration", effectDuration);
            _log.writeToBundle("Effect name: \"" + effectName + "\", duration: " + effectDuration);
            arrayNode.add(objNode);
        }
        _log.endSubBundle();
        return arrayNode;
    }

    @Override
    public void setNodeProvider(NodeProvider nodeProvider) {
        _nodeProvider = nodeProvider;
    }

    public boolean effectsAreEqual(JsonNode effectsOne, JsonNode effectsTwo) {
        Hashtable<String, Integer> effectValuesOne = getEffects(effectsOne);
        Hashtable<String, Integer> effectValuesTwo = getEffects(effectsTwo);
        if(effectValuesOne == null && effectValuesTwo == null) {
            return true;
        }
        if(effectValuesOne == null || effectValuesTwo == null) {
            return false;
        }

        if(effectValuesOne.size() != effectValuesTwo.size()) {
            return false;
        }

        boolean isSame = true;
        Enumeration<String> effectKeysOne = effectValuesOne.keys();
        while(effectKeysOne.hasMoreElements()) {
            String key = effectKeysOne.nextElement();
            if(!effectValuesTwo.containsKey(key)) {
                isSame = false;
                break;
            }
            if(!effectValuesOne.get(key).equals(effectValuesTwo.get(key))) {
                isSame = false;
                break;
            }
        }
        return isSame;
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

    public ArrayNode getMutuallyExclusiveEffects(JsonNode effectsNode) {
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
        Enumeration<String> keys = effects.keys();
        while(keys.hasMoreElements()) {
            String name = keys.nextElement();
            if(_mutuallyExclusiveEffects.containsKey(name)) {
                ArrayList<String> mutuallyExclusiveEffects = _mutuallyExclusiveEffects.get(name);
                for(String eff : mutuallyExclusiveEffects) {
                    if(effects.containsKey(eff)) {
                        effects.remove(eff);
                    }
                }
            }
        }

        ArrayNode node = _nodeProvider.createArrayNode();
        ArrayNode subNode = _nodeProvider.createArrayNode();
        Enumeration<String> newKeys = effects.keys();
        while(newKeys.hasMoreElements()) {
            String key = newKeys.nextElement();
            Integer duration = effects.get(key);
            ObjectNode objNode = _nodeProvider.createObjectNode();
            objNode.put("effect", key);
            objNode.put("duration", duration);
            subNode.add(objNode);
        }
        node.add(subNode);
        return node;
    }



}
