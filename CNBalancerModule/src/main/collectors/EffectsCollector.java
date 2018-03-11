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

    private Hashtable<String, Integer> _effects;

    public EffectsCollector(BalancerSettings settings,
                            CNLog log,
                            StatusEffectStore statusEffectStore) {
        _settings = settings;
        _statusEffectStore = statusEffectStore;
        _log = log;
        _nodeProvider = new NodeProvider();
        _effects = new Hashtable<String, Integer>();
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
        if(ingredient.filePath != null && !ingredient.filePath.endsWith(".consumable")) {
            return false;
        }
        if(!ingredient.hasEffects() && _effects.size() == 0) {
            return false;
        }
        if(!_settings.enableEffectsUpdate || _effects.size() == 0) {
            return false;
        }
        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        ArrayNode combinedEffects = toEffectsArrayNode(ingredient.getName(), _effects, outputCount);
        ArrayNode combined = _nodeProvider.createArrayNode();
        combined.add(combinedEffects);
        ingredient.update(IngredientProperty.Effects, combined);
        return !ingredient.effectsAreEqual(ingredient.effects, ingredient.getEffects());
    }

    @Override
    public String getDescriptionOfUpdate(Ingredient ingredient) {
        boolean hasOldEffects = ingredient.hasEffects(ingredient.effects);
        boolean hasNewEffects = ingredient.hasEffects(ingredient.getEffects());
        if(!hasOldEffects && !hasNewEffects) {
            return "No Change";
        }
        if(!hasOldEffects && hasNewEffects) {
            return "Has New Effects, but no Old Effects";
        }
        if(hasOldEffects && !hasNewEffects) {
            return "Has Old Effects, but no New Effects";
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

                boolean isFoodPoisonOnRawFood = effectName.equals("foodpoison")
                        && isRawFood;
                if(!isFoodPoisonOnRawFood && CNStringUtils.contains(effectName, _settings.excludedEffects)) {
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
}
