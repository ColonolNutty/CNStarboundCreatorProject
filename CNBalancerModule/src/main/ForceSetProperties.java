package main;

import com.colonolnutty.module.shareddata.models.Ingredient;

/**
 * User: Jack's Computer
 * Date: 01/27/2018
 * Time: 1:40 PM
 */
public class ForceSetProperties {
    public static void forceSet(Ingredient ingredient) {
        if(ingredient.filePath != null && ingredient.filePath.endsWith(".object")) {
            ingredient.printable = false;
        }
    }
}
