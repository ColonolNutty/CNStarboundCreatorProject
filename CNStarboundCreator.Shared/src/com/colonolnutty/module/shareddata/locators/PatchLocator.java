package com.colonolnutty.module.shareddata.locators;


import com.colonolnutty.module.shareddata.debug.CNLog;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/14/2017
 * Time: 11:04 AM
 */
public class PatchLocator {

    private CNLog _log;

    public PatchLocator(CNLog log) {
        _log = log;
    }

    public String locatePatchFileFor(String toCheckFilePath, ArrayList<String> filePaths) {
        File toCheckFile = new File(toCheckFilePath);
        if(!toCheckFile.exists()) {
            return null;
        }
        String toCheckFileName = toCheckFile.getName();
        String foundPatchFileName = null;
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            File file = new File(filePath);
            String fileName = file.getName();
            if(fileName.endsWith(".patch") && fileName.startsWith(toCheckFileName)) {
                foundPatchFileName = filePath;
                i = filePaths.size();
            }
        }
        if(foundPatchFileName != null) {
            _log.debug("Found patch file for: " + toCheckFileName);
        }
        return foundPatchFileName;
    }
}
