package main.settings;

import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 9:43 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeConfigCreatorSettings extends BaseSettings {
    public String creationPath;
    public String[] ingredientLocations;
    public String[] ingredientFileTypes;
    public String friendlyNamesFilePath;
    public String[] recipePaths;
    public String[] includeRecipeGroups;
    public Boolean configAsPatchFile;
}
