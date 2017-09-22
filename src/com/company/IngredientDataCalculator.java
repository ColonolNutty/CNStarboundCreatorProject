package com.company;

import com.company.locators.IngredientStore;
import com.company.locators.RecipeStore;
import com.company.models.ConfigSettings;
import com.company.models.Ingredient;
import com.company.models.Recipe;
import com.company.models.RecipeIngredient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:31 PM
 */
public class IngredientDataCalculator {
    private DebugLog _log;
    private RecipeStore _recipeStore;
    private IngredientStore _ingredientStore;
    private JsonManipulator _manipulator;
    private Double _increasePercentage;

    public IngredientDataCalculator(DebugLog log,
                                    ConfigSettings configSettings,
                                    RecipeStore recipeStore,
                                    IngredientStore ingredientStore,
                                    JsonManipulator manipulator) {
        _log = log;
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
        _log.logDebug("Calculating new values for: " + recipe.output.item, true);
        ArrayList<JsonNode> totalEffects = new ArrayList<JsonNode>();

        for(int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = recipeIngredients.get(i);
            Ingredient ingredient = recipeIngredient.ingredient;
            if(ingredient.effects != null) {
                for (int j = 0; j < ingredient.effects.length; j++) {
                    JsonNode[] subEffects = ingredient.effects[j];
                    if(subEffects == null) {
                        continue;
                    }
                    for (int k = 0; k < subEffects.length; k++) {
                        totalEffects.add(ingredient.effects[j][k]);
                    }
                }
            }
            _log.logDebug("    Ingredient " + (i + 1) + " is " + ingredient.getName() + " with count: " + recipeIngredient.count + " p: " + ingredient.price + " and fv: " + ingredient.foodValue, true);

            if(ingredient != null) {
                newPrice += calculateValue(recipeIngredient.count, ingredient.price);
                newFoodValue += calculateValue(recipeIngredient.count, ingredient.foodValue);
            }
        }

        ArrayList<JsonNode> combinedEffects = _manipulator.combineEffects(totalEffects);

        JsonNode[] jsonNode = new JsonNode[combinedEffects.size()];

        for(int i = 0; i < combinedEffects.size(); i++) {
            jsonNode[i] = combinedEffects.get(i);
        }

        JsonNode[][] combined = new JsonNode[1][];
        combined[0] = jsonNode;

        Double outputCount = recipe.output.count;
        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        newPrice = roundTwoDecimalPlaces(newPrice / outputCount);
        newFoodValue = roundTwoDecimalPlaces(newFoodValue / outputCount);

        _log.logDebug("New values for: " + recipe.output.item + " are p: " + newPrice + " and fv: " + newFoodValue, true);

        Ingredient newIngredient = new Ingredient(recipe.output.item, newPrice, newFoodValue, combined);
        _ingredientStore.updateIngredient(newIngredient);
        return _ingredientStore.getIngredient(recipe.output.item);
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
