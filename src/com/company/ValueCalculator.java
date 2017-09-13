package com.company;

import com.company.locators.IngredientStore;
import com.company.locators.RecipeLocator;
import com.company.models.ConfigSettings;
import com.company.models.Ingredient;
import com.company.models.Recipe;
import com.company.models.RecipeIngredient;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:31 PM
 */
public class ValueCalculator {
    private Double increasePercentage = 0.05;
    private RecipeLocator _recipeLocator;
    private IngredientStore _ingredientStore;
    private DebugLog _log;

    public ValueCalculator(DebugLog log,
                           ConfigSettings configSettings,
                           RecipeLocator recipeLocator,
                           IngredientStore ingredientStore) {
        _recipeLocator = recipeLocator;
        _ingredientStore = ingredientStore;
        increasePercentage = configSettings.increasePercentage;
        _log = log;
    }

    public Ingredient updateValues(Ingredient ingredient){
        Recipe recipe = _recipeLocator.locateRecipe(ingredient.itemName);
        if(recipe == null) {
            _log.logDebug("No recipe found for: " + ingredient.itemName);
            return ingredient;
        }
        return calculateNewValues(recipe);
    }

    private Ingredient calculateNewValues(Recipe recipe) {
        ArrayList<RecipeIngredient> recipeIngredients = findIngredientsFor(recipe);

        Double newFoodValue = 0.0;
        Double newPrice = 0.0;
        _log.logDebug("Calculating new values for: " + recipe.output.item);

        for(int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = recipeIngredients.get(i);
            Ingredient ingredient = recipeIngredient.ingredient;
            _log.logDebug("Ingredient " + (i + 1) + " is " + ingredient.itemName + " with p: " + ingredient.price + " and fv: " + ingredient.foodValue);

            if(ingredient != null) {
                newPrice += calculateValue(recipeIngredient.count, ingredient.price);
                newFoodValue += calculateValue(recipeIngredient.count, ingredient.foodValue);
            }
        }
        _log.logDebug("New values of: " + recipe.output.item + " before output calculation are p: " + newPrice + " and fv: " + newFoodValue);

        Double outputCount = recipe.output.count;
        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        newPrice = roundTwoDecimalPlaces(newPrice / outputCount);
        newFoodValue = roundTwoDecimalPlaces(newFoodValue / outputCount);

        _log.logDebug("New values for: " + recipe.output.item + " are p: " + newPrice + " and fv: " + newFoodValue);

        return new Ingredient(recipe.output.item, newPrice, newFoodValue);
    }

    private ArrayList<RecipeIngredient> findIngredientsFor(Recipe recipe) {
        ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<RecipeIngredient>();
        for(int i = 0; i < recipe.input.length; i++) {
            ItemDescriptor input = recipe.input[i];
            String ingredientName = input.item;
            Double ingredientCount = input.count;

            Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
            if(ingredient == null) {
                ingredient = new Ingredient(ingredientName);
                _ingredientStore.updateIngredients(ingredientName, ingredient);
            }
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
        return (value * count) + (value * increasePercentage);
    }

    private Double roundTwoDecimalPlaces(Double val) {
        return (double)Math.round(val * 100)/100;
    }
}
