package com.company;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 3:43 PM
 */
public class DebugLog {

    private boolean _enable;

    public DebugLog(boolean enable) {
        _enable = enable;
    }

    public void logDebug(String message) {
        if(_enable) {
            System.out.println("[DEBUG] " + message);
            System.out.flush();
        }
    }

    public void logException(Exception e) {
        if(_enable) {
            e.printStackTrace();
            System.out.flush();
        }
    }

    public void logInfo(String message) {
        System.out.println("[INFO] " +message);
        System.out.flush();
    }
}
