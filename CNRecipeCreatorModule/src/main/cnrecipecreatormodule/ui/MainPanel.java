package main.cnrecipecreatormodule.ui;

import com.colonolnutty.module.shareddata.ui.DefaultMainPanel;
import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;
import main.settings.RecipeCreatorSettings;
import main.RecipeCreatorMain;
import main.settings.RecipeCreatorCRData;

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
                    RecipeCreatorSettings.class,
                    RecipeCreatorSettingsDisplay.class,
                    RecipeCreatorMain.class,
                    RecipeCreatorCRData.class);
        }
        return _mainPanel.create();
    }

    @Override
    public String getName() {
        return "Recipe Creator";
    }

    @Override
    public void dispose() {
        if(_mainPanel != null) {
            _mainPanel.dispose();
        }
    }
}
