package main.locators;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.utils.CNCollectionUtils;
import com.colonolnutty.module.shareddata.utils.CNFileUtils;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 11/04/2018
 * Time: 3:30 PM
 */
public class IngredientFileLocator extends FileLocator {

    public IngredientFileLocator(CNLog log, String[] ingredientFileType) {
        super(log);
        setupIncludedFileTypes(CNCollectionUtils.toArrayList(ingredientFileType));
    }

    @Override
    protected boolean isValidFileType(String fileName) {
        return CNFileUtils.fileEndsWith(fileName, getIncludedFileTypes());
    }
}
