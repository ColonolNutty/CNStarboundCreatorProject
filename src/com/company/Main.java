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
        CNLog log = new CNLog(new ConsoleDebugWriter(), new ConfigSettings());
        ConfigReader configReader = new ConfigReader(log);
        ConfigSettings configSettings = configReader.readConfigSettings(valueBalancerConfigName);
        RecipeCreatorSettings recipeCreatorSettings = configReader.readCreatorSettings(recipeCreatorConfigName);
        MainWindow main = new MainWindow(configSettings, recipeCreatorSettings, new SettingsWriter());
        main.start();
    }

    private static String getConfigName(String[] args, int index, String defaultName) {
        if(args.length > 0 && index < args.length) {
            return args[index];
        }
        System.out.println("[INFO] No configuration file specified, using default configuration path: " + defaultName);
        return defaultName;
    }
}
