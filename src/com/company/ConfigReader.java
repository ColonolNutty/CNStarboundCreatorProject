package com.company;

import com.company.models.ConfigSettings;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:33 PM
 */
public class ConfigReader {
    private Gson gson;


    public ConfigReader() {
        gson = new Gson();
    }

    public ConfigSettings readSettings(String configFile) {
        ConfigSettings settings = null;
        Reader reader = null;
        try {
            reader = new FileReader(configFile);
            settings = gson.fromJson(reader, ConfigSettings.class);
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
