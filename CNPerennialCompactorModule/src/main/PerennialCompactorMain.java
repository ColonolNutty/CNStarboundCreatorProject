package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.FileReaderWrapper;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.io.IReadFiles;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.models.Farmable;
import com.colonolnutty.module.shareddata.models.ObjectFrames;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.StopWatchTimer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.settings.PandCSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:11 AM
 */
public class PerennialCompactorMain extends MainFunctionModule implements IReadFiles, IRequireNodeProvider {
    private PandCSettings _settings;
    private CNLog _log;
    private IFileReader _fileReader;
    private NodeProvider _nodeProvider;
    private JsonPatchManipulator _patchManipulator;

    public PerennialCompactorMain(PandCSettings settings, CNLog log) {
        _settings = settings;
        _log = log;
        _fileReader = new FileReaderWrapper();
        _nodeProvider = new NodeProvider();
        _patchManipulator = new JsonPatchManipulator(log, settings);
    }

    @Override
    public void run() {
        if(_settings == null) {
            _log.error("No configuration file found, exiting.");
            return;
        }
        ensureCreatePath();
        StopWatchTimer timer = new StopWatchTimer(_log);
        timer.start("Running");

        ArrayList<String> cropLocations = CNCollectionUtils.toArrayList(_settings.locationsOfCrops);
        _log.debug("Locating seeds");

        FileLocator fileLocator = new FileLocator(_log);
        ArrayList<String> seedFiles = fileLocator.getFilePathsByExtension(cropLocations, "seed.object");
        perennializeAndCompact(seedFiles);
        timer.logTime();
    }

    private void perennializeAndCompact(ArrayList<String> seedFiles) {
        if(seedFiles == null || seedFiles.isEmpty()) {
            _log.debug("No seeds found");
            return;
        }
        _log.debug("Seeds found, creating patch files");
        for(String seedFile : seedFiles) {
            writePatchFile(seedFile);
        }
        _log.debug("Finished creating patch files");
    }

    private void writePatchFile(String seedFile) {
        File file = new File(seedFile);
        try {
            Farmable farmable = _fileReader.read(seedFile, Farmable.class);
            _log.startSubBundle(farmable.getName());
            if(_settings.makePatchFiles) {
                String basePathName = _settings.creationPath;
                String relativeModPathName = file.getParentFile().getAbsolutePath().replace(System.getProperty("user.dir") + "\\", "");
                String patchFileName = basePathName + "\\" + relativeModPathName + "\\" + file.getName() + ".patch";
                farmable.patchFile = patchFileName;
                createPatch(file.getParent(), patchFileName, farmable);
            }
            else {
                farmable.filePath = seedFile;
                overrideExisting(file.getAbsolutePath(), farmable);
            }
            _log.endSubBundle();
        }
        catch(IOException e) {
            _log.error("Failed to read file: " + seedFile, e);
        }
    }

    private void overrideExisting(String filePath, Farmable farmable) {
        _log.error("Overriding original files is not supported yet");
    }

    private void createPatch(String seedPath,
                             String patchFileName,
                             Farmable farmable) {
        _log.startSubBundle("Creating patch file for: " + farmable.getName());
        _log.writeToAll(4,"Creating patch file for: " + farmable.getName());
        ArrayNode patchNodes = _nodeProvider.createArrayNode();
        ArrayNode replaceNodes = _nodeProvider.createArrayNode();
        ObjectNode replaceNode = addPerennialNodes(farmable, patchNodes);
        if(replaceNode != null) {
            replaceNodes.add(replaceNode);
        }

        ArrayList<ObjectNode> compactReplaceNodes = addCompactNodes(seedPath, farmable, patchNodes);
        for(ObjectNode objNode : compactReplaceNodes) {
            replaceNodes.add(objNode);
        }

        if(!CNCollectionUtils.isEmpty(replaceNodes)) {
            patchNodes.add(replaceNodes);
            _patchManipulator.writeNew(patchFileName, patchNodes);
        }
        _log.endSubBundle();
    }

