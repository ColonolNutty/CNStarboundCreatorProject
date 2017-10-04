package com.company.locators;

import com.company.CNLog;
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
    private CNLog _log;
    private ConfigSettings _settings;
    private JsonManipulator _manipulator;
    private PatchLocator _patchLocator;
    private FileLocator _fileLocator;
    private StopWatchTimer _stopWatch;

    public IngredientStore(CNLog log,
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
        _log.debug("Found overrides, overriding values");
        for(int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            _log.debug("Overriding ingredient: " + ingredient.getName() + " with p: " + ingredient.price + " and fv: " + ingredient.foodValue);
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
        _log.info("Loading ingredients from disk");
        String[] ingredientFileExtensions = new String[6];
        ingredientFileExtensions[0] = ".item";
        ingredientFileExtensions[1] = ".consumable";
        ingredientFileExtensions[2] = ".object";
        ingredientFileExtensions[3] = ".matitem";
        ingredientFileExtensions[4] = ".liquid";
        ingredientFileExtensions[5] = ".projectile";
        String[] ingredientPatchFileExt = new String[6];
        ingredientPatchFileExt[0] = ".item.patch";
        ingredientPatchFileExt[1] = ".consumable.patch";
        ingredientPatchFileExt[2] = ".object.patch";
        ingredientPatchFileExt[3] = ".matitem.patch";
        ingredientPatchFileExt[4] = ".liquid.patch";
        ingredientPatchFileExt[5] = ".projectile.patch";
        ArrayList<String> ingredientPatchFiles = _fileLocator.getFilePathsByExtension(ingredientPatchFileExt);
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(ingredientFileExtensions);
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
                String patchFile = _patchLocator.locatePatchFileFor(filePath, ingredientPatchFiles);
                addIngredient(filePath, patchFile);
        }
        _stopWatch.stop();
        _stopWatch.logTime();
        initializeIngredientOverrides();
    }

    private void addIngredient(String filePath, String patchFilePath) {
        try {
            _log.debug("File found at: " + filePath);
            Ingredient ingredient = _manipulator.readIngredient(filePath);
            if(ingredient != null && ingredient.hasName()) {
                ingredient.filePath = filePath;
                ingredient.patchFile = patchFilePath;
                String ingredientName = ingredient.getName();
                if(!_ingredients.containsKey(ingredientName)) {
                    Ingredient patchedIngredient = _manipulator.patch(ingredient, patchFilePath, Ingredient.class);
                    _log.debug("Pre-patch ingredient values: " + ingredientName + " p: " + ingredient.price + " fv: " + ingredient.foodValue);
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
            _log.error("{IOE] Reading recipe at path: " + filePath, e);
        }
    }

    private void updateIngredient(Ingredient ingredient, boolean isOverride) {
        String ingredientName = ingredient.getName();
        if(!_ingredients.containsKey(ingredientName)) {
            _log.debug("No ingredient found, so adding: " + ingredientName + " p: " + ingredient.price + " fv: " + ingredient.foodValue);
            _ingredients.put(ingredientName, ingredient);
            return;
        }
        Ingredient existing = _ingredients.get(ingredientName);
        if(ingredient.hasFoodValue() && (isOverride || !existing.hasFoodValue())) {
            _log.debug("Overriding ingredient foodValue: " + existing.getName() + " with " + ingredient.foodValue);
            existing.foodValue = ingredient.foodValue;
        }
        if(ingredient.hasPrice() && (isOverride || !existing.hasPrice())) {
            _log.debug("Overriding ingredient price: " + existing.getName() + " with " + ingredient.price);
            existing.price = ingredient.price;
        }
        if(ingredient.effects != null && (_settings.enableEffectsUpdate || existing.hasPatchFile())) {
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
            _log.info("Loading ingredient overrides");
            _stopWatch.start("loading ingredient overrides");
            IngredientOverrides replacementIngredientValues = _manipulator.read(_settings.ingredientOverridePath, IngredientOverrides.class);
            overrideIngredients(replacementIngredientValues.ingredients);
        }
        catch(FileNotFoundException e) { }
        catch (IOException e) {
            _log.error(e);
        }
        _stopWatch.stop();
        _stopWatch.logTime();
    }
}
