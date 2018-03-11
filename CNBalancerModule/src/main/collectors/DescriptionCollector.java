package main.collectors;

import com.colonolnutty.module.shareddata.JsonManipulator;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.FileReaderWrapper;
import com.colonolnutty.module.shareddata.io.IFileReader;
import com.colonolnutty.module.shareddata.io.IReadFiles;
import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.IngredientProperty;
import com.colonolnutty.module.shareddata.models.Recipe;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import main.settings.BalancerSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Jack's Computer
 * Date: 02/09/2018
 * Time: 11:24 AM
 */
public class DescriptionCollector implements ICollector, IReadFiles {
    public static final String RECIPE_GROUP_DELIMITER = ":-} ";

    private IFileReader _fileReader;
    private BalancerSettings _settings;
    private CNLog _log;
    private HashMap<String, String> _friendlyGroupNames;

    private String[] _groupNames;

    public DescriptionCollector(BalancerSettings settings, CNLog log) {
        _settings = settings;
        _log = log;
        _fileReader = new FileReaderWrapper();
    }

    @Override
    public void collectData(Ingredient ingredient, double inputCount, Recipe recipe) {
        if(_groupNames == null) {
            _groupNames = recipe.groups;
        }
    }

    @Override
    public boolean applyData(Ingredient ingredient, double outputCount) {
        try {
            if (_friendlyGroupNames == null) {
                _friendlyGroupNames = readFriendlyNames(_settings.friendlyNamesFilePath);
            }
        }
        catch(IOException e) {
            _log.error("Error reading file: " + _settings.friendlyNamesFilePath, e);
            return false;
        }
        String newDescription = createDescription(ingredient.getDescription(), _groupNames, _friendlyGroupNames);
        ingredient.update(IngredientProperty.Description, newDescription);
        String value = ingredient.getDescription();
        if(ingredient.description == null && value != null) {
            return true;
        }
        if(ingredient.description != null && value == null) {
            return true;
        }
        if(ingredient.description == null && value == null) {
            return false;
        }
        return !ingredient.description.equals(value);
    }

    @Override
    public String getDescriptionOfUpdate(Ingredient ingredient) {
        String oldDescription = ingredient.description;
        String newDescription = ingredient.getDescription();
        if(oldDescription == null) {
            oldDescription = "none";
        }
        if(newDescription == null) {
            newDescription = "none";
        }
        return "Description was: " + oldDescription + " it is now: " + newDescription;
    }

    public String createDescription(String description, String[] groupNames, HashMap<String, String> friendlyGroupNames) {
        if(CNStringUtils.isNullOrWhitespace(description)) {
            return null;
        }
        String[] splitOnDelimiter = description.split(RECIPE_GROUP_DELIMITER);

        String existingDescriptionText = splitOnDelimiter[splitOnDelimiter.length - 1];

        if(!_settings.includeCraftGroups) {
            return existingDescriptionText;
        }

        ArrayList<String> recipeGroupNames = getRecipeGroupNames(groupNames, friendlyGroupNames);
        if(recipeGroupNames == null || recipeGroupNames.isEmpty()) {
            return existingDescriptionText;
        }

        String groupText = createMethodText(recipeGroupNames);

        return groupText + RECIPE_GROUP_DELIMITER + existingDescriptionText;
    }

    public String createMethodText(ArrayList<String> groupNames) {
        String methodsText = "";
        if(groupNames == null || groupNames.size() == 0) {
            return methodsText;
        }
        for(int i = 0; i < groupNames.size(); i++) {
            String recipeGroupName = groupNames.get(i);
            methodsText += "(" + recipeGroupName + ")";
        }
        return methodsText;
    }

    public ArrayList<String> getRecipeGroupNames(String[] recipeGroupNames, HashMap<String, String> friendlyGroupNames) {
        ArrayList<String> groupNames = new ArrayList<String>();
        if(recipeGroupNames == null || friendlyGroupNames == null) {
            return groupNames;
        }
        for(String group : recipeGroupNames) {
            if(friendlyGroupNames.containsKey(group)) {
                groupNames.add(friendlyGroupNames.get(group));
            }
        }
        return groupNames;
    }

    public HashMap<String, String> readFriendlyNames(String friendlyNamesPath) throws IOException {
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

    @Override
    public void setFileReader(IFileReader fileReader) {
        _fileReader = fileReader;
    }
}
