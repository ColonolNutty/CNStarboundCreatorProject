package com.colonolnutty.module.shareddata.models.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 01/05/2018
 * Time: 2:10 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CNBaseSettings {
    public String logFile;
    public Boolean enableConsoleDebug;
    public Boolean enableTreeView;
    public Boolean enableVerboseLogging;

    @JsonIgnore
    public String configLocation;
}
