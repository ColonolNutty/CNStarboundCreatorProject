package main;

import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.JsonPatchManipulator;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.utils.CNFileUtils;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.locators.IngredientStore;
import com.colonolnutty.module.shareddata.ui.ConfirmationController;
import com.colonolnutty.module.shareddata.ui.ProgressController;
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
    private JsonManipulator _manipulator;
    private JsonPatchManipulator _patchManipulator;
    private IngredientUpdater _ingredientUpdater;
    private IngredientStore _ingredientStore;
    private FileLocator _fileLocator;
    private ArrayList<String> _fileLocations;
    private ProgressController _progressController;

    public FileUpdater(CNLog log,
                       BalancerSettings settings,
                       JsonManipulator manipulator,
                       JsonPatchManipulator patchManipulator,
                       IngredientUpdater ingredientUpdater,
                       IngredientStore ingredientStore,
                       FileLocator fileLocator,
                       ArrayList<String> fileLocations,
                       ProgressController progressController) {
        _log = log;
        _settings = settings;
        _manipulator = manipulator;
        _patchManipulator = patchManipulator;
        _ingredientUpdater = ingredientUpdater;
        _ingredientStore = ingredientStore;
        _fileLocator = fileLocator;
        _fileLocations = fileLocations;
        _progressController = progressController;
    }

    public void updateValues(String[] fileTypesToUpdate) {
        String[] ingredientFileExts = fileTypesToUpdate;
        String currentDirectory = System.getProperty("user.dir");
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(_fileLocations, ingredientFileExts);

        int totalIterations = filePaths.size() * _settings.numberOfPasses;
        boolean shouldContinue = ConfirmationController.getConfirmation("Total number of iterations (Larger numbers will take awhile): " + totalIterations + ", continue?");
        if(!shouldContinue) {
            _log.debug("User chose to not continue, aborting balance");
            return;
        }

        _progressController.setMaximum(totalIterations);
        Hashtable<String, String> ingredientsToUpdate = new Hashtable<String, String>();
        for(int k = 0; k < _settings.numberOfPasses; k++) {
            String currentPass = "Beginning pass: " + (k + 1);
            _log.clearCurrentBundles();
            for (int i = 0; i < filePaths.size(); i++) {
                String filePath = filePaths.get(i);
                if(filePath.endsWith(".recipe") || filePath.endsWith(".applyPatch")) {
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
                _progressController.add(1);
            }
        }

        _log.clearCurrentBundles();

        if(ingredientsToUpdate.isEmpty()) {
            _log.info("No files to update");
            return;
        }

        int totalToUpdate = ingredientsToUpdate.size();
        boolean shouldUpdateIngredients = ConfirmationController.getConfirmation("Number of files to update: " + totalToUpdate + ", continue?");
        if(!shouldUpdateIngredients) {
            _log.debug("User chose to abort, aborting balancer");
            return;
        }
        _progressController.reset();
        _progressController.setMaximum(totalToUpdate);
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
            if(!CNFileUtils.fileStartsWith(filePath, _settings.locationsToUpdate)) {
                _progressController.add(1);
                continue;
            }
            String[] relativePathNames = startPathBundle(filePath, currentDirectory);
            _log.startSubBundle("Update");
            ForceSetProperties.forceSet(ingredient);

            if(isPatchFile) {
                _log.writeToAll("Attempting to update applyPatch: " + ingredientName);
                _patchManipulator.write(ingredient.patchFile, ingredient);
            }
            else {
                _log.writeToAll("Attempting to update file: " + ingredientName);
                _manipulator.write(ingredient.filePath, ingredient);
            }

            _log.endSubBundle();
            _log.endSubBundle();
            endPathBundle(relativePathNames);
            _progressController.add(1);
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
