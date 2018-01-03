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
import com.colonolnutty.module.shareddata.utils.CNMathUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.settings.BalancerSettings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:31 PM
 */
public class IngredientDataCalculator implements IReadFiles, IRequireNodeProvider {
    private CNLog _log;
    private BalancerSettings _settings;
    private RecipeStore _recipeStore;
    private IngredientStore _ingredientStore;
    private JsonManipulator _manipulator;
    private IFileReader _fileReader;
    private NodeProvider _nodeProvider;
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
        _nodeProvider = new NodeProvider();
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
        Double increasePercentage = _settings.increasePercentage;
        ArrayList<RecipeIngredient> recipeIngredients = findIngredientsFor(recipe);

        Double newFoodValue = 0.0;
        Double newPrice = 0.0;

        String outputName = recipe.output.item;
        boolean isRawFood = outputName.startsWith("raw");
        String subName = "Calculating \"" + outputName + "\" values";
        _log.startSubBundle(subName);
        _log.debug(subName);
        Hashtable<String, Integer> effectValues = new Hashtable<String, Integer>();

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
            newPrice += calculateValue(recipeIngredient.count, ingredient.price, increasePercentage);
            newFoodValue += calculateValue(recipeIngredient.count, ingredient.foodValue, increasePercentage);

            if(!_settings.enableEffectsUpdate || !ingredient.hasEffects()) {
                _log.endSubBundle();
                continue;
            }

            Hashtable<String, Integer> ingredientEffects = getEffects(ingredient, isRawFood);
            Enumeration<String> subEffectNames = ingredientEffects.keys();
            while(subEffectNames.hasMoreElements()) {
                String subEffectName = subEffectNames.nextElement();
                Integer subEffectDuration = ingredientEffects.get(subEffectName);

                if(!effectValues.containsKey(subEffectName)) {
                    effectValues.put(subEffectName, subEffectDuration);
                }
                else {
                    int existingValue = effectValues.get(subEffectName);
                    int durationAddition = (int)(subEffectDuration * recipeIngredient.count);
                    effectValues.put(subEffectName, existingValue + durationAddition);
                }
            }

            _log.endSubBundle();
        }

        _log.endSubBundle();

        Double outputCount = recipe.output.count;
        ArrayNode combined = null;
        if(_settings.enableEffectsUpdate) {
            ArrayNode combinedEffects = toEffectsArrayNode(outputName, effectValues, outputCount);
            combined = _nodeProvider.createArrayNode();
            combined.add(combinedEffects);
        }

        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }
        newPrice = CNMathUtils.roundTwoDecimalPlaces(newPrice / outputCount);
        newFoodValue = CNMathUtils.roundTwoDecimalPlaces(newFoodValue / outputCount);

        _log.debug("After calculations, the new values for: " + outputName + " are p: " + newPrice + " and fv: " + newFoodValue);

        Ingredient newIngredient = new Ingredient(outputName, newPrice, newFoodValue, combined);
        Ingredient existingIngredient = _ingredientStore.getIngredient(outputName);
        newIngredient.description = createDescription(existingIngredient.description, recipe);
        _ingredientStore.updateIngredient(newIngredient);
        return _ingredientStore.getIngredient(outputName);
    }

    public Hashtable<String, Integer> getEffects(Ingredient ingredient, boolean isRawFood) {
        Hashtable<String, Integer> effectValues = new Hashtable<String, Integer>();
        for(JsonNode baseNode : ingredient.effects) {
            if(!baseNode.isArray()) {
                continue;
            }
            for(JsonNode subNode : baseNode) {
                if(subNode == null) {
                    continue;
                }

                String effectName = null;
                if(CNJsonUtils.isValueType(subNode)) {
                    effectName = subNode.asText();
                }
                else if(subNode.has("effect")) {
                    effectName = subNode.get("effect").asText();
                }

                if(CNStringUtils.isNullOrWhitespace(effectName)) {
                    _log.debug("Effect with no name found on ingredient: " + ingredient.getName(), 4);
                    continue;
                }

                int defaultDuration = _statusEffectStore.getDefaultStatusEffectDuration(effectName);
                int effectDuration = defaultDuration;
                if(subNode.has("duration")) {
                    effectDuration = subNode.get("duration").asInt(defaultDuration);
                }
                if(effectDuration <= 0) {
                    continue;
                }

                boolean isFoodPoisonOnRawFood = effectName.equals("foodpoison")
                        && isRawFood;
                if(!isFoodPoisonOnRawFood && CNStringUtils.contains(effectName, _settings.excludedEffects)) {
                    continue;
                }

                if(!effectValues.containsKey(effectName)) {
                    effectValues.put(effectName, effectDuration);
                }
                else {
                    int existingValue = effectValues.get(effectName);
                    effectValues.put(effectName, existingValue + effectDuration);
                }
                _log.debug(effectName + " with duration: " + effectDuration, 4);
            }
        }
        return effectValues;
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

    public String createDescription(String description, Recipe recipe) {
        if(CNStringUtils.isNullOrWhitespace(description)) {
            return null;
        }
        String[] splitOnDelimiter = description.split(RECIPE_GROUP_DELIMITER);

        String existingDescriptionText = splitOnDelimiter[splitOnDelimiter.length - 1];

        ArrayList<String> recipeGroupNames = getRecipeGroupNames(recipe, _friendlyGroupNames);
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
        if(recipe == null || recipe.groups == null) {
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

    public ArrayNode toEffectsArrayNode(String ingredientName, Hashtable<String, Integer> effects, Double outputCount) {
        ArrayNode arrayNode = _nodeProvider.createArrayNode();
        if(effects.isEmpty()) {
            return arrayNode;
        }
        _log.startSubBundle("New effects for " + ingredientName);

        Enumeration<String> effectKeys = effects.keys();
        while(effectKeys.hasMoreElements()) {
            String effectName = effectKeys.nextElement();
            int effectDuration = (int)(effects.get(effectName)/outputCount);
            if(effectDuration == 0) {
                continue;
            }
            ObjectNode objNode = _nodeProvider.createObjectNode();
            objNode.put("effect", effectName);
            objNode.put("duration", effectDuration);
            _log.writeToBundle("Effect name: \"" + effectName + "\", duration: " + effectDuration);
            arrayNode.add(objNode);
        }
        _log.endSubBundle();
        return arrayNode;
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }

    @Override
    public void setNodeProvider(NodeProvider nodeProvider) {
        _nodeProvider = nodeProvider;
    }
}
