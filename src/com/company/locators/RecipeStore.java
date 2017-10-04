package com.company.locators;

import com.company.CNLog;
import com.company.JsonManipulator;
import com.company.StopWatchTimer;
import com.company.models.Recipe;

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
    private PatchLocator _patchLocator;
    private FileLocator _fileLocator;
    private StopWatchTimer _stopWatchTimer;

    public RecipeStore(CNLog log,
                       JsonManipulator manipulator,
                       PatchLocator patchLocator,
                       FileLocator fileLocator) {
        _recipes = new Hashtable<String, Recipe>();
        _log = log;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
        _fileLocator = fileLocator;
        _stopWatchTimer = new StopWatchTimer(_log);
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
        ArrayList<String> filePathPatches = _fileLocator.getFilePathsByExtension(".recipe.patch");
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(".recipe");
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
                    Recipe patchedRecipe = _manipulator.patch(recipe, patchFilePath, Recipe.class);
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
