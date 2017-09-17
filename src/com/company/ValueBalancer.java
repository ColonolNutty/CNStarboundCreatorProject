package com.company;

import com.company.locators.*;
import com.company.models.ConfigSettings;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:35 AM
 */
public class ValueBalancer {

    private String _configFilePath;
    private DebugLog _log;

    public ValueBalancer(String configSettingsFile) {
        _configFilePath = configSettingsFile;
        if(_configFilePath == null) {
            _configFilePath = "balancerConfiguration.json";
            System.out.println("[INFO] No configuration file specified, using default configuration path: " + _configFilePath);
        }
    }

    public void run() {
        if(_log != null) {
            _log.dispose();
        }
        StopWatchTimer timer = new StopWatchTimer();
        timer.start();
        ConfigSettings configSettings = readConfigSettings(_configFilePath);
        if(configSettings == null) {
            System.out.println("[ERROR] No configuration file found, exiting.");
            return;
        }
        _log = new DebugLog(configSettings.logFile, configSettings.enableConsoleDebug);
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
        timer.stop();
        long time = timer.timeInMinutes();
        String unitOfMeasurement = "minutes";
        if(time == 0) {
            time = timer.timeInSeconds();
            unitOfMeasurement = "seconds";
        }
        if(time == 0) {
            time = timer.timeInMilliseconds();
            unitOfMeasurement = "milliseconds";
        }
        _log.logInfo("Finished running in " + time + " " + unitOfMeasurement);
    }

    private ConfigSettings readConfigSettings(String configPath) {
        System.out.println("[INFO] Looking for configuration file with name: " + configPath);
        ConfigSettings configSettings = new ConfigReader().readSettings(configPath);
        return configSettings;
    }

    public void dispose() {
        if(_log != null) {
            _log.dispose();
        }
    }
}
