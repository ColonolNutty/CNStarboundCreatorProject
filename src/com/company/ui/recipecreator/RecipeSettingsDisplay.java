package com.company.ui.recipecreator;

import com.company.models.ConfigSettings;
import com.company.models.RecipeCreatorSettings;
import com.company.ui.CNUIExtensions;
import com.company.ui.balancer.ConfigSettingsDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 5:04 PM
 */
public class RecipeSettingsDisplay {
    public JPanel setup(RecipeCreatorSettings settings, ActionListener onRun) {
        JPanel settingsDisplay = new JPanel();
        GroupLayout layout = new GroupLayout(settingsDisplay);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        settingsDisplay.setLayout(layout);
        JPanel currentDir = addCurrentDirectoryField();
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
                                                        )
                                                        .addGroup(
                                                                layout.createParallelGroup()
                                                        )
                                        )
                                        //Middle - Middle
                                        .addGroup(
                                                layout.createParallelGroup()
                                        )
                        )
                        //Bottom
                        .addGroup(layout.createSequentialGroup())
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
                                                )
                                                .addGroup(
                                                        layout.createSequentialGroup()
                                                )
                                )
                                //Middle - Middle
                                .addGroup(
                                        layout.createSequentialGroup()
                                )
                        )
                        //Middle - Right
                        .addGroup(
                                layout.createParallelGroup()
                        )
        );

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

    private JTextField createTextField(String name) {
        JTextField textField = new JTextField();
        textField.setName(name);
        CNUIExtensions.addInternalPadding(textField, 5);
        return textField;
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


    private JButton setupRunButton(ActionListener onRun) {
        final JButton runButton = new JButton("Run");
        runButton.setDefaultCapable(true);
        runButton.setEnabled(true);
        runButton.addActionListener(onRun);
        return runButton;
    }
}
