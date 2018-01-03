package com.colonolnutty.module.shareddata.io;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.CRData;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;

import java.io.File;
import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:33 PM
 */
public class ConfigReader implements IReadFiles {
    private CNLog _log;
    private IFileReader _fileReader;

    public ConfigReader(CNLog log) {
        _log = log;
        _fileReader = new FileReaderWrapper();
    }

    public <T extends BaseSettings> T readSettingsFile(CRData crData, Class<T> classOfT) {
        String configFile = crData.getSettingsFilePath();
        File file = new File(configFile);
        _log.info("Looking for configuration file at path: " + file.getAbsolutePath());
        try {
            T settings = _fileReader.read(configFile, classOfT);
            if(settings == null || !crData.settingsAreValid(settings, _log)) {
                return null;
            }
            settings.configLocation = configFile;
            return settings;
        }
        catch(IOException e) {
            _log.error("Could not read configuration settings file: " + configFile, e);
        }
        return null;
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }
}
