package main.settings;


import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.CRData;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:31 AM
 */
public class PandCCRData extends CRData {
    @Override
    protected ArrayList<String> getPropertyNames() {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("locationsOfCrops");
        settingNames.add("creationPath");
        settingNames.add("makePerennial");
        settingNames.add("makeCompact");
        settingNames.add("makePatchFiles");

        return settingNames;
    }

    @Override
    public String getSettingsFilePath() {
        return "configuration\\perennialAndCompactorConfig.json";
    }

    @Override
    public <T extends BaseSettings> boolean settingsAreValid(T baseSettings, CNLog log) {
        if(!(baseSettings instanceof PandCSettings)) {
            return false;
        }
        PandCSettings settings = (PandCSettings) baseSettings;

        return verifySettings(log, settings,
                settings.locationsOfCrops,
                settings.creationPath,
                settings.makePerennial,
                settings.makeCompact,
                settings.makePatchFiles);
    }
}