    private ObjectNode addPerennialNodes(Farmable farmable, ArrayNode patchNodes) {
        if(!_settings.makePerennial || CNCollectionUtils.isEmpty(farmable.stages)) {
            return null;
        }
        int lastIdx = farmable.stages.size() - 1;
        JsonNode lastStage = farmable.stages.get(lastIdx);
        if(lastStage.has("resetToStage")) {
            return null;
        }

        int resetStageIdx = lastIdx - 1;
        _log.writeToAll(4,"Adding Perennial Patch");
        String pathName = "stages/" + lastIdx + "/resetToStage";
        ArrayNode testNodes = _nodeProvider.createTestAddIntegerNode(pathName);
        patchNodes.add(testNodes);
        ObjectNode replaceNode = _nodeProvider.createReplaceIntegerNode(pathName, resetStageIdx);
        return replaceNode;
    }

    private ArrayList<ObjectNode> addCompactNodes(String seedPath,
                                                  Farmable farmable,
                                                  ArrayNode patchNodes) {
        if(!_settings.makeCompact || CNCollectionUtils.isEmpty(farmable.orientations)) {
            return new ArrayList<ObjectNode>();
        }
        ArrayList<ObjectNode> replaceNodes = new ArrayList<ObjectNode>();
        for(int i = 0; i < farmable.orientations.size(); i++) {
            JsonNode orientationNode = farmable.orientations.get(i);
            ObjectFrames frames = loadFrames(seedPath, _fileReader, orientationNode);
            if(frames == null
                    || frames.frameGrid == null
                    || frames.frameGrid.size.size() == 0
                    || frames.frameGrid.size.get(0).asInt() <= 8) {
                continue;
            }
            if(orientationNode.has("spaceScan")) {
                String spaceScanPath = "orientations/" + i + "/spaceScan";
                ArrayNode testRemoveSpaceScan = _nodeProvider.createTestRemoveNodes(spaceScanPath, orientationNode.get("spaceScan").asDouble());
                patchNodes.add(testRemoveSpaceScan);
            }

            String imagePosPath = "orientations/" + i + "/imagePosition";
            ArrayNode testAddImagePosition = _nodeProvider.createTestAddArrayNode(imagePosPath);
            patchNodes.add(testAddImagePosition);
            ArrayNode imagePosArrNode = _nodeProvider.createArrayNode();
            imagePosArrNode.add(-4);
            imagePosArrNode.add(0);
            ObjectNode imagePosReplaceNode = _nodeProvider.createReplaceArrayNode(imagePosPath, imagePosArrNode);
            replaceNodes.add(imagePosReplaceNode);

            String spacesPath = "orientations/" + i + "/spaces";
            ArrayNode testAddSpaces = _nodeProvider.createTestAddArrayNode(spacesPath);
            patchNodes.add(testAddSpaces);
            ArrayNode spacesArrNode = _nodeProvider.createArrayNode();
            ArrayNode spacesArrNodeTwo = _nodeProvider.createArrayNode();
            spacesArrNodeTwo.add(0);
            spacesArrNodeTwo.add(0);
            spacesArrNode.add(spacesArrNodeTwo);
            ObjectNode spacesReplaceNode = _nodeProvider.createReplaceArrayNode(spacesPath, spacesArrNode);
            replaceNodes.add(spacesReplaceNode);
        }

        return replaceNodes;
    }

    private ObjectFrames loadFrames(String seedPath, IFileReader fileReader, JsonNode orientationNode) {
        try {
            String imageName = null;
            if(orientationNode.has("dualImage")) {
                imageName = orientationNode.get("dualImage").asText();
            }
            else if(orientationNode.has("image")) {
                imageName = orientationNode.get("image").asText();
            }
            if(imageName == null) {
                return null;
            }
            String framesFileName = seedPath + "\\" + imageName.split(":")[0].replace(".png", ".frames");
            ObjectFrames farmableFrames = fileReader.read(framesFileName, ObjectFrames.class);
            return farmableFrames;
        }
        catch(IOException e) {

        }
        return null;
    }

    private void ensureCreatePath() {
        String createPath = _settings.creationPath;
        if(createPath == null) {
            return;
        }
        File file = new File(createPath);
        file.mkdirs();
    }

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }

    @Override
    public void setNodeProvider(NodeProvider nodeProvider) {
        _nodeProvider = nodeProvider;
    }
}
