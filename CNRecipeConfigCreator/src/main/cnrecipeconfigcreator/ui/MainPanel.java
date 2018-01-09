package main.cnrecipeconfigcreator.ui;

import com.colonolnutty.module.shareddata.ui.DefaultMainPanel;
import com.colonolnutty.module.shareddata.ui.IMainFunctionPanel;
import main.RecipeConfigCreatorMain;
import main.settings.RecipeConfigCreatorCRData;
import main.settings.RecipeConfigCreatorSettings;

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
                    RecipeConfigCreatorSettings.class,
                    RecipeConfigCreatorSettingsDisplay.class,
                    RecipeConfigCreatorMain.class,
                    RecipeConfigCreatorCRData.class);
        }
        return _mainPanel.create();
    }

    @Override
    public String getName() {
        return "Recipe Config Creator";
    }

    @Override
    public void dispose() {
        if(_mainPanel != null) {
            _mainPanel.dispose();
        }
    }
}
