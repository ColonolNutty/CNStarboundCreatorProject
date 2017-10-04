package com.company.ui;

import com.company.CNLog;
import com.company.SettingsWriter;
import com.company.balancer.ValueBalancer;
import com.company.models.ConfigSettings;
import com.company.models.MessageBundle;
import com.company.models.RecipeCreatorSettings;
import com.company.ui.balancer.ConfigSettingsDisplay;
import com.company.ui.balancer.MainBalancerPanel;
import com.company.ui.recipecreator.MainRecipeCreatorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/27/2017
 * Time: 10:18 AM
 */
public class MainWindow {
    private JFrame _mainFrame;
    private MainBalancerPanel _mainBalancerPanel;
    private MainRecipeCreatorPanel _mainRecipeCreatorPanel;

    public MainWindow(ConfigSettings settings,
                      RecipeCreatorSettings recipeCreatorSettings,
                      SettingsWriter settingsWriter) {
        _mainBalancerPanel = new MainBalancerPanel(settings, settingsWriter);
        _mainRecipeCreatorPanel = new MainRecipeCreatorPanel(recipeCreatorSettings, settingsWriter);
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
            _mainBalancerPanel.dispose();
            _mainRecipeCreatorPanel.dispose();
            _mainFrame = null;
            }
        });
        _mainFrame.setLayout(new BorderLayout());
        _mainFrame.getContentPane().setBackground(Color.BLUE);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Balancer", _mainBalancerPanel.create(_mainFrame.getSize()));
        tabbedPane.add("Creator", _mainRecipeCreatorPanel.create(_mainFrame.getSize()));
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
