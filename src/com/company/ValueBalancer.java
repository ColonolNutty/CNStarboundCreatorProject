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

    public ValueBalancer(String configSettingsFile) {
        _configFilePath = configSettingsFile;
        if(_configFilePath == null) {
            _configFilePath = "balancerConfiguration.json";
            System.out.println("[INFO] No configuration file specified, using default configuration path: " + _configFilePath);
        }
    }

    public void run() {
        StopWatchTimer timer = new StopWatchTimer();
        timer.start();
        ConfigSettings configSettings = readConfigSettings(_configFilePath);
        if(configSettings == null) {
            System.out.println("[ERROR] No configuration file found, exiting.");
            return;
        }
        DebugLog debugLog = new DebugLog(configSettings.logFile, configSettings.enableConsoleDebug);
        JsonManipulator manipulator = new JsonManipulator(debugLog);
        PatchLocator patchLocator = new PatchLocator(debugLog);
        FileLocator fileLocator = new FileLocator(debugLog, configSettings);
        IngredientStore ingredientStore = new IngredientStore(debugLog, configSettings, manipulator, patchLocator, fileLocator);
        RecipeStore recipeStore = new RecipeStore(debugLog, manipulator, patchLocator, fileLocator);
        ValueCalculator valueCalculator = new ValueCalculator(debugLog, configSettings, recipeStore, ingredientStore);
        IngredientUpdater ingredientUpdater = new IngredientUpdater(debugLog, manipulator, ingredientStore, valueCalculator);

        FileUpdater fileUpdater = new FileUpdater(debugLog, configSettings,
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
        debugLog.logInfo("Finished running in " + time + " " + unitOfMeasurement);
        debugLog.dispose();
    }

    private ConfigSettings readConfigSettings(String configPath) {
        System.out.println("[INFO] Looking for configuration file with name: " + configPath);
        ConfigSettings configSettings = new ConfigReader().readSettings(configPath);
        return configSettings;
    }
}
