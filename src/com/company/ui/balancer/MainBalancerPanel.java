package com.company.ui.balancer;

import com.company.CNLog;
import com.company.SettingsWriter;
import com.company.balancer.ValueBalancer;
import com.company.models.ConfigSettings;
import com.company.models.MessageBundle;
import com.company.ui.OutputDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 4:56 PM
 */
public class MainBalancerPanel {
    private CNLog _log;
    private ConfigSettings _configSettings;
    private OutputDisplay _outputDisplay;
    private ConfigSettingsDisplay _settingsDisplay;
    private SettingsWriter _settingsWriter;
    private ValueBalancer _valueBalancer;

    public MainBalancerPanel(ConfigSettings settings,
                             SettingsWriter settingsWriter) {
        _configSettings = settings;
        _settingsWriter = settingsWriter;
    }

    public JPanel create(Dimension size) {
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(size);
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        _settingsDisplay = new ConfigSettingsDisplay();

        _outputDisplay = new OutputDisplay();
        JPanel outputDisplayPanel = _outputDisplay.get();
        if(_log != null) {
            _log.dispose();
        }
        _log = new CNLog(_outputDisplay, _configSettings);
        mainPanel.setVisible(true);
        JPanel settingsPanel = _settingsDisplay.setup(_configSettings, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JButton source = (JButton) e.getSource();
                source.setEnabled(false);
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            _log.clear();
                            _outputDisplay.clear();
                            _settingsDisplay.disable();
                            _settingsDisplay.updateConfigSettings(_configSettings);
                            _settingsWriter.write(_configSettings);
                            _valueBalancer = new ValueBalancer(_configSettings, _log);
                            _valueBalancer.run();
                            Hashtable<String, MessageBundle> messages = _log.getMessages();
                            _outputDisplay.updateTreeDisplay(messages);
                        }
                        catch(Exception e1) {
                            e1.printStackTrace();
                        }
                        finally {
                            source.setEnabled(true);
                            _settingsDisplay.enable();
                        }
                    }
                };
                thread.start();
            }
        });
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(settingsPanel)
                        .addComponent(outputDisplayPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(settingsPanel)
                        .addComponent(outputDisplayPanel)
        );
        return mainPanel;
    }

    public void dispose() {
        if(_log != null) {
            _log.dispose();
        }
    }
}
