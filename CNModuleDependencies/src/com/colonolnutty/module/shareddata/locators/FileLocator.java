package com.colonolnutty.module.shareddata.locators;


import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.utils.CNFileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:11 AM
 */
public class FileLocator {

    private CNLog _log;
    private ArrayList<String> _includedFileTypes;
    private ArrayList<String> _fileNameCache;

    public FileLocator(CNLog log) {
        _log = log;
        _includedFileTypes = new ArrayList<String>();
        _includedFileTypes.add(".item");
        _includedFileTypes.add(".consumable");
        _includedFileTypes.add(".patch");
        _includedFileTypes.add(".object");
        _includedFileTypes.add(".matitem");
        _includedFileTypes.add(".liquid");
        _includedFileTypes.add(".liqitem");
        _includedFileTypes.add(".material");
        _includedFileTypes.add(".projectile");
        _includedFileTypes.add(".recipe");
        _includedFileTypes.add(".statuseffect");
    }

    public void setupIncludedFileTypes(ArrayList<String> fileTypes) {
        if(fileTypes == null) {
            return;
        }
        _includedFileTypes = fileTypes;
    }

    public ArrayList<String> getFilePaths(ArrayList<String> locations) {
        if(_fileNameCache != null) {
            return _fileNameCache;
        }
        ArrayList<String> filePaths = new ArrayList<String>();
        for(String location : locations) {
            filePaths.addAll(getFilePaths(location));
        }
        _fileNameCache = filePaths;
        return _fileNameCache;
    }

    public ArrayList<String> getFilePathsByExtension(ArrayList<String> locations, String extension) {
        if(extension == null) {
            return new ArrayList<String>();
        }
        ArrayList<String> filePaths = getFilePaths(locations);
        ArrayList<String> matchingFilePaths = new ArrayList<String>();
        for(String filePath : filePaths) {
            if (filePath.endsWith(extension)) {
                matchingFilePaths.add(filePath);
            }
        }
        return matchingFilePaths;
    }

    public ArrayList<String> getFilePathsByExtension(ArrayList<String> locations, String[] extensions) {
        int extensionsArrLength = extensions.length;
        if(extensionsArrLength == 0) {
            return new ArrayList<String>();
        }
        ArrayList<String> filePaths = getFilePaths(locations);
        ArrayList<String> matchingFilePaths = new ArrayList<String>();
        for(String filePath : filePaths) {
            for(int i = 0; i < extensionsArrLength; i++) {
                if (filePath.endsWith(extensions[i])) {
                    matchingFilePaths.add(filePath);
                    i = extensionsArrLength;
                }
            }
        }
        return matchingFilePaths;
    }

    private ArrayList<String> getFilePaths(String file) {
        ArrayList<String> filePaths = new ArrayList<String>();
        File directory = new File(file);
        ArrayList<String> subFilePaths = getFilePaths(directory);
        filePaths.addAll(subFilePaths);
        return filePaths;
    }

    private ArrayList<String> getFilePaths(File directory) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //getTextArea all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && isValidFileType(file.getName())){
                filePaths.add(file.getAbsolutePath());
            }
            else if (file.isDirectory()){
                ArrayList<String> subPaths = getFilePaths(file);
                for(int i = 0; i < subPaths.size(); i++) {
                    filePaths.add(subPaths.get(i));
                }
            }
        }
        return filePaths;
    }

    private boolean isValidFileType(String fileName) {
        if(fileName.startsWith("obsolete") || fileName.startsWith("ignore")) {
            return false;
        }
        return CNFileUtils.fileEndsWith(fileName, _includedFileTypes);
    }
}
