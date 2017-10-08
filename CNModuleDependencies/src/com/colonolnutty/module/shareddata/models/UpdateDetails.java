package com.colonolnutty.module.shareddata.models;

/**
 * User: Jack's Computer
 * Date: 09/13/2017
 * Time: 12:48 PM
 */
public class UpdateDetails {
    public String filePath;
    public Ingredient ingredient;

    public UpdateDetails(String filePath, Ingredient ingredient) {
        this.filePath = filePath;
        this.ingredient = ingredient;
    }
}
