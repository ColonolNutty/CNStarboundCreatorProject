package main.settings;


import com.colonolnutty.module.shareddata.CRData;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:31 AM
 */
public class PandCCRData extends CRData {
    @Override
    public ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("propertyOrderFile");
        settingNames.add("logFile");
        settingNames.add("enableTreeView");
        settingNames.add("enableConsoleDebug");
        settingNames.add("enableVerboseLogging");

        settingNames.add("locationsOfCrops");
        settingNames.add("createPath");
        settingNames.add("makePerennial");
        settingNames.add("makeCompact");
        settingNames.add("makePatchFiles");

        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\perennialAndCompactorConfig.json";
    }
}
