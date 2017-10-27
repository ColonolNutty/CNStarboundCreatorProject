package main.settings;


import com.colonolnutty.module.shareddata.CRData;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:22 AM
 */
public class BalancerCRData extends CRData {
    @Override
    public ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("propertyOrderFile");
        settingNames.add("logFile");
        settingNames.add("enableTreeView");
        settingNames.add("enableConsoleDebug");
        settingNames.add("enableVerboseLogging");

        settingNames.add("locationsToUpdate");
        settingNames.add("includeLocations");
        settingNames.add("excludedEffects");
        settingNames.add("increasePercentage");
        settingNames.add("minimumFoodValue");
        settingNames.add("ingredientOverridePath");
        settingNames.add("numberOfPasses");
        settingNames.add("enableEffectsUpdate");
        settingNames.add("includeCraftGroups");
        settingNames.add("forceUpdate");

        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\balancerConfiguration.json";
    }
}
