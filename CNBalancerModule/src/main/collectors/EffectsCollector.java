package main.collectors;

import com.colonolnutty.module.shareddata.IRequireNodeProvider;
import com.colonolnutty.module.shareddata.NodeProvider;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.locators.StatusEffectStore;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 12:22 PM
 */
public class EffectsCollector implements ICollector, IRequireNodeProvider {
    private CNLog _log;
    private Double _increasePercentage;
    private boolean _isRawFood;
    private Hashtable<String, Integer> _effects;
    private StatusEffectStore _statusEffectStore;
    private String[] _excludedEffects;
    private boolean _enableEffectsUpdate;
    private NodeProvider _nodeProvider;

    public EffectsCollector(boolean enableEffectsUpdate,
                            String[] excludedEffects, StatusEffectStore statusEffectStore,
                            boolean isRawFood, CNLog log, Double increasePercentage) {
        _enableEffectsUpdate = enableEffectsUpdate;
        _excludedEffects = excludedEffects;
        _statusEffectStore = statusEffectStore;
        _isRawFood = isRawFood;
        _log = log;
        _increasePercentage = increasePercentage;
        _nodeProvider = new NodeProvider();
        _effects = new Hashtable<String, Integer>();
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount) {
        if(!ingredient.hasEffects()) {
            return;
        }
        Hashtable<String, Integer> ingredientEffects = getEffects(ingredient, _isRawFood);
        Enumeration<String> effectNames = ingredientEffects.keys();
        while(effectNames.hasMoreElements()) {
            String effectName = effectNames.nextElement();
            Integer effectDuration = ingredientEffects.get(effectName);
            int duration = calculateValue(inputCount, (double)effectDuration, _increasePercentage).intValue();
            addOrUpdateEffect(effectName, duration);
        }
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        if(_enableEffectsUpdate) {
            ArrayNode combinedEffects = toEffectsArrayNode(ingredient.getName(), _effects, outputCount);
            ArrayNode combined = _nodeProvider.createArrayNode();
            combined.add(combinedEffects);
            ingredient.effects = combined;
        }
        return false;
    }

    /**
     * Calculates using formula: (v * c) + (v * iP)
     * @param count (c) number of items
     * @param value (v) value of items
     * @param increasePercentage (iP) amount of value to add to the total
     * @return (v * c) + (v * iP)
     */
    public Double calculateValue(Double count, Double value, Double increasePercentage) {
        if(count == null || value == null || increasePercentage == null) {
            return 0.0;
        }
        if(count <= 0.0) {
            count = 1.0;
        }
        if(value < 0.0) {
            value = 0.0;
        }
        return (value * count) + (value * increasePercentage);
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
        Hashtable<String, Integer> effectValues = new Hashtable<String, Integer>();
        for(JsonNode baseNode : ingredient.effects) {
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

                boolean isFoodPoisonOnRawFood = effectName.equals("foodpoison")
                        && isRawFood;
                if(!isFoodPoisonOnRawFood && CNStringUtils.contains(effectName, _excludedEffects)) {
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
            int effectDuration = (int)(effects.get(effectName)/outputCount);
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
}
