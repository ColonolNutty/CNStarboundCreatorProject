package com.colonolnutty.module.shareddata.locators;

import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.JsonPatchManipulator;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.IngredientProperty;
import com.colonolnutty.module.shareddata.utils.StopWatchTimer;

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
    private JsonManipulator _manipulator;
    private JsonPatchManipulator _patchManipulator;
    private PatchLocator _patchLocator;
    private FileLocator _fileLocator;
    private ArrayList<String> _fileLocations;
    private String[] _fileTypes;
    private StopWatchTimer _stopWatch;

    public IngredientStore(CNLog log,
                           JsonManipulator manipulator,
                           JsonPatchManipulator patchManipulator,
                           PatchLocator patchLocator,
                           FileLocator fileLocator,
                           ArrayList<String> fileLocations,
                           String[] fileTypes) {
        _ingredients = new Hashtable<String, Ingredient>();
        _log = log;
        _manipulator = manipulator;
        _patchManipulator = patchManipulator;
        _patchLocator = patchLocator;
        _fileLocator = fileLocator;
        _fileLocations = fileLocations;
        _stopWatch = new StopWatchTimer(log);
        _fileTypes = fileTypes;
        initializeIngredientStore();
    }

    public void overrideIngredients(Ingredient[] ingredients) {
        if(ingredients == null) {
            return;
        }
        _log.debug("Found overrides, overriding values");
        for(int i = 0; i < ingredients.length; i++) {
            Ingredient ingredient = ingredients[i];
            _log.debug("Overriding ingredient: " + ingredient.getIdentifier() + " with p: " + ingredient.getPrice() + " and fv: " + ingredient.getFoodValue());
            updateIngredient(ingredient, true);
        }
    }

    public Ingredient getIngredient(String itemName) {
        if(itemName == null) {
            return null;
        }
        initializeIngredientStore();
        if(_ingredients.containsKey(itemName)) {
            Ingredient found = _ingredients.get(itemName);
            if(found == null) {
                found = new Ingredient(itemName);
                _ingredients.put(itemName, found);
            }
            return found;
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

    private void initializeIngredientStore() {
        if(!_ingredients.isEmpty()) {
            return;
        }
        _stopWatch.reset();
        _stopWatch.start("loading ingredients from disk");
        _log.info("Loading ingredients from disk");
        String[] ingredientFileExtensions = _fileTypes;
        String[] ingredientPatchFileExt = new String[_fileTypes.length];
        for(int i = 0; i < _fileTypes.length; i++) {
            ingredientPatchFileExt[i] = _fileTypes[i] + ".patch";
        }
        ArrayList<String> ingredientPatchFiles = _fileLocator.getFilePathsByExtension(_fileLocations, ingredientPatchFileExt);
        ArrayList<String> filePaths = _fileLocator.getFilePathsByExtension(_fileLocations, ingredientFileExtensions);
        for(String filePath : filePaths) {
            if(filePath != null) {
                _log.debug("Located file path: " + filePath);
            }
            String patchFile = _patchLocator.locatePatchFileFor(filePath, ingredientPatchFiles);
            if(patchFile != null) {
                _log.debug("Located patch file: " + patchFile);
            }
            addIngredient(filePath, patchFile);
        }
        _stopWatch.stop();
        _stopWatch.logTime();
    }

    private void addIngredient(String filePath, String patchFilePath) {
        try {
            _log.debug("File found at: " + filePath);
            Ingredient ingredient = _manipulator.readIngredient(filePath);
            if(ingredient == null || !ingredient.hasName()) {
                return;
            }
            ingredient.filePath = filePath;
            ingredient.patchFile = patchFilePath;
            String ingredientName = ingredient.getName();
            if(!_ingredients.containsKey(ingredientName)) {
                Ingredient patchedIngredient = _patchManipulator.applyPatch(ingredient, patchFilePath, Ingredient.class);
                _log.debug("Pre-patch ingredient values: " + ingredient.getIdentifier() + " p: " + ingredient.getPrice() + " fv: " + ingredient.getFoodValue());
                if(patchedIngredient != null) {
                    patchedIngredient.filePath = filePath;
                    patchedIngredient.patchFile = patchFilePath;
                    _log.debug("Patch values: " + patchedIngredient.getIdentifier() + " p: " + patchedIngredient.getPrice() + " fv: " + patchedIngredient.getFoodValue());
                    updateIngredient(patchedIngredient, true);
                }
                else {
                    _log.debug("No patch ingredient");
                    updateIngredient(ingredient, false);
                }
            }
            else {
                updateIngredient(ingredient, false);
            }
        }
        catch(IOException e) {
            _log.error("{IOE] Reading ingredient at path: " + filePath, e);
        }
    }

    private void updateIngredient(Ingredient ingredient, boolean isOverride) {
        String ingredientName = ingredient.getName();
        String identifier = ingredient.getIdentifier();
        if(identifier == null) {
            _log.error("Ingredient found with no identifier(Name and File Paths), how did it get here?");
            return;
        }
        _log.debug("Attempting to update ingredient: " + ingredient.getIdentifier());
        if(!_ingredients.containsKey(ingredientName)) {
            _log.debug("No ingredient found, so adding: " + ingredientName + " p: " + ingredient.getPrice() + " fv: " + ingredient.getFoodValue());
            _ingredients.put(ingredientName, ingredient);
            return;
        }
        Ingredient existing = _ingredients.get(ingredientName);
        if(ingredient.hasFoodValue() && (isOverride || !existing.hasFoodValue())) {
            _log.debug("Overriding ingredient foodValue: " + existing.getName() + " with " + ingredient.getFoodValue());
            existing.update(IngredientProperty.FoodValue, ingredient.getFoodValue());
        }
        if(ingredient.hasPrice() && (isOverride || !existing.hasPrice())) {
            _log.debug("Overriding ingredient price: " + existing.getName() + " with " + ingredient.price);
            existing.update(IngredientProperty.Price, ingredient.getPrice());
        }
        if(ingredient.printable != null) {
            existing.printable = ingredient.printable;
        }
        if(ingredient.hasEffects() || isOverride) {
            existing.update(IngredientProperty.Effects, ingredient.getEffects());
        }
        if(ingredient.itemName != null) {
            existing.itemName = ingredient.itemName;
        }
        if(ingredient.objectName != null) {
            existing.objectName = ingredient.objectName;
        }
        if(ingredient.name != null) {
            existing.name = ingredient.name;
        }
        if(ingredient.projectileName != null) {
            existing.projectileName = ingredient.projectileName;
        }
        if(ingredient.hasDescription()) {
            existing.update(IngredientProperty.Description, ingredient.getDescription());
        }
        if(ingredient.inventoryIcon != null) {
            existing.inventoryIcon = ingredient.inventoryIcon;
        }
        if(ingredient.shortdescription != null) {
            existing.shortdescription = ingredient.shortdescription;
        }
        if(ingredient.interactData != null) {
            existing.interactData = ingredient.interactData;
        }
        if(ingredient.category != null) {
            existing.category = ingredient.category;
        }
        if(ingredient.filePath != null) {
            existing.filePath = ingredient.filePath;
        }
        if(ingredient.patchFile != null) {
            existing.patchFile = ingredient.patchFile;
        }
    }
}
