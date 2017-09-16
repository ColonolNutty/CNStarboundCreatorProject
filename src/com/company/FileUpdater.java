package com.company;

import com.company.locators.FileLocator;
import com.company.locators.IngredientStore;
import com.company.models.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

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
            _log.logInfo("Beginning pass: " + (k + 1));
            for (int i = 0; i < filePaths.size(); i++) {
                String filePath = filePaths.get(i);
                if(!filePath.endsWith(".recipe")) {
                    String ingredientName = _ingredientUpdater.update(filePath);
                    if (ingredientName != null && !ingredientsToUpdate.containsKey(filePath)) {
                        ingredientsToUpdate.put(filePath, ingredientName);
                    }
                    if (!ingredientsToUpdate.containsKey(filePath) && !filePath.endsWith(".patch")) {
                        Ingredient ingredient = _ingredientStore.getIngredientWithFilePathAndPatch(filePath);
                        if (ingredient != null) {
                            ingredientsToUpdate.put(filePath, ingredient.getName());
                        }
                    }
                }
            }
        }
        _log.logInfo("Finished passes, updating files");
        for (int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if (ingredientsToUpdate.containsKey(filePath)) {
                String ingredientName = ingredientsToUpdate.get(filePath);
                Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
                if (ingredient != null) {
                    if (isFakeLocation(ingredient.filePath) || ingredient.patchFile != null) {
                        _log.logInfo("Updating ingredient as patch file: " + ingredient.getName());
                        _manipulator.writeIngredientAsPatch(ingredient);
                    }
                    else {
                        _log.logInfo("Updating ingredient: " + ingredient.getName());
                        _manipulator.write(filePath, ingredient);
                    }
                }
            }
        }
    }

    private boolean isFakeLocation(String filePath) {
        boolean isFakeLocation = false;
        for(int i = 0; i < _settings.includeLocations.length; i++) {
            String fakeLocation = _settings.includeLocations[i];
            File file = new File(fakeLocation);
            _log.logDebug("Check for " + filePath + " to start with " + file.getAbsolutePath());
            if(filePath.startsWith(file.getAbsolutePath())) {
                isFakeLocation = true;
                i = _settings.includeLocations.length;
            }
        }
        return isFakeLocation;
    }
}
