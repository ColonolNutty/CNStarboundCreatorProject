package main.settings;


import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.CRData;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:29 AM
 */
public class RecipeCreatorCRData extends CRData {
    @Override
    protected ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("creationPath");
        settingNames.add("ingredientListFile");
        settingNames.add("recipeTemplateFile");
        settingNames.add("ingredientTemplateFile");
        settingNames.add("ingredientImageTemplateFile");
        settingNames.add("recipeConfigFileName");
        settingNames.add("filePrefix");
        settingNames.add("fileSuffix");
        settingNames.add("fileExtension");
        settingNames.add("outputItemDescription");
        settingNames.add("outputItemShortDescription");
        settingNames.add("countPerIngredient");
        settingNames.add("numberOfIngredientsPerRecipe");
        settingNames.add("configAsPatchFile");
        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\recipeCreatorSettings.json";
    }

    @Override
    public <T extends BaseSettings> boolean settingsAreValid(T baseSettings, CNLog log) {
        if(!(baseSettings instanceof RecipeCreatorSettings)) {
            return false;
        }
        RecipeCreatorSettings settings = (RecipeCreatorSettings) baseSettings;

        return verifySettings(log, settings,
                settings.creationPath,
                settings.ingredientListFile,
                settings.recipeTemplateFile,
                settings.ingredientTemplateFile,
                settings.ingredientImageTemplateFile,
                settings.recipeConfigFileName,
                settings.filePrefix,
                settings.fileSuffix,
                settings.fileExtension,
                settings.outputItemDescription,
                settings.outputItemShortDescription,
                settings.countPerIngredient,
                settings.numberOfIngredientsPerRecipe,
                settings.configAsPatchFile);
    }
}
