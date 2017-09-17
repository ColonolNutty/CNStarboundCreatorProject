package com.company.locators;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.models.ConfigSettings;
import com.company.models.Recipe;

import java.io.File;
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
    private DebugLog _log;
    private JsonManipulator _manipulator;
    private PatchLocator _patchLocator;
    private FileLocator _fileLocator;

    public RecipeStore(DebugLog log,
                       JsonManipulator manipulator,
                       PatchLocator patchLocator,
                       FileLocator fileLocator) {
        _recipes = new Hashtable<String, Recipe>();
        _log = log;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
        _fileLocator = fileLocator;
        setupRecipes();
    }

    public Recipe locateRecipe(String itemName) {
        if(_recipes.containsKey(itemName)) {
            return _recipes.get(itemName);
        }
        return null;
    }

    private void setupRecipes() {
        _log.logInfo("Locating recipes");
        ArrayList<String> filePaths = _fileLocator.getFilePaths();
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if(filePath.endsWith(".recipe")) {
                String patchFile = _patchLocator.locatePatchFileFor(filePath, filePaths);
                addRecipe(filePath, patchFile);
            }
        }
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
            _log.logDebug("{IOE] Problem encountered reading recipe at path: " + filePath + "\n" + e.getMessage());
        }
    }
}
