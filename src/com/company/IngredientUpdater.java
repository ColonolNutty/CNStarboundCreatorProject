package com.company;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientStore;
import com.company.models.Ingredient;
import com.company.models.UpdateDetails;

import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:25 AM
 */
public class IngredientUpdater {
    protected DebugLog _log;
    protected JsonManipulator _manipulator;
    protected IngredientStore _ingredientStore;
    protected ValueCalculator _valueCalculator;

    public IngredientUpdater(DebugLog log,
                             JsonManipulator manipulator,
                             IngredientStore ingredientStore,
                             ValueCalculator valueCalculator) {
        _log = log;
        _manipulator = manipulator;
        _ingredientStore = ingredientStore;
        _valueCalculator = valueCalculator;
    }

    public String update(String filePath) {
        try {
            _log.logDebug("Attempting to update: " + filePath);
            Ingredient ingredient = _manipulator.readIngredient(filePath);
            ingredient = _ingredientStore.getIngredient(ingredient.getName());
            if(ingredient == null) {
                _log.logDebug("No ingredient found in store for: " + filePath);
                return null;
            }
            Ingredient updatedIngredient = _valueCalculator.updateValues(ingredient);
            if(ingredient.equals(updatedIngredient)) {
                _log.logDebug("1. Values were the same, so no update: " + filePath);
                return null;
            }
            Ingredient base = _manipulator.readIngredient(filePath);
            if(base == null || base.equals(updatedIngredient)) {
                if(base == null) {
                    _log.logDebug("No base value found: " + filePath);
                }
                else {
                    _log.logDebug("2. Values were the same, so no update: " + filePath);
                }
                return null;
            }
            _ingredientStore.updateIngredient(updatedIngredient);
            return ingredient.getName();
        }
        catch(IOException e) {
            _log.logDebug("[IOE] Big Problem: " + e.getMessage());
        }
        return null;
    }
}
