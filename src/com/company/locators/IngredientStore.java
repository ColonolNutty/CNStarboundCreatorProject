package com.company.locators;

import com.company.DebugLog;
import com.company.JsonManipulator;
import com.company.StopWatchTimer;
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
    private StopWatchTimer _stopWatch;

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
        _stopWatch = new StopWatchTimer(log);
        initializeIngredientStore();
    }

    public void overrideIngredients(Ingredient[] ingredients) {
        if(ingredients == null) {
            return;
        }
        _log.logDebug("Found overrides, overriding values", true);
        for(int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            _log.logDebug("Overriding ingredient: " + ingredient.getName() + " with p: " + ingredient.price + " and fv: " + ingredient.foodValue, true);
            updateIngredient(ingredient, true);
        }
    }

    public Ingredient getIngredient(String itemName) {
        if(itemName == null) {
            return null;
        }
        initializeIngredientStore();
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
        if(filePath == null) {
            return null;
        }
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
        _stopWatch.reset();
        _stopWatch.start("loading ingredients from disk");
        _log.logInfo("Loading ingredients from disk", false);
        ArrayList<String> filePaths = _fileLocator.getFilePaths();
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if(!filePath.endsWith(".recipe") && !filePath.endsWith(".patch")) {
                String patchFile = _patchLocator.locatePatchFileFor(filePath, filePaths);
                addIngredient(filePath, patchFile);
            }
        }
        _stopWatch.stop();
        _stopWatch.logTime();
        initializeIngredientOverrides();
    }

    private void addIngredient(String filePath, String patchFilePath) {
        try {
            _log.logDebug("File found at: " + filePath, true);
            Ingredient ingredient = _manipulator.readIngredient(filePath);
            if(ingredient != null && ingredient.hasName()) {
                ingredient.filePath = filePath;
                ingredient.patchFile = patchFilePath;
                String ingredientName = ingredient.getName();
                if(!_ingredients.containsKey(ingredientName)) {
                    Ingredient patchedIngredient = _manipulator.patch(ingredient, patchFilePath, Ingredient.class);
                    _log.logDebug("Pre-patch ingredient values: " + ingredientName + " p: " + ingredient.price + " fv: " + ingredient.foodValue, true);
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
            _log.logError("{IOE] Reading recipe at path: " + filePath, e);
        }
    }

    private void updateIngredient(Ingredient ingredient, boolean isOverride) {
        String ingredientName = ingredient.getName();
        if(!_ingredients.containsKey(ingredientName)) {
            _log.logDebug("No ingredient found, so adding: " + ingredientName + " p: " + ingredient.price + " fv: " + ingredient.foodValue, true);
            _ingredients.put(ingredientName, ingredient);
            return;
        }
        Ingredient existing = _ingredients.get(ingredientName);
        if(ingredient.foodValue != null && (isOverride || existing.foodValue == null)) {
            _log.logDebug("Overriding ingredient foodValue: " + existing.getName() + " with " + ingredient.foodValue, true);
            existing.foodValue = ingredient.foodValue;
        }
        if(ingredient.price != null && (isOverride || existing.price == null)) {
            _log.logDebug("Overriding ingredient price: " + existing.getName() + " with " + ingredient.price, true);
            existing.price = ingredient.price;
        }
        if(ingredient.hasEffects() && (isOverride || !existing.hasEffects())) {
            existing.effects = ingredient.effects;
        }
        existing.itemName = ingredient.itemName;
        existing.objectName = ingredient.objectName;
        existing.name = ingredient.name;
        existing.projectileName = ingredient.projectileName;
        existing.description = ingredient.description;
        existing.inventoryIcon = ingredient.inventoryIcon;
        existing.shortdescription = ingredient.shortdescription;
        existing.stages = ingredient.stages;
        existing.interactData = ingredient.interactData;
        if(ingredient.filePath != null) {
            existing.filePath = ingredient.filePath;
        }
        if(ingredient.patchFile != null) {
            existing.patchFile = ingredient.patchFile;
        }
        _ingredients.put(existing.getName(), existing);
    }

    private void initializeIngredientOverrides() {
        _stopWatch.reset();
        try {
            _log.logInfo("Loading ingredient overrides", false);
            _stopWatch.start("loading ingredient overrides");
            IngredientOverrides replacementIngredientValues = _manipulator.read(_settings.ingredientOverridePath, IngredientOverrides.class);
            overrideIngredients(replacementIngredientValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            _log.logError(e);
        }
        _stopWatch.stop();
        _stopWatch.logTime();
    }
}
