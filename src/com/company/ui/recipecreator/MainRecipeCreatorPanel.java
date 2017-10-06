package com.company.ui.recipecreator;

import com.company.CNLog;
import com.company.SettingsWriter;
import com.company.models.MessageBundle;
import com.company.models.RecipeCreatorSettings;
import com.company.recipecreator.MassRecipeCreator;
import com.company.ui.OutputDisplay;

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
    private RecipeCreatorSettings _settings;
    private OutputDisplay _outputDisplay;
    private RecipeSettingsDisplay _settingsDisplay;
    private SettingsWriter _settingsWriter;

    public MainRecipeCreatorPanel(RecipeCreatorSettings settings,
                             SettingsWriter settingsWriter) {
        _settings = settings;
        _settingsWriter = settingsWriter;
    }

    public JPanel create(Dimension size) {
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(size);
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        _settingsDisplay = new RecipeSettingsDisplay(_settingsWriter, _settings);

        _outputDisplay = new OutputDisplay();
        JPanel outputDisplayPanel = _outputDisplay.get();
        if(_log != null) {
            _log.dispose();
        }
        _log = new CNLog(_outputDisplay, _settings);
        mainPanel.setVisible(true);
        JPanel settingsPanel = _settingsDisplay.setup(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JButton source = (JButton) e.getSource();
                source.setEnabled(false);
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            _log.clear();
                            _log.setupDebugLogFile();
                            _outputDisplay.clear();
                            _settingsDisplay.disable();
                            MassRecipeCreator creator = new MassRecipeCreator(_settings, _log);
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
