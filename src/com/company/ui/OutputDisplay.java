package com.company.ui;

import com.company.DebugWriter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * User: Jack's Computer
 * Date: 09/27/2017
 * Time: 10:25 AM
 */
public class OutputDisplay extends DebugWriter {
    private JPanel _displayPanel;
    private JTextArea _outputDisplay;

    public OutputDisplay() {
        setup();
    }

    public JPanel get(int maxWidth, int maxHeight) {
        setup();
        _displayPanel.setMaximumSize(new Dimension(maxWidth, maxHeight));
        return _displayPanel;
    }

    private void setup() {
        if(_displayPanel != null) {
            return;
        }
        _displayPanel = new JPanel();
        GroupLayout layout = new GroupLayout(_displayPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        _displayPanel.setLayout(layout);

        JLabel label = new JLabel("Console Output: ");

        _outputDisplay = new JTextArea();
        _outputDisplay.setEditable(false);
        _outputDisplay.setVisible(true);
        _outputDisplay.setName(ComponentNames.CONSOLEDISPLAY);
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        _outputDisplay.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        JScrollPane scrollPanel = new JScrollPane(_outputDisplay);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addComponent(label)
                .addComponent(scrollPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(label)
                .addComponent(scrollPanel)
        );
        _displayPanel.add(scrollPanel);
        _displayPanel.setVisible(true);
    }

    public void clear() {
        _outputDisplay.setText("");
    }

    @Override
    public void write(String text) {
        _outputDisplay.append(text);
    }

    @Override
    public void writeln(String text) {
        if(_outputDisplay.getText().isEmpty()) {
            _outputDisplay.append(text);
            return;
        }
        _outputDisplay.append("\n" + text);
    }
}
