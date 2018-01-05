package com.colonolnutty.module.shareddata.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/27/2017
 * Time: 10:18 AM
 */
public class MainWindow {
    private JFrame _mainFrame;
    private ArrayList<IMainFunctionPanel> _mainFunctionPanels;
    private String _name;

    public MainWindow(String name, ArrayList<IMainFunctionPanel> mainFunctionPanels) {
        _name = name;
        _mainFunctionPanels = mainFunctionPanels;
    }

    public void start() {
        setup();
        if(_mainFrame == null) {
            return;
        }
        _mainFrame.setVisible(true);
    }

    private void setup() {
        if(_mainFrame != null) {
            return;
        }
        _mainFrame = new JFrame(_name);
        _mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            for(IMainFunctionPanel panel : _mainFunctionPanels) {
                panel.dispose();
            }
            _mainFrame = null;
            }
        });
        _mainFrame.setLayout(new BorderLayout());
        _mainFrame.getContentPane().setBackground(Color.BLUE);
        JTabbedPane tabbedPane = new JTabbedPane();
        Dimension mainSize = _mainFrame.getSize();
        boolean hasModules = false;
        for(IMainFunctionPanel panel : _mainFunctionPanels) {
            JPanel createdPanel;
            try {
                createdPanel = panel.create();
                createdPanel.setSize(mainSize);
                tabbedPane.add(panel.getName(), createdPanel);
                hasModules = true;
            }
            catch(Exception e) {
                System.out.println("Error loading module: " + panel.getName());
                e.printStackTrace(System.err);
            }
        }
        if(!hasModules) {
            _mainFrame.dispose();
            _mainFrame = null;
            return;
        }
        _mainFrame.add(tabbedPane);
        _mainFrame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameDimensions = _mainFrame.getSize();
        int newX = screenSize.width/2 - frameDimensions.width/2;
        if(newX < 0) {
            newX = 0;
        }
        int newY = screenSize.height/2 - frameDimensions.height/2;
        if(newY < 0) {
            newY = 0;
        }
        _mainFrame.setLocation(newX, newY);
    }
}
