package com.company.locators;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.models.ConfigSettings;
import com.company.models.Ingredient;
import com.company.models.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 1:28 PM
 */
public class IngredientStore {
    private Hashtable<String, Ingredient> _ingredients;
    private JsonManipulator _manipulator;
    private ConfigSettings _settings;
    private DebugLog _log;
    private PatchLocator _patchLocator;

    public IngredientStore(DebugLog log, ConfigSettings settings, JsonManipulator manipulator, PatchLocator patchLocator) {
        _ingredients = new Hashtable<String, Ingredient>();
        _log = log;
        _settings = settings;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
        storeIngredients();
    }

    public void updateIngredients(Ingredient[] ingredients) {
        for(int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            if(_ingredients.containsKey(ingredient.itemName)) {
                Ingredient existing = _ingredients.get(ingredient.itemName);
                if(existing.foodValue == null || existing.foodValue <= 0.0) {
                    if(ingredient.foodValue != null) {
                        existing.foodValue = ingredient.foodValue;
                    }
                }
                if(existing.price == null || existing.price <= 0.0) {
                    if(ingredient.price != null) {
                        existing.price = ingredient.price;
                    }
                }
                _ingredients.put(existing.itemName, existing);
            }
        }
    }

    public void overrideIngredients(Ingredient[] ingredients) {
        for(int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            if(_ingredients.containsKey(ingredient.itemName)) {
                _log.logInfo("Overriding ingredient: " + ingredient.itemName + " with p: " + ingredient.price + " and fv: " + ingredient.foodValue);
                Ingredient existing = _ingredients.get(ingredient.itemName);
                if(ingredient.foodValue != null) {
                    existing.foodValue = ingredient.foodValue;
                }
                if(ingredient.price != null) {
                    existing.price = ingredient.price;
                }
                _ingredients.put(existing.itemName, existing);
            }
            else {
                _ingredients.put(ingredient.itemName, ingredient);
            }
        }
    }

    public Ingredient getIngredient(String itemName) {
        if(_ingredients.isEmpty()) {
            storeIngredients();
        }
        if(_ingredients.containsKey(itemName)) {
            return _ingredients.get(itemName);
        }
        return null;
    }

    public Ingredient[] getIngredients() {
        Enumeration<Ingredient> ingredientElements = _ingredients.elements();
        Ingredient[] ingredientsArray = new Ingredient[_ingredients.size()];
        int i = 0;
        while(ingredientElements.hasMoreElements()) {
            ingredientsArray[i] = ingredientElements.nextElement();
            i += 1;
        }
        return ingredientsArray;
    }

    public void updateIngredients(String itemName, Ingredient ingredient) {
        if(_ingredients.containsKey(itemName)) {
            _ingredients.remove(itemName);
        }
        _ingredients.put(itemName, ingredient);
    }

    private void storeIngredients() {
        ArrayList<String> filePaths = getIngredientPaths();
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if(!filePath.endsWith(".patch")) {
                String patchFile = _patchLocator.locatePatchFileFor(filePath, filePaths);
                addIngredient(filePath, patchFile);
            }
        }
    }

    private ArrayList<String> getIngredientPaths() {
        ArrayList<String> filePaths = new ArrayList<String>();
        for(int i = 0; i < _settings.ingredientLocations.length; i++) {
            String path = _settings.ingredientLocations[i];
            File directory = new File(path);
            ArrayList<String> foundFilePaths = findIngredients(directory);
            filePaths.addAll(foundFilePaths);
        }
        return filePaths;
    }

    private ArrayList<String> findIngredients(File directory) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && isFileIncluded(file.getName())){
                filePaths.add(file.getAbsolutePath());
            }
            else if (file.isDirectory()){
                ArrayList<String> foundFilePaths = findIngredients(file);
                filePaths.addAll(foundFilePaths);
            }
        }
        return filePaths;
    }

    private void addIngredient(String filePath, String patchFilePath) {
        try {
            Ingredient ingredient = _manipulator.readIngredientVal(filePath);
            if(ingredient != null && ingredient.itemName != null) {
                String itemName = ingredient.itemName;
                if(!_ingredients.containsKey(itemName)) {
                    Ingredient patchedIngredient = _manipulator.patch(ingredient, patchFilePath, Ingredient.class);
                    if(patchedIngredient != null) {
                        _ingredients.put(itemName, patchedIngredient);
                    }
                    else {
                        _ingredients.put(itemName, ingredient);
                    }
                }
            }
        }
        catch(IOException e) {
            _log.logDebug("{IOE] Problem encountered reading recipe at path: " + filePath + "\n" + e.getMessage());
        }
    }

    private boolean isFileIncluded(String fileName) {
        boolean isIncluded = false;
        String[] exclusionList = _settings.ingredientExtensionInclusionList;
        for(int i = 0; i < exclusionList.length; i++) {
            if(fileName.endsWith(".patch") || fileName.endsWith(exclusionList[i])) {
                isIncluded = true;
                i = exclusionList.length;
            }
        }
        return isIncluded;
    }
}
