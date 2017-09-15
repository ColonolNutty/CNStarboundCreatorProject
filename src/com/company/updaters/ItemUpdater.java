package com.company.updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientStore;
import com.company.models.ConsumableBase;
import com.company.models.Ingredient;

import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:23 AM
 */
public class ItemUpdater extends Updater {

    public ItemUpdater(DebugLog log, JsonManipulator manipulator, IngredientStore ingredientStore,
                       ValueCalculator valueCalculator) {
        super(log, manipulator, ingredientStore, valueCalculator);
    }

    @Override
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
            if((updatedIngredient.foodValue == null || updatedIngredient.foodValue.equals(ingredient.foodValue))
                    && (updatedIngredient.price == null || updatedIngredient.price.equals(ingredient.price))) {
                return null;
            }
            ConsumableBase base = _manipulator.readConsumable(filePath);
            if(base == null
                    || ((updatedIngredient.price == null || updatedIngredient.price.equals(base.price))
                    && (updatedIngredient.foodValue == null || updatedIngredient.foodValue.equals(base.foodValue)))) {
                return null;
            }
            _ingredientStore.updateIngredient(updatedIngredient);
            if(updatedIngredient.price == null || updatedIngredient.price.equals(base.price)) {
                return null;
            }
            return ingredient.getName();
        }
        catch(IOException e) {
            _log.logDebug("[IOE] Big Problem: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean canUpdate(String filePath) {
        return false;
    }
}
