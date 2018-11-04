package main.settings;


import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.settings.CRData;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:29 AM
 */
public class RecipeConfigCreatorCRData extends CRData<RecipeConfigCreatorSettings> {
    @Override
    public ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();

        settingNames.add("creationPath");
        settingNames.add("recipePaths");
        settingNames.add("includeRecipeGroups");
        settingNames.add("configAsPatchFile");
        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\recipeConfigCreatorSettings.json";
    }

    @Override
    public boolean settingsAreValid(RecipeConfigCreatorSettings settings, CNLog log) {
        return verifySettings(log, settings,
                settings.creationPath,
                settings.recipePaths,
                settings.includeRecipeGroups,
                settings.configAsPatchFile);
    }
}
