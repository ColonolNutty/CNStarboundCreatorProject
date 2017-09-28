package com.company;

import com.company.models.ConfigSettings;
import com.company.models.PropertyOrder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 12:31 PM
 */
public class SettingsWriter {
    private String _settingsLocation;
    private ObjectMapper _mapper;

    public SettingsWriter(String settingsLocation) {
        _settingsLocation = settingsLocation;
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public void write(ConfigSettings settings) {
        try {
            Writer writer = new FileWriter(_settingsLocation);
            _mapper.writeValue(writer, settings);
            writer.close();
        }
        catch(IOException e) {
            System.out.println("[IOE] Failed to write file: " + _settingsLocation);
            e.printStackTrace();
        }
    }
}
