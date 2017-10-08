package main;

import com.colonolnutty.module.shareddata.CNLog;
import com.colonolnutty.module.shareddata.MainFunctionModule;
import main.settings.PandCSettings;

/**
 * User: Jack's Computer
 * Date: 10/08/2017
 * Time: 10:11 AM
 */
public class PerennialCompactorMain extends MainFunctionModule {
    private PandCSettings _settings;
    private CNLog _log;

    public PerennialCompactorMain(PandCSettings settings, CNLog log) {
        _settings = settings;
        _log = log;
    }

    @Override
    public void run() {

    }
}
