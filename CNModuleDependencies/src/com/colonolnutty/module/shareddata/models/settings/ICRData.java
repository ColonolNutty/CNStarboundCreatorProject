package com.colonolnutty.module.shareddata.models.settings;

import com.colonolnutty.module.shareddata.debug.CNLog;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 01/05/2018
 * Time: 12:38 PM
 */
public interface ICRData<T extends CNBaseSettings> {
    ArrayList<String> getPropertyNames();
    String getSettingsFilePath();
    boolean settingsAreValid(T baseSettings, CNLog log);
}
