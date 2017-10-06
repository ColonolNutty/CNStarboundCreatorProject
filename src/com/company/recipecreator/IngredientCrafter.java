package com.company.recipecreator;

import com.company.CNLog;
import com.company.JsonManipulator;
import com.company.models.Ingredient;
import com.company.models.IngredientListItem;
import com.company.models.RecipeCreatorSettings;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 12:41 PM
 */
public class IngredientCrafter extends CNCrafter {
    private CNLog _log;
    private RecipeCreatorSettings _settings;
    private JsonManipulator _manipulator;
    private Ingredient _template;

    public IngredientCrafter(CNLog log,
                         RecipeCreatorSettings settings,
                         JsonManipulator manipulator) {
        _log = log;
        _settings = settings;
        _manipulator = manipulator;

        _template = read(_settings.ingredientTemplateFile, Ingredient.class);
    }

    @Override
    public void craft(String name, ArrayList<IngredientListItem> ingredients, int countPer) {
        if(_template == null) {
            return;
        }
        String extension = _settings.fileExtension;
        String outputItemDescription = _settings.outputItemDescription;

        String newShortDescrip = _settings.outputItemShortDescription + " - ";
        String newDescription = outputItemDescription + " made with ";
        for(int i = 0; i < ingredients.size(); i++) {
            if(ingredients.size() != 1 && (i + 1) >= ingredients.size()) {
                newDescription += "and ";
            }
            IngredientListItem ingredient = ingredients.get(i);
            if(countPer > 1) {
                newDescription += ingredient.plural;
            }
            else {
                newDescription += ingredient.singular;
            }
            newShortDescrip += ingredient.shortName;
            if((i + 1) < ingredients.size()) {
                if(ingredients.size() > 2) {
                    newDescription += ",";
                }
                newShortDescrip += "+";
                newDescription += " ";
            }
        }

        newDescription += ".";

        Ingredient newIngredient = _template.copy();
        newIngredient.setName(name);
        newIngredient.description = newDescription;
        newIngredient.shortdescription = newShortDescrip;
        newIngredient.inventoryIcon = name + ".png";

        String newPath = _settings.creationPath + "\\ingredients\\";
        ensurePath(newPath);
        String newIngredPath = newPath + name + "." + extension;
        String newImagePath = newPath + newIngredient.inventoryIcon;
        copyImage(newImagePath);
        _log.debug("Creating ingredient with name: " + name);
        _manipulator.writeNewWithTemplate(_settings.ingredientTemplateFile, newIngredPath, newIngredient);
    }

    private void copyImage(String newPath) {
        try {
            Files.copy(new File(_settings.ingredientImageTemplateFile), new File(newPath));
        }
        catch(IOException e) {
            _log.error("[IOE] When copying image: " + newPath, e);
        }
    }

    private <T> T read(String path, Class<T> classOfT){
        try {
            return _manipulator.read(path, classOfT);
        }
        catch(IOException e) {
            _log.error("[IOE] Failed to read: " + path, e);
        }
        return null;
    }
}
