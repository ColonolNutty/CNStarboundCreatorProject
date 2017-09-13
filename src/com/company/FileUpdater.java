package com.company;

import com.company.locators.IngredientStore;
import com.company.updaters.Updater;
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
    private ArrayList<Updater> _updaters;
    private IngredientStore _ingredientStore;

    public FileUpdater(DebugLog log,
                       ConfigSettings settings,
                       ValueCalculator valueCalculator,
                       JsonManipulator manipulator,
                       ArrayList<Updater> updaters,
                       IngredientStore ingredientStore) {
        _log = log;
        _settings = settings;
        _valueCalculator = valueCalculator;
        _manipulator = manipulator;
        _updaters = updaters;
        _ingredientStore = ingredientStore;
    }

    public void updateValues() {
        ArrayList<String> filePaths = getFileNames();
        Hashtable<String, String> ingredientsToUpdate = new Hashtable<String, String>();
        for(int k = 0; k < _settings.numberOfPasses; k++) {
            _log.logInfo("Beginning pass: " + (k + 1));
            for (int i = 0; i < filePaths.size(); i++) {
                String filePath = filePaths.get(i);
                for (int j = 0; j < _updaters.size(); j++) {
                    Updater updater = _updaters.get(j);
                    if (updater.canUpdate(filePath)) {
                        String ingredientName = updater.update(filePath);
                        if(ingredientName != null && !ingredientsToUpdate.containsKey(filePath)) {
                            ingredientsToUpdate.put(filePath, ingredientName);
                        }
                        j = _updaters.size();
                    }
                }
            }
        }
        _log.logInfo("Finished passes, updating files");
        for (int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if(!isFakeLocation(filePath)) {
                if (ingredientsToUpdate.containsKey(filePath)) {
                    String ingredientName = ingredientsToUpdate.get(filePath);
                    Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
                    if (ingredient != null) {
                        _log.logInfo("Updating ingredient: " + ingredient.itemName);
                        _manipulator.write(filePath, ingredient);
                    }
                } else {
                    _log.logInfo("Skipping file: " + filePath);
                }
            }
        }
    }

    private ArrayList<String> getFileNames() {
        ArrayList<String> filePaths = new ArrayList<String>();
        for(int i = 0; i < _settings.locationsToUpdate.length; i++) {
            File directory = new File(_settings.locationsToUpdate[i]);
            ArrayList<String> subFilePaths = getFileNames(directory);
            for(int j = 0; j < subFilePaths.size(); j++) {
                filePaths.add(subFilePaths.get(j));
            }
        }
        for(int i = 0; i < _settings.locationsToFakeUpdate.length; i++) {
            File directory = new File(_settings.locationsToUpdate[i]);
            ArrayList<String> subFilePaths = getFileNames(directory);
            for(int j = 0; j < subFilePaths.size(); j++) {
                filePaths.add(subFilePaths.get(j));
            }
        }
        return filePaths;
    }

    private ArrayList<String> getFileNames(File directory) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
                filePaths.add(file.getAbsolutePath());
            }
            else if (file.isDirectory()){
                ArrayList<String> subPaths = getFileNames(file);
                for(int i = 0; i < subPaths.size(); i++) {
                    filePaths.add(subPaths.get(i));
                }
            }
        }
        return filePaths;
    }

    private boolean isFakeLocation(String filePath) {
        boolean isFakeLocation = false;
        for(int i = 0; i < _settings.locationsToFakeUpdate.length; i++) {
            String fakeLocation = _settings.locationsToFakeUpdate[i];
            if(filePath.startsWith(fakeLocation)) {
                isFakeLocation = true;
                i = _settings.locationsToFakeUpdate.length;
            }
        }
        return isFakeLocation;
    }
}
