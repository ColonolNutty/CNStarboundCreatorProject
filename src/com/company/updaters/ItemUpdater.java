package com.company.updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientStore;

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

    }

    @Override
    public boolean canUpdate(String filePath) {
        return filePath.endsWith(".item");
    }
}
