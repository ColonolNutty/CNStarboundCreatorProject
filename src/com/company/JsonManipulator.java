package com.company;

import com.company.models.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:32 PM
 */
public class JsonManipulator {
    private DebugLog _log;
    private ObjectMapper _mapper;
    private ArrayList<String> _keysToWrite;
    private String[] _propertyOrder;
    private JsonPrettyPrinter _prettyPrinter;

    public JsonManipulator(DebugLog log) {
        _log = log;
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        _keysToWrite = new ArrayList<String>();
        _keysToWrite.add("foodValue");
        _keysToWrite.add("price");
        try {
            _propertyOrder = read("propertyOrder.json", PropertyOrder.class).order;
            _prettyPrinter = new JsonPrettyPrinter(_propertyOrder);
        }
        catch(IOException e) {
            _log.logError("propertyOrder.json file not found", e);
        }
    }

    public Recipe readRecipe(String path) throws IOException {
        return read(path, Recipe.class);
    }

    public Ingredient readIngredient(String path) throws IOException {
        return read(path, Ingredient.class);
    }

    public <T> T read(String filePath, Class<T> classOfT) throws IOException {
        Reader reader = new FileReader(filePath);
        return _mapper.readValue(reader, classOfT);
    }

    public void write(String filePath, Object obj) {
        try {
            String fileData = readExistingFile(filePath);
            String toWriteObj = _mapper.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);
            JSONObject combined = combineJsonValues(toWrite, fileData);
            if(combined == null) {
                return;
            }
            Writer writer = new FileWriter(filePath);
            writer.write(_prettyPrinter.makePretty(combined, 0));
            writer.close();
        }
        catch(IOException e) {
            _log.logError("[IOE] Failed to write file: " + filePath, e);
        }
    }

    private JSONObject combineJsonValues(JSONObject toWrite, String existingJson) throws IOException {
        if(existingJson == null) {
            return null;
        }
        try {
            JSONObject existingObject = new JSONObject(existingJson);

            Iterator<String> toUpdateKeys = toWrite.keys();
            while (toUpdateKeys.hasNext()) {
                String key = toUpdateKeys.next();
                if(canWriteKey(key) && existingObject.has(key)) {
                    Object value = toWrite.get(key);
                    existingObject.put(key, value);
                }
            }
            return existingObject;
        }
        catch(JSONException e) {
            _log.logError("Problem when parsing: " + existingJson, e);
            return null;
        }
    }

    private boolean canWriteKey(String key) {
        return _keysToWrite.contains(key);
    }

    private String readExistingFile(String filePath) throws IOException {
        String fileData = "";
        try {
            File file = new File(filePath);
            if(!file.exists() || file.isDirectory()) {
                return null;
            }
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] texts = line.split("//");
                if(texts.length != 0 && !texts[0].trim().equals("")) {
                    fileData += texts[0];
                }
            }
            br.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("Failed to find file: " + filePath);
            throw e;
        }
        return fileData;
    }

    public void writeIngredientAsPatch(Ingredient ingredient) {
        if(ingredient.patchFile == null) {
            return;
        }
        try {
            Reader reader = new FileReader(ingredient.patchFile);
            ObjectNode[] patchNodes = _mapper.readValue(reader, ObjectNode[].class);
            reader.close();
            PatchResult result = updateNodes(ingredient, patchNodes, new PatchResult());
            if(!result.foundFood || !result.foundPrice) {
                if(!result.foundFood) {
                    _log.logDebug("    Food Value not found on patch file: " + ingredient.getName(), true);
                }
                if(!result.foundPrice) {
                    _log.logDebug("    Price not found on patch file: " + ingredient.getName(), true);
                }
                List<ObjectNode> objectNodes = Arrays.asList(patchNodes);
                if (!result.foundFood) {
                    if(ingredient.foodValue != null && ingredient.filePath.endsWith("consumable")) {
                        objectNodes.add(createReplaceNode("foodValue", ingredient.foodValue));
                        result.needsUpdate = true;
                    }
                }
                if (!result.foundPrice) {
                    if(ingredient.price != null) {
                        objectNodes.add(createReplaceNode("price", ingredient.price));
                        result.needsUpdate = true;
                    }
                }
                if(!result.needsUpdate) {
                    _log.logInfo("    Skipping patch file for: " + ingredient.getName(), false);
                    return;
                }
                patchNodes = new ObjectNode[objectNodes.size()];
                for(int i = 0; i < objectNodes.size(); i++) {
                    patchNodes[i] = objectNodes.get(i);
                }
            }
            if(result.needsUpdate) {
                _log.logInfo("    Applying update to patch file: " + ingredient.getName(), false);
                Writer writer = new FileWriter(ingredient.patchFile);
                writer.write( _prettyPrinter.makePretty(patchNodes, 0));
                writer.close();
            }
            else {
                _log.logInfo("    Skipping patch file for: " + ingredient.getName(), false);
            }
        }
        catch(JsonMappingException e) {
            writeIngredientPatchAlt(ingredient);
        }
        catch (IOException e) {
            _log.logError("[IOE] Failed to read file: " + ingredient.patchFile, e);
        }
    }

    private void writeIngredientPatchAlt(Ingredient ingredient) {
        try {
            Reader reader = new FileReader(ingredient.patchFile);
            ObjectNode[][] patchNodes = _mapper.readValue(reader, ObjectNode[][].class);
            reader.close();
            PatchResult result = new PatchResult();
            for (int i = 0; i < patchNodes.length; i++) {
                result = updateNodes(ingredient, patchNodes[i], result);

                if(result.foundFood && result.foundPrice) {
                    i = patchNodes.length;
                }
            }
            if (!result.foundFood || !result.foundPrice) {
                List<ObjectNode[]> objectNodes = Arrays.asList(patchNodes);
                if (!result.foundFood) {
                    if (ingredient.foodValue != null && ingredient.filePath.endsWith("consumable")) {
                        objectNodes.add(createTestNodes("foodValue", ingredient.foodValue));
                        ObjectNode[] replaceNodes = new ObjectNode[1];
                        replaceNodes[0] = createReplaceNode("foodValue", ingredient.foodValue);
                        objectNodes.add(replaceNodes);
                        result.needsUpdate = true;
                    }
                }
                if (!result.foundPrice) {
                    if(ingredient.price != null) {
                        objectNodes.add(createTestNodes("price", ingredient.price));
                        ObjectNode[] replaceNodes = new ObjectNode[1];
                        replaceNodes[0] = createReplaceNode("price", ingredient.price);
                        objectNodes.add(replaceNodes);
                        result.needsUpdate = true;
                    }
                }
                if(!result.needsUpdate) {
                    _log.logInfo("    Skipping patch file for: " + ingredient.getName(), false);
                    return;
                }
                patchNodes = new ObjectNode[objectNodes.size()][];
                for (int i = 0; i < objectNodes.size(); i++) {
                    ObjectNode[] nodes = new ObjectNode[objectNodes.get(i).length];
                    for(int j = 0; j < objectNodes.get(i).length; j++) {
                        ObjectNode node = objectNodes.get(i)[j];
                        nodes[j] = node;
                    }
                    patchNodes[i] = nodes;
                }
            }
            if (result.needsUpdate) {
                _log.logInfo("    Applying update to patch file: " + ingredient.getName(), false);
                Writer writer = new FileWriter(ingredient.patchFile);
                writer.write( _prettyPrinter.makePretty(patchNodes, 0));
                writer.close();
            }
            else {
                _log.logInfo("    Skipping patch file for: " + ingredient.getName(), true);
            }
        }
        catch(JsonMappingException e1) {_log.logError(e1);}
        catch(IOException e1) {_log.logError(e1);}
    }

    public <T> T patch(Object entity, String patchFileName, Class<T> valueType) {
        if(patchFileName == null) {
            return null;
        }
        String patchFileAsJson = null;
        try {
            Reader reader = new FileReader(patchFileName);
            JsonNode patchAsNode = _mapper.readTree(reader);
            reader.close();
            patchFileAsJson = patchAsNode.toString();
            JsonPatch patchTool = JsonPatch.fromJson(patchAsNode);
            JsonNode entityAsNode = _mapper.valueToTree(entity);
            JsonNode modifiedAsNode = patchTool.apply(entityAsNode);
            return _mapper.treeToValue(modifiedAsNode, valueType);
        }
        catch(JsonMappingException e) {
            return patchAlt(entity, patchFileName, valueType);
        }
        catch(JsonPatchException e) {
            if(patchFileAsJson != null) {
                _log.logError("Json \"" + patchFileName + "\": " + patchFileAsJson, e);
            }
            else {
                _log.logError("When parsing patch file: " + patchFileName, e);
            }
        }
        catch (IOException e) {
            _log.logError("[IOE1] Failed to read file: " + patchFileName, e);
        }
        return null;
    }

    private <T> T patchAlt(Object entity, String patchFileName, Class<T> valueType) {
        try {
            _log.logDebug("Failed to parse patch file initial, trying a different way: " + patchFileName, true);
            Reader reader = new FileReader(patchFileName);
            JsonNode[] patchAsNode = _mapper.readValue(reader, JsonNode[].class);
            reader.close();
            JsonNode entityAsNode = _mapper.valueToTree(entity);
            JsonNode modifiedAsNode = entityAsNode;
            for(int i = 0; i < patchAsNode.length; i++) {
                JsonNode jsonNode = patchAsNode[i];
                try {
                    JsonPatch patchTool = JsonPatch.fromJson(jsonNode);
                    modifiedAsNode = patchTool.apply(modifiedAsNode);
                }
                catch(JsonPatchException e1) {}
            }
            return _mapper.treeToValue(modifiedAsNode, valueType);
        }
        catch (IOException e1) {
            _log.logError("[IOE2] Failed to read file: " + patchFileName, e1);
        }
        return null;
    }

    private PatchResult updateNodes(Ingredient ingredient, ObjectNode[] nodes, PatchResult result) {
        if(result.foundFood && result.foundPrice) {
            return result;
        }
        for(int j = 0; j < nodes.length; j++) {
            ObjectNode node = nodes[j];
            String nodeOperation = node.get("op").asText();
            String nodePath = node.get("path").asText();
            boolean isReplaceOperation = nodeOperation.equals("replace");
            if(nodeOperation.equals("add") || isReplaceOperation) {
                if (nodePath.contains("foodValue")) {
                    Double nodeValue = node.get("value").asDouble();
                    if (!nodeValue.equals(ingredient.foodValue)) {
                        node.put("value", ingredient.foodValue);
                        result.needsUpdate = true;
                    }
                    if(isReplaceOperation) {
                        result.foundFood = true;
                    }
                } else if (nodePath.contains("price")) {
                    Double nodeValue = node.get("value").asDouble();
                    if (!nodeValue.equals(ingredient.price)) {
                        node.put("value", ingredient.price);
                        result.needsUpdate = true;
                    }
                    if(isReplaceOperation) {
                        result.foundPrice = true;
                    }
                }
            }
        }
        return result;
    }

    private ObjectNode[] createTestNodes(String pathName, Double value) {
        ObjectNode[] nodeArray = new ObjectNode[2];
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray[0] = testNode;
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "add");
        node.put("path", "/" + pathName);
        node.put("value", value);
        nodeArray[1] = node;
        return nodeArray;
    }

    private ObjectNode createReplaceNode(String pathName, Double value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        node.put("value", value);
        return node;
    }


    private class PatchResult {
        public boolean needsUpdate;
        public boolean foundFood;
        public boolean foundPrice;

        public PatchResult() {
            needsUpdate = false;
            foundFood = false;
            foundPrice = false;
        }
    }
}
