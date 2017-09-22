package com.company;

import com.company.models.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

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
        _keysToWrite.add("effects");
        try {
            _propertyOrder = read("propertyOrder.json", PropertyOrder.class).order;
            _prettyPrinter = new JsonPrettyPrinter(_log, _propertyOrder);
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
            JSONObject combined = combineJsonValues(toWrite, fileData, filePath.endsWith(".consumable"));
            if(combined == null) {
                return;
            }

            String result = _prettyPrinter.makePretty(combined, 0);
            if(result == null || result.equals("")) {
                return;
            }
            Writer writer = new FileWriter(filePath);
            writer.write(result);
            writer.close();
        }
        catch(IOException e) {
            _log.logError("[IOE] Failed to write file: " + filePath, e);
        }
    }

    private JSONObject combineJsonValues(JSONObject toWrite, String existingJson, boolean canUpdateEffects) throws IOException {
        if(existingJson == null) {
            return null;
        }
        try {
            JSONObject existingObject = new JSONObject(existingJson);

            Iterator<String> toUpdateKeys = toWrite.keys();
            while (toUpdateKeys.hasNext()) {
                String key = toUpdateKeys.next();
                if(canWriteKey(key) && (existingObject.has(key) || (key.equals("effects") && canUpdateEffects))) {
                    Object value = toWrite.get(key);
                    if(value != null) {
                        existingObject.put(key, value);
                    }
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
            if(!result.foundFood || !result.foundPrice || !result.foundEffects) {
                if(!result.foundFood) {
                    _log.logDebug("    Food Value not found on patch file: " + ingredient.getName(), true);
                }
                if(!result.foundPrice) {
                    _log.logDebug("    Price not found on patch file: " + ingredient.getName(), true);
                }
                ArrayList<ObjectNode> objectNodes = new ArrayList<ObjectNode>();
                for(int i = 0; i < patchNodes.length; i++) {
                    objectNodes.add(patchNodes[i]);
                }
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
                if (!result.foundEffects) {
                    if(ingredient.effects != null) {
                        objectNodes.add(createReplaceNode("effects", toArrayNode(ingredient.effects)));
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
                String prettyJson = _prettyPrinter.makePretty(patchNodes, 0);
                if(prettyJson == null || prettyJson.equals("")) {
                    return;
                }
                Writer writer = new FileWriter(ingredient.patchFile);
                writer.write(prettyJson);
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

                if(result.foundFood && result.foundPrice && result.foundEffects) {
                    i = patchNodes.length;
                }
            }
            if (!result.foundFood || !result.foundPrice || !result.foundEffects) {
                ArrayList<ObjectNode[]> objectNodes = new ArrayList<ObjectNode[]>();
                for(int i = 0; i < patchNodes.length; i++) {
                    objectNodes.add(patchNodes[i]);
                }
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
                if (!result.foundEffects) {
                    if(ingredient.effects != null) {
                        ObjectNode[] testNodes = createTestNodes("effects", toArrayNode(ingredient.effects));
                        objectNodes.add(testNodes);
                        ObjectNode[] replaceNodes = new ObjectNode[1];
                        replaceNodes[0] = createReplaceNode("effects", toArrayNode(ingredient.effects));
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
                String prettyJson = _prettyPrinter.makePretty(patchNodes, 0);
                if(prettyJson == null || prettyJson.equals("")) {
                    return;
                }
                Writer writer = new FileWriter(ingredient.patchFile);
                writer.write(prettyJson);
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
        if(result.foundFood && result.foundPrice && result.foundEffects) {
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
                else if (nodePath.contains("effects")) {
                    JsonNode[][] effects = CNUtils.toDoubleArray(node.get("value"));
                    if(!ingredient.effectsAreEqual(effects)) {
                        node.put("value", toArrayNode(ingredient.effects));
                        result.needsUpdate = true;
                    }
                    if(isReplaceOperation) {
                        result.foundEffects = true;
                    }
                }
            }
        }
        return result;
    }

    private ArrayNode toArrayNode(JsonNode[][] jsonNodes) {
        ArrayNode node = _mapper.createArrayNode();
        for(int i = 0; i < jsonNodes.length; i++) {
            ArrayNode subNode = toArrayNode(jsonNodes[i]);
            node.add(subNode);
        }
        return node;
    }

    private ArrayNode toArrayNode(JsonNode[] jsonNodes) {
        ArrayNode node = _mapper.createArrayNode();
        for(int i = 0; i < jsonNodes.length; i++) {
            node.add(jsonNodes[i]);
        }
        return node;
    }

    private ObjectNode[] createTestNodes(String pathName, ArrayNode value) throws IOException {
        ObjectNode[] nodeArray = new ObjectNode[2];
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray[0] = testNode;
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "add");
        node.put("path", "/" + pathName);
        ArrayNode arrNode = node.putArray("value");
        for(int i = 0; i < value.size(); i++) {
            arrNode.add(value.get(i));
        }
        nodeArray[1] = node;
        return nodeArray;
    }

    private ObjectNode createReplaceNode(String pathName, ArrayNode value) throws IOException {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        ArrayNode arrNode = node.putArray("value");
        for(int i = 0; i < value.size(); i++) {
            arrNode.add(value.get(i));
        }
        return node;
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

    private int defaultDuration = 20;

    public ArrayList<JsonNode> combineEffects(ArrayList<JsonNode> nodes) {
        ArrayList<JsonNode> combined = new ArrayList<JsonNode>();
        Hashtable<String, ObjectNode> objNodes = new Hashtable<String, ObjectNode>();
        for(int i = 0; i < nodes.size(); i++) {
            JsonNode node = nodes.get(i);
            ObjectNode objNode = _mapper.createObjectNode();
            String key;
            if(node.isDouble() || node.isInt() || node.isBoolean() || node.isTextual()) {
                key = node.asText();
                objNode.put("effect", key);
                objNode.put("duration", defaultDuration);
            }
            else {
                key = node.get("effect").asText();
                objNode.put("effect", key);
                if(node.has("duration")) {
                    objNode.put("duration", node.get("duration").asDouble(defaultDuration));
                }
                else {
                    objNode.put("duration", defaultDuration);
                }
            }
            objNode.put("effect", key);
            if(objNodes.containsKey(key)) {
                ObjectNode existing = objNodes.get(key);
                Double existingDuration = existing.get("duration").asDouble(defaultDuration);
                existing.put("duration", existingDuration + objNode.get("duration").asDouble(defaultDuration));
            }
            else {
                objNodes.put(key, objNode);
            }
        }

        Enumeration<ObjectNode> objNodesValues = objNodes.elements();
        while(objNodesValues.hasMoreElements()) {
            ObjectNode objNode = objNodesValues.nextElement();
            try {
                JsonNode node = _mapper.readTree(objNode.toString());
                combined.add(node);
            }
            catch(IOException e) {
            }
        }

        return combined;
    }

    private class PatchResult {
        public boolean needsUpdate;
        public boolean foundFood;
        public boolean foundPrice;
        public boolean foundEffects;

        public PatchResult() {
            needsUpdate = false;
            foundFood = false;
            foundPrice = false;
            foundEffects = false;
        }
    }
}
