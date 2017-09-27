package com.company.ui;

import com.company.CNUtils;
import com.company.models.ConfigSettings;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

public class ConfigSettingsDisplay {
    private Hashtable<String, JTextComponent> _textFields;
    private Hashtable<String, JCheckBox> _checkBoxes;
    private Hashtable<String, JSlider> _sliders;

    public ConfigSettingsDisplay() {
        _textFields = new Hashtable<String, JTextComponent>();
        _checkBoxes = new Hashtable<String, JCheckBox>();
        _sliders = new Hashtable<String, JSlider>();
    }

    public JPanel setup(ConfigSettings settings) {
        JPanel settingsDisplay = new JPanel();
        settingsDisplay.setLayout(new FlowLayout());
        addCurrentDirectoryField(settingsDisplay);
        addField(settingsDisplay, FieldType.TextArea,
                "Relative Locations To Update (Comma Separated): ",
                "locationsToUpdate",
                200,
                50,
                settings.locationsToUpdate);
        addField(settingsDisplay,
                FieldType.TextArea,
                "Relative Locations To Include In Searches (Comma Separated): ",
                "includeLocations",
                200,
                25,
                settings.includeLocations);
        addField(settingsDisplay,
                FieldType.TextArea,
                "Exclude Effects With Name (Comma Separated): ",
                "excludedEffects",
                200,
                25,
                settings.excludedEffects);
        addField(settingsDisplay,
                FieldType.TextField,
                "Relative Path To Ingredient Overrides: ",
                "ingredientOverridePath",
                200,
                25,
                settings.ingredientOverridePath);
        addField(settingsDisplay,
                FieldType.TextField,
                "Relative Log File Path: ",
                "logFile",
                200,
                25,
                settings.logFile);
        addSlider(settingsDisplay,
                "Increase Percent Each Step (Ex. 0.05): ",
                "increasePercentage",
                0,
                100,
                5,
                20,
                (int)(settings.increasePercentage*100));
        addSlider(settingsDisplay,
                "Minimum Food Value (No food will be allowed a value below this): ",
                "minimumFoodValue",
                1,
                21,
                2,
                10,
                settings.minimumFoodValue);
        addSlider(settingsDisplay,
                "Number Of Passes:",
                "numberOfPasses",
                1,
                31,
                1,
                5,
                settings.numberOfPasses);
        addField(settingsDisplay,
                FieldType.CheckBox,
                "Enable Console Debug",
                "enableConsoleDebug",
                0,
                0,
                settings.enableConsoleDebug);
        addField(settingsDisplay,
                FieldType.CheckBox,
                "Enable Verbose Logging",
                "enableVerboseLogging",
                0,
                0,
                settings.enableVerboseLogging);
        addField(settingsDisplay,
                FieldType.CheckBox,
                "Update Effects",
                "enableEffectsUpdate",
                0,
                0,
                settings.enableEffectsUpdate);
        setupChangeListeners(settings);
        return settingsDisplay;
    }

    private void addCurrentDirectoryField(JPanel settingsDisplay) {
        JLabel label = new JLabel("Current Working Directory: ");
        JTextField field = createTextField("currentDirectory", 500, 25);
        field.setEditable(false);
        field.setEnabled(false);
        field.setText(System.getProperty("user.dir"));
        settingsDisplay.add(createPanel(true, label, field));
    }

