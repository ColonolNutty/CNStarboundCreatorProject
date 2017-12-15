package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.models.settings.BaseSettings;
import com.colonolnutty.module.shareddata.models.MessageBundle;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 3:43 PM
 */
public class CNLog {

    private String defaultLogFile = "updateLog.log";
    private String debugPrefix = "[DEBUG] ";
    private String errorPrefix = "[ERROR] ";
    private String infoPrefix = "[INFO] ";
    private PrintWriter writer;
    private ArrayList<String> _ignoredErrors;
    private DebugWriter _debugWriter;
    private MessageBundler _messageBundler;
    private BaseSettings _settings;

    public CNLog(DebugWriter debugWriter, BaseSettings settings) {
        _settings = settings;
        _debugWriter = debugWriter;
        _ignoredErrors = new ArrayList<String>();
        _ignoredErrors.add("value differs from expectations");
        setupDebugLogFile();
    }

    public void debug(String message) {
        writeMessage(MessageType.Debug, message);
    }

    public void debug(String message, int indentSize) {
        writeMessage(MessageType.Debug, CNStringUtils.createIndent(indentSize) + message);
    }

    public void info(String message) {
        writeMessage(MessageType.Info, message);
    }

    public void info(String message, int indentSize) {
        writeMessage(MessageType.Info, CNStringUtils.createIndent(indentSize) + message);
    }

    public void error(String message) {
        error(message, null);
    }

    public void error(Exception e) {
        error(e.getMessage(), e);
    }

    public void error(String message, Exception e) {
        if(e != null && isIgnoredMessage(e.getMessage())) {
            return;
        }
        writeMessage(MessageType.Error, message);
        if(e == null) {
            return;
        }
        writeMessage(e);
    }


    private void writeMessage(MessageType messageType, String message) {
        boolean isDebug = false;
        String messagePrefix = "";
        switch(messageType) {
            case Info:
                messagePrefix = infoPrefix;
                break;
            case Debug:
                messagePrefix = debugPrefix;
                isDebug = true;
                break;
            case Error:
                messagePrefix = errorPrefix;
                break;
        }
        if(isDebug && !_settings.enableVerboseLogging) {
            return;
        }
        if(!isDebug ||  _settings.enableConsoleDebug) {
            if(_debugWriter != null) {
                _debugWriter.writeln(messagePrefix + message);
            }
        }
        if(writer != null) {
            writer.println(messagePrefix + message);
        }
    }

    private void writeMessage(Exception e) {
        writeMessage(MessageType.None, e.toString());
    }

    private boolean isIgnoredMessage(String errMessage) {
        return _ignoredErrors.contains(errMessage);
    }

    private ArrayList<MessageBundle> _messageBundles;

    public void startSubBundle(String name) {
        startSubBundle(name, false);
    }

    public void startSubBundle(String name, boolean highlight) {
        if(_messageBundles == null) {
            _messageBundles = new ArrayList<MessageBundle>();
        }
        if(_messageBundles.isEmpty()) {
            _messageBundles.add(_messageBundler.getBundle(name, highlight));
            return;
        }
        MessageBundle lastBundle = _messageBundles.get(_messageBundles.size() - 1);
        _messageBundles.add(lastBundle.add(name, highlight));
    }

    public void endSubBundle() {
        if(_messageBundles.isEmpty()) {
            return;
        }
        _messageBundles.remove(_messageBundles.size() - 1);
    }

    public Hashtable<String, MessageBundle> getMessages() {
        return _messageBundler.getBundles();
    }

    public void writeToBundle(String... messages) {
        writeToBundle(false, messages);
    }

    public void writeToBundle(String message) {
        writeToBundle(message, false);
    }

    public void writeToBundle(boolean highlight, String... messages) {
        if(messages == null || messages.length == 0) {
            return;
        }
        for(String message : messages) {
            writeToBundle(message, highlight);
        }
    }

    public void writeToBundle(String message, boolean highlight) {
        MessageBundle messageBundle;
        if(_messageBundles == null || _messageBundles.isEmpty()) {
            messageBundle = _messageBundler.getBundle("Root", highlight);
        }
        else {
            messageBundle = _messageBundles.get(_messageBundles.size() - 1);
        }
        messageBundle.add(message);
    }

    public void writeToAll(String... messages) {
        writeToAll(0, messages);
    }

    public void writeToAll(int indentSize, String... messages) {
        writeToAll(indentSize, false, messages);
    }

    public void writeToAll(int indentSize, boolean highlight, String... messages) {
        if(messages == null || messages.length == 0) {
            return;
        }
        String combined = "";
        for(int i = 0; i < messages.length; i++) {
            String message = messages[i];
            writeToBundle(message, highlight);
            combined += message;
            if((i + 1) < messages.length) {
                combined += ", ";
            }
        }
        debug(combined, indentSize);
    }

    public void clearCurrentBundles() {
        _messageBundles = new ArrayList<MessageBundle>();
    }

    public void clear() {
        _messageBundler.clear();
    }

    public void dispose() {
        System.out.println("Closing log.");
        if(writer != null) {
            writer.flush();
            writer.close();
        }
    }

    public void setupDebugLogFile() {
        if(writer != null) {
            writer.flush();
            writer.close();
        }
        String debugLogFile = _settings.logFile;
        _messageBundler = new MessageBundler();
        if(debugLogFile == null) {
            info("'logFile' not specified in configuration file, using default: " + defaultLogFile);
            debugLogFile = defaultLogFile;
        }
        try {
            File file = new File(debugLogFile);
            file.getParentFile().mkdirs();
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writer = new PrintWriter(file);
        }
        catch(IOException e) {
            error(e);
        }
    }

    private enum MessageType {
        None,
        Info,
        Debug,
        Error
    }
}
