package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:26 AM
 */
public abstract class CRData {
    protected abstract ArrayList<String> getPropertyNames();
    public abstract String getSettingsFilePath();
    public abstract  <T extends BaseSettings> boolean settingsAreValid(T baseSettings, CNLog log);

    protected boolean verifySettings(CNLog log, BaseSettings settings, Object... objs) {
        ArrayList<String> properties = getPropertyNames();
        if(properties == null) {
            return false;
        }
        return verifyBaseSettings(settings, log) && verifySettings(log, properties, objs);
    }

    protected boolean verifySettings(CNLog log, ArrayList<String> settings, Object... objs) {
        if(objs.length != settings.size()) {
            log.debug("Found settings did not match expected setting count");
            StringBuilder builder = new StringBuilder();
            for(String str : settings) {
                builder.append(str);
                builder.append(", ");
            }
            log.debug("Expected settings: " + builder.toString());
            return false;
        }
        boolean isValid = true;
        for(int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if(obj == null) {
                log.debug("Missing property with name: " + settings.get(i));
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean verifyBaseSettings(BaseSettings settings, CNLog log) {
        ArrayList<String> baseSettings = getBaseSettingNames();
        if(baseSettings == null) {
            return false;
        }
        return verifySettings(log,
                baseSettings,
                settings.propertyOrderFile,
                settings.logFile,
                settings.enableTreeView,
                settings.enableConsoleDebug,
                settings.enableVerboseLogging);
    }

    private ArrayList<String> getBaseSettingNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("propertyOrderFile");
        settingNames.add("logFile");
        settingNames.add("enableTreeView");
        settingNames.add("enableConsoleDebug");
        settingNames.add("enableVerboseLogging");
        return settingNames;
    }
}
