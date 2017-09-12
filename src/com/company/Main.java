package com.company;

import com.company.updaters.*;
import com.company.locators.IngredientStore;
import com.company.locators.RecipeLocator;
import com.company.models.ConfigSettings;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String configFile = null;
        if(args.length > 0) {
            configFile = args[0];
        }
        else {
            configFile = System.getProperty("user.dir") + "/updateValuesConfiguration.json";
        }
        ConfigSettings settings = new ConfigReader().readSettings(configFile);
        if(settings == null) {
            return;
        }
        String savedValuesLocation = System.getProperty("user.dir") + "/savedUpdatedValues.json";
        DebugLog log = new DebugLog(settings.enableDebug);
        JsonManipulator manipulator = new JsonManipulator();
        SavedValuesReadWriter readWriter = new SavedValuesReadWriter(log, settings, savedValuesLocation, manipulator);
        IngredientStore ingredientStore = readWriter.readStore();
        RecipeLocator recipeLocator = new RecipeLocator(log, settings, manipulator);
        ValueCalculator valueCalculator = new ValueCalculator(log, settings, recipeLocator, ingredientStore);
        ArrayList<Updater> updaters = new ArrayList<Updater>();
        updaters.add(new ConsumableUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new ItemUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new MatItemUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new ObjectUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new PatchFileUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new ProjectileUpdater(log, manipulator, ingredientStore, valueCalculator));
        FileUpdater updater = new FileUpdater(log, settings, valueCalculator, manipulator, updaters);
        updater.updateValues();
        readWriter.saveStore(ingredientStore);
    }
}
