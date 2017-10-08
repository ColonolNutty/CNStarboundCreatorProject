package com.colonolnutty.module.shareddata.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 2:07 PM
 */
public class MessageBundle {
    private String _name;

    private ArrayList<MessageBundle> _subBundles;

    public MessageBundle(String name) {
        _name = name;
        _subBundles = new ArrayList<MessageBundle>();
    }

    public MessageBundle get(int index) {
        return _subBundles.get(index);
    }

    public MessageBundle add(String message) {
        if(_name == null) {
            _name = message;
            return this;
        }
        if(_name.equals(message)) {
            return this;
        }
        MessageBundle foundBundle = null;
        for(MessageBundle bundle : _subBundles) {
            if(bundle.hasMessage(message)) {
                foundBundle = bundle;
                break;
            }
        }
        if(foundBundle != null) {
            return foundBundle;
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

    public boolean hasMessage(String message) {
        return _name != null && message != null && _name.equals(message);
    }

    public void orderBy(Comparator<MessageBundle> comparator) {
        Collections.sort(_subBundles, comparator);
    }

    @Override
    public String toString() {
        return _name;
    }
}
