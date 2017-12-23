package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.models.IngredientListItem;
import com.colonolnutty.module.shareddata.models.RecipesConfig;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.settings.RecipeConfigCreatorSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 9:43 AM
 */
public class RecipeConfigCreatorMain extends MainFunctionModule implements IReadFiles, IRequireNodeProvider {

    private CNLog _log;
    private RecipeConfigCreatorSettings _settings;
    private JsonManipulator _manipulator;
    private JsonPatchManipulator _patchManipulator;
    private IFileReader _fileReader;
    private NodeProvider _nodeProvider;

    public RecipeConfigCreatorMain(RecipeConfigCreatorSettings settings,
                                   CNLog log) {
        _settings = settings;
        _log = log;
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

        IngredientListItem[] ingredientList = read(_settings.ingredientListFile, IngredientListItem[].class);
        if(ingredientList == null) {
            return;
        }

        ArrayList<String> outputNames = createFromTemplate(ingredientList);
        writeToConfigurationFile(outputNames);
        timer.logTime();
    }

    private void ensureCreatePath() {
        String createPath = _settings.creationPath;
        if(createPath == null) {
            return;
        }
        File file = new File(createPath);
        file.mkdirs();
    }

    private void writeToConfigurationFile(ArrayList<String> names) {
        if(_settings.configAsPatchFile) {
            ArrayNode patchNode = _nodeProvider.createArrayNode();
            for(String name : names) {
                patchNode.add(_nodeProvider.createAddStringNode("possibleOutput/-", name));
            }
            _patchManipulator.writeNew(_settings.recipeConfigFileName + ".patch", patchNode);
            return;
        }
        String recipeConfigFile = _settings.recipeConfigFileName;
        RecipesConfig recipesConfig = read(recipeConfigFile, RecipesConfig.class);
        if(recipesConfig == null) {
            recipesConfig = new RecipesConfig();
            recipesConfig.possibleOutput = _nodeProvider.createArrayNode();
        }
        for(String name : names) {
            if(!contains(recipesConfig.possibleOutput, name)) {
                recipesConfig.possibleOutput.add(name);
            }
        }
        _manipulator.writeNew(recipeConfigFile, recipesConfig);
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

    public ArrayList<String> createFromTemplate(IngredientListItem[] ingredientNames) {
        int numberPerRecipe = _settings.numberOfIngredientsPerRecipe;

        _log.startSubBundle("Ingredients");
        ArrayList<String> newNames = createIngredients(ingredientNames, new ArrayList<IngredientListItem>(), -1, numberPerRecipe);
        _log.endSubBundle();
        return newNames;
    }

    private ArrayList<String> createIngredients(IngredientListItem[] ingredientList,
                                   ArrayList<IngredientListItem> currentIngredients,
                                   int currentIngredientIndex,
                                   int ingredientsLeft) {
        ArrayList<String> names = new ArrayList<String>();
        if(ingredientsLeft == 0) {
            return names;
        }
        for(int i = currentIngredientIndex + 1; i < ingredientList.length; i++) {
            ArrayList<IngredientListItem> ingredients = new ArrayList<IngredientListItem>();
            ingredients.addAll(currentIngredients);
            IngredientListItem nextIngredient = ingredientList[i];
            _log.startSubBundle(nextIngredient.name);
            ingredients.add(nextIngredient);
            if(ingredientsLeft != 0) {
                names.addAll(createIngredients(ingredientList, ingredients, i, ingredientsLeft - 1));
            }

            String outputName = _settings.filePrefix;
            for(IngredientListItem ingred : ingredients) {
                outputName += ingred.shortName;
            }
            outputName += _settings.fileSuffix;
            names.add(outputName);
            _log.endSubBundle();
        }
        return names;
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
