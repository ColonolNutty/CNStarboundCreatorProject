package com.company.Updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:28 AM
 */
public class PatchFileUpdater extends Updater {
    public PatchFileUpdater(DebugLog log, JsonManipulator manipulator) {
        super(log, manipulator);
    }

    @Override
    public void update(String filePath) {

    }

    @Override
    public boolean canUpdate(String filePath) {
        return false;
    }
}
