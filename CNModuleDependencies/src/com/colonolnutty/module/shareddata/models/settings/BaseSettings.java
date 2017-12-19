package com.colonolnutty.module.shareddata.models.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 10:13 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseSettings {
    public String propertyOrderFile;
    public String logFile;
    public Boolean enableTreeView;
    public Boolean enableConsoleDebug;
    public Boolean enableVerboseLogging;
    public String[] propertiesToUpdate;
    public Boolean forceUpdate;

    @JsonIgnore
    public String configLocation;
}
