package com.company.locators;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.models.ConfigSettings;
import com.company.models.Ingredient;
import com.company.models.IngredientOverrides;

import java.io.FileNotFoundException;
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
    private DebugLog _log;
    private ConfigSettings _settings;
    private JsonManipulator _manipulator;
    private PatchLocator _patchLocator;
    private FileLocator _fileLocator;

    public IngredientStore(DebugLog log,
                           ConfigSettings settings,
                           JsonManipulator manipulator,
                           PatchLocator patchLocator,
                           FileLocator fileLocator) {
        _ingredients = new Hashtable<String, Ingredient>();
        _log = log;
        _settings = settings;
        _manipulator = manipulator;
        _patchLocator = patchLocator;
        _fileLocator = fileLocator;
        initializeIngredientStore();
    }

    public void overrideIngredients(Ingredient[] ingredients) {
        if(ingredients == null) {
            return;
        }
        _log.logDebug("Found overrides, overriding values");
        for(int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            _log.logDebug("Overriding ingredient: " + ingredient.getName() + " with p: " + ingredient.price + " and fv: " + ingredient.foodValue);
            updateIngredient(ingredient, true);
        }
    }

    public Ingredient getIngredient(String itemName) {
        if(_ingredients.isEmpty()) {
            initializeIngredientStore();
        }
        if(_ingredients.containsKey(itemName)) {
            return _ingredients.get(itemName);
        }
        else {
            Ingredient ingredient = new Ingredient(itemName);
            _ingredients.put(itemName, ingredient);
            return ingredient;
        }
    }

    public Ingredient getIngredientWithFilePath(String filePath) {
        initializeIngredientStore();
        Ingredient foundIngredient = null;
        Enumeration<Ingredient> ingredients = _ingredients.elements();
        while(foundIngredient == null && ingredients.hasMoreElements()) {
            Ingredient ingredient = ingredients.nextElement();
            if(ingredient.filePath != null && ingredient.filePath.equals(filePath)) {
                foundIngredient = ingredient;
            }
        }
        return foundIngredient;
    }

    public void updateIngredient(Ingredient ingredient) {
        if(ingredient == null) {
            return;
        }
        updateIngredient(ingredient, true);
    }

    private void initializeIngredientStore() {
        if(!_ingredients.isEmpty()) {
            return;
        }
        _log.logInfo("Loading ingredients from disk");
        ArrayList<String> filePaths = _fileLocator.getFilePaths();
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if(!filePath.endsWith(".recipe") && !filePath.endsWith(".patch")) {
                String patchFile = _patchLocator.locatePatchFileFor(filePath, filePaths);
                addIngredient(filePath, patchFile);
            }
        }
        initializeIngredientOverrides();
    }

    private void addIngredient(String filePath, String patchFilePath) {
        try {
            _log.logDebug("File found at: " + filePath);
            Ingredient ingredient = _manipulator.readIngredient(filePath);
            if(ingredient != null && ingredient.hasName()) {
                ingredient.filePath = filePath;
                ingredient.patchFile = patchFilePath;
                String ingredientName = ingredient.getName();
                if(!_ingredients.containsKey(ingredientName)) {
                    Ingredient patchedIngredient = _manipulator.patch(ingredient, patchFilePath, Ingredient.class);
                    _log.logDebug("Pre-patch ingredient values: " + ingredientName + " p: " + ingredient.price + " fv: " + ingredient.foodValue);
                    if(patchedIngredient != null) {
                        patchedIngredient.filePath = filePath;
                        patchedIngredient.patchFile = patchFilePath;
                        updateIngredient(patchedIngredient, true);
                    }
                    else {
                        updateIngredient(ingredient, false);
                    }
                }
                else {
                    updateIngredient(ingredient, false);
                }
            }
        }
        catch(IOException e) {
            _log.logDebug("{IOE] Problem encountered reading recipe at path: " + filePath + "\n" + e.getMessage());
        }
    }

    private void updateIngredient(Ingredient ingredient, boolean isOverride) {
        String ingredientName = ingredient.getName();
        if(_ingredients.containsKey(ingredientName)) {
            Ingredient existing = _ingredients.get(ingredientName);
            if(ingredient.foodValue != null && (isOverride || existing.foodValue == null)) {
                _log.logDebug("Overriding ingredient foodValue: " + existing.getName() + " with " + ingredient.foodValue);
                existing.foodValue = ingredient.foodValue;
            }
            if(ingredient.price != null && (isOverride || existing.price == null)) {
                _log.logDebug("Overriding ingredient price: " + existing.getName() + " with " + ingredient.price);
                existing.price = ingredient.price;
            }
            _log.logDebug("New values: " + existing.getName() + " p: " + existing.price + " fv: " + existing.foodValue);
            existing.itemName = ingredient.itemName;
            existing.objectName = ingredient.objectName;
            existing.name = ingredient.name;
            existing.projectileName = ingredient.projectileName;
            existing.description = ingredient.description;
            existing.inventoryIcon = ingredient.inventoryIcon;
            existing.shortdescription = ingredient.shortdescription;
            existing.stages = ingredient.stages;
            existing.interactData = ingredient.interactData;
            _ingredients.put(existing.getName(), existing);
        }
        else {
            _log.logDebug("No ingredient found, so adding: " + ingredientName + " p: " + ingredient.price + " fv: " + ingredient.foodValue);
            _ingredients.put(ingredientName, ingredient);
        }
    }

    private void initializeIngredientOverrides() {
        try {
            IngredientOverrides replacementIngredientValues = _manipulator.read(_settings.ingredientOverridePath, IngredientOverrides.class);
            overrideIngredients(replacementIngredientValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            _log.logError(e);
        }
    }
}
