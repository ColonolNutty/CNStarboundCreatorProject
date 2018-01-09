package com.colonolnutty.module.shareddata.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 01/06/2018
 * Time: 3:26 PM
 */
public class FileFinder implements IFileFinder {
    private Hashtable<String, ArrayList<String>> _foundFiles;

    public FileFinder() {
        _foundFiles = new Hashtable<String, ArrayList<String>>();
    }

    @Override
    public ArrayList<String> findFiles(String rootFolder) {
        return findFiles(new File(rootFolder));
    }

    @Override
    public ArrayList<String> findFiles(File rootFolder) {
        String path = rootFolder.getAbsolutePath();
        File root = new File(path);
        return getFiles(root, root);
    }

    public ArrayList<String> getFiles(File root, File folder) {
        if(!folder.exists() || !folder.isDirectory()) {
            return new ArrayList<String>();
        }

        ArrayList<String> filePaths = new ArrayList<String>();
        for(File file : folder.listFiles()) {
            if(file.isDirectory()) {
                ArrayList<String> subFiles = getFiles(root, file);
                if(subFiles != null) {
                    filePaths.addAll(subFiles);
                }
                continue;
            }
            else if(file.isFile()) {
                filePaths.add(flipSlashes(toRelativePathFrom(root, file)));
            }
        }

        return filePaths;
    }

    public String flipSlashes(String filePath) {
        return filePath.replace("\\", "/");
    }

    public String toRelativePathFrom(File root, File file) {
        return file.getAbsolutePath().replace(root.getAbsolutePath() + "\\", "");
    }
}
