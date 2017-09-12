package com.company;

import com.company.models.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:32 PM
 */
public class JsonManipulator {

    private Gson _gson;
    private ObjectMapper _mapper;

    public JsonManipulator() {
        _gson = new Gson();
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
    }

    public Recipe readRecipe(String path) throws IOException {
        return read(path, Recipe.class);
    }

    public IngredientValues readIngredientVal(String path) throws IOException {
        return read(path, IngredientValues.class);
    }

    public ConsumableBase readConsumable(String path) throws IOException {
        return read(path, ConsumableBase.class);
    }

    public <T> T read(String filePath, Class<T> classOfT) throws IOException, JsonSyntaxException {
        Reader reader = new FileReader(filePath);
        return _mapper.readValue(reader, classOfT);
    }

    public void write(String filePath, Object obj) {
        try {
            String fileData = readExistingFile(filePath);
            JSONObject toWrite = combineWithExisting(obj, fileData);
            Writer writer = new FileWriter(filePath);
            toWrite.write(writer, 1, 1);
            writer.close();
        }
        catch(IOException e) {
            System.out.println("Failed to write file: " + filePath);
        }
    }

    private JSONObject combineWithExisting(Object dataToWrite, String existingJson) throws IOException {
        String toWriteObj = _mapper.writeValueAsString(dataToWrite);
        JSONObject toWrite = new JSONObject(toWriteObj);
        JSONObject existingObject = new JSONObject(existingJson);

        Iterator<String> toUpdateKeys = toWrite.keys();
        while(toUpdateKeys.hasNext()) {
            String key = toUpdateKeys.next();
            existingObject.put(key, toWrite.get(key));
        }
        return existingObject;
    }

    private String readExistingFile(String filePath) throws IOException {
        String fileData = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                fileData += line;
            }
            br.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Failed to find file: " + filePath);
        }
        return fileData;
    }
}
