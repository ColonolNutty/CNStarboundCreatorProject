package main;

import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.MainFunctionModule;
import com.colonolnutty.module.shareddata.StopWatchTimer;
import com.colonolnutty.module.shareddata.models.IngredientListItem;
import com.colonolnutty.module.shareddata.models.RecipesConfig;
import main.crafters.CNCrafter;
import main.crafters.IngredientCrafter;
import main.crafters.RecipeCrafter;
import main.settings.RecipeCreatorSettings;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 9:43 AM
 */
public class RecipeCreatorMain extends MainFunctionModule {

    private CNLog _log;
    private RecipeCreatorSettings _settings;
    private JsonManipulator _manipulator;
    private ArrayList<CNCrafter> _crafters;

    public RecipeCreatorMain(RecipeCreatorSettings settings,
                             CNLog log) {
        _settings = settings;
        _log = log;
        _manipulator = new JsonManipulator(log, settings);
        _crafters = new ArrayList<CNCrafter>();
        _crafters.add(new RecipeCrafter(log, settings, _manipulator));
        _crafters.add(new IngredientCrafter(log, settings, _manipulator));
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
        String recipeConfigFile = _settings.recipeConfigFileName;
        RecipesConfig recipesConfig = read(recipeConfigFile, RecipesConfig.class);
        if(recipesConfig == null) {
            recipesConfig = new RecipesConfig();
            recipesConfig.possibleOutput = _manipulator.createArrayNode();
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
            for(CNCrafter crafter : _crafters) {
                crafter.craft(outputName, ingredients, _settings.countPerIngredient);
            }
            names.add(outputName);
            _log.endSubBundle();
        }
        return names;
    }

    private <T> T read(String path, Class<T> classOfT){
        File file = new File(path);
        if(!file.exists()) {
            return null;
        }
        try {
            return _manipulator.read(path, classOfT);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to read: " + path, e);
        }
        return null;
    }
}
