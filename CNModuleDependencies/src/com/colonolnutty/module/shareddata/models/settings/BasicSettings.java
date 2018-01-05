package com.colonolnutty.module.shareddata.models.settings;

/**
 * User: Jack's Computer
 * Date: 01/05/2018
 * Time: 11:45 AM
 */
public class BasicSettings extends BaseSettings {
    public BasicSettings() {
        logFile = "logs\\prerunlog.log";
        enableConsoleDebug = true;
        enableVerboseLogging = true;
        enableTreeView = true;
    }
}
