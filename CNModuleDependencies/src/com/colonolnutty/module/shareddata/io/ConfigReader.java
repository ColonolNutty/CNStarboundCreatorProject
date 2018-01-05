package com.colonolnutty.module.shareddata.io;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.settings.CNBaseSettings;
import com.colonolnutty.module.shareddata.models.settings.ICRData;

import java.io.File;
import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:33 PM
 */
public class ConfigReader<T extends CNBaseSettings> implements IReadFiles {
    private CNLog _log;
    private IFileReader _fileReader;

    public ConfigReader(CNLog log) {
        _log = log;
        _fileReader = new FileReaderWrapper();
    }

    public T readSettingsFile(ICRData<T> crData, Class<T> classOfT) {
        String configFile = crData.getSettingsFilePath();
        File file = new File(configFile);
        System.out.println("Looking for configuration file at path: " + file.getAbsolutePath());
        try {
            T settings = _fileReader.read(configFile, classOfT);
            if(settings == null || !crData.settingsAreValid(settings, _log)) {
                return null;
            }
            settings.configLocation = configFile;
            return settings;
        }
        catch(IOException e) {
            System.out.println("[ERROR] Could not read configuration settings file: " + configFile);
            e.printStackTrace(System.out);
        }
        return null;
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }
}
