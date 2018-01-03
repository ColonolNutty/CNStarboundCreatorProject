package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.io.FileWriterWrapper;
import com.colonolnutty.module.shareddata.io.IFileWriter;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 12:31 PM
 */
public class SettingsWriter implements IWriteFiles {
    private CNLog _log;
    private IFileWriter _fileWriter;

    public SettingsWriter(CNLog log) {
        _log = log;
        setFileWriter(new FileWriterWrapper());
    }

    public void write(BaseSettings settings) {
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