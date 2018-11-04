package main.cnrecipeconfigcreator.ui;

import com.colonolnutty.module.shareddata.io.ConfigWriter;
import com.colonolnutty.module.shareddata.ui.SettingsDisplayBase;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import main.settings.RecipeConfigCreatorSettings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 5:04 PM
 */
public class RecipeConfigCreatorSettingsDisplay extends SettingsDisplayBase {
    private ConfigWriter _writer;
    private RecipeConfigCreatorSettings _settings;

    public RecipeConfigCreatorSettingsDisplay(ConfigWriter writer, RecipeConfigCreatorSettings settings) {
        _settings = settings;
        _writer = writer;
    }

    @Override
    public JPanel setup(ActionListener onRun) {
        JPanel settingsDisplay = new JPanel();
        GroupLayout layout = new GroupLayout(settingsDisplay);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        settingsDisplay.setLayout(layout);
        JPanel currentDir = addCurrentDirectoryField();
        JButton runButton = createButton("Run", onRun);

        JPanel creationPath = createField(FieldType.TextField,
                "What folder will the files be created at? ",
                "creationPath",
                _settings.creationPath);
        JPanel recipePaths = addTextArea(
                "Relative Location of Recipes (Comma Separated): ",
                "recipePaths",
                _settings.recipePaths);
        JPanel includeRecipeGroupsPanel = addTextArea(
                "Recipe Groups to Include in file creation (Comma Separated): ",
                "includeRecipeGroups",
                _settings.includeRecipeGroups);

        JPanel propertyOrderFile = createField(FieldType.TextField,
                "Relative path to file listing the order of JSON properties: ",
                "propertyOrderFile",
                _settings.propertyOrderFile);
        JPanel logFile = createField(FieldType.TextField,
                "Relative Log File Path: ",
                "logFile",
                _settings.logFile);

        JPanel configAsPatchFile = createField(FieldType.CheckBox,
                "Write recipe config as Patch?",
                "configAsPatchFile",
                _settings.configAsPatchFile);
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
                                                        .addComponent(creationPath)
                                                        .addComponent(recipePaths)
                                                        .addComponent(includeRecipeGroupsPanel)
                                        )
                                        //Middle - Middle
                                        .addGroup(
                                                layout.createParallelGroup()
                                                        .addComponent(propertyOrderFile)
                                                        .addComponent(logFile)
                                        )
                        )
                        //Bottom
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(configAsPatchFile)
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
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
                                                .addComponent(creationPath)
                                                .addComponent(recipePaths)
                                                .addComponent(includeRecipeGroupsPanel)
                                )
                                //Middle - Middle
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(propertyOrderFile)
                                                .addComponent(logFile)
                                )
                        )
                        //Bottom - Right
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(configAsPatchFile)
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                        )
        );

        setupChangeListeners();
        return settingsDisplay;
    }

    private void setupChangeListeners() {
        setupTextEntryFocusListener("creationPath", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.creationPath = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("recipePaths", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.recipePaths = CNStringUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
        setupTextEntryFocusListener("includeRecipeGroups", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.includeRecipeGroups = CNStringUtils.fromCommaSeparated(text.getText());
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

        setupCheckBoxListener("configAsPatchFile", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.configAsPatchFile = checkbox.isSelected();
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
    }

    private void writeSettings() {
        _writer.write(_settings);
    }
}
