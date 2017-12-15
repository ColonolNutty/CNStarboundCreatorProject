package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.PropertyOrder;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
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
    private CNLog _log;
    private ObjectMapper _mapper;
    private ArrayList<String> _keysToWrite;
    private JsonPrettyPrinter _prettyPrinter;

    public JsonManipulator(CNLog log, BaseSettings settings) {
        _log = log;
        JsonFactory jf = new JsonFactory();
        jf.enable(JsonParser.Feature.ALLOW_COMMENTS);
        _mapper = new ObjectMapper(jf);
        _mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        if(settings.propertiesToUpdate == null) {
            _keysToWrite = new ArrayList<String>();
        }
        else {
            _keysToWrite = CNCollectionUtils.toStringArrayList(settings.propertiesToUpdate);
        }
        if(settings.propertyOrderFile == null) {
            _prettyPrinter = new JsonPrettyPrinter(_log, new String[0]);
            return;
        }
        try {
            PropertyOrder propertyOrder = read(settings.propertyOrderFile, PropertyOrder.class);
            String[] order = new String[0];
            if(propertyOrder != null) {
                order = propertyOrder.order;
            }
            _prettyPrinter = new JsonPrettyPrinter(_log, order);
        }
        catch(IOException e) {
            _log.error("propertyOrderFile.json file not found", e);
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

    public void writeNew(String filePath, Object obj) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            String toWriteObj = _mapper.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);

            String result = _prettyPrinter.makePretty(toWrite, 0);
            if(result == null || result.equals("")) {
                return;
            }
            Writer writer = new FileWriter(file);
            writer.write(result);
            writer.close();
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + filePath, e);
        }
    }

    public void writeNewWithTemplate(String templateFile, String filePath, Object obj) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            String fileData = readExistingFile(templateFile);
            String toWriteObj = _mapper.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);
            JSONObject combined = applyMissingProperties(toWrite, fileData);
            if(combined == null) {
                return;
            }

            String result = _prettyPrinter.makePretty(combined, 0);
            if(result == null || result.equals("")) {
                return;
            }
            Writer writer = new FileWriter(file);
            writer.write(result);
            writer.close();
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + filePath, e);
        }
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
            _log.error("[IOE] Failed to write file: " + filePath, e);
        }
    }

    public void writeAsPatch(Ingredient ingredient) {
        if (ingredient.patchFile == null) {
            return;
        }
        String skipMessage = "Skipping patch file for: " + ingredient.getName();
        ArrayNode patchNodes = null;
        try {
            Reader reader = new FileReader(ingredient.patchFile);
            patchNodes = _mapper.readValue(reader, ArrayNode.class);
            reader.close();
        } catch (IOException e) {
            _log.error("[IOE] Failed to read file: " + ingredient.patchFile, e);
        }
        if(patchNodes == null) {
            _log.writeToAll(4, skipMessage);
            return;
        }
        try {
            PatchResult result = new PatchResult();
            result = updateNodes(ingredient, patchNodes, result);
            if (!result.needsUpdate) {
                _log.writeToAll(4, skipMessage);
                return;
            }
            if (!result.foundFood) {
                _log.writeToAll(4, "Food Value not found on patch file: " + ingredient.getName());
            }
            if (!result.foundPrice) {
                _log.writeToAll(4, "Price not found on patch file: " + ingredient.getName());
            }
            if (!result.foundDescription) {
                _log.writeToAll(4, "Description not found on patch file: " + ingredient.getName());
            }
            if (!result.foundEffects) {
                _log.writeToAll(4, "Effects not found on patch file: " + ingredient.getName());
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
            if (!result.foundPrice && ingredient.hasPrice()) {
                nodes = addReplaceNode(nodes, "price", ingredient.price);
                result.needsUpdate = true;
            }
            if (!result.foundDescription
                    && ingredient.hasDescription()) {
                nodes = addReplaceNode(nodes, "description", ingredient.description);
                result.needsUpdate = true;
            }
            boolean isConsumable = ingredient.filePath.endsWith("consumable");
            if (!result.foundFood
                    && ingredient.hasFoodValue()
                    && isConsumable) {
                nodes = addReplaceNode(nodes, "foodValue", ingredient.foodValue);
                result.needsUpdate = true;
            }
            if (!result.foundEffects
                    && ingredient.hasEffects()
                    && isConsumable) {
                nodes = addReplaceNode(nodes, "effects", ingredient.effects);
                result.needsUpdate = true;
            }
            if (!result.needsUpdate) {
                _log.writeToAll(4, skipMessage);
                return;
            }
            patchNodes = nodes;
            _log.writeToAll(4, "Applying update to patch file: " + ingredient.getName());
            logValues(ingredient);
            String prettyJson = _prettyPrinter.makePretty(patchNodes, 0);
            if(prettyJson == null || prettyJson.equals("")) {
                return;
            }
            Writer writer = new FileWriter(ingredient.patchFile);
            writer.write(prettyJson);
            writer.close();
        }
        catch(JsonMappingException e) { _log.error("Error while mapping: " + ingredient.patchFile, e); }
        catch (IOException e) { _log.error("[IOE] Failed to read file: " + ingredient.patchFile, e); }
    }

    public void writeNewPatch(String fileName, ArrayNode patchNodes) {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        try {
            String prettyJson = _prettyPrinter.makePretty(patchNodes, 0);
            if (prettyJson == null || prettyJson.equals("")) {
                return;
            }
            String absPath = file.getAbsolutePath();
            Writer writer = new FileWriter(absPath);
            writer.write(prettyJson);
            writer.close();
        }
        catch(IOException e) {
            _log.error("[IOE] Problems writing file: " + file.getAbsolutePath(), e);
        }
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
                        _log.error("Json Test Patch \"" + patchFileName + "\"\n    " + patchFileAsJson, e);
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
            _log.error("Json file being patched:\n    " + patchedFileAsJson, e);
        }
        catch(JsonPatchException e) {
            if(patchFileAsJson != null) {
                _log.error("Json \"" + patchFileName + "\"\n    " + patchFileAsJson, e);
            }
            else {
                _log.error("When parsing patch file: " + patchFileName, e);
            }
        }
        catch (IOException e) {
            _log.error("[IOE1] Failed to read file: " + patchFileName, e);
        }
        return null;
    }

    private JSONObject applyMissingProperties(JSONObject toWrite,
                                              String existingJson) throws IOException {
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

    private JSONObject combineJsonValues(JSONObject toWrite,
                                         String existingJson,
                                         boolean canUpdateEffects) throws IOException {
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

    private void logValues(Ingredient ingredient) {
        boolean logCombined = false;
        String combined = "New Values: ";
        _log.startSubBundle("New Values");
        if(ingredient.hasPrice()) {
            _log.writeToBundle("Price: " + ingredient.price);
            combined += " p: " + ingredient.price;
            logCombined = true;
        }
        if(ingredient.hasFoodValue()) {
            _log.writeToBundle("Food Value: " + ingredient.foodValue);
            combined += " fv: " + ingredient.foodValue;
            logCombined = true;
        }
        if(ingredient.hasFoodValue()) {
            _log.writeToBundle("Food Value: " + ingredient.foodValue);
            combined += " fv: " + ingredient.foodValue;
            logCombined = true;
        }
        if(ingredient.hasDescription()) {
            _log.writeToBundle("Description: " + ingredient.description);
            combined += " description: " + ingredient.description;
            logCombined = true;
        }
        if(ingredient.hasEffects()) {
            _log.startSubBundle("New Effects");
            combined += " effects: ";
            for(int i = 0; i < ingredient.effects.size(); i++) {
                ArrayNode subEffects = (ArrayNode)ingredient.effects.get(i);
                for(int j = 0; j < subEffects.size(); j++) {
                    JsonNode subEffect = subEffects.get(j);
                    String name = subEffect.get("effect").asText();
                    String duration = subEffect.get("duration").asText();

                    _log.writeToBundle("Name: " + name + " Duration: " + duration);
                    combined += "{ n: \"" + name + "\" d: " + duration + " }";
                    if((j + 1) < subEffects.size()) {
                        combined += ", ";
                    }
                }
            }
            _log.endSubBundle();
            logCombined = true;
        }
        _log.endSubBundle();
        if(logCombined) {
            _log.debug(combined, 4);
        }
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

    private ArrayNode addReplaceNode(ArrayNode node, String opName, String value) {
        node.add(createTestNodes(opName, value));
        ArrayNode replaceNodes = _mapper.createArrayNode();
        replaceNodes.add(createReplaceNode(opName, value));
        node.add(replaceNodes);
        return node;
    }

    private PatchResult updateNodes(Ingredient ingredient, ArrayNode nodes, PatchResult result) {
        if(result.foundFood && result.foundPrice && result.foundEffects && result.foundDescription) {
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
                else if (nodePath.equals("/description")) {
                    String description = objNode.get("value").asText();
                    if(!description.equals("")) {
                        objNode.put("value", "");
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
                }
                else if (nodePath.contains("price")) {
                    Double nodeValue = objNode.get("value").asDouble();
                    if (!nodeValue.equals(ingredient.price)) {
                        objNode.put("value", ingredient.price);
                        result.needsUpdate = true;
                    }
                    result.foundPrice = true;
                }
                else if (nodePath.equals("/description")) {
                    JsonNode description = objNode.get("value");
                    if(!description.asText().equals(ingredient.description)) {
                        objNode.put("value", ingredient.description);
                        result.needsUpdate = true;
                    }
                    result.foundDescription = true;
                }
                else if (nodePath.contains("effects")) {
                    JsonNode effects = objNode.get("value");
                    if(!ingredient.effectsAreEqual(effects)) {
                        if(ingredient.hasEffects()) {
                            ArrayNode arrNode = objNode.putArray("value");
                            for (int i = 0; i < ingredient.effects.size(); i++) {
                                arrNode.add(ingredient.effects.get(i));
                            }
                        }
                        else {
                            ArrayNode arrNode = objNode.putArray("value");
                            arrNode.add(createArrayNode());
                        }
                        result.needsUpdate = true;
                    }
                    result.foundEffects = true;
                }
            }
        }
        return result;
    }

    public ArrayNode createTestNodes(String pathName) {
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

    public ObjectNode createReplaceNode(String pathName, ArrayNode value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        ArrayNode arrNode = node.putArray("value");
        for(int i = 0; i < value.size(); i++) {
            arrNode.add(value.get(i));
        }
        return node;
    }

    public ArrayNode createTestNodes(String pathName, String value) {
        ArrayNode nodeArray = _mapper.createArrayNode();
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray.add(testNode);
        ObjectNode addNode = _mapper.createObjectNode();
        addNode.put("op", "add");
        addNode.put("path", "/" + pathName);
        addNode.put("value", "");
        nodeArray.add(addNode);
        return nodeArray;
    }

    public ArrayNode createTestNodes(String pathName, Double value) {
        ArrayNode nodeArray = _mapper.createArrayNode();
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray.add(testNode);
        ObjectNode addNode = _mapper.createObjectNode();
        addNode.put("op", "add");
        addNode.put("path", "/" + pathName);
        addNode.put("value", 0.0);
        nodeArray.add(addNode);
        return nodeArray;
    }

    public ArrayNode createTestRemoveNodes(String pathName, Double value) {
        ArrayNode nodeArray = _mapper.createArrayNode();
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("value",  value);
        nodeArray.add(testNode);
        ObjectNode addNode = _mapper.createObjectNode();
        addNode.put("op", "remove");
        addNode.put("path", "/" + pathName);
        nodeArray.add(addNode);
        return nodeArray;
    }

    public ObjectNode createReplaceNode(String pathName, Double value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        node.put("value", value);
        return node;
    }

    public ArrayNode createTestNodes(String pathName, Integer value) {
        ArrayNode nodeArray = _mapper.createArrayNode();
        ObjectNode testNode = _mapper.createObjectNode();
        testNode.put("op", "test");
        testNode.put("path", "/" + pathName);
        testNode.put("inverse", true);
        nodeArray.add(testNode);
        ObjectNode addNode = _mapper.createObjectNode();
        addNode.put("op", "add");
        addNode.put("path", "/" + pathName);
        addNode.put("value", 0);
        nodeArray.add(addNode);
        return nodeArray;
    }

    public ObjectNode createReplaceNode(String pathName, String value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        node.put("value", value);
        return node;
    }

    public ObjectNode createReplaceNode(String pathName, Integer value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "replace");
        node.put("path", "/" + pathName);
        node.put("value", value);
        return node;
    }

    public ObjectNode createAddNode(String pathName, String value) {
        ObjectNode node = _mapper.createObjectNode();
        node.put("op", "add");
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
            if(effectDuration == 0) {
                continue;
            }
            ObjectNode objNode = _mapper.createObjectNode();
            objNode.put("effect", effectName);
            objNode.put("duration", effectDuration);
            _log.writeToBundle("Effect name: \"" + effectName + "\", duration: " + effectDuration);
            arrayNode.add(objNode);
        }
        _log.endSubBundle();
        return arrayNode;
    }

    public ArrayNode createArrayNode() {
        return _mapper.createArrayNode();
    }

    public ArrayNode createJsonNode() {
        return _mapper.createArrayNode();
    }


    private class PatchResult {
        public boolean needsUpdate;
        public boolean foundFood;
        public boolean foundPrice;
        public boolean foundEffects;
        public boolean foundDescription;

        public PatchResult() {
            needsUpdate = false;
            foundFood = false;
            foundPrice = false;
            foundEffects = false;
            foundDescription = false;
        }
    }
}
