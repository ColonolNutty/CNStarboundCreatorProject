package com.company.balancer;

import com.company.CNLog;
import com.company.models.BaseSettings;
import com.company.models.ConfigSettings;
import com.company.models.RecipeCreatorSettings;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

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

    public ConfigSettings readConfigSettings(String path) {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("locationsToUpdate");
        settingNames.add("includeLocations");
        settingNames.add("excludedEffects");
        settingNames.add("increasePercentage");
        settingNames.add("minimumFoodValue");
        settingNames.add("ingredientOverridePath");
        settingNames.add("logFile");
        settingNames.add("numberOfPasses");
        settingNames.add("enableTreeView");
        settingNames.add("enableConsoleDebug");
        settingNames.add("enableVerboseLogging");
        settingNames.add("enableEffectsUpdate");

        return readSettingsFile(path, ConfigSettings.class, settingNames);
    }

    public RecipeCreatorSettings readCreatorSettings(String path) {
        ArrayList<String> settingNames = new ArrayList<String>();
        settingNames.add("creationPath");
        settingNames.add("ingredientListFile");
        settingNames.add("recipeTemplateFile");
        settingNames.add("ingredientTemplateFile");
        settingNames.add("ingredientImageTemplateFile");
        settingNames.add("recipeConfigFileName");
        settingNames.add("filePrefix");
        settingNames.add("fileSuffix");
        settingNames.add("fileExtension");
        settingNames.add("outputItemDescription");
        settingNames.add("outputItemShortDescription");
        settingNames.add("countPerIngredient");
        settingNames.add("numberOfIngredientsPerRecipe");

        return readSettingsFile(path, RecipeCreatorSettings.class, settingNames);
    }

    private <T extends BaseSettings> T readSettingsFile(String configFile,
                                                        Class<T> classOfT,
                                                        ArrayList<String> expectedProperties) {
        System.out.println("[INFO] Looking for configuration file path: " + configFile);
        T settings = null;
        Reader reader = null;
        try {
            reader = new FileReader(configFile);
            JsonNode node = _mapper.readValue(reader, JsonNode.class);
            if(node == null || !validateSettings(node, expectedProperties)) {
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

    private boolean validateSettings(JsonNode settingsNode, ArrayList<String> settingNames) {
        if(settingNames == null || settingNames.size() == 0) {
            return true;
        }
        boolean isValid = true;
        for(int i = 0; i < settingNames.size(); i++) {
            String settingName = settingNames.get(i);
            if(!settingsNode.has(settingName)) {
                JsonNode node = settingsNode.get(settingName);
                if(!node.isNull() && (node.isArray() && ((ArrayNode)node).size() > 0)) {
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
