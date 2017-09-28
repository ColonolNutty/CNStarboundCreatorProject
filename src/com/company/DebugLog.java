package com.company;

import com.company.models.ConfigSettings;

import java.io.*;
import java.util.ArrayList;

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
    private ArrayList<String> _ignoredErrors;
    private DebugWriter _debugWriter;

    public DebugLog(DebugWriter debugWriter, ConfigSettings settings) {
        _debugWriter = debugWriter;
        _enableConsoleDebug = settings.enableConsoleDebug;
        _enableVerboseLogging = settings.enableVerboseLogging;
        _ignoredErrors = new ArrayList<String>();
        _ignoredErrors.add("value differs from expectations");
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
            writeOutput(debugPrefix + message, isVerbose, true);
            return;
        }
        writeToLog(debugPrefix + message, isVerbose);
    }

    public void logInfo(String message, boolean isVerbose) {
        writeOutput(infoPrefix + message, isVerbose, false);
    }

    public void logError(String message, boolean isVerbose) {
        writeOutput(errorPrefix + message, isVerbose, true);
    }

    public void logError(Exception e) {
        String errMessage = e.getMessage();
        if(isIgnoredError(errMessage)) {
            return;
        }
        if(_enableConsoleDebug) {
            System.out.println("Exception:");
            e.printStackTrace(System.out);
            writeToWriter("Exception: " + e.toString());
            System.out.flush();
        }
        if(writer != null) {
            e.printStackTrace(writer);
        }
    }

    public void logError(String message, Exception e) {
        String errMessage = e.getMessage();
        if(isIgnoredError(errMessage)) {
            return;
        }
        logError(message, false);
        logError(e);
    }

    private boolean isIgnoredError(String errMessage) {
        return _ignoredErrors.contains(errMessage);
    }

    private void writeOutput(String message, boolean isVerbose, boolean isDebug) {
        if(isVerbose && !_enableVerboseLogging) {
            return;
        }
        if(!isDebug ||  _enableConsoleDebug) {
            writeToWriter(message);
            System.out.println(message);
            System.out.flush();
        }
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

    private void writeToWriter(String text) {
        if(_debugWriter != null) {
            _debugWriter.writeln(text);
        }
    }

    public void dispose() {
        System.out.println("Closing log.");
        if(writer != null) {
            writer.flush();
            writer.close();
        }
    }
}
