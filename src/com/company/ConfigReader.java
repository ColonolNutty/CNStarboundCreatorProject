package com.company;

import com.company.models.ConfigSettings;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:33 PM
 */
public class ConfigReader {

    private ObjectMapper _mapper;

    public ConfigReader() {
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
    }

    public ConfigSettings readSettings(String configFile) {
        ConfigSettings settings = null;
        Reader reader = null;
        try {
            reader = new FileReader(configFile);
            settings = _mapper.readValue(reader, ConfigSettings.class);
        }
        catch(IOException e) {
            System.out.println("Could not read configuration settings file: " + configFile);
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException e) { }
            }
        }
        return settings;
    }
}
