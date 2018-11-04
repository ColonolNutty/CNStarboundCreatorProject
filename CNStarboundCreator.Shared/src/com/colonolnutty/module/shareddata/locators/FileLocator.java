package com.colonolnutty.module.shareddata.locators;


import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.utils.CNFileUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/16/2017
 * Time: 11:11 AM
 */
public class FileLocator {

    protected CNLog Log;
    private ArrayList<String> _includedFileTypes;
    private ArrayList<String> _fileNameCache;

    public FileLocator(CNLog log) {
        Log = log;
        ArrayList<String> includedFileTypes = new ArrayList<>();
        includedFileTypes.add(".item");
        includedFileTypes.add(".consumable");
        includedFileTypes.add(".patch");
        includedFileTypes.add(".object");
        includedFileTypes.add(".matitem");
        includedFileTypes.add(".liquid");
        includedFileTypes.add(".liqitem");
        includedFileTypes.add(".material");
        includedFileTypes.add(".projectile");
        includedFileTypes.add(".recipe");
        includedFileTypes.add(".statuseffect");
        setupIncludedFileTypes(includedFileTypes);
    }

    protected ArrayList<String> getIncludedFileTypes() {
        return _includedFileTypes;
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
        if(directory == null || !directory.exists()) {
            return filePaths;
        }
        Log.debug("Locating files within directory: " + directory.getPath());
        //getTextArea all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && isValidFileType(file.getName())){
                Log.debug("Located file: " + file.getPath());
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

    protected boolean isValidFileType(String fileName) {
        if(fileName.startsWith("obsolete") || fileName.startsWith("ignore")) {
            Log.debug("Ignoring file with name: " + fileName);
            return false;
        }
        return CNFileUtils.fileEndsWith(fileName, _includedFileTypes);
    }
}
