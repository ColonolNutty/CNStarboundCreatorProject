package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 9:43 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeCreatorSettings extends BaseSettings {
    public String creationPath;
    public String ingredientListFile;
    public String recipeTemplateFile;
    public String ingredientTemplateFile;
    public String ingredientImageTemplateFile;
    public String recipeConfigFileName;
    public String filePrefix;
    public String fileSuffix;
    public String fileExtension;
    public String outputItemDescription;
    public String outputItemShortDescription;
    public Integer countPerIngredient;
    public Integer numberOfIngredientsPerRecipe;
}
