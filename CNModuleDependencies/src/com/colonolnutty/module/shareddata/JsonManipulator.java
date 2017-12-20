package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.jsonhandlers.*;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.PropertyOrder;
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

    public void writeAsPatch(String patchFileName, Ingredient ingredient) {
        if (patchFileName == null) {
            return;
        }
        String skipMessage = "Skipping applyPatch file for: " + ingredient.getName();
        ArrayNode existingPatchNodes = null;
        try {
            existingPatchNodes = _fileReader.read(patchFileName, ArrayNode.class);
        } catch (IOException e) {
            _log.error("[IOE] Failed to read file: " + patchFileName, e);
        }
        if(existingPatchNodes == null) {
            _log.writeToAll(4, skipMessage);
            return;
        }
        try {
            //Contains the new applyPatch nodes, overwrite the applyPatch file with these
            PatchNodes newPatchNodes = createPatchNodes(existingPatchNodes, ingredient);
            if(newPatchNodes == null) {
                _log.writeToAll(4, skipMessage);
                return;
            }
            //Overwrite the applyPatch file with the new nodes (sortPatchNodes)
            ArrayNode newPatch = _nodeProvider.createArrayNode();
            for(JsonNode testNode : newPatchNodes.TestNodes) {
                newPatch.add(testNode);
            }

            ArrayNode nonTestNodesArray = _nodeProvider.createArrayNode();
            for(JsonNode nonTestNode : newPatchNodes.NonTestNodes) {
                nonTestNodesArray.add(nonTestNode);
            }
            if(nonTestNodesArray.size() > 0) {
                newPatch.add(nonTestNodesArray);
            }
            _log.writeToAll(4, "Applying update to applyPatch file: " + ingredient.getName());
            logValues(ingredient);
            String prettyJson = _prettyPrinter.makePretty(newPatch, 0);
            if(prettyJson == null || prettyJson.equals("")) {
                _log.writeToAll(4, skipMessage);
                return;
            }
            _fileWriter.writeData(patchFileName, prettyJson);
        }
        catch(JsonMappingException e) { _log.error("Error while mapping: " + patchFileName, e); }
        catch (IOException e) { _log.error("[IOE] Failed to read file: " + patchFileName, e); }
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

    public <T> T applyPatch(Object entity, String patchFileName, Class<T> valueType) {
        if(patchFileName == null) {
            return null;
        }
        String patchedFileAsJson = null;
        String patchFileAsJson = null;
        try {
            ArrayNode existingPatchNodes = _fileReader.read(patchFileName, ArrayNode.class);
            patchFileAsJson = existingPatchNodes.toString();
            if(!existingPatchNodes.isArray()) {
                return null;
            }
            JsonNode patchedNode = null;
            //To handle starbound patch files
            if(existingPatchNodes.size() > 0 && existingPatchNodes.get(0).isArray()) {
                for(JsonNode patch : existingPatchNodes) {
                    try {
                        patchFileAsJson = patch.toString();
                        JsonPatch patchTool = JsonPatch.fromJson(patch);
                        if(patchedNode == null) {
                            patchedNode = patchTool.apply(_fileWriter.valueToTree(entity));
                        }
                        else {
                            patchedNode = patchTool.apply(patchedNode);
                        }
                        patchedFileAsJson = patchedNode.toString();
                    }
                    catch(JsonPatchException e) {
                        //_log.error("Json Test Patch \"" + patchFileName + "\"\n    " + patchFileAsJson, e);
                    }
                }
            }
            else if (existingPatchNodes.size() == 0) {
                return null;
            }
            //To handle normal patch files
            else {
                JsonPatch patchTool = JsonPatch.fromJson(existingPatchNodes);
                JsonNode entityAsNode = _fileWriter.valueToTree(entity);
                patchedNode = patchTool.apply(entityAsNode);
            }

            if(patchedNode == null) {
                return null;
            }
            ObjectNode newNode = (ObjectNode) patchedNode;
            if(!patchedNode.has("effects")) {
                newNode.putArray("effects");
            }
            else {
                JsonNode node = patchedNode.get("effects");
                if(node != null && node.isNull()) {
                    newNode.putArray("effects");
                }
            }
            patchedFileAsJson = newNode.toString();
            return _fileWriter.treeToValue(newNode, valueType);
        }
        catch(JsonMappingException e) {
            _log.error("Json file being patched:\n    " + patchedFileAsJson, e);
        }
        catch(JsonPatchException e) {
            if(patchFileAsJson != null) {
                _log.error("Json \"" + patchFileName + "\"\n    " + patchFileAsJson, e);
            }
            else {
                _log.error("When parsing applyPatch file: " + patchFileName, e);
            }
        }
        catch (IOException e) {
            _log.error("[IOE1] Failed to read file: " + patchFileName, e);
        }
        return null;
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

    private void logValues(Ingredient ingredient) {
        boolean logCombined = false;
        StringBuilder stringBuilder = new StringBuilder("New Values: ");
        _log.startSubBundle("New Values");
        if(ingredient.hasPrice()) {
            _log.writeToBundle("Price: " + ingredient.price);
            stringBuilder.append(" p: " + ingredient.price);
            logCombined = true;
        }
        if(ingredient.hasFoodValue()) {
            _log.writeToBundle("Food Value: " + ingredient.foodValue);
            stringBuilder.append(" fv: " + ingredient.foodValue);
            logCombined = true;
        }
        if(ingredient.hasFoodValue()) {
            _log.writeToBundle("Food Value: " + ingredient.foodValue);
            stringBuilder.append(" fv: " + ingredient.foodValue);
            logCombined = true;
        }
        if(ingredient.hasDescription()) {
            _log.writeToBundle("Description: " + ingredient.description);
            stringBuilder.append(" description: " + ingredient.description);
            logCombined = true;
        }
        if(ingredient.hasEffects()) {
            _log.startSubBundle("New Effects");
            stringBuilder.append(" effects: ");
            for(JsonNode effectNodes : ingredient.effects) {
                JsonNode lastEffect = effectNodes.get(effectNodes.size() - 1);
                for(JsonNode effectNode : effectNodes) {
                    String name = effectNode.get("effect").asText();
                    String duration = effectNode.get("duration").asText();

                    _log.writeToBundle("Name: " + name + " Duration: " + duration);
                    stringBuilder.append("{ n: \"" + name + "\" d: " + duration + " }");
                    if(!effectNode.equals(lastEffect)) {
                        stringBuilder.append(", ");
                    }
                }
            }
            _log.endSubBundle();
            logCombined = true;
        }
        _log.endSubBundle();
        if(logCombined) {
            _log.debug(stringBuilder.toString(), 4);
        }
    }

    private PatchNodes createPatchNodes(ArrayNode existingPatchNodes, Ingredient ingredient) {
        PatchNodes sortedPatchNodes = sortPatchNodes(existingPatchNodes);
        Hashtable<String, NodeAvailability> nodesAvailability = getNodeAvailability(sortedPatchNodes);
        return addMissingPatchNodes(nodesAvailability, ingredient);
    }

    private IJsonHandler findNodeHandler(String pathName) {
        IJsonHandler found = null;
        for(IJsonHandler handler : _jsonHandlers) {
            if(handler.canHandle(pathName)) {
                found = handler;
                break;
            }
        }
        return found;
    }

    private PatchNodes addMissingPatchNodes(Hashtable<String, NodeAvailability> nodesAvailability, Ingredient ingredient) {
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
                }
            }

            if(nodeAvailability.TestNode == null && nodeAvailability.NonTestNode != null) {
                JsonNode testNode = handler.createTestNode(ingredient);
                if(testNode != null) {
                    nodeAvailability.TestNode = testNode;
                }
            }
            // If there is no NonTestNode then we remove the TestNode. No Changes. No Test.
            if(nodeAvailability.NonTestNode == null) {
                nodeAvailability.TestNode = null;
            }

            if(nodeAvailability.NonTestNode != null) {
                newNonTestNodes.add(nodeAvailability.NonTestNode);
            }
            if(nodeAvailability.TestNode != null) {
                newTestNodes.add(nodeAvailability.TestNode);
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
            return new PatchNodes(newTestNodes, newNonTestNodes);
        }
        return null;
    }

    private Hashtable<String, NodeAvailability> getNodeAvailability(PatchNodes patchNodes) {
        Hashtable<String, NodeAvailability> nodeMap = new Hashtable<String, NodeAvailability>();

        for(JsonNode testNode : patchNodes.TestNodes) {
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

        for(JsonNode nonTestNode : patchNodes.NonTestNodes) {
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
            for(JsonNode subNode : node) {
                pathName = getNodePathName(subNode);
                if(pathName != null) {
                    break;
                }
            }
        }
        else if(!node.isArray()
                && node.has("path")) {
            pathName = node.get("path").asText();
        }
        return pathName;
    }

    private PatchNodes sortPatchNodes(JsonNode patchNodes) {
        ArrayList<JsonNode> testNodes = new ArrayList<JsonNode>();
        ArrayList<JsonNode> nonTestNodes = new ArrayList<JsonNode>();
        if(!patchNodes.isArray() || patchNodes.size() == 0) {
            return new PatchNodes(testNodes, nonTestNodes);
        }
        for(JsonNode patchNode : patchNodes) {
            if(CNJsonUtils.hasTestNode(patchNode)) {
                testNodes.add(patchNode);
                continue;
            }
            if(patchNode.isArray()) {
                PatchNodes subPatchNodes = sortPatchNodes(patchNode);
                for(JsonNode testNode : subPatchNodes.TestNodes) {
                    testNodes.add(testNode);
                }
                for(JsonNode nonTestNode : subPatchNodes.NonTestNodes) {
                    nonTestNodes.add(nonTestNode);
                }
            }
            else {
                nonTestNodes.add(patchNode);
            }
        }
        return new PatchNodes(testNodes, nonTestNodes);
    }

    private class PatchNodes {
        public ArrayList<JsonNode> TestNodes;
        public ArrayList<JsonNode> NonTestNodes;

        public PatchNodes(ArrayList<JsonNode> testNodes, ArrayList<JsonNode> nonTestNodes) {
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
