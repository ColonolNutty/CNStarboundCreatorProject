package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:31 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSettings extends BaseSettings {
    public String[] locationsToUpdate;
    public String[] includeLocations;
    public String[] excludedEffects;
    public Double increasePercentage;
    public int minimumFoodValue;
    public String ingredientOverridePath;
    public int numberOfPasses;
    public Boolean enableEffectsUpdate;

    public ConfigSettings() {super();}
}
