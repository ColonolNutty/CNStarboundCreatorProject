package com.company.models;

/**
 * User: Jack's Computer
 * Date: 09/13/2017
 * Time: 11:00 AM
 */
public class RecipeIngredient {
    public Double count;
    public Ingredient ingredient;

    public RecipeIngredient(Ingredient ingredient, Double count) {
        this.ingredient = ingredient;
        this.count = count;
    }
}
