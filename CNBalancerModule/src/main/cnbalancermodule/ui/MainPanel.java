package main.cnbalancermodule.ui;

import com.colonolnutty.module.shareddata.ui.DefaultMainPanel;
import main.settings.BalancerCRData;
import main.BalancerMain;
import main.settings.BalancerSettings;
import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;

import javax.swing.*;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 4:56 PM
 */
public class MainPanel implements IMainFunctionPanel {
    private DefaultMainPanel _mainPanel;

    @Override
    public JPanel create() {
        if(_mainPanel == null) {
        _mainPanel = new DefaultMainPanel(getName(),
                BalancerSettings.class,
                BalancerSettingsDisplay.class,
                BalancerMain.class,
                BalancerCRData.class);
        }
        return _mainPanel.create();
    }

    @Override
    public String getName() {
        return "Balancer";
    }

    @Override
    public void dispose() {
        if(_mainPanel != null) {
            _mainPanel.dispose();
        }
    }
}
