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
        if (ingredient.patchFile == null) {
            return;
        }
        ArrayNode patchNodes = null;
        try {
            Reader reader = new FileReader(ingredient.patchFile);
            patchNodes = _mapper.readValue(reader, ArrayNode.class);
            reader.close();
        } catch (IOException e) {
            _log.logError("[IOE] Failed to read file: " + ingredient.patchFile, e);
        }
        try {
            PatchResult result = new PatchResult();
            result = updateNodes(ingredient, patchNodes, result);
            if (!result.foundFood || !result.foundPrice || !result.foundEffects) {
                if (!result.foundFood) {
                    _log.logDebug("   Food Value not found on patch file: " + ingredient.getName(), true);
                }
                if (!result.foundPrice) {
                    _log.logDebug("    Price not found on patch file: " + ingredient.getName(), true);
                }
                ArrayNode nodes = _mapper.createArrayNode();
                if(patchNodes.size() > 0 && patchNodes.get(0).isArray()) {
                    for(int i = 0; i < patchNodes.size(); i++) {
                        nodes.add(patchNodes.get(i));
                    }
                }
                else {
                    ArrayNode arrNode = _mapper.createArrayNode();
                    for (int i = 0; i < patchNodes.size(); i++) {
                        arrNode.add(patchNodes.get(i));
                    }
                    nodes.add(arrNode);
                }
                if (!result.foundFood) {
                    if (ingredient.hasFoodValue() && ingredient.filePath.endsWith("consumable")) {
                        nodes = addReplaceNode(nodes, "foodValue", ingredient.foodValue);
                        result.needsUpdate = true;
                    }
                }
                if (!result.foundPrice) {
                    if (ingredient.hasPrice()) {
                        nodes = addReplaceNode(nodes, "price", ingredient.price);
                        result.needsUpdate = true;
                    }
                }
                if (!result.foundEffects) {
                    if (ingredient.hasEffects()) {
                        _log.logDebug("Adding replace node for ingredient: " + ingredient.getName() + " effects: " + ingredient.effects.toString(), true);
                        nodes = addReplaceNode(nodes, "effects", ingredient.effects);
                        result.needsUpdate = true;
                    }
                }
                if (!result.needsUpdate) {
                    _log.logInfo("    Skipping patch file for: " + ingredient.getName(), false);
                    return;
                }
                patchNodes = nodes;
            }
            if(!result.needsUpdate) {
                _log.logInfo("    Skipping patch file for: " + ingredient.getName(), false);
                return;
            }
            _log.logInfo("    Applying update to patch file: " + ingredient.getName(), false);
            _log.startSubBundle("New Values");
            if(ingredient.hasPrice()) {
                _log.logDebug("Price: " + ingredient.price, true);
            }
            if(ingredient.hasFoodValue()) {
                _log.logDebug("Food Value: " + ingredient.foodValue, true);
            }
            if(ingredient.hasEffects()) {
                _log.startSubBundle("New Effects");
                for(int i = 0; i < ingredient.effects.size(); i++) {
                    ArrayNode subEffects = (ArrayNode)ingredient.effects.get(i);
                    for(int j = 0; j < subEffects.size(); j++) {
                        JsonNode subEffect = subEffects.get(j);
                        String name = subEffect.get("effect").asText();
                        String duration = subEffect.get("duration").asText();

                        _log.logDebug("Name: " + name + " Duration: " + duration, true);
                    }
                }
                _log.endSubBundle();
            }
            _log.endSubBundle();
            _log.logDebug("  New Patch: " + patchNodes.toString(), true);
            String prettyJson = _prettyPrinter.makePretty(patchNodes, 0);
            if(prettyJson == null || prettyJson.equals("")) {
                return;
            }
            Writer writer = new FileWriter(ingredient.patchFile);
            writer.write(prettyJson);
            writer.close();
        }
        catch(JsonMappingException e) { _log.logError("Error while mapping: " + ingredient.patchFile, e); }
        catch (IOException e) { _log.logError("[IOE] Failed to read file: " + ingredient.patchFile, e); }
    }

    private ArrayNode addReplaceNode(ArrayNode node, String opName, ArrayNode value) throws IOException {
        node.add(createTestNodes(opName));
        ArrayNode replaceNodes = _mapper.createArrayNode();
        replaceNodes.add(createReplaceNode(opName, value));
        node.add(replaceNodes);
        return node;
    }

    private ArrayNode addReplaceNode(ArrayNode node, String opName, Double value) {
        node.add(createTestNodes(opName, value));
        ArrayNode replaceNodes = _mapper.createArrayNode();
        replaceNodes.add(createReplaceNode(opName, value));
        node.add(replaceNodes);
        return node;
    }

    public <T> T patch(Object entity, String patchFileName, Class<T> valueType) {
        if(patchFileName == null) {
            return null;
        }
        String patchedFileAsJson = null;
        String patchFileAsJson = null;
        try {
            Reader reader = new FileReader(patchFileName);
            ArrayNode patchAsNode = _mapper.readValue(reader, ArrayNode.class);
            reader.close();
            patchFileAsJson = patchAsNode.toString();
            if(!patchAsNode.isArray()) {
                return null;
            }
            JsonNode modifiedAsNode = null;
            if(patchAsNode.size() > 0 && patchAsNode.get(0).isArray()) {
                for(JsonNode patch : patchAsNode) {
                    try {
                        patchFileAsJson = patch.toString();
                        JsonPatch patchTool = JsonPatch.fromJson(patch);
                        JsonNode entityAsNode = _mapper.valueToTree(entity);
                        modifiedAsNode = patchTool.apply(entityAsNode);
                        patchedFileAsJson = modifiedAsNode.toString();
                    }
                    catch(JsonPatchException e) {
                        _log.logError("Json Test Patch \"" + patchFileName + "\"\n    " + patchFileAsJson, e);
                    }
                }
            }
            else if (patchAsNode.size() == 0) {
                return null;
            }
            else {
                JsonPatch patchTool = JsonPatch.fromJson(patchAsNode);
                JsonNode entityAsNode = _mapper.valueToTree(entity);
                modifiedAsNode = patchTool.apply(entityAsNode);
            }
            if(modifiedAsNode == null) {
                return null;
            }
            ObjectNode modNode = (ObjectNode) modifiedAsNode;
            if(!modifiedAsNode.has("effects")) {
                modNode.putArray("effects");
            }
            else {
                JsonNode node = modifiedAsNode.get("effects");
                if(node != null && node.isNull()) {
                    modNode.putArray("effects");
                }
            }
            patchedFileAsJson = modNode.toString();
            return _mapper.treeToValue(modNode, valueType);
        }
        catch(JsonMappingException e) {
            _log.logError("Json file being patched:\n    " + patchedFileAsJson, e);
        }
        catch(JsonPatchException e) {
            if(patchFileAsJson != null) {
                _log.logError("Json \"" + patchFileName + "\"\n    " + patchFileAsJson, e);
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

    private PatchResult updateNodes(Ingredient ingredient, ArrayNode nodes, PatchResult result) {
        if(result.foundFood && result.foundPrice && result.foundEffects) {
            return result;
        }
        for(int j = 0; j < nodes.size(); j++) {
            JsonNode node = nodes.get(j);
            if(node.isArray()) {
                result = updateNodes(ingredient, (ArrayNode)node, result);
                continue;
            }
            if(!node.isObject()) {
                continue;
            }
            ObjectNode objNode = (ObjectNode)nodes.get(j);
            String nodeOperation = objNode.get("op").asText();
            String nodePath = objNode.get("path").asText();
            boolean isReplaceOperation = nodeOperation.equals("replace");
            if(nodeOperation.equals("add")) {
                if (nodePath.contains("foodValue") || nodePath.contains("price")) {
                    Double nodeValue = objNode.get("value").asDouble();
                    if(nodeValue != 0.0) {
                        objNode.put("value", 0.0);
                        result.needsUpdate = true;
                    }
                }
                else if (nodePath.contains("effects")) {
                    JsonNode effects = objNode.get("value");
                    if(!effects.isArray() || effects.size() != 0 || effects.get(0).size() != 0) {
                        ArrayNode arrNode = objNode.putArray("value");
                        arrNode.add(_mapper.createArrayNode());
                        result.needsUpdate = true;
                    }
                }
            }
            else if(isReplaceOperation) {
                if (nodePath.contains("foodValue")) {
                    Double nodeValue = objNode.get("value").asDouble();
                    if (!nodeValue.equals(ingredient.foodValue)) {
                        objNode.put("value", ingredient.foodValue);
                        result.needsUpdate = true;
                    }
                    result.foundFood = true;
                } else if (nodePath.contains("price")) {
                    Double nodeValue = objNode.get("value").asDouble();
                    if (!nodeValue.equals(ingredient.price)) {
                        objNode.put("value", ingredient.price);
                        result.needsUpdate = true;
                    }
                    result.foundPrice = true;
                }
                else if (nodePath.contains("effects")) {
                    JsonNode effects = objNode.get("value");
                    if(!ingredient.effectsAreEqual(effects)) {
                        ArrayNode arrNode = objNode.putArray("value");
                        for (int i = 0; i < ingredient.effects.size(); i++) {
                            arrNode.add(ingredient.effects.get(i));
                        }
                        result.needsUpdate = true;
                    }
                    result.foundEffects = true;
                }
            }
        }
        return result;
    }

    private ArrayNode createTestNodes(String pathName) throws IOException {
        ArrayNode nodeArray = _mapper.createArrayNode();
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray.add(testNode);
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "add");
        node.put("path", "/" + pathName);
        ArrayNode arrNode = node.putArray("value");
        arrNode.add(_mapper.createArrayNode());
        nodeArray.add(node);
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

    private ArrayNode createTestNodes(String pathName, Double value) {
        ArrayNode nodeArray = _mapper.createArrayNode();
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray.add(testNode);
        ObjectNode addNode = _mapper.createObjectNode();
        addNode.put("op", "add");
        addNode.put("path", "/" + pathName);
        addNode.put("value", value);
        nodeArray.add(addNode);
        return nodeArray;
    }

    private ObjectNode createReplaceNode(String pathName, Double value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        node.put("value", value);
        return node;
    }

    public ArrayNode toEffectsArrayNode(String ingredientName, Hashtable<String, Integer> effects, Double outputCount) {
        ArrayNode arrayNode = _mapper.createArrayNode();
        if(effects.isEmpty()) {
            return arrayNode;
        }
        _log.startSubBundle("New effects for " + ingredientName);

        Enumeration<String> effectKeys = effects.keys();
        while(effectKeys.hasMoreElements()) {
            String effectName = effectKeys.nextElement();
            int effectDuration = (int)(effects.get(effectName)/outputCount);
            ObjectNode objNode = _mapper.createObjectNode();
            objNode.put("effect", effectName);
            objNode.put("duration", effectDuration);
            _log.logDebug("    Effect name: \"" + effectName + "\", duration: " + effectDuration, true);
            arrayNode.add(objNode);
        }
        _log.endSubBundle();
        return arrayNode;
    }

    public ArrayNode createArrayNode() {
        return _mapper.createArrayNode();
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
