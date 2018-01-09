package main.cnperennialcompactormodule.ui;

import com.colonolnutty.module.shareddata.ui.DefaultMainPanel;
import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;
import main.settings.PandCSettings;
import main.settings.PandCCRData;
import main.PerennialCompactorMain;

import javax.swing.*;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 5:01 PM
 */
public class MainPanel implements IMainFunctionPanel {
    private DefaultMainPanel _mainPanel;

    @Override
    public JPanel create() {
        if(_mainPanel == null) {
            _mainPanel = new DefaultMainPanel(getName(),
                    PandCSettings.class,
                    PandCSettingsDisplay.class,
                    PerennialCompactorMain.class,
                    PandCCRData.class);
        }
        return _mainPanel.create();
    }

    @Override
    public String getName() {
        return "Perennial Compactor";
    }

    @Override
    public void dispose() {
        if(_mainPanel != null) {
            _mainPanel.dispose();
        }
    }
}
