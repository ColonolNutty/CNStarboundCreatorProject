package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.jsonhandlers.*;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.PropertyOrder;
import com.colonolnutty.module.shareddata.models.json.ReplaceNode;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
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
public class JsonManipulator implements IReadFiles, IWriteFiles, IRequireNodeProvider {
    private CNLog _log;
    private ArrayList<String> _keysToWrite;
    private IPrettyPrinter _prettyPrinter;
    private IFileWriter _fileWriter;
    private IFileReader _fileReader;
    private NodeProvider _nodeProvider;
    private ArrayList<IJsonHandler> _jsonHandlers;
    private boolean _forceUpdate;

    public JsonManipulator(CNLog log, BaseSettings settings) {
        _log = log;
        _fileWriter = new FileWriterWrapper();
        _fileReader = new FileReaderWrapper();
        _nodeProvider = new NodeProvider();
        _forceUpdate = settings.forceUpdate;
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
            PropertyOrder propertyOrder = _fileReader.read(settings.propertyOrderFile, PropertyOrder.class);
            String[] order = new String[0];
            if(propertyOrder != null) {
                order = propertyOrder.order;
            }
            _prettyPrinter = new JsonPrettyPrinter(_log, order);
        }
        catch(IOException e) {
            _log.error("propertyOrderFile.json file not found", e);
        }

