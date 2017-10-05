package com.company.ui.recipecreator;

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
    public JPanel setup(RecipeCreatorSettings settings, ActionListener onRun) {
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
                settings.creationPath);
        JPanel ingredientListFile = createField(FieldType.TextField,
                "What is the name of the file that denotes ingredients to use?",
                "ingredientListFile",
                settings.ingredientListFile);
        JPanel recipeTemplateFile = createField(FieldType.TextField,
                "What file will be used as a template (Recipe)?",
                "recipeTemplateFile",
                settings.recipeTemplateFile);
        JPanel ingredientTemplateFile = createField(FieldType.TextField,
                "What file will be used as a template (Ingredient)?",
                "ingredientTemplateFile",
                settings.ingredientTemplateFile);
        JPanel ingredientImageTemplateFile = createField(FieldType.TextField,
                "What file will be used as a template (IngredientImage)?",
                "ingredientImageTemplateFile",
                settings.ingredientImageTemplateFile);
        JPanel recipeConfigFileName = createField(FieldType.TextField,
                "What file will the names of created recipes be written?",
                "recipeConfigFileName",
                settings.recipeConfigFileName);
        JPanel filePrefix = createField(FieldType.TextField,
                "What text will be put in front of created recipes/ingredients?",
                "filePrefix",
                settings.filePrefix);
        JPanel fileSuffix = createField(FieldType.TextField,
                "What text will be put at the end of created recipes/ingredients?",
                "fileSuffix",
                settings.fileSuffix);
        JPanel fileExtension = createField(FieldType.TextField,
                "What file extension will created recipes/ingredients be created with?",
                "fileExtension",
                settings.fileExtension);
        JPanel outputItemDescription = createField(FieldType.TextField,
                "What will be at the start of created ingredient descriptions?",
                "outputItemDescription",
                settings.outputItemDescription);
        JPanel outputItemShortDescription = createField(FieldType.TextField,
                "What will be at the start of created ingredient shortdescriptions?",
                "outputItemShortDescription",
                settings.outputItemShortDescription);

        JPanel countPerIngredient = addSlider(
                "The number of each ingredient added to a recipe?",
                "countPerIngredient",
                1,
                101,
                1,
                10,
                settings.countPerIngredient);

        JPanel numberOfIngredientsPerRecipe = addSlider(
                "The number of ingredients possible per recipe(i.e. value of 8 = 8, 7, 6, 5, etc.)?",
                "numberOfIngredientsPerRecipe",
                1,
                8,
                1,
                2,
                settings.numberOfIngredientsPerRecipe);

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
                                layout.createSequentialGroup()
                                    .addComponent(countPerIngredient)
                                    .addComponent(numberOfIngredientsPerRecipe)
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
                        //Middle - Right
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(countPerIngredient)
                                        .addComponent(numberOfIngredientsPerRecipe)
                        )
        );

        setupChangeListeners(settings);
        return settingsDisplay;
    }

    public void updateConfigSettings(RecipeCreatorSettings settings) {
        String creationPath = getCurrentText("creationPath");
        if(creationPath != null) {
            settings.creationPath = creationPath;
        }
        String ingredientListFile = getCurrentText("ingredientListFile");
        if(ingredientListFile != null) {
            settings.ingredientListFile = ingredientListFile;
        }
        String recipeTemplateFile = getCurrentText("recipeTemplateFile");
        if(recipeTemplateFile != null) {
            settings.recipeTemplateFile = recipeTemplateFile;
        }
        String ingredientTemplateFile = getCurrentText("ingredientTemplateFile");
        if(ingredientTemplateFile != null) {
            settings.ingredientTemplateFile = ingredientTemplateFile;
        }
        String ingredientImageTemplateFile = getCurrentText("ingredientImageTemplateFile");
        if(ingredientImageTemplateFile != null) {
            settings.ingredientImageTemplateFile = ingredientImageTemplateFile;
        }
        String recipeConfigFileName = getCurrentText("recipeConfigFileName");
        if(recipeConfigFileName != null) {
            settings.recipeConfigFileName = recipeConfigFileName;
        }
        String filePrefix = getCurrentText("filePrefix");
        if(filePrefix != null) {
            settings.filePrefix = filePrefix;
        }
        String fileSuffix = getCurrentText("fileSuffix");
        if(fileSuffix != null) {
            settings.fileSuffix = fileSuffix;
        }
        String fileExtension = getCurrentText("fileExtension");
        if(fileExtension != null) {
            settings.fileExtension = fileExtension;
        }
        String outputItemDescription = getCurrentText("outputItemDescription");
        if(outputItemDescription != null) {
            settings.outputItemDescription = outputItemDescription;
        }
        String outputItemShortDescription = getCurrentText("outputItemShortDescription");
        if(outputItemShortDescription != null) {
            settings.outputItemShortDescription = outputItemShortDescription;
        }

        Integer countPerIngredient = getCurrentValue("countPerIngredient");
        if(countPerIngredient != null) {
            settings.countPerIngredient = countPerIngredient;
        }
        Integer numberOfIngredientsPerRecipe = getCurrentValue("numberOfIngredientsPerRecipe");
        if(numberOfIngredientsPerRecipe != null) {
            settings.numberOfIngredientsPerRecipe = numberOfIngredientsPerRecipe;
        }
    }

    private void setupChangeListeners(final RecipeCreatorSettings settings) {
        setupTextEntryFocusListener("creationPath", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.creationPath = text.getText();
            }
        });
        setupTextEntryFocusListener("ingredientListFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.ingredientListFile = text.getText();
            }
        });
        setupTextEntryFocusListener("recipeTemplateFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.recipeTemplateFile = text.getText();
            }
        });
        setupTextEntryFocusListener("ingredientTemplateFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.ingredientTemplateFile = text.getText();
            }
        });
        setupTextEntryFocusListener("ingredientImageTemplateFile", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.ingredientImageTemplateFile = text.getText();
            }
        });
        setupTextEntryFocusListener("recipeConfigFileName", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.recipeConfigFileName = text.getText();
            }
        });
        setupTextEntryFocusListener("filePrefix", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.filePrefix = text.getText();
            }
        });
        setupTextEntryFocusListener("fileSuffix", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.fileSuffix = text.getText();
            }
        });
        setupTextEntryFocusListener("fileExtension", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.fileExtension = text.getText();
            }
        });
        setupTextEntryFocusListener("outputItemDescription", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.outputItemDescription = text.getText();
            }
        });
        setupTextEntryFocusListener("outputItemShortDescription", new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) { }

            @Override
            public void focusLost(FocusEvent e) {
                JTextComponent text = (JTextComponent)e.getSource();
                settings.outputItemShortDescription = text.getText();
            }
        });

        setupSliderListener("countPerIngredient", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                settings.countPerIngredient = slider.getValue();
            }
        });

        setupSliderListener("numberOfIngredientsPerRecipe", new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider = (JSlider)e.getSource();
                settings.numberOfIngredientsPerRecipe = slider.getValue();
            }
        });
    }
}
