package com.company.balancer;

import com.company.CNUtils;
import com.company.JsonManipulator;
import com.company.CNLog;
import com.company.locators.FileLocator;
import com.company.locators.IngredientStore;
import com.company.models.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:38 PM
 */
public class FileUpdater {
    private CNLog _log;
    private ConfigSettings _settings;
    private IngredientDataCalculator _ingredientDataCalculator;
    private JsonManipulator _manipulator;
    private IngredientUpdater _ingredientUpdater;
    private IngredientStore _ingredientStore;
    private FileLocator _fileLocator;

    public FileUpdater(CNLog log,
                       ConfigSettings settings,
                       IngredientDataCalculator ingredientDataCalculator,
                       JsonManipulator manipulator,
                       IngredientUpdater ingredientUpdater,
                       IngredientStore ingredientStore,
                       FileLocator fileLocator) {
        _log = log;
        _settings = settings;
        _ingredientDataCalculator = ingredientDataCalculator;
        _manipulator = manipulator;
        _ingredientUpdater = ingredientUpdater;
        _ingredientStore = ingredientStore;
        _fileLocator = fileLocator;
    }

    public void updateValues() {
        String[] ingredientFileExts = new String[6];
        ingredientFileExts[0] = ".item";
        ingredientFileExts[1] = ".consumable";
        ingredientFileExts[2] = ".object";
        ingredientFileExts[3] = ".matitem";
        ingredientFileExts[4] = ".liquid";
        ingredientFileExts[5] = ".projectile";
        String currentDirectory = System.getProperty("user.dir");
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(ingredientFileExts);
        Hashtable<String, String> ingredientsToUpdate = new Hashtable<String, String>();
        for(int k = 0; k < _settings.numberOfPasses; k++) {
            String currentPass = "Beginning pass: " + (k + 1);
            _log.clearCurrentBundles();
            for (int i = 0; i < filePaths.size(); i++) {
                String filePath = filePaths.get(i);
                if(filePath.endsWith(".recipe") || filePath.endsWith(".patch")) {
                    continue;
                }
                String[] relativePathNames = startPathBundle(filePath, currentDirectory);
                _log.startSubBundle(currentPass);
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
                _log.endSubBundle();
                _log.endSubBundle();
                endPathBundle(relativePathNames);
            }
        }

        _log.clearCurrentBundles();

        if(ingredientsToUpdate.isEmpty()) {
            _log.info("No files to update");
            return;
        }
        _log.info("Finished passes, updating files");
        Enumeration<String> ingredientNames = ingredientsToUpdate.elements();
        while(ingredientNames.hasMoreElements()) {
            String ingredientName = ingredientNames.nextElement();
            Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
            if (ingredient == null) {
                continue;
            }
            verifyMinimumValues(ingredient);
            boolean isPatchFile = ingredient.patchFile != null;
            String filePath = isPatchFile ? ingredient.patchFile : ingredient.filePath;
            if(!CNUtils.fileStartsWith(filePath, _settings.locationsToUpdate)) {
                continue;
            }
            String[] relativePathNames = startPathBundle(filePath, currentDirectory);
            _log.startSubBundle("Update");

            if(isPatchFile) {
                _log.writeToAll("Attempting to update patch: " + ingredientName);
                _manipulator.writeAsPatch(ingredient);
            }
            else {
                _log.writeToAll("Attempting to update file: " + ingredientName);
                _manipulator.write(ingredient.filePath, ingredient);
            }

            _log.endSubBundle();
            _log.endSubBundle();
            endPathBundle(relativePathNames);
        }
    }

    private String[] startPathBundle(String fileName, String rootDir) {
        File file = new File(fileName);
        if(!_settings.enableTreeView) {
            _log.startSubBundle(file.getName());
            return null;
        }
        String fileNameParentDirectories = file.getParentFile().getAbsolutePath().substring(rootDir.length() + 1);
        String[] relativePathNames = fileNameParentDirectories.split("\\\\");
        for(String relativePathName : relativePathNames) {
            _log.startSubBundle(relativePathName);
        }
        _log.startSubBundle(file.getName());
        return relativePathNames;
    }

    private void endPathBundle(String[] pathNames) {
        if(!_settings.enableTreeView || pathNames == null) {
            _log.endSubBundle();
            return;
        }
        _log.endSubBundle();
        for (String pathName : pathNames) {
            _log.endSubBundle();
        }
    }

    private void verifyMinimumValues(Ingredient ingredient) {
        if(ingredient.foodValue != null && ingredient.foodValue < _settings.minimumFoodValue) {
            ingredient.foodValue = (double)_settings.minimumFoodValue;
        }
    }
}
