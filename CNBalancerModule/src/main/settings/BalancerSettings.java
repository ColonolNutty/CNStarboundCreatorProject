package main.settings;

import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:31 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalancerSettings extends BaseSettings {
    public String[] locationsToUpdate;
    public String[] includeLocations;
    public String[] excludedEffects;
    public Double increasePercentage;
    public Integer minimumFoodValue;
    public String ingredientOverridePath;
    public Integer numberOfPasses;
    public Boolean enableEffectsUpdate;
    public Boolean includeCraftGroups;
    public String friendlyNamesFilePath;
    public String[] fileTypesToUpdate;
}
