package com.colonolnutty;

import com.colonolnutty.module.shareddata.ModuleLoader;
import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;
import com.colonolnutty.module.shareddata.ui.MainWindow;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ModuleLoader loader = new ModuleLoader();
        ArrayList<IMainFunctionPanel> mainPanels = loader.loadModulePanels("modules");
        MainWindow main = new MainWindow("Ingredient Balancer", mainPanels);
        main.start();
    }
}
