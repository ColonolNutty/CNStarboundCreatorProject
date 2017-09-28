package com.company;

import com.company.locators.IngredientStore;
import com.company.locators.RecipeStore;
import com.company.locators.StatusEffectStore;
import com.company.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:31 PM
 */
public class IngredientDataCalculator {
    private DebugLog _log;
    private ConfigSettings _settings;
    private RecipeStore _recipeStore;
    private IngredientStore _ingredientStore;
    private JsonManipulator _manipulator;
    private StatusEffectStore _statusEffectStore;

    public IngredientDataCalculator(DebugLog log,
                                    ConfigSettings settings,
                                    RecipeStore recipeStore,
                                    IngredientStore ingredientStore,
                                    StatusEffectStore statusEffectStore,
                                    JsonManipulator manipulator) {
        _log = log;
        _settings = settings;
        _recipeStore = recipeStore;
        _ingredientStore = ingredientStore;
        _statusEffectStore = statusEffectStore;
        _manipulator = manipulator;
    }

    public Ingredient updateIngredient(Ingredient ingredient){
        Recipe recipe = _recipeStore.locateRecipe(ingredient.getName());
        if(recipe == null) {
            _log.logDebug("No recipe found for: " + ingredient.getName(), true);
            return ingredient;
        }
        return calculateNewValues(recipe);
    }

    private Ingredient calculateNewValues(Recipe recipe) {
        ArrayList<RecipeIngredient> recipeIngredients = findIngredientsFor(recipe);

        Double newFoodValue = 0.0;
        Double newPrice = 0.0;

        String outputName = recipe.output.item;
        boolean outputIsRaw = outputName.startsWith("raw");
        String subName = "Calculating new values for: " + outputName;
        _log.startSubBundle(subName);
        _log.logDebug(subName, true);
        Hashtable<String, Integer> effectValues = new Hashtable<String, Integer>();

        for(int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = recipeIngredients.get(i);
            Ingredient ingredient = recipeIngredient.ingredient;
            _log.startSubBundle("Ingredient " + (i + 1) + " name: " + ingredient.getName());
            _log.logDebug("    count: " + recipeIngredient.count, true);
            _log.logDebug("    price: " + ingredient.price, true);
            _log.logDebug("    food value: " + ingredient.foodValue, true);

            if(ingredient == null) {
                continue;
            }
            newPrice += calculateValue(recipeIngredient.count, ingredient.price);
            newFoodValue += calculateValue(recipeIngredient.count, ingredient.foodValue);

            if(!_settings.enableEffectsUpdate || !ingredient.hasEffects()) {
                _log.endSubBundle();
                continue;
            }

            _log.startSubBundle("    Effects: ");
            for(JsonNode effect : ingredient.effects) {
                if(!effect.isArray()) {
                    continue;
                }
                for(JsonNode subEffect : effect) {
                    if(subEffect == null) {
                        continue;
                    }
                    String subEffectName;
                    if(CNUtils.isValueType(subEffect)) {
                        subEffectName = subEffect.asText();
                    }
                    else if(subEffect.has("effect")) {
                        subEffectName = subEffect.get("effect").asText();
                    }
                    else {
                        _log.logDebug("Effect with no name found on ingredient: " + ingredient.getName(), true);
                        continue;
                    }
                    int defaultDuration = findDefaultDuration(subEffectName);
                    int duration = defaultDuration;
                    if(subEffect.has("duration")) {
                        duration = subEffect.get("duration").asInt(defaultDuration);
                    }
                    boolean isFoodPoisonOnRawFood = subEffectName.equals("foodpoison")
                            && outputIsRaw;
                    if(!isFoodPoisonOnRawFood && CNUtils.contains(subEffectName, _settings.excludedEffects)) {
                        continue;
                    }
                    if(!effectValues.containsKey(subEffectName)) {
                        effectValues.put(subEffectName, duration);
                    }
                    else {
                        effectValues.put(subEffectName, effectValues.get(subEffectName) + (int)(duration * recipeIngredient.count));
                    }
                    _log.logDebug("    " + subEffectName + " with duration: " + duration, true);
                }
            }
            _log.endSubBundle();
            _log.endSubBundle();
        }

        _log.endSubBundle();

        Double outputCount = recipe.output.count;
        ArrayNode combined = null;
        if(_settings.enableEffectsUpdate) {
            ArrayNode combinedEffects = _manipulator.toEffectsArrayNode(outputName, effectValues, outputCount);
            combined = _manipulator.createArrayNode();
            combined.add(combinedEffects);
        }

        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        newPrice = roundTwoDecimalPlaces(newPrice / outputCount);
        newFoodValue = roundTwoDecimalPlaces(newFoodValue / outputCount);

        _log.logDebug("New values for: " + outputName + " are p: " + newPrice + " and fv: " + newFoodValue, true);

        Ingredient newIngredient = new Ingredient(outputName, newPrice, newFoodValue, combined);
        _ingredientStore.updateIngredient(newIngredient);
        return _ingredientStore.getIngredient(outputName);
    }

    private ArrayList<RecipeIngredient> findIngredientsFor(Recipe recipe) {
        ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<RecipeIngredient>();
        for(int i = 0; i < recipe.input.length; i++) {
            ItemDescriptor input = recipe.input[i];
            String ingredientName = input.item;
            Double ingredientCount = input.count;

            Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
            RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient, ingredientCount);
            recipeIngredients.add(recipeIngredient);
        }
        return recipeIngredients;
    }

    private Double calculateValue(Double count, Double value) {
        if(count <= 0.0) {
            count = 1.0;
        }
        if(count == null || value == null) {
            return 0.0;
        }
        return (value * count) + (value * _settings.increasePercentage);
    }

    private Double roundTwoDecimalPlaces(Double val) {
        return (double)Math.round(val * 100)/100;
    }

    private int findDefaultDuration(String effectName) {
        StatusEffect statusEffect = _statusEffectStore.getStatusEffect(effectName);
        if(statusEffect == null || statusEffect.defaultDuration == 0) {
            return Ingredient.DefaultEffectDuration;
        }
        return statusEffect.defaultDuration;
    }
}
