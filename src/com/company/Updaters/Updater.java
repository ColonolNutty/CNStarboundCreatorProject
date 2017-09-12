package com.company.Updaters;

import com.company.DebugLog;
import com.company.JsonManipulator;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:25 AM
 */
public abstract class Updater {
    public abstract void update(String filePath);
    public abstract boolean canUpdate(String filePath);
    protected DebugLog _log;
    protected JsonManipulator _manipulator;

    public Updater(DebugLog log, JsonManipulator manipulator) {
        _log = log;
        _manipulator = manipulator;
    }
}
