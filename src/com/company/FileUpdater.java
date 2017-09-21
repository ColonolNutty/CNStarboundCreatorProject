package com.company;

import com.company.locators.FileLocator;
import com.company.locators.IngredientStore;
import com.company.models.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:38 PM
 */
public class FileUpdater {
    private DebugLog _log;
    private ConfigSettings _settings;
    private ValueCalculator _valueCalculator;
    private JsonManipulator _manipulator;
    private IngredientUpdater _ingredientUpdater;
    private IngredientStore _ingredientStore;
    private FileLocator _fileLocator;

    public FileUpdater(DebugLog log,
                       ConfigSettings settings,
                       ValueCalculator valueCalculator,
                       JsonManipulator manipulator,
                       IngredientUpdater ingredientUpdater,
                       IngredientStore ingredientStore,
                       FileLocator fileLocator) {
        _log = log;
        _settings = settings;
        _valueCalculator = valueCalculator;
        _manipulator = manipulator;
        _ingredientUpdater = ingredientUpdater;
        _ingredientStore = ingredientStore;
        _fileLocator = fileLocator;
    }

    public void updateValues() {
        ArrayList<String> filePaths = _fileLocator.getFilePaths();
        Hashtable<String, String> ingredientsToUpdate = new Hashtable<String, String>();
        for(int k = 0; k < _settings.numberOfPasses; k++) {
            _log.logInfo("Beginning pass: " + (k + 1), false);
            for (int i = 0; i < filePaths.size(); i++) {
                String filePath = filePaths.get(i);
                if(!filePath.endsWith(".recipe") && !filePath.endsWith(".patch")) {
                    String ingredientName = _ingredientUpdater.update(filePath);
                    //If ingredientName is null, it means the file doesn't need an update
                    if(ingredientName == null) {
                        if(ingredientsToUpdate.containsKey(filePath)) {
                            ingredientsToUpdate.remove(filePath);
                        }
                    }
                    else if (!ingredientsToUpdate.containsKey(filePath)) {
                        ingredientsToUpdate.put(filePath, ingredientName);
                    }
                }
            }
        }

        if(ingredientsToUpdate.isEmpty()) {
            _log.logInfo("No files to update", false);
            return;
        }
        _log.logInfo("Finished passes, updating files", false);
        Enumeration<String> ingredientNames = ingredientsToUpdate.elements();
        while(ingredientNames.hasMoreElements()) {
            String ingredientName = ingredientNames.nextElement();
            Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
            if (ingredient != null) {
                if(ingredient.patchFile != null) {
                    _log.logInfo("Attempting to update patch file: " + ingredient.getName(), false);
                    _manipulator.writeIngredientAsPatch(ingredient);
                }
                else if(!isIncludeLocation(ingredient.filePath)) {
                    _log.logInfo("Updating ingredient: " + ingredient.getName(), false);
                    _manipulator.write(ingredient.filePath, ingredient);
                }
            }
        }
    }

    private boolean isIncludeLocation(String filePath) {
        boolean isIncludeLocation = false;
        for(int i = 0; i < _settings.includeLocations.length; i++) {
            String location = _settings.includeLocations[i];
            File file = new File(location);
            if(filePath.startsWith(file.getAbsolutePath())) {
                isIncludeLocation = true;
                i = _settings.includeLocations.length;
            }
        }
        return isIncludeLocation;
    }
}
