package com.colonolnutty.module.shareddata.ui;

import javax.swing.*;

/**
 * User: Jack's Computer
 * Date: 11/19/2017
 * Time: 1:18 PM
 */
public class ProgressDisplay extends ProgressController {
    private JPanel _progressPanel;
    private JProgressBar _progressBar;
    private JLabel _progressLabel;

    public JPanel get() {
        setup();
        return _progressPanel;
    }

    private void setup() {
        if(_progressPanel != null) {
            return;
        }

        _progressBar = new JProgressBar();
        _progressLabel = new JLabel("0/0 completed");

        _progressPanel = new JPanel();
        GroupLayout layout = new GroupLayout(_progressPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addComponent(_progressBar)
                    .addComponent(_progressLabel)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup()
                    .addComponent(_progressBar)
                    .addComponent(_progressLabel)
        );
        _progressPanel.setVisible(true);
    }

    @Override
    public void setMaximum(int total) {
        _progressBar.setMaximum(total);
        _progressBar.setMinimum(0);
        _progressBar.setValue(0);
        updateLabel();
    }

    @Override
    public void add(int amount) {
        int newValue = _progressBar.getValue() + amount;
        if(newValue > _progressBar.getMaximum()) {
            setMaximum(newValue);
        }
        _progressBar.setValue(newValue);
        updateLabel();
    }

    @Override
    public void reset() {
        _progressBar.setMaximum(0);
        _progressBar.setMinimum(0);
        _progressBar.setValue(0);
        updateLabel();
    }

    private void updateLabel() {
        String newText = _progressBar.getValue() + "/" + _progressBar.getMaximum() + " completed";
        _progressLabel.setText(newText);
        _progressPanel.updateUI();
    }
}
