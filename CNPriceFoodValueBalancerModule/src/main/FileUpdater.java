package main;

import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.CNUtils;
import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.locators.IngredientStore;
import com.colonolnutty.module.shareddata.models.Ingredient;
import main.settings.BalancerSettings;

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
    private BalancerSettings _settings;
    private IngredientDataCalculator _ingredientDataCalculator;
    private JsonManipulator _manipulator;
    private IngredientUpdater _ingredientUpdater;
    private IngredientStore _ingredientStore;
    private FileLocator _fileLocator;
    private ArrayList<String> _fileLocations;

    public FileUpdater(CNLog log,
                       BalancerSettings settings,
                       IngredientDataCalculator ingredientDataCalculator,
                       JsonManipulator manipulator,
                       IngredientUpdater ingredientUpdater,
                       IngredientStore ingredientStore,
                       FileLocator fileLocator,
                       ArrayList<String> fileLocations) {
        _log = log;
        _settings = settings;
        _ingredientDataCalculator = ingredientDataCalculator;
        _manipulator = manipulator;
        _ingredientUpdater = ingredientUpdater;
        _ingredientStore = ingredientStore;
        _fileLocator = fileLocator;
        _fileLocations = fileLocations;
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
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(_fileLocations, ingredientFileExts);
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
