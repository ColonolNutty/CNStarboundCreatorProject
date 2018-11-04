package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 4:10 PM
 */
public class RecipesConfig {
    public ArrayNode possibleOutput;
    public ObjectNode recipesToCraft;
    public ObjectNode recipesCraftFrom;
}
