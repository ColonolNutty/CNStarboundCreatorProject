package com.colonolnutty.module.shareddata.models.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 01/05/2018
 * Time: 12:27 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseSettings {
    public String logFile;
    public Boolean enableConsoleDebug;
    public Boolean enableTreeView;
    public Boolean enableVerboseLogging;

    public String propertyOrderFile;
    public String[] propertiesToUpdate;
    public Boolean forceUpdate;

    @JsonIgnore
    public String configLocation;
}
