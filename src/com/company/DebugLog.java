package com.company;

import com.company.models.ConfigSettings;

import java.io.*;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 3:43 PM
 */
public class DebugLog {

    private boolean _enableConsoleDebug;
    private boolean _enableVerboseLogging;
    private String defaultLogFile = "updateLog.log";
    private String debugPrefix = "[DEBUG] ";
    private String errorPrefix = "[ERROR] ";
    private String infoPrefix = "[INFO] ";
    private PrintWriter writer;

    public DebugLog(ConfigSettings settings) {
        _enableConsoleDebug = settings.enableConsoleDebug;
        _enableVerboseLogging = settings.enableVerboseLogging;
        String debugLogFile = settings.logFile;
        if(debugLogFile == null) {
            logInfo("'logFile' not specified in configuration file, using default: " + defaultLogFile, false);
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

    public void logDebug(String message, boolean isVerbose) {
        if(_enableConsoleDebug) {
            writeOutput(debugPrefix + message, isVerbose);
            return;
        }
        writeToLog(debugPrefix + message, isVerbose);
    }

    public void logInfo(String message, boolean isVerbose) {
        writeOutput(infoPrefix + message, isVerbose);
    }

    public void logError(String message, boolean isVerbose) {
        writeOutput(errorPrefix + message, isVerbose);
    }

    public void logError(Exception e) {
        e.printStackTrace(System.out);
        System.out.flush();
        if(writer != null) {
            e.printStackTrace(writer);
        }
    }

    public void logError(String message, Exception e) {
        logError(message, false);
        logError(e);
    }

    private void writeOutput(String message, boolean isVerbose) {
        if(isVerbose && !_enableVerboseLogging) {
            return;
        }
        System.out.println(message);
        System.out.flush();
        writeToLog(message, isVerbose);
    }

    private void writeToLog(String message, boolean isVerbose) {
        if(isVerbose && !_enableVerboseLogging) {
            return;
        }
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
