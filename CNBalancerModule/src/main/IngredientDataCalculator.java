package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.FileReaderWrapper;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.io.IReadFiles;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.locators.IngredientStore;
import com.colonolnutty.module.shareddata.locators.RecipeStore;
import com.colonolnutty.module.shareddata.locators.StatusEffectStore;
import com.colonolnutty.module.shareddata.models.*;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import main.collectors.EffectsCollector;
import main.collectors.FoodValueCollector;
import main.collectors.ICollector;
import main.collectors.PriceCollector;
import main.settings.BalancerSettings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:31 PM
 */
public class IngredientDataCalculator implements IReadFiles {
    private CNLog _log;
    private BalancerSettings _settings;
    private RecipeStore _recipeStore;
    private IngredientStore _ingredientStore;
    private JsonManipulator _manipulator;
    private IFileReader _fileReader;
    private StatusEffectStore _statusEffectStore;
    public static final String RECIPE_GROUP_DELIMITER = ":-} ";

    private HashMap<String, String> _friendlyGroupNames;

    public IngredientDataCalculator(CNLog log,
                                    BalancerSettings settings,
                                    RecipeStore recipeStore,
                                    IngredientStore ingredientStore,
                                    StatusEffectStore statusEffectStore,
                                    JsonManipulator manipulator){
        _log = log;
        _settings = settings;
        _recipeStore = recipeStore;
        _ingredientStore = ingredientStore;
        _statusEffectStore = statusEffectStore;
        _manipulator = manipulator;
        _fileReader = new FileReaderWrapper();
    }

    public Ingredient updateIngredient(Ingredient ingredient) throws IOException{
        if(_friendlyGroupNames == null) {
            _friendlyGroupNames = readFriendlyNames(_settings.friendlyNamesFilePath);
        }
        Recipe recipe = _recipeStore.locateRecipe(ingredient.getName());
        if(recipe == null) {
            _log.debug("No recipe found for: " + ingredient.getName());
            return ingredient;
        }
        return calculateNewValues(recipe);
    }

    private Ingredient calculateNewValues(Recipe recipe) {
        Ingredient newIngredient = balanceIngredient(recipe);
        _ingredientStore.updateIngredient(newIngredient);
        return _ingredientStore.getIngredient(newIngredient.getName());
    }

    public Ingredient balanceIngredient(Recipe recipe) {
        Double increasePercentage = _settings.increasePercentage;
        ArrayList<RecipeIngredient> recipeIngredients = findIngredientsFor(recipe);
        String outputName = recipe.output.item;
        boolean isRawFood = outputName.startsWith("raw");
        String subName = "Calculating \"" + outputName + "\" values";
        _log.startSubBundle(subName);
        _log.debug(subName);

        ArrayList<ICollector> collectors = new ArrayList<ICollector>();

        collectors.add(new PriceCollector(increasePercentage));
        collectors.add(new FoodValueCollector(increasePercentage));
        collectors.add(new EffectsCollector(_settings.enableEffectsUpdate, _settings.excludedEffects, _statusEffectStore, isRawFood, _log, increasePercentage));

        for(int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = recipeIngredients.get(i);
            Ingredient ingredient = recipeIngredient.ingredient;
            String subBundleMessage = "Ingredient " + (i + 1) + " name: " + ingredient.getName();
            _log.debug(subBundleMessage + " c: " + recipeIngredient.count + " p: " + ingredient.price + " fv: " + ingredient.foodValue, 4);
            _log.startSubBundle(subBundleMessage);
            _log.writeToBundle("count: " + recipeIngredient.count, "price: " + ingredient.price, "food value: " + ingredient.foodValue);

            if(ingredient == null) {
                continue;
            }
            for(ICollector collector : collectors) {
                collector.collectData(ingredient, recipeIngredient.count);
            }

            _log.endSubBundle();
        }

        _log.endSubBundle();

        Double outputCount = recipe.output.count;

        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }

