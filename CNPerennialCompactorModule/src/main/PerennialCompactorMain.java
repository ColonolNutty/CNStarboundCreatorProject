package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.models.Farmable;
import com.colonolnutty.module.shareddata.models.ObjectFrames;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
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
public class PerennialCompactorMain extends MainFunctionModule {
    private PandCSettings _settings;
    private CNLog _log;

    public PerennialCompactorMain(PandCSettings settings, CNLog log) {
        _settings = settings;
        _log = log;
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

        ArrayList<String> cropLocations = CNCollectionUtils.toStringArrayList(_settings.locationsOfCrops);
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
        JsonManipulator manipulator = new JsonManipulator(_log, _settings);
        for(String seedFile : seedFiles) {
            writePatchFile(manipulator, seedFile);
        }
        _log.debug("Finished creating patch files");
    }

    private void writePatchFile(JsonManipulator manipulator, String seedFile) {
        File file = new File(seedFile);
        try {
            Farmable farmable = manipulator.read(seedFile, Farmable.class);
            _log.startSubBundle(farmable.getName());
            if(_settings.makePatchFiles) {
                String basePathName = _settings.creationPath;
                String relativeModPathName = file.getParentFile().getAbsolutePath().replace(System.getProperty("user.dir") + "\\", "");
                String patchFileName = basePathName + "\\" + relativeModPathName + "\\" + file.getName() + ".patch";
                farmable.patchFile = patchFileName;
                createPatch(file.getParent(), patchFileName, manipulator, farmable);
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
                             JsonManipulator manipulator,
                             Farmable farmable) {
        _log.startSubBundle("Creating patch file for: " + farmable.getName());
        _log.writeToAll(4,"Creating patch file for: " + farmable.getName());
        ArrayNode patchNodes = manipulator.createArrayNode();
        ArrayNode replaceNodes = manipulator.createArrayNode();
        ObjectNode replaceNode = addPerennialNodes(manipulator, farmable, patchNodes);
        if(replaceNode != null) {
            replaceNodes.add(replaceNode);
        }

        ArrayList<ObjectNode> compactReplaceNodes = addCompactNodes(seedPath, manipulator, farmable, patchNodes);
        for(ObjectNode objNode : compactReplaceNodes) {
            replaceNodes.add(objNode);
        }

        if(!CNCollectionUtils.isEmpty(replaceNodes)) {
            patchNodes.add(replaceNodes);
            manipulator.writeNewPatch(patchFileName, patchNodes);
        }
        _log.endSubBundle();
    }

    private ObjectNode addPerennialNodes(JsonManipulator manipulator, Farmable farmable, ArrayNode patchNodes) {
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
        ArrayNode testNodes = manipulator.createTestNodes(pathName, resetStageIdx);
        patchNodes.add(testNodes);
        ObjectNode replaceNode = manipulator.createReplaceNode(pathName,resetStageIdx);
        return replaceNode;
    }

    private ArrayList<ObjectNode> addCompactNodes(String seedPath,
                                                  JsonManipulator manipulator,
                                                  Farmable farmable,
                                                  ArrayNode patchNodes) {
        if(!_settings.makeCompact || CNCollectionUtils.isEmpty(farmable.orientations)) {
            return new ArrayList<ObjectNode>();
        }

        ArrayList<ObjectNode> replaceNodes = new ArrayList<ObjectNode>();
        for(int i = 0; i < farmable.orientations.size(); i++) {
            JsonNode orientationNode = farmable.orientations.get(i);
            ObjectFrames frames = loadFrames(seedPath, manipulator, orientationNode);
            if(frames == null
                    || frames.frameGrid == null
                    || frames.frameGrid.size.size() == 0
                    || frames.frameGrid.size.get(0).asInt() <= 8) {
                continue;
            }
            if(orientationNode.has("spaceScan")) {
                String spaceScanPath = "orientations/" + i + "/spaceScan";
                ArrayNode testRemoveSpaceScan = manipulator.createTestRemoveNodes(spaceScanPath, orientationNode.get("spaceScan").asDouble());
                patchNodes.add(testRemoveSpaceScan);
            }

            String imagePosPath = "orientations/" + i + "/imagePosition";
            ArrayNode testAddImagePosition = manipulator.createTestNodes(imagePosPath);
            patchNodes.add(testAddImagePosition);
            ArrayNode imagePosArrNode = manipulator.createArrayNode();
            imagePosArrNode.add(-4);
            imagePosArrNode.add(0);
            ObjectNode imagePosReplaceNode = manipulator.createReplaceNode(imagePosPath, imagePosArrNode);
            replaceNodes.add(imagePosReplaceNode);

            String spacesPath = "orientations/" + i + "/spaces";
            ArrayNode testAddSpaces = manipulator.createTestNodes(spacesPath);
            patchNodes.add(testAddSpaces);
            ArrayNode spacesArrNode = manipulator.createArrayNode();
            ArrayNode spacesArrNodeTwo = manipulator.createArrayNode();
            spacesArrNodeTwo.add(0);
            spacesArrNodeTwo.add(0);
            spacesArrNode.add(spacesArrNodeTwo);
            ObjectNode spacesReplaceNode = manipulator.createReplaceNode(spacesPath, spacesArrNode);
            replaceNodes.add(spacesReplaceNode);
        }

        return replaceNodes;
    }

    private ObjectFrames loadFrames(String seedPath, JsonManipulator manipulator, JsonNode orientationNode) {
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
            ObjectFrames farmableFrames = manipulator.read(framesFileName, ObjectFrames.class);
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
}
