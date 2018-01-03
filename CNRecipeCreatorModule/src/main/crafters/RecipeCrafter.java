package main.crafters;

import com.colonolnutty.module.shareddata.*;
import com.colonolnutty.module.shareddata.io.FileReaderWrapper;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.models.IngredientListItem;
import com.colonolnutty.module.shareddata.models.ItemDescriptor;
import com.colonolnutty.module.shareddata.models.Recipe;
import main.settings.RecipeCreatorSettings;

import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 12:01 PM
 */
public class RecipeCrafter extends CNCrafter implements IReadFiles {

    private CNLog _log;
    private RecipeCreatorSettings _settings;
    private JsonManipulator _manipulator;
    private Recipe _template;
    private IFileReader _fileReader;

    public RecipeCrafter(CNLog log,
                         RecipeCreatorSettings settings,
                         JsonManipulator manipulator) {
        _log = log;
        _settings = settings;
        _manipulator = manipulator;
        _fileReader = new FileReaderWrapper();

        _template = read(_settings.recipeTemplateFile, Recipe.class);
    }

    @Override
    public void craft(String name, ArrayList<IngredientListItem> ingredients, int countPer) {
        if(_template == null) {
            return;
        }
        Recipe recipe = new Recipe();
        ArrayList<ItemDescriptor> newInputs = new ArrayList<ItemDescriptor>();
        for(int i = 0; i < _template.input.length; i++) {
            newInputs.add(_template.input[i]);
        }
        for(IngredientListItem ingred : ingredients) {
            newInputs.add(new ItemDescriptor(ingred.name, (double)countPer));
        }
        recipe.input = new ItemDescriptor[newInputs.size()];
        for(int i = 0; i < newInputs.size(); i++) {
            recipe.input[i] = newInputs.get(i);
        }
        recipe.output = new ItemDescriptor(name, _template.output.count);
        recipe.groups = _template.groups;
        String recipePath = _settings.creationPath + "\\recipes\\";
        ensurePath(recipePath);
        String recipeFile = recipePath + name + ".recipe";
        _log.debug("Creating recipe with name: " + name);
        _manipulator.writeNewWithTemplate(_settings.recipeTemplateFile, recipeFile, recipe);
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
}
