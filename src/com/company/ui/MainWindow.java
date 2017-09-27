package com.company.ui;

import com.company.DebugLog;
import com.company.ValueBalancer;
import com.company.models.ConfigSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Jack's Computer
 * Date: 09/27/2017
 * Time: 10:18 AM
 */
public class MainWindow {
    private JFrame _mainFrame;
    private ConfigSettings _configSettings;
    private ValueBalancer _valueBalancer;
    private DebugLog _log;

    public MainWindow(ConfigSettings settings) {
        _configSettings = settings;
        setup();
    }

    public void start() {
        setup();
        _valueBalancer = new ValueBalancer(_configSettings, _log);
        _mainFrame.setVisible(true);
    }

    private void setup() {
        if(_mainFrame != null) {
            return;
        }
        _mainFrame = new JFrame("Ingredient Balancer");
        _mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                _log.dispose();
                _mainFrame = null;
            }
        });
        _mainFrame.setBounds(0, 0, 1080, 800);
        _mainFrame.setLayout(new BorderLayout());
        _mainFrame.getContentPane().setBackground(Color.BLUE);
        JPanel mainPanel = setupMainPanel(_mainFrame.getSize());
        _mainFrame.add(mainPanel);
    }

    private JPanel setupMainPanel(Dimension size) {
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(size);
        mainPanel.setLayout(new GridLayout(2, 1));
        ConfigSettingsDisplay settings = new ConfigSettingsDisplay();
        JPanel settingsPanel = settings.setup(_configSettings);
        settingsPanel.add(setupRunButton());
        mainPanel.add(settingsPanel);
        OutputDisplay outputDisplay = new OutputDisplay();
        mainPanel.add(outputDisplay.get(_mainFrame.getWidth(), _mainFrame.getHeight()/2));
        if(_log != null) {
            _log.dispose();
        }
        _log = new DebugLog(outputDisplay, _configSettings);
        mainPanel.setVisible(true);
        return mainPanel;
    }

    private JButton setupRunButton() {
        final JButton runButton = new JButton("Run");
        runButton.setDefaultCapable(true);
        runButton.setEnabled(true);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runButton.setEnabled(false);
                try {
                    _valueBalancer.run();
                }
                catch(Exception e1) {
                    e1.printStackTrace();
                }
                finally {
                    runButton.setEnabled(true);
                }
            }
        });
        return runButton;
    }
}
