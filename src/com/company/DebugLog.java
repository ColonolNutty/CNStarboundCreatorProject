package com.company;

import java.io.*;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 3:43 PM
 */
public class DebugLog {

    private boolean _enableConsoleDebug;
    private String defaultLogFile = "updateLog.log";
    private String debugPrefix = "[DEBUG] ";
    private String errorPrefix = "[ERROR] ";
    private String infoPrefix = "[INFO] ";
    private PrintWriter writer;

    public DebugLog(String debugLogFile, boolean enableConsoleDebug) {
        _enableConsoleDebug = enableConsoleDebug;
        if(debugLogFile == null) {
            logInfo("No output file specified, using default log file: " + defaultLogFile);
            debugLogFile = defaultLogFile;
        }
        try {
            File file = new File(debugLogFile);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer = new PrintWriter(file);
        }
        catch(IOException e) {
            logError(e);
        }
    }

    public void logDebug(String message) {
        if(_enableConsoleDebug) {
            writeOutput(debugPrefix + message);
            return;
        }
        writeToLog(debugPrefix + message);
    }

    public void logInfo(String message) {
        writeOutput(infoPrefix + message);
    }

    public void logError(String message) {
        writeOutput(errorPrefix + message);
    }

    public void logError(Exception e) {
        e.printStackTrace(System.out);
        System.out.flush();
        if(writer != null) {
            e.printStackTrace(writer);
        }
    }

    public void logError(String message, Exception e) {
        logError(message);
        logError(e);
    }

    private void writeOutput(String message) {
        System.out.println(message);
        System.out.flush();
        writeToLog(message);
    }

    private void writeToLog(String message) {
        if(writer != null) {
            writer.println(message);
        }
    }

    public void dispose() {
        if(writer != null) {
            writer.flush();
            writer.close();
        }
    }
}
