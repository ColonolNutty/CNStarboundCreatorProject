package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.jsonhandlers.*;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.NodeAvailability;
import com.colonolnutty.module.shareddata.models.PatchNodes;
import com.colonolnutty.module.shareddata.models.PropertyOrder;
import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.prettyprinters.IPrettyPrinter;
import com.colonolnutty.module.shareddata.prettyprinters.JsonNodePrettyPrinter;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 12/23/2017
 * Time: 10:41 AM
 */
public class JsonPatchManipulator implements IReadFiles, IWriteFiles, IRequireNodeProvider {

    private CNLog _log;
    private ArrayList<String> _keysToWrite;
    private IPrettyPrinter _prettyPrinter;
    private IFileWriter _fileWriter;
    private IFileReader _fileReader;
    private NodeProvider _nodeProvider;
    private ArrayList<IJsonHandler> _jsonHandlers;
    private boolean _forceUpdate;

    public JsonPatchManipulator(CNLog log, BaseSettings settings) {
        _log = log;
        _fileWriter = new FileWriterWrapper();
        _fileReader = new FileReaderWrapper();
        _nodeProvider = new NodeProvider();
        if(settings.forceUpdate == null) {
            _forceUpdate = false;
        }
        else {
            _forceUpdate = settings.forceUpdate;
        }

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
        _prettyPrinter = new JsonNodePrettyPrinter();
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
    @Override
    public void setNodeProvider(NodeProvider nodeProvider) { _nodeProvider = nodeProvider; }

    public void write(String patchFileName, Ingredient ingredient) {
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

    public void writeNew(String fileName, ArrayNode patchNodes) {
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

    public PatchNodes sortPatchNodes(JsonNode patchNodes) {
        ArrayList<JsonNode> testNodes = new ArrayList<JsonNode>();
        ArrayList<JsonNode> nonTestNodes = new ArrayList<JsonNode>();
        if(patchNodes == null || !patchNodes.isArray() || patchNodes.size() == 0) {
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


    private PatchNodes createPatchNodes(ArrayNode existingPatchNodes, Ingredient ingredient) {
        PatchNodes sortedPatchNodes = sortPatchNodes(existingPatchNodes);
        Hashtable<String, NodeAvailability> nodesAvailability = getNodeAvailability(sortedPatchNodes);
        return addMissingPatchNodes(nodesAvailability, ingredient);
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
                if(nodeAvailability.TestNode != null
                        && nodeAvailability.NonTestNodes != null
                        && nodeAvailability.NonTestNodes.size() > 0) {
                    newTestNodes.add(nodeAvailability.TestNode);
                }
                if(nodeAvailability.hasNonTestNodes()) {
                    newNonTestNodes.addAll(nodeAvailability.NonTestNodes);
                }
                continue;
            }

            if(!nodeAvailability.hasNonTestNodes()) {
                JsonNode replaceNode = handler.createReplaceNode(ingredient);
                if(replaceNode != null) {
                    nodeAvailability.NonTestNodes = new ArrayList<JsonNode>();
                    nodeAvailability.NonTestNodes.add(replaceNode);
                }
            }

            if(nodeAvailability.TestNode == null
                    && nodeAvailability.hasNonTestNodes()) {
                JsonNode testNode = handler.createTestNode(ingredient);
                if(testNode != null) {
                    nodeAvailability.TestNode = testNode;
                }
            }
            // If there is no NonTestNode then we remove the TestNode. No Changes. No Test.
            if(!nodeAvailability.hasNonTestNodes()) {
                nodeAvailability.TestNode = null;
            }

            if(nodeAvailability.hasNonTestNodes()) {
                newNonTestNodes.addAll(nodeAvailability.NonTestNodes);
            }
            if(nodeAvailability.TestNode != null) {
                newTestNodes.add(nodeAvailability.TestNode);
            }

            //If forcing an update, no need to check, needsUpdate will be true no matter what
            //If needsUpdate already true, no need to check, updating anyways
            if(_forceUpdate || needsUpdate) {
                continue;
            }

            if(nodeAvailability.hasNonTestNodes()) {
                for(JsonNode node : nodeAvailability.NonTestNodes) {
                    needsUpdate = handler.needsUpdate(node, ingredient);
                    if(needsUpdate) {
                        break;
                    }
                }
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

    public Hashtable<String, NodeAvailability> getNodeAvailability(PatchNodes patchNodes) {
        Hashtable<String, NodeAvailability> nodeMap = new Hashtable<String, NodeAvailability>();

        for(JsonNode testNode : patchNodes.TestNodes) {
            String nodePathName = CNJsonUtils.getNodePath(testNode);
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
            String nodePathName = CNJsonUtils.getNodePath(nonTestNode);
            if(nodePathName == null) {
                continue;
            }
            if(nodePathName.endsWith("/-")) {
                nodePathName = nodePathName.replace("/-", "");
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
            if(!nodeAvailable.hasNonTestNodes()) {
                nodeAvailable.NonTestNodes = new ArrayList<JsonNode>();
            }
            nodeAvailable.NonTestNodes.add(nonTestNode);
        }
        return nodeMap;
    }


    private void logValues(Ingredient ingredient) {
        boolean logCombined = false;
        StringBuilder stringBuilder = new StringBuilder("New Values: ");
        _log.startSubBundle("New Values");
        for(IJsonHandler handler : _jsonHandlers) {
            String shortStringValue = handler.getShortStringValue(ingredient);
            if(shortStringValue == null) {
                continue;
            }
            _log.writeToBundle(shortStringValue);
            stringBuilder.append(" " + shortStringValue);
            logCombined = true;
        }
        _log.endSubBundle();
        if(logCombined) {
            _log.debug(stringBuilder.toString(), 4);
        }
    }
}
