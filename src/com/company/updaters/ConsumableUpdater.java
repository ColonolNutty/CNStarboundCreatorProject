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
public class ConsumableUpdater extends Updater {

    public ConsumableUpdater(DebugLog log, JsonManipulator manipulator, IngredientStore ingredientStore,
                             ValueCalculator valueCalculator) {
        super(log, manipulator, ingredientStore, valueCalculator);
    }

    @Override
    public void update(String filePath) {
        try {
            Ingredient ingredient = _manipulator.readIngredientVal(filePath);
            ingredient = _ingredientStore.getIngredient(ingredient.itemName);
            Ingredient updatedValues = _valueCalculator.updateValues(ingredient);
            if(updatedValues == null
                    || (ingredient.foodValue.equals(updatedValues.foodValue) && ingredient.price.equals(updatedValues.price))) {
                _log.logInfo("Skipping file: " + filePath);
                return;
            }
            ConsumableBase consumableBase = _manipulator.readConsumable(filePath);
            if(consumableBase == null
                    || (consumableBase.foodValue != null && consumableBase.foodValue.equals(updatedValues.foodValue)
                    && consumableBase.price != null && consumableBase.price.equals(updatedValues.price))) {
                _log.logInfo("Skipping file: " + filePath);
                return;
            }
            _log.logInfo("Updating file: " + filePath);
            consumableBase.foodValue = updatedValues.foodValue;
            consumableBase.price = updatedValues.price;
            _ingredientStore.updateIngredients(consumableBase.itemName, updatedValues);
            _manipulator.write(filePath, consumableBase);
        }
        catch(IOException e) {
            _log.logDebug("[IOE] Big Problem: " + e.getMessage());
        }
    }

    @Override
    public boolean canUpdate(String filePath) {
        return filePath.endsWith(".consumable");
    }
}
