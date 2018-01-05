package main.settings;


import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.models.settings.CRData;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:31 AM
 */
public class PandCCRData extends CRData<PandCSettings> {
    @Override
    public ArrayList<String> getPropertyNames() {
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
    public boolean settingsAreValid(PandCSettings settings, CNLog log) {
        return verifySettings(log, settings,
                settings.locationsOfCrops,
                settings.creationPath,
                settings.makePerennial,
                settings.makeCompact,
                settings.makePatchFiles);
    }
}
