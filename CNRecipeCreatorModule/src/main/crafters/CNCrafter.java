package main.crafters;

import com.colonolnutty.module.shareddata.models.IngredientListItem;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 12:53 PM
 */
public abstract class CNCrafter {
    public abstract void craft(String name, ArrayList<IngredientListItem> ingredients, int countPer);

    protected void ensurePath(String path) {
        if(path == null) {
            return;
        }
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
    }
}
