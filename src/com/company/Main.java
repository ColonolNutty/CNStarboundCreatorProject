package com.company;

import com.company.models.ConfigSettings;
import com.company.ui.MainWindow;

public class Main {

    public static void main(String[] args) {
        String configFile = null;
        if(args.length > 0) {
            configFile = args[0];
        }
        if(configFile == null) {
            configFile = "balancerConfiguration.json";
            System.out.println("[INFO] No configuration file specified, using default configuration path: " + configFile);
        }
        ConfigSettings configSettings = readConfigSettings(configFile);
        //ValueBalancer balancer = new ValueBalancer(configSettings);
        //balancer.run();
        MainWindow main = new MainWindow(configSettings);
        main.start();
    }

    private static ConfigSettings readConfigSettings(String configPath) {
        System.out.println("[INFO] Looking for configuration file with name: " + configPath);
        ConfigSettings configSettings = new ConfigReader().readSettings(configPath);
        return configSettings;
    }
}
