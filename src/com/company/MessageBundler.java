package com.company;

import com.company.models.MessageBundle;

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

    public MessageBundle getBundle(String name, String message) {
        if(_bundles.containsKey(name)) {
            return _bundles.get(name);
        }
        MessageBundle bundle = new MessageBundle(message);
        _bundles.put(name, bundle);
        return bundle;
    }

    public MessageBundle getBundle(String name) {
        return getBundle(name, null);
    }

    public Hashtable<String, MessageBundle> getBundles() {
        return _bundles;
    }
}
