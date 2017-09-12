package com.company.updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.ValueCalculator;
import com.company.locators.IngredientStore;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:25 AM
 */
public abstract class Updater {
    public abstract void update(String filePath);
    public abstract boolean canUpdate(String filePath);
    protected DebugLog _log;
    protected JsonManipulator _manipulator;
    protected IngredientStore _ingredientStore;
    protected ValueCalculator _valueCalculator;

    public Updater(DebugLog log, JsonManipulator manipulator, IngredientStore ingredientStore,
                   ValueCalculator valueCalculator) {
        _log = log;
        _manipulator = manipulator;
        _ingredientStore = ingredientStore;
        _valueCalculator = valueCalculator;
    }
}
