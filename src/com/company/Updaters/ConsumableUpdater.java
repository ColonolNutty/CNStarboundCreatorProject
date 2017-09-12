package com.company.Updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientLocator;
import com.company.models.ConsumableBase;
import com.company.models.IngredientValues;

import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:23 AM
 */
public class ConsumableUpdater extends Updater {
    private IngredientLocator _ingredientLocator;
    private ValueCalculator _valueCalculator;

    public ConsumableUpdater(DebugLog log, JsonManipulator manipulator, IngredientLocator ingredientLocator,
                             ValueCalculator valueCalculator) {
        super(log, manipulator);
        _ingredientLocator = ingredientLocator;
        _valueCalculator = valueCalculator;
    }

    @Override
    public void update(String filePath) {
        try {
            IngredientValues values = _manipulator.readIngredientVal(filePath);
            IngredientValues updatedValues = _valueCalculator.updateValues(values);
            if(updatedValues == null
                    || (values.foodValue.equals(updatedValues.foodValue) && values.price.equals(updatedValues.price))) {
                return;
            }
            ConsumableBase consumableBase = _manipulator.readConsumable(filePath);
            if(consumableBase == null
                    || (consumableBase.foodValue != null && consumableBase.foodValue.equals(updatedValues.foodValue)
                    && consumableBase.price != null && consumableBase.price.equals(updatedValues.price))) {
                return;
            }
            _log.logInfo("Updating file: " + filePath);
            consumableBase.foodValue = updatedValues.foodValue;
            consumableBase.price = updatedValues.price;
            _ingredientLocator.updateValue(consumableBase.itemName, updatedValues);
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
