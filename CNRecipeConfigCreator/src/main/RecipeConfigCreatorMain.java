package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.FileReaderWrapper;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.io.IReadFiles;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.locators.PatchLocator;
import com.colonolnutty.module.shareddata.locators.RecipeStore;
import com.colonolnutty.module.shareddata.models.IngredientListItem;
import com.colonolnutty.module.shareddata.models.ItemDescriptor;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.models.RecipesConfig;
import com.colonolnutty.module.shareddata.ui.ProgressController;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.colonolnutty.module.shareddata.utils.StopWatchTimer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.locators.RecipeFileLocator;
import main.settings.RecipeConfigCreatorSettings;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 9:43 AM
 */
public class RecipeConfigCreatorMain extends MainFunctionModule implements IReadFiles, IRequireNodeProvider {

    private CNLog _log;
    private RecipeConfigCreatorSettings _settings;
    private ProgressController _controller;
    private JsonManipulator _manipulator;
    private JsonPatchManipulator _patchManipulator;
    private IFileReader _fileReader;
    private NodeProvider _nodeProvider;

    public RecipeConfigCreatorMain(RecipeConfigCreatorSettings settings,
                                   CNLog log, ProgressController controller) {
        _settings = settings;
        _log = log;
        _controller = controller;
        _manipulator = new JsonManipulator(log, settings);
        _patchManipulator = new JsonPatchManipulator(log, settings);
        _fileReader = new FileReaderWrapper();
        _nodeProvider = new NodeProvider();
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
        PatchLocator patchLocator = new PatchLocator(_log);
        ArrayList<String> searchLocations = setupSearchLocations(_settings);
        _log.info("Locating files");
        FileLocator fileLocator = new RecipeFileLocator(_log);

        RecipeStore recipeStore = new RecipeStore(_log, _manipulator, _patchManipulator, patchLocator, fileLocator, searchLocations);

        Hashtable<String, ArrayList<Recipe>> recipes = recipeStore.getRecipes();
        Hashtable<String, Hashtable<String, ArrayList<Recipe>>> grouped = groupRecipesByMethod(recipes);
        Enumeration methodNames = grouped.keys();
        while(methodNames.hasMoreElements()) {
            String methodName = (String) methodNames.nextElement();
            String fileName = _settings.creationPath + "\\" + methodName + "Recipes.config";
            Hashtable<String, ArrayList<Recipe>> methodRecipes = grouped.get(methodName);
            writeToConfigurationFile(fileName, CNCollectionUtils.toArrayList(methodRecipes.keys()), methodRecipes);
        }

        timer.logTime();
    }

    private ArrayList<String> setupSearchLocations(RecipeConfigCreatorSettings settings) {
        ArrayList<String> searchLocations = new ArrayList<String>();
        for(String location : settings.recipePaths) {
            searchLocations.add(location);
        }
        return searchLocations;
    }

    private void ensureCreatePath() {
        String createPath = _settings.creationPath;
        if(createPath == null) {
            return;
        }
        File file = new File(createPath);
        file.mkdirs();
    }

    private Hashtable<String, Hashtable<String, ArrayList<Recipe>>> groupRecipesByMethod(Hashtable<String, ArrayList<Recipe>> recipes) {
        Hashtable<String, Hashtable<String, ArrayList<Recipe>>> grouped = new Hashtable<>();

        Enumeration itemNames = recipes.keys();
        while(itemNames.hasMoreElements()) {
            String itemName = (String) itemNames.nextElement();
            _log.debug("Checking item: " + itemName);
            for(Recipe recipe : recipes.get(itemName)) {
                if (CNCollectionUtils.contains(recipe.groups, "ExcludeFromRecipeBook")) {
                    recipe.excludeFromRecipeBook = true;
                }
                for (String group : recipe.groups) {
                    if (!CNCollectionUtils.contains(_settings.includeRecipeGroups, group)) {
                        continue;
                    }
                    _log.debug("Checking group: " + group);
                    Hashtable<String, ArrayList<Recipe>> groupedRecipes = new Hashtable<>();
                    if (grouped.containsKey(group)) {
                        groupedRecipes = grouped.get(group);
                    }
                    ArrayList<Recipe> itemRecipes = new ArrayList<>();
                    if(groupedRecipes.containsKey(recipe.output.item)) {
                        itemRecipes = groupedRecipes.get(recipe.output.item);
                    }
                    itemRecipes.add(recipe);
                    groupedRecipes.put(recipe.output.item, itemRecipes);
                    _log.debug("Updating group: " + group);
                    grouped.put(group, groupedRecipes);
                }
            }
        }

        return grouped;
    }

