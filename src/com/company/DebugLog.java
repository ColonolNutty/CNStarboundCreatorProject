package com.company;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 3:43 PM
 */
public class DebugLog {

    private boolean _enable;
    private String debugPrefix = "[DEBUG] ";
    private String errorPrefix = "[ERROR] ";
    private String infoPrefix = "[INFO] ";

    public void logDebug(String message) {
        if(_enable) {
            System.out.println(debugPrefix + message);
            System.out.flush();
        }
    }

    public void logInfo(String message) {
        System.out.println(infoPrefix + message);
        System.out.flush();
    }

    public void logError(String message) {
        System.out.println(errorPrefix + message);
        System.out.flush();
    }

    public void logError(Exception e) {
        e.printStackTrace();
        System.out.println(errorPrefix + e.getMessage());
        System.out.flush();
    }

    public void logError(String message, Exception e) {
        logError(message);
        logError(e);
    }

    public void enableDebug(boolean enable) {
        _enable = enable;
    }
}
