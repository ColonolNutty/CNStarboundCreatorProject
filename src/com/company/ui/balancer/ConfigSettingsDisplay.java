package com.company.ui.balancer;

import com.company.CNUtils;
import com.company.models.ConfigSettings;
import com.company.ui.CNUIExtensions;
import com.company.ui.SettingsDisplayBase;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Hashtable;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;

public class ConfigSettingsDisplay extends SettingsDisplayBase {
    public JPanel setup(ConfigSettings settings, ActionListener onRun) {
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
                settings.locationsToUpdate);
        JPanel includeLocsPanel = addTextArea(
                "Relative Locations To Include In Searches (Comma Separated): ",
                "includeLocations",
                settings.includeLocations);
        JPanel excludeEffectsPanel = addTextArea(
                "Exclude Effects With Name (Comma Separated): ",
                "excludedEffects",
                settings.excludedEffects);

        //TextFields
        JPanel ingredientOverridePath = createField(FieldType.TextField,
                "Relative Path To Ingredient Overrides: ",
                "ingredientOverridePath",
                settings.ingredientOverridePath);
        JPanel logFile = createField(FieldType.TextField,
                "Relative Log File Path: ",
                "logFile",
                settings.logFile);

        //Sliders
        JPanel increasePercentage = addSlider("Increase Percent Each Step (Ex. 0.05): ",
                "increasePercentage",
                0,
                100,
                5,
                20,
                (int)(settings.increasePercentage*100));
        JPanel minimumFoodValue = addSlider("Minimum Food Value (No food will be allowed a value below this): ",
                "minimumFoodValue",
                1,
                21,
                2,
                10,
                settings.minimumFoodValue);
        JPanel numberOfPasses = addSlider("Number Of Passes:",
                "numberOfPasses",
                1,
                31,
                1,
                5,
                settings.numberOfPasses);

        //CheckBox
        JPanel enableTreeView = createField(FieldType.CheckBox,
                "Enable Tree View",
                "enableTreeView",
                settings.enableTreeView);
        JPanel enableConsoleDebug = createField(FieldType.CheckBox,
                "Enable Console Debug",
                "enableConsoleDebug",
                settings.enableConsoleDebug);
        JPanel enableVerboseLogging = createField(FieldType.CheckBox,
                "Enable Verbose Logging",
                "enableVerboseLogging",
                settings.enableVerboseLogging);
        JPanel enableEffectsUpdate = createField(FieldType.CheckBox,
                "Update Effects",
                "enableEffectsUpdate",
                settings.enableEffectsUpdate);
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
                                        .addComponent(enableEffectsUpdate))
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

        setupChangeListeners(settings);
        return settingsDisplay;
    }

    public void updateConfigSettings(ConfigSettings settings) {
        String[] newLocToUpdate = CNUtils.fromCommaSeparated(getCurrentText("locationsToUpdate"));
        if(newLocToUpdate != null) {
            settings.locationsToUpdate = newLocToUpdate;
        }
        String[] includeLocations = CNUtils.fromCommaSeparated(getCurrentText("includeLocations"));
        if(includeLocations != null) {
            settings.includeLocations = includeLocations;
        }
        String[] excludedEffects = CNUtils.fromCommaSeparated(getCurrentText("excludedEffects"));
        if(excludedEffects != null) {
            settings.excludedEffects = excludedEffects;
        }

        String ingredientOverridePath = getCurrentText("ingredientOverridePath");
        if(ingredientOverridePath != null) {
            settings.ingredientOverridePath = ingredientOverridePath;
        }

        String logFile = getCurrentText("logFile");
        if(logFile != null) {
            settings.logFile = logFile;
        }

        Integer minimumFoodValue = getCurrentValue("minimumFoodValue");
        if(minimumFoodValue != null) {
            settings.minimumFoodValue = minimumFoodValue;
        }
        Integer increasePercentage = getCurrentValue("increasePercentage");
        if(increasePercentage != null) {
            settings.increasePercentage = increasePercentage/100.0;
        }
        Integer numberOfPasses = getCurrentValue("numberOfPasses");
        if(numberOfPasses != null) {
            settings.numberOfPasses = numberOfPasses;
        }

        Boolean enableTreeView = getIsSelected("enableTreeView");
        if(enableTreeView != null) {
            settings.enableTreeView = enableTreeView;
        }
        Boolean enableConsoleDebug = getIsSelected("enableConsoleDebug");
        if(enableConsoleDebug != null) {
            settings.enableConsoleDebug = enableConsoleDebug;
        }
        Boolean enableVerboseLogging = getIsSelected("enableVerboseLogging");
        if(enableVerboseLogging != null) {
            settings.enableVerboseLogging = enableVerboseLogging;
        }
        Boolean enableEffectsUpdate = getIsSelected("enableEffectsUpdate");
        if(enableEffectsUpdate != null) {
            settings.enableEffectsUpdate = enableEffectsUpdate;
        }
    }

    private void setupChangeListeners(final ConfigSettings settings) {
        setupTextEntryFocusListener("locationsToUpdate", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.locationsToUpdate = CNUtils.fromCommaSeparated(text.getText());
            }
        });
        setupTextEntryFocusListener("includeLocations", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.includeLocations = CNUtils.fromCommaSeparated(text.getText());
            }
        });
        setupTextEntryFocusListener("excludedEffects", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.excludedEffects = CNUtils.fromCommaSeparated(text.getText());
            }
        });
        setupTextEntryFocusListener("ingredientOverridePath", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.ingredientOverridePath = text.getText();
            }
        });
        setupTextEntryFocusListener("logFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.logFile = text.getText();
            }
        });

        setupSliderListener("increasePercentage", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int val = slider.getValue();
                settings.increasePercentage = val/100.0;
            }
        });
        setupSliderListener("minimumFoodValue", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int val = slider.getValue();
                settings.minimumFoodValue = val;
            }
        });
        setupSliderListener("numberOfPasses", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int val = slider.getValue();
                settings.numberOfPasses = val;
            }
        });

        setupCheckBoxListener("enableTreeView", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                settings.enableTreeView = checkbox.isSelected();
            }
        });
        setupCheckBoxListener("enableConsoleDebug", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                settings.enableConsoleDebug = checkbox.isSelected();
            }
        });
        setupCheckBoxListener("enableVerboseLogging", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                settings.enableVerboseLogging = checkbox.isSelected();
            }
        });
        setupCheckBoxListener("enableEffectsUpdate", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                settings.enableEffectsUpdate = checkbox.isSelected();
            }
        });
    }
}
