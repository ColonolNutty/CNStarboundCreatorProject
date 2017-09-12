package com.company;

import com.company.Updaters.*;
import com.company.locators.IngredientLocator;
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
        DebugLog log = new DebugLog(settings.enableDebug);
        JsonManipulator manipulator = new JsonManipulator();
        IngredientLocator ingredientLocator = new IngredientLocator(log, settings, manipulator);
        RecipeLocator recipeLocator = new RecipeLocator(log, settings, manipulator);
        ValueCalculator valueCalculator = new ValueCalculator(log, settings, recipeLocator, ingredientLocator);
        ArrayList<Updater> updaters = new ArrayList<Updater>();
        updaters.add(new ConsumableUpdater(log, manipulator, ingredientLocator, valueCalculator));
        updaters.add(new ItemUpdater(log, manipulator));
        updaters.add(new MatItemUpdater(log, manipulator));
        updaters.add(new ObjectUpdater(log, manipulator));
        updaters.add(new PatchFileUpdater(log, manipulator));
        updaters.add(new ProjectileUpdater(log, manipulator));
        FileUpdater updater = new FileUpdater(log, settings, valueCalculator, manipulator, updaters);
        updater.updateValues();
    }
}
