package com.colonolnutty.module.shareddata.ui;

import com.colonolnutty.module.shareddata.MainFunctionModule;
import com.colonolnutty.module.shareddata.debug.CNLog;
import com.colonolnutty.module.shareddata.io.ConfigReader;
import com.colonolnutty.module.shareddata.io.ConfigWriter;
import com.colonolnutty.module.shareddata.models.MessageBundle;
import com.colonolnutty.module.shareddata.models.settings.CNBaseSettings;
import com.colonolnutty.module.shareddata.models.settings.ICRData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 5:01 PM
 */
public class DefaultMainPanel<T extends CNBaseSettings, Y extends SettingsDisplayBase, X extends ICRData<T>, Z extends MainFunctionModule> implements IMainFunctionPanel {
    private CNLog _log;
    private OutputDisplay _outputDisplay;
    private ProgressDisplay _progressDisplay;
    private String _name;
    private Class<T> _settingsClass;
    private Class<Y> _settingsDisplayClass;
    private Class<Z> _mainRunClass;
    private Class<X> _crDataClass;
    private T _settings;
    private Y _settingsDisplay;

    public DefaultMainPanel(String name,
                            Class<T> settingsClass,
                            Class<Y> settingsDisplayClass,
                            Class<Z> mainRunClass,
                            Class<X> crData) {
        _name = name;
        _settingsClass = settingsClass;
        _settingsDisplayClass = settingsDisplayClass;
        _crDataClass = crData;
        _mainRunClass = mainRunClass;
    }

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
        try {
            ConfigReader<T> reader = new ConfigReader<T>(tempLog);
            ICRData<T> crData = _crDataClass.newInstance();
            _settings = reader.readSettingsFile(crData, _settingsClass);
            tempLog.dispose();
            _log = new CNLog(_outputDisplay, _settings);
            ConfigWriter writer = new ConfigWriter(_log);
            _settingsDisplay = _settingsDisplayClass.getConstructor(ConfigWriter.class, _settingsClass).newInstance(writer, _settings);
        }
        catch(Exception e) {
            if(_log != null) {
                _log.error(e);
                _log.dispose();
            }
            if(tempLog != null) {
                tempLog.dispose();
            }
            return mainPanel;
        }
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
                            _log.updateSettings(_settings);
                            _outputDisplay.clear();
                            _settingsDisplay.disable();
                            _progressDisplay.reset();
                            Z mainRun = _mainRunClass.getConstructor(_settingsClass, CNLog.class, ProgressController.class).newInstance(_settings, _log, _progressDisplay);
                            mainRun.run();
                            Hashtable<String, MessageBundle> messages;
                            if(_settings.enableTreeView) {
                                messages = _log.getMessages();
                            }
                            else {
                                messages = new Hashtable<>();
                            }
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
        mainPanel.setVisible(true);
        return mainPanel;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void dispose() {
        if(_log != null) {
            _log.dispose();
        }
    }
}
