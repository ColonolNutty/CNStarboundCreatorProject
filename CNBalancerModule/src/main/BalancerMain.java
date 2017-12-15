package main;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.locators.*;
import com.colonolnutty.module.shareddata.models.IngredientOverrides;
import com.colonolnutty.module.shareddata.ui.ConfirmationController;
import com.colonolnutty.module.shareddata.ui.ProgressController;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import main.settings.BalancerSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:35 AM
 */
public class BalancerMain extends MainFunctionModule {
    private BalancerSettings _settings;
    private CNLog _log;
    private ProgressController _progressController;

    public BalancerMain(BalancerSettings balancerSettings,
                        CNLog log,
                        ProgressController progressController) {
        _settings = balancerSettings;
        _log = log;
        _progressController = progressController;
    }

    @Override
    public void run() {
        if(_settings == null) {
            _log.error("No configuration file found, exiting.");
            return;
        }
        StopWatchTimer timer = new StopWatchTimer(_log);
        timer.start();
        JsonManipulator manipulator = new JsonManipulator(_log, _settings);
        PatchLocator patchLocator = new PatchLocator(_log);
        ArrayList<String> searchLocations = setupSearchLocations(_settings);
        FileLocator fileLocator = new FileLocator(_log);

        StatusEffectStore statusEffectStore = new StatusEffectStore(_log, fileLocator, manipulator, patchLocator, searchLocations);
        IngredientStore ingredientStore = new IngredientStore(_log, manipulator, patchLocator, fileLocator, searchLocations, _settings.fileTypesToUpdate);
        IngredientOverrides ingredientOverrides = loadIngredientOverrides(_settings.ingredientOverridePath, manipulator);
        if(ingredientOverrides != null) {
            ingredientStore.overrideIngredients(ingredientOverrides.ingredients);
        }
        RecipeStore recipeStore = new RecipeStore(_log, manipulator, patchLocator, fileLocator, searchLocations);
        IngredientDataCalculator ingredientDataCalculator = new IngredientDataCalculator(_log, _settings, recipeStore, ingredientStore, statusEffectStore, manipulator);
        IngredientUpdater ingredientUpdater = new IngredientUpdater(_log, _settings, manipulator, ingredientStore, ingredientDataCalculator);

        FileUpdater fileUpdater = new FileUpdater(_log, _settings, manipulator, ingredientUpdater, ingredientStore, fileLocator, searchLocations, _progressController);
        fileUpdater.updateValues(_settings.fileTypesToUpdate);

        timer.logTime();
    }

    private ArrayList<String> setupSearchLocations(BalancerSettings settings) {
        ArrayList<String> searchLocations = new ArrayList<String>();
        for(String location : settings.locationsToUpdate) {
            searchLocations.add(location);
        }
        for(String location : settings.includeLocations) {
            searchLocations.add(location);
        }
        return searchLocations;
    }

    private IngredientOverrides loadIngredientOverrides(String overridesPath, JsonManipulator manipulator) {
        try {
            _log.info("Loading ingredient overrides");
            IngredientOverrides replacementIngredientValues = manipulator.read(overridesPath, IngredientOverrides.class);
            return replacementIngredientValues;
        }
        catch(FileNotFoundException e) {
            _log.error(e);
        }
        catch (IOException e) {
            _log.error(e);
        }
        return null;
    }
}
