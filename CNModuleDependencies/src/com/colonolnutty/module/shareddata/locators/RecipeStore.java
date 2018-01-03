package com.colonolnutty.module.shareddata.locators;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.JsonPatchManipulator;
import com.colonolnutty.module.shareddata.utils.StopWatchTimer;
import com.colonolnutty.module.shareddata.models.Recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 1:50 PM
 */
public class RecipeStore {

    private Hashtable<String, Recipe> _recipes;
    private CNLog _log;
    private JsonManipulator _manipulator;
    private JsonPatchManipulator _patchManipulator;
    private PatchLocator _patchLocator;
    private FileLocator _fileLocator;
    private ArrayList<String> _fileLocations;
    private StopWatchTimer _stopWatchTimer;

    public RecipeStore(CNLog log,
                       JsonManipulator manipulator,
                       JsonPatchManipulator patchManipulator,
                       PatchLocator patchLocator,
                       FileLocator fileLocator,
                       ArrayList<String> fileLocations) {
        _log = log;
        _manipulator = manipulator;
        _patchManipulator = patchManipulator;
        _patchLocator = patchLocator;
        _fileLocator = fileLocator;
        _fileLocations = fileLocations;
        _stopWatchTimer = new StopWatchTimer(_log);
        _recipes = new Hashtable<String, Recipe>();
        setupRecipes();
    }

    public Recipe locateRecipe(String itemName) {
        if(_recipes.containsKey(itemName)) {
            return _recipes.get(itemName);
        }
        return null;
    }

    private void setupRecipes() {
        _log.info("Locating recipes");
        _stopWatchTimer.start("locating recipes");
        ArrayList<String> filePathPatches = _fileLocator.getFilePathsByExtension(_fileLocations, ".recipe.patch");
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(_fileLocations, ".recipe");
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            String patchFile = _patchLocator.locatePatchFileFor(filePath, filePathPatches);
            addRecipe(filePath, patchFile);
        }
        _stopWatchTimer.stop();
        _stopWatchTimer.logTime();
    }

    private void addRecipe(String filePath, String patchFilePath) {
        try {
            Recipe recipe = _manipulator.readRecipe(filePath);
            if(recipe.output != null && recipe.output.item != null) {
                String itemName = recipe.output.item;
                if(!_recipes.containsKey(itemName)) {
                    Recipe patchedRecipe = _patchManipulator.applyPatch(recipe, patchFilePath, Recipe.class);
                    if(patchedRecipe != null) {
                        _recipes.put(itemName, patchedRecipe);
                    }
                    else {
                        _recipes.put(itemName, recipe);
                    }
                }
            }
        }
        catch(IOException e) {
            _log.error("{IOE] Adding recipe at path: " + filePath, e);
        }
    }
}