    private void writeToConfigurationFile(String recipeConfigFileName, ArrayList<String> possibleOutputs, Hashtable<String, ArrayList<Recipe>> recipesToCraft) {
        if(_settings.configAsPatchFile) {
            ArrayNode patchNode = _nodeProvider.createArrayNode();
            for(String possibleOutput : possibleOutputs) {
                patchNode.add(_nodeProvider.createAddStringNode("possibleOutput/-", possibleOutput));
            }
            _patchManipulator.writeNew(recipeConfigFileName + ".patch", patchNode);
            return;
        }
        RecipesConfig recipesConfig = new RecipesConfig();
        recipesConfig.possibleOutput = _nodeProvider.createArrayNode();
        for(String possibleOutput : possibleOutputs) {
            if(!contains(recipesConfig.possibleOutput, possibleOutput)) {
                recipesConfig.possibleOutput.add(possibleOutput);
            }
        }
        if(recipesConfig.recipesToCraft == null) {
            recipesConfig.recipesToCraft = _nodeProvider.createObjectNode();
        }
        if(recipesConfig.recipesCraftFrom == null) {
            recipesConfig.recipesCraftFrom = _nodeProvider.createObjectNode();
        }
        if(recipesConfig.recipesToCraft == null) {
            recipesConfig.recipesToCraft = _nodeProvider.createObjectNode();
        }
        for (String possibleOutput : possibleOutputs) {
            for(Recipe recipe : recipesToCraft.get(possibleOutput)) {
                ArrayNode recipes;
                if (recipesConfig.recipesToCraft.has(recipe.output.item)) {
                    recipes = (ArrayNode) recipesConfig.recipesToCraft.get(recipe.output.item);
                }
                else {
                    recipes = _nodeProvider.createArrayNode();
                }
                ObjectNode recipeNode = _nodeProvider.createObjectNode();
                ObjectNode inNode = _nodeProvider.createObjectNode();
                for (ItemDescriptor input : recipe.input) {
                    ArrayNode inputNames = _nodeProvider.createArrayNode();
                    if(recipesConfig.recipesCraftFrom.has(input.item)) {
                        inputNames = (ArrayNode) recipesConfig.recipesCraftFrom.get(input.item);
                    }
                    if(!contains(inputNames, recipe.output.item)) {
                        inputNames.add(recipe.output.item);
                    }
                    if (!inNode.has(input.item)) {
                        ObjectNode countNode = _nodeProvider.createObjectNode();
                        countNode.put("count", input.count);
                        inNode.put(input.item, countNode);
                    }
                    recipesConfig.recipesCraftFrom.put(input.item, inputNames);
                }
                recipeNode.put("input", inNode);
                ObjectNode outCount = _nodeProvider.createObjectNode();
                outCount.put("count", recipe.output.count);
                recipeNode.put("output", outCount);
                recipeNode.put("excludeFromRecipeBook", recipe.excludeFromRecipeBook);
                ArrayNode groupNode = _nodeProvider.createArrayNode();
                for (String group : recipe.groups) {
                    groupNode.add(group);
                }
                recipeNode.put("groups", groupNode);
                recipes.add(recipeNode);
                recipesConfig.recipesToCraft.put(recipe.output.item, recipes);
            }
        }

        _manipulator.writeNew(recipeConfigFileName, recipesConfig);
    }

    private boolean contains(ArrayNode node, String name) {
        boolean contains = false;
        for(int i = 0; i < node.size(); i++){
            if(node.get(i).asText().equals(name)) {
                contains = true;
                i = node.size();
            }
        }
        return contains;
    }

    private <T> T read(String path, Class<T> classOfT){
        try {
            return _fileReader.read(path, classOfT);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to read: " + path, e);
        }
        return null;
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
