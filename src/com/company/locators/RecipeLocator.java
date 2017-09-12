package com.company.locators;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.models.ConfigSettings;
import com.company.models.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 1:50 PM
 */
public class RecipeLocator {

    private String[] _recipePaths;
    private JsonManipulator manip;
    private ConfigSettings _settings;
    private DebugLog _log;
    private Hashtable<String, Recipe> _recipes;

    public RecipeLocator(DebugLog log, ConfigSettings settings, JsonManipulator manipulator) {
        _settings = settings;
        _recipePaths = settings.recipeLocations;
        manip = manipulator;
        _log = log;
        _recipes = new Hashtable<String, Recipe>();
    }

    public Recipe locateRecipe(String itemName) {
        if(_recipes.isEmpty()) {
            setupRecipes();
        }
        if(_recipes.containsKey(itemName)) {
            return _recipes.get(itemName);
        }
        return null;
    }

    private void setupRecipes() {
        for(int i = 0; i < _recipePaths.length; i++) {
            String recipePath = _recipePaths[i];
            File directory = new File(recipePath);
            findRecipes(directory);
        }
    }

    private void findRecipes(File directory) {
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && file.getAbsolutePath().endsWith(".recipe")){
                addRecipe(file.getAbsolutePath());
            }
            else if (file.isDirectory()){
                findRecipes(file);
            }
        }
    }

    private void addRecipe(String filePath) {
        try {
            Recipe recipe = manip.readRecipe(filePath);
            if(recipe.output != null && recipe.output.item != null) {
                String itemName = recipe.output.item;
                if(!_recipes.containsKey(itemName)) {
                    _recipes.put(itemName, recipe);
                }
            }
        }
        catch(IOException e) {
            _log.logDebug("{IOE] Problem encountered reading recipe at path: " + filePath + "\n" + e.getMessage());
        }
    }
}
