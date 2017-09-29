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

    private ArrayList<MessageBundle> _subBundles;

    public void startSubBundle(String name) {
        if(_subBundles == null) {
            _subBundles = new ArrayList<MessageBundle>();
        }
        if(_subBundles.isEmpty()) {
            _subBundles.add(_messageBundler.getBundle(name));
            return;
        }
        _subBundles.add(_subBundles.get(_subBundles.size() - 1).add(name));
    }

    public void endSubBundle() {
        if(_subBundles.isEmpty()) {
            return;
        }
        if(_subBundles.size() == 1) {
            _subBundles.clear();
            return;
        }
        _subBundles.remove(_subBundles.size() - 1);
    }

    public void clearCurrentBundle() {
        _subBundles = new ArrayList<MessageBundle>();
    }

    public void clear() {
        _messageBundler.clear();
    }

    public void logDebug(String message, boolean isVerbose) {
        String toWrite = debugPrefix + message;
        writeToCurrentBundle(message);
        if(_enableConsoleDebug) {
            writeOutput(toWrite, isVerbose, true);
            return;
        }
        writeToLog(toWrite, isVerbose);
    }

    public void logInfo(String message, boolean isVerbose) {
        writeToCurrentBundle(message);
        writeOutput(infoPrefix + message, isVerbose, false);
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
        writeOutput(errorPrefix + message, false, true);
        writeToCurrentBundle(message);
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
        if(_subBundles == null || _subBundles.isEmpty()) {
            return _messageBundler.getBundle("Root").add(message);
        }
        return _subBundles.get(_subBundles.size() - 1).add(message);
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
