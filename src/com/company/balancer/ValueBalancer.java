package com.company.balancer;

import com.company.JsonManipulator;
import com.company.CNLog;
import com.company.StopWatchTimer;
import com.company.locators.*;
import com.company.models.ConfigSettings;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:35 AM
 */
public class ValueBalancer {
    private ConfigSettings _configSettings;
    private CNLog _log;

    public ValueBalancer(ConfigSettings configSettings, CNLog log) {
        _configSettings = configSettings;
        _log = log;
    }

    public void run() {
        if(_configSettings == null) {
            _log.error("No configuration file found, exiting.");
            return;
        }
        StopWatchTimer timer = new StopWatchTimer(_log);
        timer.start();
        JsonManipulator manipulator = new JsonManipulator(_log);
        PatchLocator patchLocator = new PatchLocator(_log);
        FileLocator fileLocator = new FileLocator(_log, _configSettings);
        StatusEffectStore statusEffectStore = new StatusEffectStore(_log, fileLocator, manipulator, patchLocator);
        IngredientStore ingredientStore = new IngredientStore(_log, _configSettings, manipulator, patchLocator, fileLocator);
        RecipeStore recipeStore = new RecipeStore(_log, manipulator, patchLocator, fileLocator);
        IngredientDataCalculator ingredientDataCalculator = new IngredientDataCalculator(_log, _configSettings, recipeStore, ingredientStore, statusEffectStore, manipulator);
        IngredientUpdater ingredientUpdater = new IngredientUpdater(_log, _configSettings, manipulator, ingredientStore, ingredientDataCalculator);

        FileUpdater fileUpdater = new FileUpdater(_log, _configSettings,
                ingredientDataCalculator, manipulator, ingredientUpdater, ingredientStore, fileLocator);
        fileUpdater.updateValues();

        timer.stop();
        timer.logTime();
    }
}
