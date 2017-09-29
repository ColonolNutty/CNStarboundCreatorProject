package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:31 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigSettings {
    public String[] locationsToUpdate;
    public String[] includeLocations;
    public String[] excludedEffects;
    public Double increasePercentage;
    public int minimumFoodValue;
    public String ingredientOverridePath;
    public String logFile;
    public int numberOfPasses;
    public Boolean enableTreeView;
    public Boolean enableConsoleDebug;
    public Boolean enableVerboseLogging;
    public Boolean enableEffectsUpdate;
}
