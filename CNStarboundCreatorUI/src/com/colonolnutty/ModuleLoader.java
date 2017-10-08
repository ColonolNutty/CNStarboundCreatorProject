package com.colonolnutty;


import com.colonolnutty.module.shareddata.ui.MainFunctionPanel;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 1:58 PM
 */
public class ModuleLoader {
    public ArrayList<MainFunctionPanel> loadModulePanels(String modulesPath) {
        ArrayList<MainFunctionPanel> mainModules = new ArrayList<MainFunctionPanel>();
        File modulesDirectory = new File(modulesPath);
        File[] files = modulesDirectory.listFiles();
        if(files == null) {
            System.err.println("No files found within folder: " + modulesDirectory.getAbsolutePath());
        }
        for(File file : files) {
            try {
                URL[] urls = new URL[1];
                urls[0] = file.toURI().toURL();
                URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());
                Class classToLoad = Class.forName("main.ui.MainPanel", true, child);
                Object instance = classToLoad.newInstance();
                if(instance == null || !(instance instanceof MainFunctionPanel)) {
                    System.out.println("Error loading module: " + file.getName());
                    continue;
                }
                mainModules.add((MainFunctionPanel) instance);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return mainModules;
    }
}
