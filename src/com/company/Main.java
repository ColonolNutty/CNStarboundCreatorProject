package com.company;

import com.company.locators.PatchLocator;
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
            configFile = "updateValuesConfiguration.json";
            System.out.println("No config file specified, using default: " + configFile);
        }
        ConfigSettings settings = new ConfigReader().readSettings(configFile);
        if(settings == null) {
            return;
        }
        DebugLog log = new DebugLog(settings.enableDebug);
        JsonManipulator manipulator = new JsonManipulator(log);
        PatchLocator patchLocator = new PatchLocator(log);
        InitialValuesReadWriter readWriter = new InitialValuesReadWriter(log, settings, manipulator, patchLocator);
        IngredientStore ingredientStore = readWriter.read();
        RecipeLocator recipeLocator = new RecipeLocator(log, settings, manipulator, patchLocator);
        ValueCalculator valueCalculator = new ValueCalculator(log, settings, recipeLocator, ingredientStore);
        ArrayList<Updater> updaters = new ArrayList<Updater>();
        updaters.add(new ConsumableUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new ItemUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new MatItemUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new ObjectUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new PatchFileUpdater(log, manipulator, ingredientStore, valueCalculator));
        updaters.add(new ProjectileUpdater(log, manipulator, ingredientStore, valueCalculator));
        FileUpdater updater = new FileUpdater(log, settings, valueCalculator, manipulator, updaters, ingredientStore);
        updater.updateValues();
    }
}
