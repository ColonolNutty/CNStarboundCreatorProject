package com.company.ui.recipecreator;

import com.company.CNLog;
import com.company.ConsoleDebugWriter;
import com.company.JsonManipulator;
import com.company.SettingsWriter;
import com.company.balancer.ValueBalancer;
import com.company.models.MessageBundle;
import com.company.models.RecipeCreatorSettings;
import com.company.recipecreator.MassRecipeCreator;
import com.company.ui.OutputDisplay;
import com.company.ui.balancer.ConfigSettingsDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 5:01 PM
 */
public class MainRecipeCreatorPanel {
    private CNLog _log;
    private RecipeCreatorSettings _configSettings;
    private OutputDisplay _outputDisplay;
    private RecipeSettingsDisplay _settingsDisplay;
    private SettingsWriter _settingsWriter;

    public MainRecipeCreatorPanel(RecipeCreatorSettings settings,
                             SettingsWriter settingsWriter) {
        _configSettings = settings;
        _settingsWriter = settingsWriter;
    }

    public JPanel create(Dimension size) {
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(size);
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        _settingsDisplay = new RecipeSettingsDisplay();

        _outputDisplay = new OutputDisplay();
        JPanel outputDisplayPanel = _outputDisplay.get();
        if(_log != null) {
            _log.dispose();
        }
        _log = new CNLog(_outputDisplay);
        mainPanel.setVisible(true);
        JPanel settingsPanel = _settingsDisplay.setup(_configSettings, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JButton source = (JButton) e.getSource();
                source.setEnabled(false);
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            _log.clear();
                            _outputDisplay.clear();
                            _settingsDisplay.disable();
                            _settingsDisplay.updateConfigSettings(_configSettings);
                            _settingsWriter.write(_configSettings);
                            MassRecipeCreator creator = new MassRecipeCreator(_configSettings, _log);
                            creator.create();
                            Hashtable<String, MessageBundle> messages = _log.getMessages();
                            _outputDisplay.updateTreeDisplay(messages);
                        }
                        catch(Exception e1) {
                            e1.printStackTrace();
                        }
                        finally {
                            source.setEnabled(true);
                            _settingsDisplay.enable();
                        }
                    }
                };
                thread.start();
            }
        });
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(settingsPanel)
                        .addComponent(outputDisplayPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(settingsPanel)
                        .addComponent(outputDisplayPanel)
        );
        return mainPanel;
    }

    public void dispose() {
        if(_log != null) {
            _log.dispose();
        }
    }
}
