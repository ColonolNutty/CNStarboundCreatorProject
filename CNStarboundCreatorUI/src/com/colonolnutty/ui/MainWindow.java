package com.colonolnutty.ui;

import com.colonolnutty.module.shareddata.ui.MainFunctionPanel;

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
    private ArrayList<MainFunctionPanel> _mainFunctionPanels;

    public MainWindow(ArrayList<MainFunctionPanel> mainFunctionPanels) {
        _mainFunctionPanels = mainFunctionPanels;
    }

    public void start() {
        setup();
        _mainFrame.setVisible(true);
    }

    private void setup() {
        if(_mainFrame != null) {
            return;
        }
        _mainFrame = new JFrame("Ingredient Balancer");
        _mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            for(MainFunctionPanel panel : _mainFunctionPanels) {
                panel.dispose();
            }
            _mainFrame = null;
            }
        });
        _mainFrame.setLayout(new BorderLayout());
        _mainFrame.getContentPane().setBackground(Color.BLUE);
        JTabbedPane tabbedPane = new JTabbedPane();
        Dimension mainSize = _mainFrame.getSize();
        for(MainFunctionPanel panel : _mainFunctionPanels) {
            JPanel createdPanel = panel.create();
            createdPanel.setSize(mainSize);
            tabbedPane.add(panel.getName(), createdPanel);
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