        Ingredient newIngredient = new Ingredient(outputName);
        for(ICollector collector : collectors) {
            collector.applyData(newIngredient, outputCount);
        }
        Ingredient existingIngredient = _ingredientStore.getIngredient(outputName);
        newIngredient.description = createDescription(existingIngredient.description, recipe, _friendlyGroupNames);
        return newIngredient;
    }

    private ArrayList<RecipeIngredient> findIngredientsFor(Recipe recipe) {
        ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<RecipeIngredient>();
        for(int i = 0; i < recipe.input.length; i++) {
            ItemDescriptor input = recipe.input[i];
            String ingredientName = input.item;
            Double ingredientCount = input.count;

            Ingredient ingredient = _ingredientStore.getIngredient(ingredientName);
            RecipeIngredient recipeIngredient = new RecipeIngredient(ingredient, ingredientCount);
            recipeIngredients.add(recipeIngredient);
        }
        return recipeIngredients;
    }

    /**
     * Calculates using formula: (v * c) + (v * iP)
     * @param count (c) number of items
     * @param value (v) value of items
     * @param increasePercentage (iP) amount of value to add to the total
     * @return (v * c) + (v * iP)
     */
    public Double calculateValue(Double count, Double value, Double increasePercentage) {
        if(count == null || value == null || increasePercentage == null) {
            return 0.0;
        }
        if(count <= 0.0) {
            count = 1.0;
        }
        if(value < 0.0) {
            value = 0.0;
        }
        return (value * count) + (value * increasePercentage);
    }

    public String createDescription(String description, Recipe recipe, HashMap<String, String> friendlyGroupNames) {
        if(CNStringUtils.isNullOrWhitespace(description)) {
            return null;
        }
        String[] splitOnDelimiter = description.split(RECIPE_GROUP_DELIMITER);

        String existingDescriptionText = splitOnDelimiter[splitOnDelimiter.length - 1];

        ArrayList<String> recipeGroupNames = getRecipeGroupNames(recipe, friendlyGroupNames);
        if(recipeGroupNames == null || recipeGroupNames.isEmpty()) {
            return description;
        }

        String groupText = createMethodText(recipeGroupNames);

        return groupText + RECIPE_GROUP_DELIMITER + existingDescriptionText;
    }

    public String createMethodText(ArrayList<String> groupNames) {
        String methodsText = "";
        if(groupNames == null || groupNames.size() == 0) {
            return methodsText;
        }
        for(int i = 0; i < groupNames.size(); i++) {
            String recipeGroupName = groupNames.get(i);
            methodsText += "(" + recipeGroupName + ")";
        }
        return methodsText;
    }

    public ArrayList<String> getRecipeGroupNames(Recipe recipe, HashMap<String, String> friendlyGroupNames) {
        ArrayList<String> groupNames = new ArrayList<String>();
        if(recipe == null || recipe.groups == null || friendlyGroupNames == null) {
            return groupNames;
        }
        for(int i = 0; i < recipe.groups.length; i++) {
            String group = recipe.groups[i];
            if(friendlyGroupNames.containsKey(group)) {
                groupNames.add(friendlyGroupNames.get(group));
            }
        }
        return groupNames;
    }

    public HashMap<String, String> readFriendlyNames(String friendlyNamesPath) throws IOException {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        if(friendlyNamesPath == null) {
            return friendlyNames;
        }

        ArrayNode friendlyNamesNode = _fileReader.read(friendlyNamesPath, ArrayNode.class);
        if(friendlyNamesNode == null || friendlyNamesNode.size() == 0) {
            return friendlyNames;
        }

        for(JsonNode subNode : friendlyNamesNode) {
            if(!subNode.isArray()) {
                continue;
            }
            ArrayNode subArr = (ArrayNode) subNode;
            if(subArr.size() <= 1) {
                continue;
            }
            String name = subArr.get(0).asText();
            String friendlyName = subArr.get(1).asText();
            if(!friendlyNames.containsKey(name)) {
                friendlyNames.put(name, friendlyName);
            }
        }

        return friendlyNames;
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }
}
