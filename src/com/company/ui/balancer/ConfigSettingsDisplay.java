package com.company.ui.balancer;

import com.company.CNUtils;
import com.company.SettingsWriter;
import com.company.models.ConfigSettings;
import com.company.ui.SettingsDisplayBase;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;

public class ConfigSettingsDisplay extends SettingsDisplayBase {
    private SettingsWriter _writer;
    private ConfigSettings _settings;

    public ConfigSettingsDisplay(SettingsWriter writer, ConfigSettings settings) {
        _writer = writer;
        this._settings = settings;
    }

    public JPanel setup(ActionListener onRun) {
        JPanel settingsDisplay = new JPanel();
        GroupLayout layout = new GroupLayout(settingsDisplay);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        settingsDisplay.setLayout(layout);
        JPanel currentDir = addCurrentDirectoryField();

        //TextAreas
        JPanel locsToUpdate = addTextArea(
                "Relative Locations To Update (Comma Separated): ",
                "locationsToUpdate",
                _settings.locationsToUpdate);
        JPanel includeLocsPanel = addTextArea(
                "Relative Locations To Include In Searches (Comma Separated): ",
                "includeLocations",
                _settings.includeLocations);
        JPanel excludeEffectsPanel = addTextArea(
                "Exclude Effects With Name (Comma Separated): ",
                "excludedEffects",
                _settings.excludedEffects);

        //TextFields
        JPanel ingredientOverridePath = createField(FieldType.TextField,
                "Relative Path To Ingredient Overrides: ",
                "ingredientOverridePath",
                _settings.ingredientOverridePath);
        JPanel logFile = createField(FieldType.TextField,
                "Relative Log File Path: ",
                "logFile",
                _settings.logFile);

        //Sliders
        JPanel increasePercentage = addSlider("Increase Percent Each Step (Ex. 0.05): ",
                "increasePercentage",
                0,
                100,
                5,
                20,
                (int)(_settings.increasePercentage*100));
        JPanel minimumFoodValue = addSlider("Minimum Food Value (No food will be allowed a value below this): ",
                "minimumFoodValue",
                1,
                21,
                2,
                10,
                _settings.minimumFoodValue);
        JPanel numberOfPasses = addSlider("Number Of Passes:",
                "numberOfPasses",
                1,
                31,
                1,
                5,
                _settings.numberOfPasses);

        //CheckBox
        JPanel enableTreeView = createField(FieldType.CheckBox,
                "Enable Tree View",
                "enableTreeView",
                _settings.enableTreeView);
        JPanel enableConsoleDebug = createField(FieldType.CheckBox,
                "Enable Console Debug",
                "enableConsoleDebug",
                _settings.enableConsoleDebug);
        JPanel enableVerboseLogging = createField(FieldType.CheckBox,
                "Enable Verbose Logging",
                "enableVerboseLogging",
                _settings.enableVerboseLogging);
        JPanel enableEffectsUpdate = createField(FieldType.CheckBox,
                "Update Effects",
                "enableEffectsUpdate",
                _settings.enableEffectsUpdate);
        JButton runButton = createButton("Run", onRun);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        //Top
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(currentDir)
                                        .addComponent(runButton)
                        )
                        //Bottom
                        .addGroup(
                                layout.createSequentialGroup()
                                        //Middle - Left
                                        .addGroup(
                                                layout.createParallelGroup()
                                                        .addGroup(
                                                                layout.createParallelGroup()
                                                                        .addComponent(locsToUpdate)
                                                                        .addComponent(includeLocsPanel)
                                                                        .addComponent(excludeEffectsPanel)
                                                        )
                                                        .addGroup(
                                                                layout.createParallelGroup()
                                                                        .addComponent(ingredientOverridePath)
                                                                        .addComponent(logFile))
                                        )
                                        //Middle - Middle
                                        .addGroup(
                                                layout.createParallelGroup()
                                                        .addComponent(increasePercentage)
                                                        .addComponent(minimumFoodValue)
                                                        .addComponent(numberOfPasses)
                                        )
                        )
                        //Bottom
                        .addGroup(layout.createSequentialGroup()
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                                        .addComponent(enableEffectsUpdate)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        //Top
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(currentDir)
                                        .addComponent(runButton)
                        )
                        .addGroup(layout.createParallelGroup()
                                //Middle - Left
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(locsToUpdate)
                                                                .addComponent(includeLocsPanel)
                                                                .addComponent(excludeEffectsPanel)
                                                )
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(ingredientOverridePath)
                                                                .addComponent(logFile)
                                                )
                                )
                                //Middle - Middle
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(increasePercentage)
                                                .addComponent(minimumFoodValue)
                                                .addComponent(numberOfPasses)
                                )
                        )
                        //Middle - Right
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                                        .addComponent(enableEffectsUpdate)
                        )
        );

        setupChangeListeners();
        return settingsDisplay;
    }

    private void setupChangeListeners() {
        setupTextEntryFocusListener("locationsToUpdate", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.locationsToUpdate = CNUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("includeLocations", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.includeLocations = CNUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("excludedEffects", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.excludedEffects = CNUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("ingredientOverridePath", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.ingredientOverridePath = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("logFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.logFile = text.getText();
                writeSettings();
            }
        });

        setupSliderListener("increasePercentage", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int val = slider.getValue();
                _settings.increasePercentage = val/100.0;
                writeSettings();
            }
        });
        setupSliderListener("minimumFoodValue", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int val = slider.getValue();
                _settings.minimumFoodValue = val;
                writeSettings();
            }
        });
        setupSliderListener("numberOfPasses", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int val = slider.getValue();
                _settings.numberOfPasses = val;
                writeSettings();
            }
        });

        setupCheckBoxListener("enableTreeView", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.enableTreeView = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("enableConsoleDebug", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.enableConsoleDebug = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("enableVerboseLogging", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.enableVerboseLogging = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("enableEffectsUpdate", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.enableEffectsUpdate = checkbox.isSelected();
                writeSettings();
            }
        });
    }

    private void writeSettings() {
        _writer.write(_settings);
    }
}
