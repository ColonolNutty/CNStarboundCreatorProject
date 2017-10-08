package main.ui;

import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.ConfigReader;
import com.colonolnutty.module.shareddata.SettingsWriter;
import com.colonolnutty.module.shareddata.models.settings.BasicSettings;
import main.settings.BalancerCRData;
import main.ValueBalancer;
import main.settings.BalancerSettings;
import com.colonolnutty.module.shareddata.models.MessageBundle;
import com.colonolnutty.module.shareddata.ui.MainFunctionPanel;
import com.colonolnutty.module.shareddata.ui.OutputDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 4:56 PM
 */
public class MainPanel extends MainFunctionPanel {
    private CNLog _log;
    private BalancerSettings _settings;
    private OutputDisplay _outputDisplay;
    private BalancerSettingsDisplay _settingsDisplay;
    private ValueBalancer _valueBalancer;

    @Override
    public JPanel create() {
        JPanel mainPanel = new JPanel();
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);

        _outputDisplay = new OutputDisplay();
        JPanel outputDisplayPanel = _outputDisplay.get();
        if(_log != null) {
            _log.dispose();
        }
        CNLog tempLog = new CNLog(_outputDisplay, new BasicSettings());
        ConfigReader reader = new ConfigReader(tempLog);
        _settings = reader.readSettingsFile(new BalancerCRData(), BalancerSettings.class);
        tempLog.dispose();
        _log = new CNLog(_outputDisplay, _settings);
        mainPanel.setVisible(true);
        SettingsWriter writer = new SettingsWriter(_log);
        _settingsDisplay = new BalancerSettingsDisplay(writer, _settings);
        JPanel settingsPanel = _settingsDisplay.setup(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JButton source = (JButton) e.getSource();
                source.setEnabled(false);
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            _log.clear();
                            _log.setupDebugLogFile();
                            _outputDisplay.clear();
                            _settingsDisplay.disable();
                            _valueBalancer = new ValueBalancer(_settings, _log);
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

    @Override
    public String getName() {
        return "Balancer";
    }

    @Override
    public void dispose() {
        if(_log != null) {
            _log.dispose();
        }
    }
}
