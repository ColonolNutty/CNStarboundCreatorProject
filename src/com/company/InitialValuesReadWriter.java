package com.company;

import com.company.locators.IngredientStore;
import com.company.models.ConfigSettings;
import com.company.models.SavedIngredientValues;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 3:21 PM
 */
public class InitialValuesReadWriter {
    private DebugLog _log;
    private ConfigSettings _settings;
    private String _storagePath;
    private String _overridePath;
    private JsonManipulator _manipulator;

    public InitialValuesReadWriter(DebugLog log,
                                   ConfigSettings settings,
                                   JsonManipulator manipulator) {
        _log = log;
        _settings = settings;
        _storagePath = settings.ingredientValueOutputStoragePath;
        _overridePath = settings.ingredientValueOverridePath;
        _manipulator = manipulator;
    }

    public IngredientStore read() {
        IngredientStore ingredientStore = new IngredientStore(_log, _settings, _manipulator);
        try {
            SavedIngredientValues savedIngredientValues = _manipulator.read(_storagePath, SavedIngredientValues.class);
            ingredientStore.updateIngredients(savedIngredientValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            SavedIngredientValues replacementValues = _manipulator.read(_overridePath, SavedIngredientValues.class);
            ingredientStore.overrideIngredients(replacementValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ingredientStore;
    }

    public void save(IngredientStore store) {
        SavedIngredientValues savedIngredientValues = new SavedIngredientValues();
        savedIngredientValues.ingredients = store.getIngredients();
        _manipulator.write(_storagePath, savedIngredientValues);
    }
}
