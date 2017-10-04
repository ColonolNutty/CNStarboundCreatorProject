package com.company;

import com.company.balancer.ConfigReader;
import com.company.models.ConfigSettings;
import com.company.models.RecipeCreatorSettings;
import com.company.recipecreator.MassRecipeCreator;
import com.company.ui.MainWindow;

public class Main {

    public static void main(String[] args) {
        String valueBalancerConfigName = getConfigName(args, 0, "balancerConfiguration.json");
        String recipeCreatorConfigName = getConfigName(args, 1, "recipeCreatorSettings.json");
        CNLog log = new CNLog(new ConsoleDebugWriter());
        ConfigReader configReader = new ConfigReader(log);
        ConfigSettings configSettings = configReader.readConfigSettings(valueBalancerConfigName);
        RecipeCreatorSettings recipeCreatorSettings = configReader.readCreatorSettings(recipeCreatorConfigName);
        //MassRecipeCreator creator = new MassRecipeCreator(recipeCreatorSettings, log, new JsonManipulator(log));
        //creator.create();
        //MainWindow main = new MainWindow(configSettings, recipeCreatorSettings, new SettingsWriter());
        //main.start();
    }

    private static String getConfigName(String[] args, int index, String defaultName) {
        String configName = null;
        if(args.length > 0 && args.length >= index) {
            configName = args[index];
        }
        if(configName == null) {
            configName = defaultName;
            System.out.println("[INFO] No configuration file specified, using default configuration path: " + configName);
        }
        return configName;
    }
}
