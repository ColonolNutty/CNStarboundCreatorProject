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
    public String ingredientOverridePath;
    public String logFile;
    public Double numberOfPasses;
    public boolean enableConsoleDebug;
    public boolean enableVerboseLogging;
    public boolean enableEffectsUpdate;
}
