package com.company;

import com.company.locators.*;
import com.company.models.ConfigSettings;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:35 AM
 */
public class ValueBalancer {

    private String _configSettingsFile;
    private DebugLog _log;

    public ValueBalancer(String configSettingsFile) {
        _log = new DebugLog();
        _configSettingsFile = configSettingsFile;
        if(_configSettingsFile == null) {
            _configSettingsFile = "updateValuesConfiguration.json";
            _log.logInfo("No configuration file specified, searching for default with path: " + _configSettingsFile);
        }
    }

    public void run() {
        ConfigSettings configSettings = readConfigSettings(_configSettingsFile);
        if(configSettings == null) {
            _log.logError("No configuration file found, exiting.");
            return;
        }
        _log.enableDebug(configSettings.enableDebug);
        JsonManipulator manipulator = new JsonManipulator(_log);
        PatchLocator patchLocator = new PatchLocator(_log);
        FileLocator fileLocator = new FileLocator(_log, configSettings);
        IngredientStore ingredientStore = new IngredientStore(_log, configSettings, manipulator, patchLocator, fileLocator);
        RecipeStore recipeStore = new RecipeStore(_log, manipulator, patchLocator, fileLocator);
        ValueCalculator valueCalculator = new ValueCalculator(_log, configSettings, recipeStore, ingredientStore);
        IngredientUpdater ingredientUpdater = new IngredientUpdater(_log, manipulator, ingredientStore, valueCalculator);
        FileUpdater fileUpdater = new FileUpdater(_log, configSettings,
                valueCalculator, manipulator, ingredientUpdater, ingredientStore, fileLocator);
        fileUpdater.updateValues();
    }

    private ConfigSettings readConfigSettings(String configPath) {
        if(configPath == null) {
        }
        _log.logInfo("Looking for configuration file with name: " + configPath);
        ConfigSettings configSettings = new ConfigReader().readSettings(configPath);
        return configSettings;
    }
}
