package main.cnbalancermodule.ui;

import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import main.settings.BalancerSettings;
import com.colonolnutty.module.shareddata.io.ConfigWriter;
import com.colonolnutty.module.shareddata.ui.SettingsDisplayBase;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.event.*;

public class BalancerSettingsDisplay extends SettingsDisplayBase {
    private ConfigWriter _writer;
    private BalancerSettings _settings;

    public BalancerSettingsDisplay(ConfigWriter writer, BalancerSettings settings) {
        _writer = writer;
        this._settings = settings;
    }

    @Override
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
        JPanel fileTypesToUpdatePanel = addTextArea(
                "The file types to update (ex. '.liquid, .matitem') (Comma Separated): ",
                "fileTypesToUpdate",
                _settings.fileTypesToUpdate);
        JPanel propertiesToUpdatePanel = addTextArea(
                "Properties to update (ex. 'foodValue') (Comma Separated): ",
                "propertiesToUpdate",
                _settings.propertiesToUpdate);

        //TextFields
        JPanel ingredientOverridePath = createField(FieldType.TextField,
                "Relative Path To Ingredient Overrides: ",
                "ingredientOverridePath",
                _settings.ingredientOverridePath);
        JPanel propertyOrderFile = createField(FieldType.TextField,
                "Relative path to file listing the order of JSON properties: ",
                "propertyOrderFile",
                _settings.propertyOrderFile);
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
        JPanel includeCraftGroups = createField(FieldType.CheckBox,
                "Include Craft Groups",
                "includeCraftGroups",
                _settings.includeCraftGroups);
        JPanel forceUpdate = createField(FieldType.CheckBox,
                "Force Update",
                "forceUpdate",
                _settings.forceUpdate);
        JPanel showConfirmation = createField(FieldType.CheckBox,
                "Show Confirmation",
                "showConfirmation",
                _settings.showConfirmation);
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
                                                                        .addComponent(propertyOrderFile)
                                                                        .addComponent(logFile)
                                                        )
                                        )
                                        //Middle - Middle
                                        .addGroup(
                                                layout.createParallelGroup()
                                                        .addComponent(increasePercentage)
                                                        .addComponent(minimumFoodValue)
                                                        .addComponent(numberOfPasses)
                                                        .addComponent(fileTypesToUpdatePanel)
                                                        .addComponent(propertiesToUpdatePanel)
                                        )
                        )
                        //Bottom
                        .addGroup(layout.createSequentialGroup()
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                                        .addComponent(enableEffectsUpdate)
                                        .addComponent(includeCraftGroups)
                                        .addComponent(forceUpdate)
                                        .addComponent(showConfirmation)
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
                                                                .addComponent(propertyOrderFile)
                                                                .addComponent(logFile)
                                                )
                                )
                                //Middle - Middle
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(increasePercentage)
                                                .addComponent(minimumFoodValue)
                                                .addComponent(numberOfPasses)
                                                .addComponent(fileTypesToUpdatePanel)
                                                .addComponent(propertiesToUpdatePanel)
                                )
                        )
                        //Middle - Right
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                                        .addComponent(enableEffectsUpdate)
                                        .addComponent(includeCraftGroups)
                                        .addComponent(forceUpdate)
                                        .addComponent(showConfirmation)
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
                _settings.locationsToUpdate = CNStringUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("includeLocations", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.includeLocations = CNStringUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("excludedEffects", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.excludedEffects = CNStringUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("fileTypesToUpdate", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.fileTypesToUpdate = CNStringUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("propertiesToUpdate", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.propertiesToUpdate = CNStringUtils.fromCommaSeparated(text.getText());
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
        setupTextEntryFocusListener("propertyOrderFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.propertyOrderFile = text.getText();
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
        setupCheckBoxListener("includeCraftGroups", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.includeCraftGroups = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("forceUpdate", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.forceUpdate = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("showConfirmation", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.showConfirmation = checkbox.isSelected();
                writeSettings();
            }
        });
    }

    private void writeSettings() {
        _writer.write(_settings);
    }
}
