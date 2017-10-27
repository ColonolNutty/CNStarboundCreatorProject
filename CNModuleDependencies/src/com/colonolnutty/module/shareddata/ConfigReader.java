package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:33 PM
 */
public class ConfigReader {
    private CNLog _log;
    private ObjectMapper _mapper;

    public ConfigReader(CNLog log) {
        _log = log;
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
    }

    public <T extends BaseSettings> T readSettingsFile(CRData crData, Class<T> classOfT) {
        String configFile = crData.getSettingsFilePath();
        File file = new File(configFile);
        System.out.println("[INFO] Looking for configuration file at path: " + file.getAbsolutePath());
        T settings = null;
        Reader reader = null;
        try {
            reader = new FileReader(configFile);
            JsonNode node = _mapper.readValue(reader, JsonNode.class);
            ArrayList<String> propertyNames = crData.getPropertyNames();
            if(node == null || !settingsAreValid(node, propertyNames)) {
                return null;
            }
            settings = _mapper.treeToValue(node, classOfT);
            settings.configLocation = configFile;
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

    private boolean settingsAreValid(JsonNode settingsNode, ArrayList<String> settingNames) {
        if(settingNames == null || settingNames.size() == 0) {
            return true;
        }
        boolean isValid = true;
        for(int i = 0; i < settingNames.size(); i++) {
            String settingName = settingNames.get(i);
            if(!settingsNode.has(settingName)) {
                JsonNode node = settingsNode.get(settingName);
                if(node == null || !node.isNull() && (node.isArray() && ((ArrayNode)node).size() > 0)) {
                    continue;
                }
                _log.error("Setting not found with name: " + settingName);
                isValid = false;
                i = settingNames.size();
            }
        }
        return isValid;
    }
}
