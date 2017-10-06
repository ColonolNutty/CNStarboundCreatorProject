package com.company.ui.recipecreator;

import com.company.SettingsWriter;
import com.company.models.RecipeCreatorSettings;
import com.company.ui.SettingsDisplayBase;

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
public class RecipeSettingsDisplay extends SettingsDisplayBase {
    private SettingsWriter _writer;
    private RecipeCreatorSettings _settings;

    public RecipeSettingsDisplay(SettingsWriter writer, RecipeCreatorSettings settings) {
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

        JPanel creationPath = createField(FieldType.TextField,
                "What folder will the files be created at? ",
                "creationPath",
                _settings.creationPath);
        JPanel ingredientListFile = createField(FieldType.TextField,
                "What is the name of the file that denotes ingredients to use?",
                "ingredientListFile",
                _settings.ingredientListFile);
        JPanel recipeTemplateFile = createField(FieldType.TextField,
                "What file will be used as a template (Recipe)?",
                "recipeTemplateFile",
                _settings.recipeTemplateFile);
        JPanel ingredientTemplateFile = createField(FieldType.TextField,
                "What file will be used as a template (Ingredient)?",
                "ingredientTemplateFile",
                _settings.ingredientTemplateFile);
        JPanel ingredientImageTemplateFile = createField(FieldType.TextField,
                "What file will be used as a template (IngredientImage)?",
                "ingredientImageTemplateFile",
                _settings.ingredientImageTemplateFile);
        JPanel recipeConfigFileName = createField(FieldType.TextField,
                "What file will the names of created recipes be written?",
                "recipeConfigFileName",
                _settings.recipeConfigFileName);
        JPanel filePrefix = createField(FieldType.TextField,
                "What text will be put in front of created recipes/ingredients?",
                "filePrefix",
                _settings.filePrefix);
        JPanel fileSuffix = createField(FieldType.TextField,
                "What text will be put at the end of created recipes/ingredients?",
                "fileSuffix",
                _settings.fileSuffix);
        JPanel fileExtension = createField(FieldType.TextField,
                "What file extension will created recipes/ingredients be created with?",
                "fileExtension",
                _settings.fileExtension);
        JPanel outputItemDescription = createField(FieldType.TextField,
                "What will be at the start of created ingredient descriptions?",
                "outputItemDescription",
                _settings.outputItemDescription);
        JPanel outputItemShortDescription = createField(FieldType.TextField,
                "What will be at the start of created ingredient shortdescriptions?",
                "outputItemShortDescription",
                _settings.outputItemShortDescription);
        JPanel logFile = createField(FieldType.TextField,
                "Relative Log File Path: ",
                "logFile",
                _settings.logFile);

        JPanel countPerIngredient = addSlider(
                "The number of each ingredient added to a recipe?",
                "countPerIngredient",
                1,
                101,
                1,
                10,
                _settings.countPerIngredient);

        JPanel numberOfIngredientsPerRecipe = addSlider(
                "The number of ingredients possible per recipe(i.e. value of 8 = 8, 7, 6, 5, etc.)?",
                "numberOfIngredientsPerRecipe",
                1,
                8,
                1,
                2,
                _settings.numberOfIngredientsPerRecipe);

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
                                                        .addGroup(
                                                                layout.createParallelGroup()
                                                                        .addComponent(creationPath)
                                                                        .addComponent(ingredientListFile)
                                                                        .addComponent(recipeTemplateFile)
                                                        )
                                                        .addGroup(
                                                                layout.createParallelGroup()
                                                                        .addComponent(ingredientTemplateFile)
                                                                        .addComponent(ingredientImageTemplateFile)
                                                                        .addComponent(recipeConfigFileName)
                                                        )
                                        )
                                        //Middle - Middle
                                        .addGroup(
                                                layout.createParallelGroup()
                                                        .addComponent(filePrefix)
                                                        .addComponent(fileSuffix)
                                                        .addComponent(fileExtension)
                                                        .addComponent(outputItemDescription)
                                                        .addComponent(outputItemShortDescription)
                                        )
                        )
                        //Bottom
                        .addGroup(
                                layout.createParallelGroup()
                                        //Bottom - Bottom
                                        .addGroup(
                                                layout.createSequentialGroup()
                                                        .addComponent(countPerIngredient)
                                                        .addComponent(numberOfIngredientsPerRecipe)
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
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(creationPath)
                                                                .addComponent(ingredientListFile)
                                                                .addComponent(recipeTemplateFile)
                                                )
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                                .addComponent(ingredientTemplateFile)
                                                                .addComponent(ingredientImageTemplateFile)
                                                                .addComponent(recipeConfigFileName)
                                                )
                                )
                                //Middle - Middle
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(filePrefix)
                                                .addComponent(fileSuffix)
                                                .addComponent(fileExtension)
                                                .addComponent(outputItemDescription)
                                                .addComponent(outputItemShortDescription)
                                )
                        )
                        //Bottom - Left
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(countPerIngredient)
                                        .addComponent(numberOfIngredientsPerRecipe)
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
        setupTextEntryFocusListener("ingredientListFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.ingredientListFile = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("recipeTemplateFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.recipeTemplateFile = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("ingredientTemplateFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.ingredientTemplateFile = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("ingredientImageTemplateFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.ingredientImageTemplateFile = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("recipeConfigFileName", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.recipeConfigFileName = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("filePrefix", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.filePrefix = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("fileSuffix", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.fileSuffix = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("fileExtension", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.fileExtension = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("outputItemDescription", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.outputItemDescription = text.getText();
                writeSettings();
            }
        });
        setupTextEntryFocusListener("outputItemShortDescription", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                _settings.outputItemShortDescription = text.getText();
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

        setupSliderListener("countPerIngredient", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                _settings.countPerIngredient = slider.getValue();
                writeSettings();
            }
        });

        setupSliderListener("numberOfIngredientsPerRecipe", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                _settings.numberOfIngredientsPerRecipe = slider.getValue();
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
