package main.models;

/**
 * User: Jack's Computer
 * Date: 03/11/2018
 * Time: 12:26 PM
 */
public class IngredientUpdateResult {
    public boolean NeedsUpdate;
    public String IngredientName;

    public IngredientUpdateResult(boolean needsUpdate, String ingredientName) {
        NeedsUpdate = needsUpdate;
        IngredientName = ingredientName;
    }
}
