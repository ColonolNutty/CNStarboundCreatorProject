package com.company;

import com.company.locators.IngredientLocator;
import com.company.locators.RecipeLocator;
import com.company.models.ConfigSettings;
import com.company.models.IngredientValues;
import com.company.models.Recipe;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:31 PM
 */
public class ValueCalculator {
    private Double increasePercentage = 0.05;
    private RecipeLocator _recipeLocator;
    private IngredientLocator _ingredientLocator;
    private DebugLog _log;

    public ValueCalculator(DebugLog log,
                           ConfigSettings configSettings,
                           RecipeLocator recipeLocator,
                           IngredientLocator ingredientLocator) {
        _recipeLocator = recipeLocator;
        _ingredientLocator = ingredientLocator;
        increasePercentage = configSettings.increasePercentage;
        _log = log;
    }

    public IngredientValues updateValues(IngredientValues ingredient){
        Recipe recipe = _recipeLocator.locateRecipe(ingredient.itemName);
        if(recipe == null) {
            _log.logDebug("No recipe found for: " + ingredient.itemName);
            return null;
        }
        IngredientValues newValues = calculateNewValues(recipe);
        if(newValues.foodValue != null && newValues.foodValue.equals(ingredient.foodValue)
                && newValues.price != null && newValues.price.equals(ingredient.price)) {
            return null;
        }
        _ingredientLocator.updateValue(ingredient.itemName, ingredient);
        return newValues;
    }

    private IngredientValues calculateNewValues(Recipe recipe) {
        IngredientValues newValues = new IngredientValues();
        newValues.itemName = recipe.output.item;
        Double newFoodValue = 0.0;
        Double newPrice = 0.0;
        for(int i = 0; i < recipe.input.length; i++) {
            ItemDescriptor input = recipe.input[i];
            String inputName = input.item;
            Double inputCount = input.count;

            IngredientValues values = _ingredientLocator.getValuesOf(inputName);
            if(values != null) {
                newPrice += calculateValue(inputCount, values.price);
                newFoodValue += calculateValue(inputCount, values.foodValue);
            }
        }
        Double outputCount = recipe.output.count;
        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        newFoodValue = newFoodValue / outputCount;
        newPrice = newPrice / outputCount;
        newValues.foodValue = (double)Math.round(newFoodValue * 100)/100;
        newValues.price = (double)Math.round(newPrice * 100)/100;
        return newValues;
    }

    private Double calculateValue(Double count, Double value) {
        if(count <= 0.0) {
            count = 1.0;
        }
        if(value == null) {
            return 0.0;
        }
        return (value * count) + (value * increasePercentage);
    }
}
