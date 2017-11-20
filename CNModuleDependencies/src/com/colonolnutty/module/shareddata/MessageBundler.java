package com.colonolnutty.module.shareddata;

import com.colonolnutty.module.shareddata.models.MessageBundle;

import java.util.Hashtable;

/**
 * User: Jack's Computer
 * Date: 09/28/2017
 * Time: 2:06 PM
 */
public class MessageBundler {
    private Hashtable<String, MessageBundle> _bundles;

    public MessageBundler() {
        _bundles = new Hashtable<String, MessageBundle>();
    }

    public MessageBundle getBundle(String name) {
        return getBundle(name, false);
    }

    public MessageBundle getBundle(String name, boolean highlight) {
        if(_bundles.containsKey(name)) {
            return _bundles.get(name);
        }
        MessageBundle bundle = new MessageBundle(name, highlight);
        _bundles.put(name, bundle);
        return bundle;
    }

    public Hashtable<String, MessageBundle> getBundles() {
        return new Hashtable<String, MessageBundle>(_bundles);
    }

    public void clear() {
        _bundles = new Hashtable<String, MessageBundle>();
    }
}
