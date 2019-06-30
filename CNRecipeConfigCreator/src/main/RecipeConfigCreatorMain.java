package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.FileReaderWrapper;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.io.IReadFiles;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.locators.IngredientStore;
import com.colonolnutty.module.shareddata.locators.PatchLocator;
import com.colonolnutty.module.shareddata.locators.RecipeStore;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.ItemDescriptor;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.models.RecipesConfig;
import com.colonolnutty.module.shareddata.ui.ProgressController;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.colonolnutty.module.shareddata.utils.StopWatchTimer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.locators.IngredientFileLocator;
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
        _log.info("Locating files");
        FileLocator fileLocator = new RecipeFileLocator(_log);
        FileLocator ingredientFileLocator = new IngredientFileLocator(_log, _settings.ingredientFileTypes);

        IngredientStore ingredientStore = new IngredientStore(_log, _manipulator, _patchManipulator, patchLocator, ingredientFileLocator, CNCollectionUtils.toArrayList(_settings.ingredientLocations), _settings.ingredientFileTypes);
        RecipeStore recipeStore = new RecipeStore(_log, _manipulator, _patchManipulator, patchLocator, fileLocator, CNCollectionUtils.toArrayList(_settings.recipePaths));

        HashMap<String, String> friendlyGroupNames;
        try {
            friendlyGroupNames = readFriendlyNames(_settings.friendlyNamesFilePath);

            Hashtable<String, ArrayList<Recipe>> recipes = recipeStore.getRecipes();
            Hashtable<String, Hashtable<String, ArrayList<Recipe>>> grouped = groupRecipesByMethod(recipes);
            Enumeration methodNames = grouped.keys();
            while(methodNames.hasMoreElements()) {
                String methodName = (String) methodNames.nextElement();
                String fileName = _settings.creationPath + "\\" + methodName + "Recipes.config";
                Hashtable<String, ArrayList<Recipe>> methodRecipes = grouped.get(methodName);
                writeToConfigurationFile(fileName, friendlyGroupNames, CNCollectionUtils.toArrayList(methodRecipes.keys()), methodRecipes, ingredientStore);
            }
        }
        catch(IOException e) {
            _log.error("Error reading file: " + _settings.friendlyNamesFilePath, e);
        }

        timer.logTime();
    }

    private HashMap<String, String> readFriendlyNames(String friendlyNamesPath) throws IOException {
        HashMap<String, String> friendlyNames = new HashMap<String, String>();
        if(friendlyNamesPath == null) {
            return friendlyNames;
        }

        ArrayNode friendlyNamesNode = _fileReader.read(friendlyNamesPath, ArrayNode.class);
        if(friendlyNamesNode == null || friendlyNamesNode.size() == 0) {
            return friendlyNames;
        }

        for(JsonNode subNode : friendlyNamesNode) {
            if(!subNode.isArray()) {
                continue;
            }
            ArrayNode subArr = (ArrayNode) subNode;
            if(subArr.size() <= 1) {
                continue;
            }
            String name = subArr.get(0).asText();
            String friendlyName = subArr.get(1).asText();
            if(!friendlyNames.containsKey(name)) {
                friendlyNames.put(name, friendlyName);
            }
        }

        return friendlyNames;
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

    private void writeToConfigurationFile(String recipeConfigFileName, HashMap<String, String> friendlyNames, ArrayList<String> possibleOutputs, Hashtable<String, ArrayList<Recipe>> recipesToCraft, IngredientStore ingredientStore) {
        if(_settings.configAsPatchFile) {
            writeAsPatchConfigurationFile(recipeConfigFileName, friendlyNames, possibleOutputs, recipesToCraft, ingredientStore);
        }
        else {
            writeAsNonPatchConfigurationFile(recipeConfigFileName, friendlyNames, possibleOutputs, recipesToCraft, ingredientStore);
        }
    }

    private void writeAsPatchConfigurationFile(String recipeConfigFileName, HashMap<String, String> friendlyNames, ArrayList<String> possibleOutputs, Hashtable<String, ArrayList<Recipe>> recipesToCraft, IngredientStore ingredientStore) {
        ArrayNode patchNode = _nodeProvider.createArrayNode();
        ArrayList<ArrayNode> testNodes = new ArrayList<ArrayNode>();
        ArrayNode changeNodes = _nodeProvider.createArrayNode();
        for(String possibleOutput : possibleOutputs) {
            changeNodes.add(_nodeProvider.createAddStringNode("/possibleOutput/-", possibleOutput));
        }
        Map<String, ArrayList<String>> recipesCraftFrom = new HashMap<>();
        for (String possibleOutput : possibleOutputs) {
            ObjectNode itemData = _nodeProvider.createObjectNode();
            ArrayList<String> itemMethods = new ArrayList<>();
            for(Recipe recipe : recipesToCraft.get(possibleOutput)) {
                String outputItemName = recipe.output.item;
                Ingredient outputItemData = ingredientStore.getIngredient(outputItemName);
                if(!itemData.has("displayName")) {
                    itemData.put("displayName", outputItemData.shortdescription);
                    itemData.put("displayNameWithMethods", outputItemData.shortdescription + " ()");
                    itemData.put("displayName", outputItemData.shortdescription);
                    itemData.put("recipes", _nodeProvider.createArrayNode());
                    itemData.put("methods", _nodeProvider.createObjectNode());
                    itemData.put("icon", outputItemData.inventoryIcon);
                    testNodes.add(_nodeProvider.createTestAddObjectNode("/recipesToCraft/" + possibleOutput, itemData));
                }
                ObjectNode recipeNode = _nodeProvider.createObjectNode();
                ObjectNode inNode = _nodeProvider.createObjectNode();
                for (ItemDescriptor input : recipe.input) {
                    ArrayList<String> inputNames = new ArrayList<>();
                    if(recipesCraftFrom.containsKey(input.item)) {
                        inputNames = recipesCraftFrom.get(input.item);
                    }
                    if(!inputNames.contains(outputItemName)) {
                        inputNames.add(outputItemName);
                    }
                    Ingredient inputItemData = ingredientStore.getIngredient(input.item);
                    if (!inNode.has(input.item)) {
                        ObjectNode itemNode = _nodeProvider.createObjectNode();
                        itemNode.put("count", input.count.intValue());
                        itemNode.put("id", input.item);
                        itemNode.put("icon", inputItemData.inventoryIcon);
                        itemNode.put("displayName", inputItemData.shortdescription);
                        inNode.put(input.item, itemNode);
                    }
                    else {
                        ObjectNode itemNode = (ObjectNode) inNode.get(input.item);
                        int count = itemNode.get("count").asInt();
                        count += input.count.intValue();
                        itemNode.put("count", count);
                        inNode.put(input.item, itemNode);
                    }
                    recipesCraftFrom.put(input.item, inputNames);
                }
                recipeNode.put("input", inNode);
                ObjectNode outNode = _nodeProvider.createObjectNode();
                outNode.put("name", outputItemName);
                outNode.put("count", recipe.output.count.intValue());
                recipeNode.put("output", outNode);
                recipeNode.put("excludeFromRecipeBook", recipe.excludeFromRecipeBook);
                ArrayList<String> recipeMethods = new ArrayList<>();
                ObjectNode methodsNode = _nodeProvider.createObjectNode();
                ArrayNode groupNode = _nodeProvider.createArrayNode();
                for (String group : recipe.groups) {
                    if(friendlyNames.containsKey(group)) {
                        groupNode.add(group);
                        String friendlyName = friendlyNames.get(group);
                        if(!itemMethods.contains(friendlyName)){
                            itemMethods.add(friendlyName);
                        }
                        if(!recipeMethods.contains(friendlyName)) {
                            recipeMethods.add(friendlyName);
                        }
                        methodsNode.put(group, friendlyName);
                        changeNodes.add(_nodeProvider.createAddStringNode("/recipesToCraft/" + outputItemName + "/groups", group));
                        changeNodes.add(_nodeProvider.createAddStringNode("/recipesToCraft/" + outputItemName + "/methods/" + group, friendlyName));
                    }
                }

                if (recipeMethods.size() == 0) {
                    recipeMethods.add("UNKNOWN");
                }

                if(itemMethods.size() == 0) {
                    itemMethods.add("UNKNOWN");
                }

                recipeNode.put("displayMethods", " (" + CNStringUtils.toCommaSeparated(CNCollectionUtils.toArray(String.class, recipeMethods)) + ")");
                recipeNode.put("methods", methodsNode);
                recipeNode.put("groups", groupNode);
                changeNodes.add(_nodeProvider.createAddObjectNode("/recipesToCraft/" + outputItemName + "/recipes/-", recipeNode));
                changeNodes.add(_nodeProvider.createReplaceStringNode("/recipesToCraft/" + outputItemName + "/displayNameWithMethods", outputItemData.shortdescription + " (" + CNStringUtils.toCommaSeparated(CNCollectionUtils.toArray(String.class, itemMethods)) + ")"));
            }
        }
        for (String ingredientName : recipesCraftFrom.keySet()) {
            ArrayList<String> recipeNames = recipesCraftFrom.get(ingredientName);
            testNodes.add(_nodeProvider.createTestAddSingleArrayNode("/recipesCraftFrom/" + ingredientName));
            for(String recipeName : recipeNames) {
                changeNodes.add(_nodeProvider.createAddStringNode("/recipesCraftFrom/" + ingredientName + "/-", recipeName));
            }
        }
        for(ArrayNode testNode : testNodes) {
            patchNode.add(testNode);
        }
        patchNode.add(changeNodes);

        _patchManipulator.writeNew(recipeConfigFileName + ".patch", patchNode);
    }

    private void writeAsNonPatchConfigurationFile(String recipeConfigFileName, HashMap<String, String> friendlyNames, ArrayList<String> possibleOutputs, Hashtable<String, ArrayList<Recipe>> recipesToCraft, IngredientStore ingredientStore) {
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
        for (String possibleOutput : possibleOutputs) {
            ArrayList<String> itemMethods = new ArrayList<>();
            for(Recipe recipe : recipesToCraft.get(possibleOutput)) {
                String outputItemName = recipe.output.item;
                Ingredient outputItemData = ingredientStore.getIngredient(outputItemName);
                ObjectNode itemData;
                if (recipesConfig.recipesToCraft.has(outputItemName)) {
                    itemData = (ObjectNode) recipesConfig.recipesToCraft.get(outputItemName);
                }
                else {
                    itemData = _nodeProvider.createObjectNode();
                    itemData.put("displayName", outputItemData.shortdescription);
                    itemData.put("displayNameWithMethods", outputItemData.shortdescription + " ()");
                    itemData.put("displayName", outputItemData.shortdescription);
                    itemData.put("recipes", _nodeProvider.createArrayNode());
                    itemData.put("methods", _nodeProvider.createObjectNode());
                    itemData.put("icon", outputItemData.inventoryIcon);
                }
                ArrayNode recipes = (ArrayNode) itemData.get("recipes");
                ObjectNode recipeNode = _nodeProvider.createObjectNode();
                ObjectNode inNode = _nodeProvider.createObjectNode();
                for (ItemDescriptor input : recipe.input) {
                    ArrayNode inputNames = _nodeProvider.createArrayNode();
                    if(recipesConfig.recipesCraftFrom.has(input.item)) {
                        inputNames = (ArrayNode) recipesConfig.recipesCraftFrom.get(input.item);
                    }
                    if(!contains(inputNames, outputItemName)) {
                        inputNames.add(outputItemName);
                    }
                    Ingredient inputItemData = ingredientStore.getIngredient(input.item);
                    if (!inNode.has(input.item)) {
                        ObjectNode itemNode = _nodeProvider.createObjectNode();
                        itemNode.put("count", input.count.intValue());
                        itemNode.put("id", input.item);
                        itemNode.put("icon", inputItemData.inventoryIcon);
                        itemNode.put("displayName", inputItemData.shortdescription);
                        inNode.put(input.item, itemNode);
                    }
                    else {
                        ObjectNode itemNode = (ObjectNode) inNode.get(input.item);
                        int count = itemNode.get("count").asInt();
                        count += input.count.intValue();
                        itemNode.put("count", count);
                        inNode.put(input.item, itemNode);
                    }
                    recipesConfig.recipesCraftFrom.put(input.item, inputNames);
                }
                recipeNode.put("input", inNode);
                ObjectNode outNode = _nodeProvider.createObjectNode();
                outNode.put("name", outputItemName);
                outNode.put("count", recipe.output.count.intValue());
                recipeNode.put("output", outNode);
                recipeNode.put("excludeFromRecipeBook", recipe.excludeFromRecipeBook);
                ArrayList<String> recipeMethods = new ArrayList<>();
                ObjectNode itemMethodsNode = (ObjectNode) itemData.get("methods");
                ObjectNode methodsNode = _nodeProvider.createObjectNode();
                ArrayNode groupNode = _nodeProvider.createArrayNode();
                for (String group : recipe.groups) {
                    if(friendlyNames.containsKey(group)) {
                        groupNode.add(group);
                        String friendlyName = friendlyNames.get(group);
                        if(!itemMethods.contains(friendlyName)){
                            itemMethods.add(friendlyName);
                        }
                        if(!recipeMethods.contains(friendlyName)) {
                            recipeMethods.add(friendlyName);
                        }
                        methodsNode.put(group, friendlyName);
                        itemMethodsNode.put(group, friendlyName);
                    }
                }

                if (recipeMethods.size() == 0) {
                    recipeMethods.add("UNKNOWN");
                }

                if(itemMethods.size() == 0) {
                    itemMethods.add("UNKNOWN");
                }

                recipeNode.put("displayMethods", " (" + CNStringUtils.toCommaSeparated(CNCollectionUtils.toArray(String.class, recipeMethods)) + ")");
                recipeNode.put("methods", methodsNode);
                recipeNode.put("groups", groupNode);
                recipes.add(recipeNode);
                itemData.put("displayNameWithMethods", outputItemData.shortdescription + " (" + CNStringUtils.toCommaSeparated(CNCollectionUtils.toArray(String.class, itemMethods)) + ")");
                itemData.put("methods", itemMethodsNode);
                itemData.put("recipes", recipes);
                recipesConfig.recipesToCraft.put(recipe.output.item, itemData);
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
