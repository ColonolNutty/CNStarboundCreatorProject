package com.colonolnutty.module.shareddata.models.settings;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:15 AM
 */
public class BasicSettings extends BaseSettings {
    public BasicSettings() {
        logFile = "logs\\prerunlog.log";
        enableConsoleDebug = true;
        enableVerboseLogging = true;
        propertyOrderFile = null;
    }
}
