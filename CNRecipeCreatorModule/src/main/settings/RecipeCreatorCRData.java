package main.settings;


import com.colonolnutty.module.shareddata.CRData;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:29 AM
 */
public class RecipeCreatorCRData extends CRData {
    @Override
    public ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("propertyOrderFile");
        settingNames.add("logFile");
        settingNames.add("enableTreeView");
        settingNames.add("enableConsoleDebug");
        settingNames.add("enableVerboseLogging");

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
        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\recipeCreatorSettings.json";
    }
}
