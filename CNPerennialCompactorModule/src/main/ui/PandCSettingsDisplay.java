package main.ui;

import com.colonolnutty.module.shareddata.CNUtils;
import com.colonolnutty.module.shareddata.SettingsWriter;
import com.colonolnutty.module.shareddata.ui.SettingsDisplayBase;
import main.settings.PandCSettings;

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
public class PandCSettingsDisplay extends SettingsDisplayBase {
    private SettingsWriter _writer;
    private PandCSettings _settings;

    public PandCSettingsDisplay(SettingsWriter writer, PandCSettings settings) {
        _settings = settings;
        _writer = writer;
    }

    public JPanel setup(ActionListener onRun) {
        JPanel settingsDisplay = new JPanel();
        GroupLayout layout = new GroupLayout(settingsDisplay);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        settingsDisplay.setLayout(layout);
        JPanel currentDir = addCurrentDirectoryField();
        JButton runButton = createButton("Run", onRun);

        JPanel locationsOfCrops = addTextArea(
                "Relative path of crops to be made perennial or compact (Comma Separated): ",
                "locationsOfCrops",
                _settings.locationsOfCrops);
        JPanel creationPath = createField(FieldType.TextField,
                "What folder will patch files be created at? ",
                "creationPath",
                _settings.creationPath);
        JPanel propertyOrderFile = createField(FieldType.TextField,
                "Relative path to file listing the order of JSON properties: ",
                "propertyOrderFile",
                _settings.propertyOrderFile);
        JPanel logFile = createField(FieldType.TextField,
                "Relative Log File Path: ",
                "logFile",
                _settings.logFile);

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
        JPanel makePerennial = createField(FieldType.CheckBox,
                "Enable Perennial Patching",
                "makePerennial",
                _settings.makePerennial);
        JPanel makeCompact = createField(FieldType.CheckBox,
                "Enable Compact Patching",
                "makeCompact",
                _settings.makeCompact);
        JPanel makePatchFiles = createField(FieldType.CheckBox,
                "Make Patch Files",
                "makePatchFiles",
                _settings.makePatchFiles);

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
                                                        .addComponent(locationsOfCrops)
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
                                layout.createParallelGroup()
                                        //Bottom - Bottom
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(makePerennial)
                                                        .addComponent(makeCompact)
                                                        .addComponent(makePatchFiles)
                                        )
                                        //Bottom - Top
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(enableTreeView)
                                                        .addComponent(enableConsoleDebug)
                                                        .addComponent(enableVerboseLogging)
                                        )
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
                                            .addComponent(locationsOfCrops)
                                )
                                //Middle - Middle
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(propertyOrderFile)
                                                .addComponent(logFile)
                                )
                        )
                        //Bottom - Left
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(makePerennial)
                                        .addComponent(makeCompact)
                                        .addComponent(makePatchFiles)
                        )
                        //Bottom - Right
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(enableTreeView)
                                        .addComponent(enableConsoleDebug)
                                        .addComponent(enableVerboseLogging)
                        )
        );

        setupChangeListeners();
        return settingsDisplay;
    }

    private void setupChangeListeners() {
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
        setupTextEntryFocusListener("locationsOfCrops", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.locationsOfCrops = CNUtils.fromCommaSeparated(text.getText());
                writeSettings();
            }
        });
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
        setupCheckBoxListener("makePerennial", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.makePerennial = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("makeCompact", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.makeCompact = checkbox.isSelected();
                writeSettings();
            }
        });
        setupCheckBoxListener("makePatchFiles", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JCheckBox checkbox = (JCheckBox)e.getSource();
                _settings.makePatchFiles = checkbox.isSelected();
                writeSettings();
            }
        });
    }

    private void writeSettings() {
        _writer.write(_settings);
    }
}
