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
    private ArrayList<String> _includedFileTypes;

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
        _includedFileTypes = new ArrayList<String>();
        _includedFileTypes.add(".item");
        _includedFileTypes.add(".consumable");
        _includedFileTypes.add(".patch");
        _includedFileTypes.add(".object");
        _includedFileTypes.add(".matitem");
        _includedFileTypes.add(".liquid");
        _includedFileTypes.add(".projectile");
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
                if(!ingredientsToUpdate.containsKey(filePath) && !filePath.endsWith(".patch")) {
                    Ingredient ingredient = _ingredientStore.getIngredientWithFilePathAndPatch(filePath);
                    if(ingredient != null) {
                        ingredientsToUpdate.put(filePath, ingredient.getName());
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

    private ArrayList<String> getFileNames() {
        ArrayList<String> filePaths = new ArrayList<String>();
        for(int i = 0; i < _settings.locationsToUpdate.length; i++) {
            File directory = new File(_settings.locationsToUpdate[i]);
            ArrayList<String> subFilePaths = getFileNames(directory);
            filePaths.addAll(subFilePaths);
        }
        for(int i = 0; i < _settings.locationsToFakeUpdate.length; i++) {
            File directory = new File(_settings.locationsToFakeUpdate[i]);
            ArrayList<String> subFilePaths = getFileNames(directory);
            filePaths.addAll(subFilePaths);
        }
        return filePaths;
    }

    private ArrayList<String> getFileNames(File directory) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && isValidFileType(file.getName())){
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
            File file = new File(fakeLocation);
            _log.logDebug("Check for " + filePath + " to start with " + file.getAbsolutePath());
            if(filePath.startsWith(file.getAbsolutePath())) {
                isFakeLocation = true;
                i = _settings.locationsToFakeUpdate.length;
            }
        }
        return isFakeLocation;
    }

    private boolean isValidFileType(String fileName) {
        boolean included = false;
        for(int i = 0; i < _includedFileTypes.size(); i++){
            String fileType = _includedFileTypes.get(i);
            if(fileName.endsWith(fileType)) {
                included = true;
                i = _includedFileTypes.size();
            }
        }
        return included;
    }
}
