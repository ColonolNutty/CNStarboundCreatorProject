package com.company;

import com.company.models.ConfigSettings;
import com.company.models.MessageBundle;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

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
    private MessageBundler _messageBundler;

    public DebugLog(DebugWriter debugWriter, ConfigSettings settings) {
        _debugWriter = debugWriter;
        _enableConsoleDebug = settings.enableConsoleDebug;
        _enableVerboseLogging = settings.enableVerboseLogging;
        _ignoredErrors = new ArrayList<String>();
        _ignoredErrors.add("value differs from expectations");
        String debugLogFile = settings.logFile;
        _messageBundler = new MessageBundler();
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

    private MessageBundle _currentBundle;

    public void setCurrentBundle(String name) {
        _currentBundle = _messageBundler.getBundle(name);
    }

    public void setCurrentBundle(String name, String message) {
        _currentBundle = _messageBundler.getBundle(name, message);
    }

    public void clearCurrentBundle() {
        _currentBundle = null;
    }

    public void addToCurrentBundle(String message, boolean setAsCurrent) {
        MessageBundle bundle = writeToCurrentBundle(message);
        if(setAsCurrent) {
            _currentBundle = bundle;
        }
    }

    public void logDebug(String message, boolean isVerbose) {
        String toWrite = debugPrefix + message;
        if(_enableConsoleDebug) {
            writeOutput(toWrite, isVerbose, true);
            writeToCurrentBundle(toWrite);
            return;
        }
        writeToLog(toWrite, isVerbose);
        writeToCurrentBundle(toWrite);
    }

    public void logInfo(String message, boolean isVerbose) {
        String toWrite = infoPrefix + message;
        writeToCurrentBundle(toWrite);
        writeOutput(toWrite, isVerbose, false);
    }

    public void logError(Exception e) {
        String errMessage = e.getMessage();
        if(isIgnoredError(errMessage)) {
            return;
        }
        if(_enableConsoleDebug) {
            System.out.println("Exception:");
            e.printStackTrace(System.out);
            System.out.flush();
        }
        if(writer != null) {
            writeToWriter("Exception: ");
            e.printStackTrace(writer);
        }
        MessageBundle exceptionBundle = writeToCurrentBundle("Exception: ");
        if(exceptionBundle != null) {
            exceptionBundle.add(e.toString());
        }
    }

    public void logError(String message, Exception e) {
        String errMessage = e.getMessage();
        if(isIgnoredError(errMessage)) {
            return;
        }
        String toWrite = errorPrefix + message;
        writeOutput(toWrite, false, true);
        writeToCurrentBundle(toWrite);
        MessageBundle subBundle = writeToCurrentBundle("Exception:");
        if(subBundle == null) {
            return;
        }
        subBundle.add(e.toString());
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

    private MessageBundle writeToCurrentBundle(String message) {
        if(_currentBundle == null) {
            return null;
        }
        return _currentBundle.add(message);
    }

    public Hashtable<String, MessageBundle> getMessages() {
        return _messageBundler.getBundles();
    }

    public void dispose() {
        System.out.println("Closing log.");
        if(writer != null) {
            writer.flush();
            writer.close();
        }
    }
}
