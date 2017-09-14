package com.company;

import com.company.locators.IngredientStore;
import com.company.locators.PatchLocator;
import com.company.models.ConfigSettings;
import com.company.models.IngredientOverrides;

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
    private PatchLocator _patchLocator;

    public InitialValuesReadWriter(DebugLog log,
                                   ConfigSettings settings,
                                   JsonManipulator manipulator,
                                   PatchLocator patchLocator) {
        _log = log;
        _settings = settings;
        _storagePath = settings.ingredientValueOutputStoragePath;
        _overridePath = settings.ingredientValueOverridePath;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
    }

    public IngredientStore read() {
        IngredientStore ingredientStore = new IngredientStore(_log, _settings, _manipulator, _patchLocator);
        try {
            IngredientOverrides replacementIngredientValues = _manipulator.read(_overridePath, IngredientOverrides.class);
            ingredientStore.overrideIngredients(replacementIngredientValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ingredientStore;
    }
}
