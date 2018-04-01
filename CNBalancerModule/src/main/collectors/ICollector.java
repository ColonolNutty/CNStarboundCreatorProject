package main.collectors;

import com.colonolnutty.module.shareddata.models.Ingredient;
import com.colonolnutty.module.shareddata.models.Recipe;

/**
 * User: Jack's Computer
 * Date: 02/08/2018
 * Time: 12:22 PM
 */
public interface ICollector {
    void collectData(Ingredient ingredient, double inputCount, Recipe recipe);
    boolean applyData(Ingredient ingredient, double outputCount);
    String getDescriptionOfUpdate(Ingredient ingredient);
    String getName();
}
