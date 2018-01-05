package com.colonolnutty.module.shareddata;


import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;

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
    public ArrayList<IMainFunctionPanel> loadModulePanels(String modulesPath) {
        ArrayList<IMainFunctionPanel> mainModules = new ArrayList<IMainFunctionPanel>();
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
                System.out.println("Loading module: " + file.getName());
                String packageName = file.getName().split("\\.")[0].toLowerCase();
                String moduleName = "main." + packageName + ".ui.MainPanel";
                Class classToLoad = Class.forName(moduleName, true, child);
                Object instance = classToLoad.newInstance();
                if(instance == null || !(instance instanceof IMainFunctionPanel)) {
                    System.out.println("Error loading module: " + file.getName());
                    continue;
                }
                mainModules.add((IMainFunctionPanel) instance);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return mainModules;
    }
}
