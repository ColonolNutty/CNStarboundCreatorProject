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
import main.collectors.*;
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
public class IngredientDataCalculator {
    private CNLog _log;
    private BalancerSettings _settings;
    private RecipeStore _recipeStore;
    private IngredientStore _ingredientStore;
    private JsonManipulator _manipulator;
    private StatusEffectStore _statusEffectStore;

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
    }

    public boolean updateIngredient(Ingredient ingredient) throws IOException{
        Recipe recipe = _recipeStore.locateRecipe(ingredient.getName());
        if(recipe == null) {
            _log.debug("No recipe found for: " + ingredient.getName());
            return false;
        }
        return balanceIngredient(recipe);
    }

    public boolean balanceIngredient(Recipe recipe) {
        ArrayList<RecipeIngredient> recipeIngredients = findIngredientsFor(recipe);
        String outputName = recipe.output.item;
        String subName = "Calculating \"" + outputName + "\" values";
        _log.startSubBundle(subName);
        _log.debug(subName);

        ArrayList<ICollector> collectors = new ArrayList<ICollector>();

        collectors.add(new PriceCollector(_settings));
        collectors.add(new FoodValueCollector(_settings));
        collectors.add(new DescriptionCollector(_settings, _log));
        collectors.add(new EffectsCollector(_settings, _log, _statusEffectStore));

        for(int i = 0; i < recipeIngredients.size(); i++) {
            RecipeIngredient recipeIngredient = recipeIngredients.get(i);
            Ingredient inputIngredient = recipeIngredient.ingredient;
            String subBundleMessage = "Ingredient " + (i + 1) + " name: " + inputIngredient.getName();
            _log.debug(subBundleMessage + " c: " + recipeIngredient.count + " p: " + inputIngredient.getPrice() + " fv: " + inputIngredient.getFoodValue(), 4);
            _log.startSubBundle(subBundleMessage);
            _log.writeToBundle("count: " + recipeIngredient.count, "price: " + inputIngredient.getPrice(), "food value: " + inputIngredient.getFoodValue());

            if(inputIngredient == null) {
                continue;
            }
            for(ICollector collector : collectors) {
                collector.collectData(inputIngredient, recipeIngredient.count, recipe);
            }

            _log.endSubBundle();
        }

        _log.endSubBundle();

        Double outputCount = recipe.output.count;

        if(outputCount <= 0.0) {
            outputCount = 1.0;
        }

        //TODO: This will be what determines whether an ingredient needs an update or not
        boolean needsUpdate = false;
        Ingredient newIngredient = _ingredientStore.getIngredient(outputName);
        for(ICollector collector : collectors) {
            if(collector.applyData(newIngredient, outputCount)) {
                needsUpdate = true;
                _log.debug(collector.getDescriptionOfUpdate(newIngredient));
            }
        }
        _log.debug("Recipe Output \"" + outputName + "\" with output count: " + outputCount + " with final price: " + newIngredient.getPrice() + " foodValue: " + newIngredient.getFoodValue(), 4);
        return needsUpdate;
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

}
