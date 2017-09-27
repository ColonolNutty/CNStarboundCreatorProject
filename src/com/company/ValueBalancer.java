package com.company;

import com.company.locators.*;
import com.company.models.ConfigSettings;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:35 AM
 */
public class ValueBalancer {
    private ConfigSettings _configSettings;
    private DebugLog _debugLog;

    public ValueBalancer(ConfigSettings configSettings, DebugLog log) {
        _configSettings = configSettings;
        _debugLog = log;
    }

    public void run() {
        if(_configSettings == null) {
            System.out.println("[ERROR] No configuration file found, exiting.");
            return;
        }
        StopWatchTimer timer = new StopWatchTimer(_debugLog);
        timer.start();
        JsonManipulator manipulator = new JsonManipulator(_debugLog);
        PatchLocator patchLocator = new PatchLocator(_debugLog);
        FileLocator fileLocator = new FileLocator(_debugLog, _configSettings);
        StatusEffectStore statusEffectStore = new StatusEffectStore(_debugLog, fileLocator, manipulator, patchLocator);
        IngredientStore ingredientStore = new IngredientStore(_debugLog, _configSettings, manipulator, patchLocator, fileLocator);
        RecipeStore recipeStore = new RecipeStore(_debugLog, manipulator, patchLocator, fileLocator);
        IngredientDataCalculator ingredientDataCalculator = new IngredientDataCalculator(_debugLog, _configSettings, recipeStore, ingredientStore, statusEffectStore, manipulator);
        IngredientUpdater ingredientUpdater = new IngredientUpdater(_debugLog, _configSettings, manipulator, ingredientStore, ingredientDataCalculator);

        FileUpdater fileUpdater = new FileUpdater(_debugLog, _configSettings,
                ingredientDataCalculator, manipulator, ingredientUpdater, ingredientStore, fileLocator);
        fileUpdater.updateValues();

        timer.stop();
        timer.logTime();
        _debugLog.dispose();
    }
}
