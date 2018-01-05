package com.colonolnutty.module.shareddata.models.settings;

import com.colonolnutty.module.shareddata.debug.CNLog;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:26 AM
 */
public abstract class CRData<T extends CNBaseSettings> implements ICRData<T> {

    protected boolean verifySettings(CNLog log, T settings, Object... objs) {
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

    private boolean verifyBaseSettings(T settings, CNLog log) {
        ArrayList<String> baseSettings = getBaseSettingNames();
        if(baseSettings == null) {
            return false;
        }
        return verifySettings(log,
                baseSettings,
                settings.logFile,
                settings.enableTreeView,
                settings.enableConsoleDebug,
                settings.enableVerboseLogging);
    }

    private ArrayList<String> getBaseSettingNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("logFile");
        settingNames.add("enableTreeView");
        settingNames.add("enableConsoleDebug");
        settingNames.add("enableVerboseLogging");
        return settingNames;
    }
}
