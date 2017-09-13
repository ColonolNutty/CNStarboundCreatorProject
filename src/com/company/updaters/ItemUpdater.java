package com.company.updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientStore;
import com.company.models.ConsumableBase;
import com.company.models.Ingredient;
import com.company.models.ItemBase;

import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:30 AM
 */
public class ItemUpdater extends Updater {

    public ItemUpdater(DebugLog log, JsonManipulator manipulator, IngredientStore ingredientStore, ValueCalculator valueCalculator) {
        super(log, manipulator, ingredientStore, valueCalculator);
    }

    @Override
    public void update(String filePath) {
        try {
            Ingredient ingredient = _manipulator.readIngredientVal(filePath);
            ingredient = _ingredientStore.getIngredient(ingredient.itemName);
            Ingredient updatedValues = _valueCalculator.updateValues(ingredient);
            if((ingredient.foodValue == null || ingredient.foodValue.equals(updatedValues.foodValue))
                    && (ingredient.price == null || ingredient.price.equals(updatedValues.price))) {
                _log.logInfo("Skipping file: " + filePath);
                return;
            }
            ItemBase base = _manipulator.read(filePath, ItemBase.class);
            if(base == null
                    || (base.foodValue != null && base.foodValue.equals(updatedValues.foodValue)
                    && base.price != null && base.price.equals(updatedValues.price))) {
                _log.logInfo("Skipping file: " + filePath);
                return;
            }
            _log.logInfo("Updating file: " + filePath);
            base.foodValue = updatedValues.foodValue;
            base.price = updatedValues.price;
            _ingredientStore.updateIngredients(base.itemName, updatedValues);
            _manipulator.write(filePath, base);
        }
        catch(IOException e) {
            _log.logDebug("[IOE] Big Problem: " + e.getMessage());
        }
    }

    @Override
    public boolean canUpdate(String filePath) {
        return filePath.endsWith(".item");
    }
}
