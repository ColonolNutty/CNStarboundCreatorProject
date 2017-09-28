package com.company.models;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 2:07 PM
 */
public class MessageBundle {
    private String _message;

    private ArrayList<MessageBundle> _subBundles;

    public MessageBundle() {
        this(null);
    }

    public MessageBundle(String message) {
        _message = message;
        _subBundles = new ArrayList<MessageBundle>();
    }

    public MessageBundle get(int index) {
        return _subBundles.get(index);
    }

    public MessageBundle add(String message) {
        if(_message == null) {
            _message = message;
            return this;
        }
        if(_message.equals(message)) {
            return this;
        }
        MessageBundle messageBundle = new MessageBundle(message);
        _subBundles.add(messageBundle);
        return messageBundle;
    }

    public int size() {
        return _subBundles.size();
    }

    public boolean hasBundles() {
        return size() > 0;
    }

    @Override
    public String toString() {
        return _message;
    }
}
