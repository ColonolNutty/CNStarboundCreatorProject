package com.company.recipecreator;

import com.company.models.IngredientListItem;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 12:53 PM
 */
public abstract class CNCrafter {
    public abstract void craft(String name, ArrayList<IngredientListItem> ingredients, int countPer);
}
