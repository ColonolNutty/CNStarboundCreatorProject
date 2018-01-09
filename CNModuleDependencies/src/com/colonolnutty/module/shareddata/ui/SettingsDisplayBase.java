package com.colonolnutty.module.shareddata.ui;

import com.colonolnutty.module.shareddata.models.settings.CNBaseSettings;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.Hashtable;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 5:49 PM
 */
public abstract class SettingsDisplayBase<T extends CNBaseSettings> {
    protected Hashtable<String, JTextComponent> _textFields;
    protected Hashtable<String, JCheckBox> _checkBoxes;
    protected Hashtable<String, JSlider> _sliders;
    protected int minTextEntryHeight = 30;

    public abstract JPanel setup(ActionListener onRun);

    protected SettingsDisplayBase() {
        _textFields = new Hashtable<String, JTextComponent>();
        _checkBoxes = new Hashtable<String, JCheckBox>();
        _sliders = new Hashtable<String, JSlider>();
    }

    public void enable() {
        Enumeration<JTextComponent> textFields = _textFields.elements();
        while(textFields.hasMoreElements()) {
            enable(textFields.nextElement());
        }

        Enumeration<JCheckBox> checkBoxes = _checkBoxes.elements();
        while(checkBoxes.hasMoreElements()) {
            enable(checkBoxes.nextElement());
        }

        Enumeration<JSlider> slider = _sliders.elements();
        while(slider.hasMoreElements()) {
            enable(slider.nextElement());
        }
    }

    public void disable() {
        Enumeration<JTextComponent> textFields = _textFields.elements();
        while(textFields.hasMoreElements()) {
            disable(textFields.nextElement());
        }

        Enumeration<JCheckBox> checkBoxes = _checkBoxes.elements();
        while(checkBoxes.hasMoreElements()) {
            disable(checkBoxes.nextElement());
        }

        Enumeration<JSlider> slider = _sliders.elements();
        while(slider.hasMoreElements()) {
            disable(slider.nextElement());
        }
    }

    protected JPanel addCurrentDirectoryField() {
        JLabel label = new JLabel("Current Working Directory: ");
        JTextField field = createTextField("currentDirectory");
        field.setEditable(false);
        field.setEnabled(false);
        field.setText(System.getProperty("user.dir"));
        return createPanel(true, minTextEntryHeight, label, field);
    }

    protected JPanel addSlider(String label,
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
        int height = 30;

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
                                        .addComponent(field, height, height, height)
                        )
                        .addComponent(slider)
        );

        panel.setVisible(true);
        return panel;
    }

    protected JSlider createSlider(String name, int min, int max, int minorTicks, int majorTicks, int init) {
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

    protected JPanel createField(FieldType fieldType,
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
                createdPanel = createPanel(true, null, checkBox, configEntryLabel);
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
                createdPanel = createPanel(true, minTextEntryHeight, configEntryLabel, scrollPane);
                break;
        }
        if(createdPanel == null) {
            return null;
        }
        return createdPanel;
    }

    protected JPanel addTextArea(String label, String name, String[] initValue) {
        JLabel configEntryLabel = new JLabel(label);
        JTextArea textArea = createTextArea(name);
        if(initValue != null) {
            textArea.setText(CNStringUtils.toCommaSeparated(initValue));
        }
        _textFields.put(name, textArea);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        panel.setLayout(layout);

        int height = minTextEntryHeight + 30;

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(configEntryLabel)
                        .addComponent(scrollPane)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(configEntryLabel)
                        .addComponent(scrollPane, height, height, height)
        );

        panel.setVisible(true);
        return panel;
    }

    private JTextArea createTextArea(String name) {
        JTextArea textArea = new JTextArea();
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        textArea.setName(name);
        textArea.setAutoscrolls(true);
        textArea.setRows(3);
        textArea.setLineWrap(true);
        CNUIExtensions.addInternalPadding(textArea, 5);
        return textArea;
    }

    protected JTextField createTextField(String name) {
        JTextField textField = new JTextField();
        textField.setName(name);
        CNUIExtensions.addInternalPadding(textField, 5);
        return textField;
    }

    protected JCheckBox createCheckBox(String name) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setName(name);
        return checkBox;
    }

    protected JPanel createPanel(boolean centerAlignLabel, Integer preferredHeight, JComponent... components) {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setLayout(layout);

        GroupLayout.SequentialGroup horzGroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup vertGroup = layout.createParallelGroup();
        if(centerAlignLabel) {
            vertGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        }
        for (Component component : components) {
            horzGroup = horzGroup.addComponent(component);
            if(preferredHeight != null) {
                vertGroup = vertGroup.addComponent(component, preferredHeight, preferredHeight, preferredHeight);
            }
            else {
                vertGroup = vertGroup.addComponent(component);
            }
        }
        layout.setHorizontalGroup(horzGroup);
        layout.setVerticalGroup(vertGroup);

        panel.setVisible(true);
        return panel;
    }

    protected Integer getCurrentValue(String name) {
        if(!_sliders.containsKey(name)) {
            return null;
        }
        JSlider comp = _sliders.get(name);
        return comp.getValue();
    }

    protected Boolean getIsSelected(String name) {
        if (!_checkBoxes.containsKey(name)) {
            return null;
        }
        JCheckBox comp = _checkBoxes.get(name);
        return comp.isSelected();
    }

    protected String getCurrentText(String name) {
        if(!_textFields.containsKey(name)) {
            return null;
        }
        JTextComponent comp = _textFields.get(name);
        return comp.getText();
    }

    protected void setupTextEntryFocusListener(String name, FocusListener listener) {
        if(!_textFields.containsKey(name)) {
            return;
        }
        JTextComponent text = _textFields.get(name);
        text.addFocusListener(listener);
    }

    protected void setupCheckBoxListener(String name, ChangeListener listener) {
        if(!_checkBoxes.containsKey(name)) {
            return;
        }
        JCheckBox checkBox = _checkBoxes.get(name);
        checkBox.addChangeListener(listener);
    }

    protected void setupSliderListener(String name, ChangeListener listener) {
        if(!_sliders.containsKey(name)) {
            return;
        }
        JSlider slider = _sliders.get(name);
        slider.addChangeListener(listener);
    }

    protected JButton createButton(String buttonText, ActionListener onRun) {
        final JButton runButton = new JButton(buttonText);
        runButton.setDefaultCapable(true);
        runButton.setEnabled(true);
        runButton.addActionListener(onRun);
        return runButton;
    }

    private void disable(JComponent comp) {
        comp.setEnabled(false);
    }

    private void enable(JComponent comp) {
        comp.setEnabled(true);
    }

    protected enum FieldType {
        TextField,
        CheckBox
    }
}
