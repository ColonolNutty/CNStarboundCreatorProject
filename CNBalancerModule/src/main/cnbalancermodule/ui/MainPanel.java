package main.cnbalancermodule.ui;

import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.ConfigReader;
import com.colonolnutty.module.shareddata.io.ConfigWriter;
import com.colonolnutty.module.shareddata.models.settings.BasicSettings;
import com.colonolnutty.module.shareddata.ui.ProgressDisplay;
import main.settings.BalancerCRData;
import main.BalancerMain;
import main.settings.BalancerSettings;
import com.colonolnutty.module.shareddata.models.MessageBundle;
import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;
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
public class MainPanel implements IMainFunctionPanel {
    private CNLog _log;
    private BalancerSettings _settings;
    private OutputDisplay _outputDisplay;
    private ProgressDisplay _progressDisplay;
    private BalancerSettingsDisplay _settingsDisplay;

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
        CNLog tempLog = new CNLog(_outputDisplay);
        BasicSettings tempSettings = new BasicSettings();
        tempLog.setLogFile(tempSettings.logFile);
        tempLog.setVerboseLogging(tempSettings.enableVerboseLogging);
        tempLog.setConsoleDebug(tempSettings.enableConsoleDebug);
        tempLog.setupDebugLogFile();
        ConfigReader<BalancerSettings> reader = new ConfigReader<BalancerSettings>(tempLog);
        _settings = reader.readSettingsFile(new BalancerCRData(), BalancerSettings.class);
        tempLog.dispose();
        _log = new CNLog(_outputDisplay);
        _log.setLogFile(_settings.logFile);
        _log.setVerboseLogging(_settings.enableVerboseLogging);
        _log.setConsoleDebug(_settings.enableConsoleDebug);
        _log.setupDebugLogFile();
        mainPanel.setVisible(true);
        ConfigWriter writer = new ConfigWriter(_log);
        _settingsDisplay = new BalancerSettingsDisplay(writer, _settings);
        _progressDisplay = new ProgressDisplay();
        JPanel progressDisplayPanel = _progressDisplay.get();
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
                            _progressDisplay.reset();
                            BalancerMain balancer = new BalancerMain(_settings,
                                    _log, _progressDisplay);
                            balancer.run();
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
                        .addComponent(progressDisplayPanel)
                        .addComponent(outputDisplayPanel)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(settingsPanel)
                        .addComponent(progressDisplayPanel)
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
