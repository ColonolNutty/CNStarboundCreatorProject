package com.colonolnutty.module.shareddata;

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
public class SettingsWriter {
    private CNLog _log;
    private ObjectMapper _mapper;

    public SettingsWriter(CNLog log) {
        _log = log;
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public void write(BaseSettings settings) {
        String settingsLocation = settings.configLocation;
        if(settingsLocation == null) {
            _log.error("Failed to write settings");
            return;
        }
        try {
            Writer writer = new FileWriter(settingsLocation);
            _mapper.writeValue(writer, settings);
            writer.close();
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + settingsLocation, e);
        }
    }
}