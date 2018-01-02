package com.colonolnutty.module.shareddata.models.json;

import com.colonolnutty.module.shareddata.utils.CNStringUtils;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:53 PM
 */
public class ValueTypeWrapper implements IJsonWrapper {
    private Object _val;

    public ValueTypeWrapper(Object val) {
        _val = val;
    }

    @Override
    public boolean isValueType() {
        return true;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public String toString() {
        if(_val == null || _val.toString().equals("null")) {
            return null;
        }
        if(_val instanceof String) {
            return "\"" + CNStringUtils.escapeString(_val.toString()) + "\"";
        }
        return _val.toString();
    }
}
