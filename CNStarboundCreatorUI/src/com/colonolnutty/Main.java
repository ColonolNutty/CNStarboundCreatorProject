package com.colonolnutty;

import com.colonolnutty.module.shareddata.ui.MainFunctionPanel;
import com.colonolnutty.ui.MainWindow;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ModuleLoader loader = new ModuleLoader();
        ArrayList<MainFunctionPanel> mainPanels = loader.loadModulePanels("modules");
        MainWindow main = new MainWindow(mainPanels);
        main.start();
    }
}