    private void addSlider(JPanel panel,
                           String label,
                           String name,
                           int min,
                           int max,
                           int minorTicks,
                           int majorTicks,
                           int initial) {
        JLabel configEntryLabel = new JLabel(label);
        JSlider slider = createSlider(name, min, max, minorTicks, majorTicks, initial);
        final JTextField field = createTextField(name + "textField", 25, 25);
        field.setText(initial + "");
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                int value = slider.getValue();
                String existingText = field.getText();
                String newText = value + "";
                if(existingText == null || !existingText.equals(newText)) {
                    field.setText(newText);
                }
            }
        });
        field.setEditable(false);

        _textFields.put(name, field);
        _sliders.put(name, slider);
        JPanel subPanel = createPanel(false, configEntryLabel, slider, field);
        panel.add(subPanel);
    }

    private void addField(JPanel panel,
                          FieldType fieldType,
                          String label,
                          String name,
                          int width,
                          int height,
                          Object initValue) {
        JLabel configEntryLabel = new JLabel(label);
        JPanel createdPanel = null;
        switch(fieldType) {
            case CheckBox:
                JCheckBox checkBox = createCheckBox(name);
                if(initValue != null) {
                    checkBox.setSelected((Boolean)initValue);
                }
                _checkBoxes.put(name, checkBox);
                createdPanel = createPanel(true, checkBox, configEntryLabel);
                break;
            case TextArea:
                JTextArea textArea = createTextArea(name, width, height);
                if(initValue != null) {
                    textArea.setText(CNUtils.toCommaSeparated((String[])initValue));
                }
                _textFields.put(name, textArea);
                createdPanel = createPanel(false, configEntryLabel, textArea);
                break;
            case TextField:
                JTextField textField = createTextField(name, width, height);
                if(initValue != null) {
                    textField.setText(initValue.toString());
                }
                _textFields.put(name, textField);
                createdPanel = createPanel(true, configEntryLabel, textField);
                break;
        }
        if(createdPanel == null) {
            return;
        }
        panel.add(createdPanel);
    }

    private JSlider createSlider(String name, int min, int max, int minorTicks, int majorTicks, int init) {
        JSlider slider = new JSlider();
        slider.setName(name);
        slider.setMinimum(min);
        slider.setMaximum(max);
        slider.setValue(init);
        slider.setMinorTickSpacing(minorTicks);
        slider.setMajorTickSpacing(majorTicks);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    private JTextArea createTextArea(String name, int width, int height) {
        JTextArea textArea = new JTextArea();
        textArea.setName(name);
        textArea.setPreferredSize(new Dimension(width, height));
        return textArea;
    }

    private JTextField createTextField(String name, int width, int height) {
        JTextField textField = new JTextField();
        textField.setName(name);
        textField.setPreferredSize(new Dimension(width, height));
        return textField;
    }

    private JCheckBox createCheckBox(String name) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setName(name);
        return checkBox;
    }

    private JPanel createPanel(boolean centerAlignLabel, Component... components) {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        GroupLayout.SequentialGroup horzGroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup vertGroup = layout.createParallelGroup();
        if(centerAlignLabel) {
            vertGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        }
        for (Component component : components) {
            horzGroup = horzGroup.addComponent(component);
            vertGroup = vertGroup.addComponent(component);
        }
        layout.setHorizontalGroup(horzGroup);
        layout.setVerticalGroup(vertGroup);

        panel.setVisible(true);
        return panel;
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
        setupTextEntryFocusListener("increasePercentage", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                String val = text.getText();
                int intVal = Integer.parseInt(val);
                settings.increasePercentage = intVal/100.0;
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
        setupTextEntryFocusListener("minimumFoodValue", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                String val = text.getText();
                settings.minimumFoodValue = Integer.parseInt(val);
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
        setupTextEntryFocusListener("numberOfPasses", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                String val = text.getText();
                settings.numberOfPasses = Integer.parseInt(val);
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

    private void setupTextEntryFocusListener(String name, FocusListener listener) {
        if(!_textFields.containsKey(name)) {
            return;
        }
        JTextComponent text = _textFields.get(name);
        text.addFocusListener(listener);
    }

    private void setupCheckBoxListener(String name, ChangeListener listener) {
        if(!_checkBoxes.containsKey(name)) {
            return;
        }
        JCheckBox checkBox = _checkBoxes.get(name);
        checkBox.addChangeListener(listener);
    }

    private void setupSliderListener(String name, ChangeListener listener) {
        if(!_sliders.containsKey(name)) {
            return;
        }
        JSlider slider = _sliders.get(name);
        slider.addChangeListener(listener);
    }

    private enum FieldType {
        TextArea,
        TextField,
        CheckBox
    }
}