        _jsonHandlers = new ArrayList<IJsonHandler>();
        _jsonHandlers.add(new PriceHandler());
        _jsonHandlers.add(new FoodValueHandler());
        _jsonHandlers.add(new EffectsHandler());
        _jsonHandlers.add(new DescriptionHandler());
    }

    public void setPrettyPrinter(IPrettyPrinter prettyPrinter) { _prettyPrinter = prettyPrinter; }

    @Override
    public void setFileWriter(IFileWriter writer) {
        _fileWriter = writer;
    }
    @Override
    public void setFileReader(IFileReader reader) { _fileReader = reader; }
    @Override
    public void setNodeProvider(NodeProvider nodeProvider) { _nodeProvider = nodeProvider; }

    public Recipe readRecipe(String path) throws IOException {
        return _fileReader.read(path, Recipe.class);
    }

    public Ingredient readIngredient(String path) throws IOException {
        return _fileReader.read(path, Ingredient.class);
    }

    public void writeNew(String filePath, Object obj) {
        try {
            File file = _fileWriter.createFile(filePath);
            String toWriteObj = _fileWriter.writeValueAsString(obj);
            JSONObject toWrite = new JSONObject(toWriteObj);

            String result = _prettyPrinter.makePretty(toWrite, 0);
            if(result == null || result.equals("")) {
                return;
            }
            _fileWriter.writeData(file, result);
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

            String result = _prettyPrinter.makePretty(combined, 0);
            if(result == null || result.equals("")) {
                return;
            }
            _fileWriter.writeData(file, result);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to write file: " + filePath, e);
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

            String result = _prettyPrinter.makePretty(combined, 0);
            if(result == null || result.equals("")) {
                return;
            }
            _fileWriter.writeData(filePath, result);
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
            patchNodes = _fileReader.read(ingredient.patchFile, ArrayNode.class);
        } catch (IOException e) {
            _log.error("[IOE] Failed to read file: " + ingredient.patchFile, e);
        }
        if(patchNodes == null) {
            _log.writeToAll(4, skipMessage);
            return;
        }
        try {
            //Contains the new patch nodes, overwrite the patch file with these
            SplitNodeTypes splitNodes = createPatchNodes(patchNodes, ingredient);
            if(splitNodes == null) {
                return;
            }
            //Overwrite the patch file with the new nodes (splitNodes)
            ArrayNode newPatch = _nodeProvider.createArrayNode();
            for(int i = 0; i < splitNodes.TestNodes.size(); i++) {
                newPatch.add(splitNodes.TestNodes.get(i));
            }

            ArrayNode nonTestNodesArray = _nodeProvider.createArrayNode();
            for(int i = 0; i < splitNodes.NonTestNodes.size(); i++) {
                nonTestNodesArray.add(splitNodes.NonTestNodes.get(i));
            }
            if(nonTestNodesArray.size() == 0) {
                newPatch.add(nonTestNodesArray);
            }
            _log.writeToAll(4, "Applying update to patch file: " + ingredient.getName());
            logValues(ingredient);
            String prettyJson = _prettyPrinter.makePretty(newPatch, 0);
            if(prettyJson == null || prettyJson.equals("")) {
                return;
            }
            _fileWriter.writeData(ingredient.patchFile, prettyJson);
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
            _fileWriter.writeData(absPath, prettyJson);
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
            ArrayNode patchAsNode = _fileReader.read(patchFileName, ArrayNode.class);
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
                        JsonNode entityAsNode = _fileWriter.valueToTree(entity);
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
                JsonNode entityAsNode = _fileWriter.valueToTree(entity);
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
            return _fileWriter.treeToValue(modNode, valueType);
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

    private SplitNodeTypes createPatchNodes(ArrayNode patchNodes, Ingredient ingredient) {
        SplitNodeTypes nodeTypes = splitNodes(patchNodes);
        Hashtable<String, NodeAvailability> nodesAvailability = getNodeAvailability(nodeTypes);
        if(nodesAvailability.size() == 0) {
            return null;
        }
        boolean needsUpdate = false;
        ArrayList<JsonNode> newTestNodes = new ArrayList<JsonNode>();
        ArrayList<JsonNode> newNonTestNodes = new ArrayList<JsonNode>();
        Enumeration<String> keys = nodesAvailability.keys();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            NodeAvailability nodeAvailability = nodesAvailability.get(key);
            IJsonHandler handler = findNodeHandler(nodeAvailability.PathName);
            if(handler == null) {
                if(nodeAvailability.TestNode != null && nodeAvailability.NonTestNode != null) {
                    newTestNodes.add(nodeAvailability.TestNode);
                }
                if(nodeAvailability.NonTestNode != null) {
                    newNonTestNodes.add(nodeAvailability.NonTestNode);
                }
                continue;
            }
            if(nodeAvailability.NonTestNode == null) {
                JsonNode replaceNode = handler.createReplaceNode(ingredient);
                if(replaceNode != null) {
                    nodeAvailability.NonTestNode = replaceNode;
                    newNonTestNodes.add(replaceNode);
                }
            }

            if(nodeAvailability.TestNode == null && nodeAvailability.NonTestNode != null) {
                JsonNode testNode = handler.createTestNode(ingredient);
                if(testNode != null) {
                    nodeAvailability.TestNode = testNode;
                    newTestNodes.add(testNode);
                }
            }

            //If forcing an update, no need to check, needsUpdate will be true no matter what
            //If needsUpdate already true, no need to check, updating anyways
            if(_forceUpdate || needsUpdate) {
                continue;
            }

            if(nodeAvailability.NonTestNode != null) {
                needsUpdate = handler.needsUpdate(nodeAvailability.NonTestNode, ingredient);
            }
        }
        if(newTestNodes.size() == 0 && newNonTestNodes.size() == 0) {
            return null;
        }
        if(_forceUpdate || needsUpdate) {
            return new SplitNodeTypes(newTestNodes, newNonTestNodes);
        }
        return null;
    }

    private IJsonHandler findNodeHandler(String pathName) {
        IJsonHandler found = null;
        for(int i = 0; i < _jsonHandlers.size(); i++) {
            IJsonHandler handler = _jsonHandlers.get(i);
            if(handler.canHandle(pathName)) {
                found = handler;
                i = _jsonHandlers.size();
            }
        }
        return found;
    }

    private Hashtable<String, NodeAvailability> getNodeAvailability(SplitNodeTypes splitNodeTypes) {
        Hashtable<String, NodeAvailability> nodeMap = new Hashtable<String, NodeAvailability>();

        for(int i = 0; i < splitNodeTypes.TestNodes.size(); i++) {
            JsonNode testNode = splitNodeTypes.TestNodes.get(i);
            String nodePathName = getNodePathName(testNode);
            if(nodePathName == null) {
                continue;
            }
            NodeAvailability nodeAvailable;
            if(nodeMap.containsKey(nodePathName)) {
                nodeAvailable = nodeMap.get(nodePathName);
            }
            else {
                nodeAvailable = new NodeAvailability(nodePathName);
                nodeMap.put(nodePathName, nodeAvailable);
            }
            nodeAvailable.PathName = nodePathName;
            if(nodeAvailable.TestNode == null) {
                nodeAvailable.TestNode = testNode;
            }
        }

        for(int i = 0; i < splitNodeTypes.NonTestNodes.size(); i++) {
            JsonNode nonTestNode = splitNodeTypes.NonTestNodes.get(i);
            String nodePathName = getNodePathName(nonTestNode);
            if(nodePathName == null) {
                continue;
            }
            NodeAvailability nodeAvailable;
            if(nodeMap.containsKey(nodePathName)) {
                nodeAvailable = nodeMap.get(nodePathName);
            }
            else {
                nodeAvailable = new NodeAvailability(nodePathName);
                nodeMap.put(nodePathName, nodeAvailable);
            }
            nodeAvailable.PathName = nodePathName;
            if(nodeAvailable.NonTestNode == null) {
                nodeAvailable.NonTestNode = nonTestNode;
            }
        }
        return nodeMap;
    }

    private String getNodePathName(JsonNode node) {
        String pathName = null;
        if(node.isArray() && node.size() > 0) {
            for(int i = 0; i < node.size(); i++) {
                JsonNode testSubNode = node.get(i);
                pathName = getNodePathName(testSubNode);
                if(pathName != null) {
                    i = node.size();
                }
            }
        }
        else if(!node.isArray()
                && node.has("path")) {
            pathName = node.get("path").asText();
        }
        return pathName;
    }

    private SplitNodeTypes splitNodes(ArrayNode patchNodes) {
        ArrayList<JsonNode> testNodes = new ArrayList<JsonNode>();
        ArrayList<JsonNode> nonTestNodes = new ArrayList<JsonNode>();
        if(patchNodes.size() == 0) {
            return new SplitNodeTypes(testNodes, nonTestNodes);
        }
        for(int i = 0; i < patchNodes.size(); i++) {
            JsonNode patchNode = patchNodes.get(i);
            if(CNJsonUtils.hasTestNode(patchNode)) {
                testNodes.add(patchNode);
                continue;
            }
            if(patchNode.isArray()) {
                for(int j = 0; j < patchNode.size(); j++) {
                    JsonNode subPatchNode = patchNode.get(j);
                    if(!subPatchNode.isArray()) {
                        nonTestNodes.add(subPatchNode);
                        continue;
                    }

                    for(int k = 0; k < subPatchNode.size(); k++) {
                        nonTestNodes.add(subPatchNode.get(k));
                    }
                }
            }
            else {
                nonTestNodes.add(patchNode);
            }
        }
        return new SplitNodeTypes(testNodes, nonTestNodes);
    }

    private class SplitNodeTypes {
        public ArrayList<JsonNode> TestNodes;
        public ArrayList<JsonNode> NonTestNodes;

        public SplitNodeTypes(ArrayList<JsonNode> testNodes, ArrayList<JsonNode> nonTestNodes) {
            TestNodes = testNodes;
            NonTestNodes = nonTestNodes;
        }
    }

    private class NodeAvailability {
        public String PathName;
        public JsonNode TestNode;
        public JsonNode NonTestNode;

        public NodeAvailability(String pathName) {
            PathName = pathName;
        }
    }
}
