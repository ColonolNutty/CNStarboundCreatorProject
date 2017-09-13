package com.company.updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientStore;
import com.company.models.UpdateDetails;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:23 AM
 */
public class ObjectUpdater extends Updater {

    public ObjectUpdater(DebugLog log, JsonManipulator manipulator, IngredientStore ingredientStore, ValueCalculator valueCalculator) {
        super(log, manipulator, ingredientStore, valueCalculator);
    }

    @Override
    public String update(String filePath) {
        return null;
    }

    @Override
    public boolean canUpdate(String filePath) {
        return filePath.endsWith(".object");
    }
}
