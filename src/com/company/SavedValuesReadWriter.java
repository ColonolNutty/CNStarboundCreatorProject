package com.company;

import com.company.locators.IngredientStore;
import com.company.models.ConfigSettings;
import com.company.models.SavedIngredientValues;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 3:21 PM
 */
public class SavedValuesReadWriter {
    private DebugLog _log;
    private ConfigSettings _settings;
    private String _storagePath;
    private JsonManipulator _manipulator;

    public SavedValuesReadWriter(DebugLog log,
                                 ConfigSettings settings,
                                 String storagepath,
                                 JsonManipulator manipulator) {
        _log = log;
        _settings = settings;
        _storagePath = storagepath;
        _manipulator = manipulator;
    }

    public IngredientStore readStore() {
        try {
            SavedIngredientValues savedIngredientValues = _manipulator.read(_storagePath, SavedIngredientValues.class);
            return new IngredientStore(_log, _settings, _manipulator, savedIngredientValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new IngredientStore(_log, _settings, _manipulator);
    }

    public void saveStore(IngredientStore store) {
        SavedIngredientValues savedIngredientValues = new SavedIngredientValues();
        savedIngredientValues.ingredients = store.getValues();
        _manipulator.write(_storagePath, savedIngredientValues);
    }
}
