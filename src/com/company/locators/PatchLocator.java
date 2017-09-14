package com.company.locators;

import com.company.DebugLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/14/2017
 * Time: 11:04 AM
 */
public class PatchLocator {

    private DebugLog _log;

    public PatchLocator(DebugLog log) {
        _log = log;
    }

    public String locatePatchFileFor(String toCheckFilePath, ArrayList<String> filePaths) {
        File toCheckFile = new File(toCheckFilePath);
        String toCheckFileName = toCheckFile.getName();
        String foundPatchFileName = null;
        for(int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            File file = new File(filePath);
            if(filePath.endsWith(".patch") && file.getName().startsWith(toCheckFileName)) {
                foundPatchFileName = filePath;
                i = filePaths.size();
            }
        }
        if(foundPatchFileName != null) {
            _log.logDebug("Found patch file for: " + toCheckFileName);
        }
        return foundPatchFileName;
    }
}
