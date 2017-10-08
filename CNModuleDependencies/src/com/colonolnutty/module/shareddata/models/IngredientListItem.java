package com.colonolnutty.module.shareddata.models;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 10:47 AM
 */
public class IngredientListItem {
    public String name;
    public String singular;
    public String plural;
    public String shortName;

    @Override
    public String toString() {
        return name;
    }
}
