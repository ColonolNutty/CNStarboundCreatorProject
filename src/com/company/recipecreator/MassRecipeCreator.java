package com.company.recipecreator;

import com.company.CNLog;
import com.company.JsonManipulator;
import com.company.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 9:43 AM
 */
public class MassRecipeCreator {

    private CNLog _log;
    private RecipeCreatorSettings _settings;
    private JsonManipulator _manipulator;
    private RecipeCrafter _recipeCrafter;
    private IngredientCrafter _ingredientCrafter;
    private ArrayList<CNCrafter> _crafters;

    public MassRecipeCreator(RecipeCreatorSettings settings,
                             CNLog log,
                             JsonManipulator manipulator) {
        _settings = settings;
        _log = log;
        _manipulator = manipulator;
        _crafters = new ArrayList<CNCrafter>();
        _crafters.add(new RecipeCrafter(log, settings, manipulator));
        _crafters.add(new IngredientCrafter(log, settings, manipulator));
    }

    public void create() {
        if(_settings == null) {
            _log.error("No configuration file found, exiting.");
            return;
        }

        IngredientListItem[] ingredientList = read(_settings.ingredientListFile, IngredientListItem[].class);
        if(ingredientList == null) {
            return;
        }

        ArrayList<String> outputNames = createFromTemplate(ingredientList);
        writeToConfigurationFile(outputNames);
    }

    private void writeToConfigurationFile(ArrayList<String> names) {
        String recipeConfigFile = _settings.recipeConfigFileName;
        RecipesConfig recipesConfig = read(recipeConfigFile, RecipesConfig.class);
        if(recipesConfig == null) {
            recipesConfig = new RecipesConfig();
            recipesConfig.possibleOutput = _manipulator.createArrayNode();
        }
        for(String name : names) {
            if(!contains(recipesConfig.possibleOutput, name)) {
                recipesConfig.possibleOutput.add(name);
            }
        }
        _manipulator.writeNew(recipeConfigFile, recipesConfig);
    }

    private boolean contains(ArrayNode node, String name) {
        boolean contains = false;
        for(int i = 0; i < node.size(); i++){
            if(node.get(i).asText().equals(name)) {
                contains = true;
                i = node.size();
            }
        }
        return contains;
    }

    public ArrayList<String> createFromTemplate(IngredientListItem[] ingredientNames) {
        String prefix = _settings.filePrefix;
        String suffix = _settings.fileSuffix;
        int countPer = _settings.countPerIngredient;
        int numberPerRecipe = _settings.numberOfIngredientsPerRecipe;

        ArrayList<String> newIngredients = new ArrayList<String>();

        for(int i = 1; i <= numberPerRecipe; i++) {
            for(int j = 0; j < ingredientNames.length; j++) {
                IngredientListItem currentIngredient = ingredientNames[j];
                ArrayList<IngredientListItem> ingredients = new ArrayList<IngredientListItem>();
                _log.debug("Using ingredient as start: " + currentIngredient);
                ingredients.add(currentIngredient);
                for(int k = j + 1; k < ingredientNames.length && k <= (j + i); k++) {
                    IngredientListItem nextIngredient = ingredientNames[k];
                    _log.debug("Including ingredient: " + nextIngredient);
                    ingredients.add(nextIngredient);
                }
                String outputName = prefix;
                for(IngredientListItem ingred : ingredients) {
                    outputName += ingred.shortName;
                }
                outputName += suffix;
                for(CNCrafter crafter : _crafters) {
                    crafter.craft(outputName, ingredients, countPer);
                }
                newIngredients.add(outputName);
            }
        }
        return newIngredients;
    }

    private <T> T read(String path, Class<T> classOfT){
        try {
            return _manipulator.read(path, classOfT);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to read: " + path, e);
        }
        return null;
    }
}
