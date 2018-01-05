package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.*;
import com.colonolnutty.module.shareddata.jsonhandlers.*;
import com.colonolnutty.module.shareddata.models.*;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.prettyprinters.*;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:32 PM
 */
public class JsonManipulator implements IReadFiles, IWriteFiles {
    private CNLog _log;
    private ArrayList<String> _keysToWrite;
    private IPrettyPrinter _prettyPrinter;
    private IFileWriter _fileWriter;
    private IFileReader _fileReader;
    private ArrayList<IJsonHandler> _jsonHandlers;

    public JsonManipulator(CNLog log, BaseSettings settings) {
        _log = log;
        _fileWriter = new FileWriterWrapper();
        _fileReader = new FileReaderWrapper();

        if(settings.propertiesToUpdate == null) {
            _keysToWrite = new ArrayList<String>();
        }
        else {
            _keysToWrite = CNCollectionUtils.toStringArrayList(settings.propertiesToUpdate);
        }

        //Json Handlers
        _jsonHandlers = new ArrayList<IJsonHandler>();
        _jsonHandlers.add(new PriceHandler());
        _jsonHandlers.add(new FoodValueHandler());
        _jsonHandlers.add(new EffectsHandler());
        _jsonHandlers.add(new DescriptionHandler());

        //Pretty Printers
        _prettyPrinter = new JsonWrapperPrettyPrinter();
        if(settings.propertyOrderFile == null) {
            return;
        }
        try {
            PropertyOrder propertyOrder = _fileReader.read(settings.propertyOrderFile, PropertyOrder.class);
            if(propertyOrder != null) {
                _prettyPrinter.setPropertyOrder(propertyOrder.order);
            }
        }
        catch(IOException e) {
            _log.error("propertyOrderFile.json file not found", e);
        }
    }

    public void setPrettyPrinter(IPrettyPrinter prettyPrinter) { _prettyPrinter = prettyPrinter; }

    @Override
    public void setFileWriter(IFileWriter writer) {
        _fileWriter = writer;
    }
    @Override
    public void setFileReader(IFileReader reader) { _fileReader = reader; }

    //Read

    public Recipe readRecipe(String path) throws IOException {
        return _fileReader.read(path, Recipe.class);
    }

    public Ingredient readIngredient(String path) throws IOException {
        return _fileReader.read(path, Ingredient.class);
    }

    //Read

    //Write
    public void writeNew(String filePath, Object obj) {
        try {
            File file = _fileWriter.createFile(filePath);
            String toWriteObj = _fileWriter.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);

            String result = null;
            try {
                result = _prettyPrinter.makePretty(toWrite, 0);
            }
            catch(JSONException e) {
                throw new JSONException("For filepath: " + filePath, e);
            }
            if(result == null || result.equals("")) {
                return;
            }
            _fileWriter.writeData(file, result);
        }
        catch(JSONException e) {
            _log.error(e);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + filePath, e);
        }
    }

    public void writeNewWithTemplate(String templateFile, String filePath, Object obj) {
        try {
            File file = _fileWriter.createFile(filePath);
            String fileData = _fileReader.readFile(templateFile);
            String toWriteObj = _fileWriter.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);
            JSONObject combined = applyMissingProperties(toWrite, fileData);
            if(combined == null) {
                return;
            }

            String result = null;
            try {
                result = _prettyPrinter.makePretty(combined, 0);
            }
            catch(JSONException e) {
                throw new JSONException("For filepath: " + filePath, e);
            }
            if(result == null || result.equals("")) {
                return;
            }
            _fileWriter.writeData(file, result);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + filePath, e);
        }
    }
    private JSONObject applyMissingProperties(JSONObject toWrite,
                                              String existingJson) {
        if(existingJson == null) {
            return null;
        }
        try {
            JSONObject existingObject = new JSONObject(existingJson);

            Iterator<String> toUpdateKeys = existingObject.keys();
            while (toUpdateKeys.hasNext()) {
                String key = toUpdateKeys.next();
                if(toWrite.has(key)) {
                    Object value = toWrite.get(key);
                    if(value != null) {
                        existingObject.put(key, value);
                    }
                }
            }
            return existingObject;
        }
        catch(JSONException e) {
            _log.error("Problem when parsing: " + existingJson, e);
            return null;
        }
    }

    public void write(String filePath, Object obj) {
        try {
            String fileData = _fileReader.readFile(filePath);
            String toWriteObj = _fileWriter.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);
            JSONObject combined = combineJsonValues(toWrite, fileData, filePath.endsWith(".consumable"));
            if(combined == null) {
                return;
            }
            String result;
            try {
                result = _prettyPrinter.makePretty(combined, 0);
            }
            catch(JSONException e) {
                throw new JSONException("For filepath: " + filePath, e);
            }
            if(result == null || result.equals("")) {
                return;
            }
            _fileWriter.writeData(filePath, result);
        }
        catch(JSONException e) {
            _log.error(e);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + filePath, e);
        }
    }

    //Write

    private JSONObject combineJsonValues(JSONObject toWrite,
                                         String existingJson,
                                         boolean canUpdateEffects) {
        if(existingJson == null) {
            return null;
        }
        try {
            JSONObject existingObject = new JSONObject(existingJson);

            Iterator<String> toUpdateKeys = toWrite.keys();
            while (toUpdateKeys.hasNext()) {
                String key = toUpdateKeys.next();
                if(canWriteKey(key) && (existingObject.has(key) || (canUpdateEffects && key.equals("effects")))) {
                    Object value = toWrite.get(key);
                    if(value != null) {
                        existingObject.put(key, value);
                    }
                }
            }
            return existingObject;
        }
        catch(JSONException e) {
            _log.error("Problem when parsing: " + existingJson, e);
            return null;
        }
    }

    private boolean canWriteKey(String key) {
        return _keysToWrite.contains(key);
    }
}
