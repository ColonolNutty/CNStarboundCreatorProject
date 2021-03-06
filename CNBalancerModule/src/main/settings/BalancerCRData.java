package main.settings;


import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.settings.CRData;
import com.colonolnutty.module.shareddata.models.settings.ICRData;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:22 AM
 */
public class BalancerCRData extends CRData<BalancerSettings> implements ICRData<BalancerSettings> {
    @Override
    public ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("propertiesToUpdate");
        settingNames.add("forceUpdate");

        settingNames.add("locationsToUpdate");
        settingNames.add("includeLocations");
        settingNames.add("excludedEffects");
        settingNames.add("increasePercentage");
        settingNames.add("minimumFoodValue");
        settingNames.add("ingredientOverridePath");
        settingNames.add("numberOfPasses");
        settingNames.add("enableEffectsUpdate");
        settingNames.add("includeCraftGroups");
        settingNames.add("showConfirmation");
        settingNames.add("friendlyNamesFilePath");
        settingNames.add("fileTypesToUpdate");

        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\balancerConfiguration.json";
    }

    @Override
    public boolean settingsAreValid(BalancerSettings settings, CNLog log) {
        return verifySettings(log, settings,
                settings.propertiesToUpdate,
                settings.forceUpdate,
                settings.locationsToUpdate,
                settings.includeLocations,
                settings.excludedEffects,
                settings.increasePercentage,
                settings.minimumFoodValue,
                settings.ingredientOverridePath,
                settings.numberOfPasses,
                settings.enableEffectsUpdate,
                settings.includeCraftGroups,
                settings.showConfirmation,
                settings.friendlyNamesFilePath,
                settings.fileTypesToUpdate);
    }
}
