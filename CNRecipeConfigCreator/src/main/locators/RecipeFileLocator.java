package main.locators;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.locators.FileLocator;
import com.colonolnutty.module.shareddata.utils.CNFileUtils;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 11/04/2018
 * Time: 3:30 PM
 */
public class RecipeFileLocator extends FileLocator {

    public RecipeFileLocator(CNLog log) {
        super(log);

        ArrayList<String> includedFileTypes = new ArrayList<>();
        includedFileTypes.add(".patch");
        includedFileTypes.add(".recipe");
        setupIncludedFileTypes(includedFileTypes);
    }

    @Override
    protected boolean isValidFileType(String fileName) {
        return CNFileUtils.fileEndsWith(fileName, getIncludedFileTypes());
    }
}
