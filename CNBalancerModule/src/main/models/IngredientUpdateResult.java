package main.models;

import com.colonolnutty.module.shareddata.models.Ingredient;

/**
 * User: Jack's Computer
 * Date: 03/11/2018
 * Time: 12:26 PM
 */
public class IngredientUpdateResult {
    public boolean NeedsUpdate;
    public String IngredientName;
    public Ingredient Ingredient;

    public IngredientUpdateResult(boolean needsUpdate, Ingredient ingredient) {
        NeedsUpdate = needsUpdate;
        Ingredient = ingredient;
        if(ingredient != null) {
            IngredientName = ingredient.getName();
        }
    }
}
