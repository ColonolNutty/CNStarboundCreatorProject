package com.company.ui;

import com.company.CNUtils;
import com.company.models.ConfigSettings;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;

public class ConfigSettingsDisplay {
    private Hashtable<String, JTextComponent> _textFields;
    private Hashtable<String, JCheckBox> _checkBoxes;
    private Hashtable<String, JSlider> _sliders;

    public ConfigSettingsDisplay() {
        _textFields = new Hashtable<String, JTextComponent>();
        _checkBoxes = new Hashtable<String, JCheckBox>();
        _sliders = new Hashtable<String, JSlider>();
    }

    public JPanel setup(ConfigSettings settings, ActionListener onRun) {
        JPanel settingsDisplay = new JPanel();
        GroupLayout layout = new GroupLayout(settingsDisplay);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        settingsDisplay.setLayout(layout);
        JPanel currentDir = addCurrentDirectoryField();

        //TextAreas
        JPanel includeLocsPanel = addTextArea(
                "Relative Locations To Include In Searches (Comma Separated): ",
                "includeLocations",
                settings.includeLocations);
        JPanel excludeEffectsPanel = addTextArea(
                "Exclude Effects With Name (Comma Separated): ",
                "excludedEffects",
                settings.excludedEffects);
        JPanel locsToUpdate = addTextArea(
                "Relative Locations To Update (Comma Separated): ",
                "locationsToUpdate",
                settings.locationsToUpdate);

        //TextFields
        JPanel ingredientOverridePath = addField(FieldType.TextField,
                "Relative Path To Ingredient Overrides: ",
                "ingredientOverridePath",
                settings.ingredientOverridePath);
        JPanel logFile = addField(FieldType.TextField,
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
        JPanel enableConsoleDebug = addField(FieldType.CheckBox,
                "Enable Console Debug",
                "enableConsoleDebug",
                settings.enableConsoleDebug);
        JPanel enableVerboseLogging = addField(FieldType.CheckBox,
                "Enable Verbose Logging",
                "enableVerboseLogging",
                settings.enableVerboseLogging);
        JPanel enableEffectsUpdate = addField(FieldType.CheckBox,
                "Update Effects",
                "enableEffectsUpdate",
                settings.enableEffectsUpdate);
        JButton runButton = setupRunButton(onRun);

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
                                                                        .addComponent(includeLocsPanel)
                                                                        .addComponent(excludeEffectsPanel)
                                                                        .addComponent(locsToUpdate)
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
                                                            .addComponent(includeLocsPanel)
                                                            .addComponent(excludeEffectsPanel)
                                                            .addComponent(locsToUpdate)
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
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                                        .addComponent(enableEffectsUpdate)
                        )
        );

        setupChangeListeners(settings);
        return settingsDisplay;
    }

    private JPanel addCurrentDirectoryField() {
        JLabel label = new JLabel("Current Working Directory: ");
        JTextField field = createTextField("currentDirectory");
        field.setEditable(false);
        field.setEnabled(false);
        field.setText(System.getProperty("user.dir"));
        return createPanel(true, label, field);
    }

    private JPanel addSlider(String label,
                           String name,
                           int min,
                           int max,
                           int minorTicks,
                           int majorTicks,
                           int initial) {
        JLabel configEntryLabel = new JLabel(label);
        JSlider slider = createSlider(name, min, max, minorTicks, majorTicks, initial);
        JPanel fieldPanel = new JPanel();
        final JTextArea field = createTextArea(name + "textField");
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
        field.setEnabled(false);
        field.setRows(1);

        _sliders.put(name, slider);
        fieldPanel.setBackground(Color.BLUE);
        fieldPanel.add(field);


        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        panel.setLayout(layout);
        int maxTextSize = (max + "").length();
        int width = 10 + (maxTextSize * 10);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(configEntryLabel)
                                        .addComponent(field, width, width, width)
                        )
                        .addComponent(slider)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                        .addComponent(configEntryLabel)
                                        .addComponent(field, 30, 30, 30)
                        )
                        .addComponent(slider)
        );

        panel.setVisible(true);
        return panel;
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

    private JPanel addField(FieldType fieldType,
                          String label,
                          String name,
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
            case TextField:
                JTextField textField = createTextField(name);
                if(initValue != null) {
                    textField.setText(initValue.toString());
                }
                _textFields.put(name, textField);
                JScrollPane scrollPane = new JScrollPane(textField);
                scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
                scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
                createdPanel = createPanel(true, configEntryLabel, scrollPane);
                break;
        }
        if(createdPanel == null) {
            return null;
        }
        return createdPanel;
    }

    private JPanel addTextArea(String label, String name, String[] initValue) {
        JLabel configEntryLabel = new JLabel(label);
        JTextArea textArea = createTextArea(name);
        if(initValue != null) {
            textArea.setText(CNUtils.toCommaSeparated(initValue));
        }
        _textFields.put(name, textArea);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        panel.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(configEntryLabel)
                        .addComponent(scrollPane)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(configEntryLabel)
                        .addComponent(scrollPane)
        );

        panel.setVisible(true);
        return panel;
    }

    private JTextArea createTextArea(String name) {
        JTextArea textArea = new JTextArea();
        textArea.setName(name);
        textArea.setAutoscrolls(true);
        textArea.setRows(3);
        textArea.setLineWrap(true);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        textArea.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textArea;
    }

    private JTextField createTextField(String name) {
        JTextField textField = new JTextField();
        textField.setName(name);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        textField.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
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

    private JButton setupRunButton(ActionListener onRun) {
        final JButton runButton = new JButton("Run");
        runButton.setDefaultCapable(true);
        runButton.setEnabled(true);
        runButton.addActionListener(onRun);
        return runButton;
    }

    private enum FieldType {
        TextArea,
        TextField,
        CheckBox
    }
}
