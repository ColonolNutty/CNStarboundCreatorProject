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
            Ingredient ingredient = _manipulator.readIngredientVal(filePath);
            ingredient = _ingredientStore.getIngredient(ingredient.itemName);
            Ingredient updatedValues = _valueCalculator.updateValues(ingredient);
            if((updatedValues.foodValue == null || updatedValues.foodValue.equals(ingredient.foodValue))
                    && (updatedValues.price == null || updatedValues.price.equals(ingredient.price))) {
                return null;
            }
            ConsumableBase base = _manipulator.readConsumable(filePath);
            if(base == null
                    || ((updatedValues.price == null || updatedValues.price.equals(base.price))
                    && (updatedValues.foodValue == null || updatedValues.foodValue.equals(base.foodValue)))) {
                return null;
            }
            _ingredientStore.updateIngredients(base.itemName, updatedValues);
            if(updatedValues.price == null || updatedValues.price.equals(base.price)) {
                return null;
            }
            return ingredient.itemName;
        }
        catch(IOException e) {
            _log.logDebug("[IOE] Big Problem: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean canUpdate(String filePath) {
        return filePath.endsWith(".item");
    }
}
