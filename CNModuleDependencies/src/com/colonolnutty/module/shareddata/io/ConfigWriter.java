package com.colonolnutty.module.shareddata.io;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.models.settings.CNBaseSettings;

import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 12:31 PM
 */
public class ConfigWriter implements IWriteFiles {
    private CNLog _log;
    private IFileWriter _fileWriter;

    public ConfigWriter(CNLog log) {
        _log = log;
        setFileWriter(new FileWriterWrapper());
    }

    public void write(CNBaseSettings settings) {
        String settingsLocation = settings.configLocation;
        if(settingsLocation == null) {
            _log.error("Failed to write settings");
            return;
        }
        try {
            String result = _fileWriter.writeValueAsString(settings);
            _fileWriter.writeData(settingsLocation, result);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + settingsLocation, e);
        }
    }

    @Override
    public void setFileWriter(IFileWriter fileWriter) {
        _fileWriter = fileWriter;
    }
}