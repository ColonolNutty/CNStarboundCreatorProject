package com.company;

import com.company.locators.IngredientStore;
import com.company.locators.RecipeStore;
import com.company.models.ConfigSettings;
import com.company.models.Ingredient;
import com.company.models.Recipe;
import com.company.models.RecipeIngredient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Enumeration;
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
    private Double _increasePercentage;

    public IngredientDataCalculator(DebugLog log,
                                    ConfigSettings settings,
                                    ConfigSettings configSettings,
                                    RecipeStore recipeStore,
                                    IngredientStore ingredientStore,
                                    JsonManipulator manipulator) {
        _log = log;
        _settings = settings;
        _recipeStore = recipeStore;
        _ingredientStore = ingredientStore;
        _increasePercentage = configSettings.increasePercentage;
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
        _log.logDebug("Calculating new values for: " + outputName, true);
        Hashtable<String, Integer> effectValues = new Hashtable<String, Integer>();

        for(int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = recipeIngredients.get(i);
            Ingredient ingredient = recipeIngredient.ingredient;
            _log.logDebug("    Ingredient " + (i + 1) + " is " + ingredient.getName() + " with count: " + recipeIngredient.count + " p: " + ingredient.price + " and fv: " + ingredient.foodValue, true);
            if(ingredient == null) {
                continue;
            }
            newPrice += calculateValue(recipeIngredient.count, ingredient.price);
            newFoodValue += calculateValue(recipeIngredient.count, ingredient.foodValue);

            if(_settings.enableEffectsUpdate && ingredient.hasEffects()) {
                for(JsonNode effect : ingredient.effects) {
                    if(!effect.isArray()) {
                        continue;
                    }
                    for(JsonNode subEffect : effect) {
                        if(subEffect == null) {
                            continue;
                        }
                        int duration = Ingredient.DefaultEffectDuration;
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
                        if(subEffect.has("duration")) {
                            duration = subEffect.get("duration").asInt(Ingredient.DefaultEffectDuration);
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
                        _log.logDebug("    Ingredient " + (i + 1) + " has effect " + subEffectName + " with duration: " + duration, true);
                    }
                }
            }
        }

        ArrayNode combined = null;
        if(_settings.enableEffectsUpdate) {
            ArrayNode combinedEffects = _manipulator.toEffectsArrayNode(outputName, effectValues);
            combined = _manipulator.createArrayNode();
            combined.add(combinedEffects);
        }

        Double outputCount = recipe.output.count;
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
        return (value * count) + (value * _increasePercentage);
    }

    private Double roundTwoDecimalPlaces(Double val) {
        return (double)Math.round(val * 100)/100;
    }
}
