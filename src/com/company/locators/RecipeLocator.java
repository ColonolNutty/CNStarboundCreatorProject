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
public class RecipeLocator {

    private Hashtable<String, Recipe> _recipes;
    private String[] _recipePaths;
    private JsonManipulator _manipulator;
    private ConfigSettings _settings;
    private DebugLog _log;
    private PatchLocator _patchLocator;

    public RecipeLocator(DebugLog log, ConfigSettings settings, JsonManipulator manipulator,
                         PatchLocator patchLocator) {
        _recipes = new Hashtable<String, Recipe>();
        _settings = settings;
        _recipePaths = settings.recipeLocations;
        _manipulator = manipulator;
        _log = log;
        _patchLocator = patchLocator;
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
        ArrayList<String> filePaths = findRecipes();
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if(!filePath.endsWith(".patch")) {
                String patchFile = _patchLocator.locatePatchFileFor(filePath, filePaths);
                addRecipe(filePath, patchFile);
            }
        }
    }

    private ArrayList<String> findRecipes() {
        ArrayList<java.lang.String> filePaths = new ArrayList<java.lang.String>();
        for(int i = 0; i < _recipePaths.length; i++) {
            java.lang.String recipePath = _recipePaths[i];
            File directory = new File(recipePath);
            ArrayList<String> filePathsFound = findRecipes(directory);
            filePaths.addAll(filePathsFound);
        }
        return filePaths;
    }

    private ArrayList<String> findRecipes(File directory) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            String filePath = file.getAbsolutePath();
            if (file.isFile() && isValidFile(filePath)) {
                filePaths.add(filePath);
            }
            else if (file.isDirectory()) {
                ArrayList<String> filePathsFound = findRecipes(file);
                filePaths.addAll(filePathsFound);
            }
        }
        return filePaths;
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

    public boolean isValidFile(String filePath) {
        File file = new File(filePath);
        return !file.getName().startsWith("obsolete") && (filePath.endsWith(".recipe") || filePath.endsWith(".recipe.patch"));
    }
}
