package com.company;

import com.company.locators.IngredientStore;
import com.company.models.Ingredient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:25 AM
 */
public class IngredientUpdater {
    protected DebugLog _log;
    protected JsonManipulator _manipulator;
    protected IngredientStore _ingredientStore;
    protected IngredientDataCalculator _ingredientDataCalculator;
    protected ArrayList<String> _fileTypesIgnoreFoodValues;

    public IngredientUpdater(DebugLog log,
                             JsonManipulator manipulator,
                             IngredientStore ingredientStore,
                             IngredientDataCalculator ingredientDataCalculator) {
        _log = log;
        _manipulator = manipulator;
        _ingredientStore = ingredientStore;
        _ingredientDataCalculator = ingredientDataCalculator;
        _fileTypesIgnoreFoodValues = new ArrayList<String>();
        _fileTypesIgnoreFoodValues.add(".item");
        _fileTypesIgnoreFoodValues.add(".object");
        _fileTypesIgnoreFoodValues.add(".projectile");
        _fileTypesIgnoreFoodValues.add(".matitem");
        _fileTypesIgnoreFoodValues.add(".liquid");
    }

    public String update(String ingredientFilePath) {
        try {
            File ingredientFile = new File(ingredientFilePath);
            _log.logDebug("Attempting to update: " + ingredientFile.getName(), true);
            Ingredient ingredient = _ingredientStore.getIngredientWithFilePath(ingredientFilePath);
            if(ingredient == null) {
                _log.logDebug("No ingredient found in store for: " + ingredientFilePath, true);
                return null;
            }
            Ingredient updatedIngredient = _ingredientDataCalculator.updateIngredient(ingredient);
            Ingredient originalIngredient = _manipulator.readIngredient(ingredientFilePath);
            if(ingredientsAreEqual(originalIngredient, updatedIngredient)) {
                _log.logDebug("    Skipping, values were the same as the ingredient on disk: " + ingredientFile.getName(), true);
                return null;
            }
            return ingredient.getName();
        }
        catch(IOException e) {
            _log.logError("[IOE] While attempting to update: " + ingredientFilePath, e);
        }
        return null;
    }

    private boolean ingredientsAreEqual(Ingredient one, Ingredient two) {
        if(one == null || two == null) {
            return false;
        }
        if(one.filePath != null && CNUtils.fileEndsWith(one.filePath, _fileTypesIgnoreFoodValues)) {
            _log.logDebug("Comparing using only price: " + one.getName(), true);
            return one.priceEquals(two);
        }
        if(two.filePath != null && CNUtils.fileEndsWith(two.filePath, _fileTypesIgnoreFoodValues)) {
            _log.logDebug("Comparing using only price: " + one.getName(), true);
            return one.priceEquals(two);
        }
        boolean shouldCheckEffects = (one.filePath != null && one.filePath.endsWith(".consumable")) || (two.filePath != null && two.filePath.endsWith(".consumable"));
        if(shouldCheckEffects && !one.effectsAreEqual(two.effects)) {
            return false;
        }
        _log.logDebug("Comparing using both price and foodValue: " + one.getName(), true);
        return one.equals(two);
    }
}
