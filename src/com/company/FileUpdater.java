package com.company;

import com.company.Updaters.Updater;
import com.company.models.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:38 PM
 */
public class FileUpdater {
    private DebugLog _log;
    private ConfigSettings _settings;
    private ValueCalculator _valueCalculator;
    private JsonManipulator _manipulator;
    private ArrayList<Updater> _updaters;

    public FileUpdater(DebugLog log,
                       ConfigSettings settings,
                       ValueCalculator valueCalculator,
                       JsonManipulator manipulator,
                       ArrayList<Updater> updaters) {
        _log = log;
        _settings = settings;
        _valueCalculator = valueCalculator;
        _manipulator = manipulator;
        _updaters = updaters;
    }

    public void updateValues() {
        ArrayList<String> filePaths = getFileNames();
        for (int i = 0; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            for(int j = 0; j < _updaters.size(); j++) {
                Updater updater = _updaters.get(j);
                if(updater.canUpdate(filePath)) {
                    updater.update(filePath);
                    j = _updaters.size();
                }
            }
        }
    }

    private ArrayList<String> getFileNames() {
        ArrayList<String> filePaths = new ArrayList<String>();
        for(int i = 0; i < _settings.locationsToUpdate.length; i++) {
            File directory = new File(_settings.locationsToUpdate[i]);
            ArrayList<String> subFilePaths = getFileNames(directory);
            for(int j = 0; j < subFilePaths.size(); j++) {
                filePaths.add(subFilePaths.get(j));
            }
        }
        return filePaths;
    }

    private ArrayList<String> getFileNames(File directory) {
        ArrayList<String> filePaths = new ArrayList<String>();
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile() && isFileIncluded(file.getName())){
                _log.logDebug("Updating file with name: " + file.getName());
                filePaths.add(file.getAbsolutePath());
            }
            else if (file.isDirectory()){
                ArrayList<String> subPaths = getFileNames(file);
                for(int i = 0; i < subPaths.size(); i++) {
                    filePaths.add(subPaths.get(i));
                }
            }
            else {
                _log.logDebug("Skipping file: " + file.getName());
            }
        }
        return filePaths;
    }

    private boolean isFileIncluded(String fileName) {
        boolean isIncluded = false;
        for(int i = 0; i < _settings.extensionsToUpdate.length; i++) {
            if(fileName.endsWith(_settings.extensionsToUpdate[i])) {
                isIncluded = true;
                i = _settings.extensionsToUpdate.length;
            }
        }
        return isIncluded;
    }
}
