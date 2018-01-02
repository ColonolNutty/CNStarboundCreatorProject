package com.colonolnutty.module.shareddata.models.json;

import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:14 PM
 */
public class JSONObjectWrapper implements IJsonObjectWrapper {
    private JSONObject _obj;
    private ArrayList<String> _fieldNames;

    public JSONObjectWrapper(JSONObject obj) {
        _obj = obj;
    }

    @Override
    public IJsonWrapper get(String fieldName) {
        if(_obj == null || !_obj.has(fieldName)) {
            return null;
        }
        Object subObj = _obj.get(fieldName);
        if(subObj instanceof JSONArray) {
            return new JSONArrayWrapper((JSONArray) subObj);
        }
        if(subObj instanceof JSONObject) {
            return new JSONObjectWrapper((JSONObject) subObj);
        }
        return new ValueTypeWrapper(subObj);
    }

    @Override
    public ArrayList<String> fieldNames() {
        if(_fieldNames != null) {
            return _fieldNames;
        }
        if(_obj == null) {
            _fieldNames = new ArrayList<String>();
        }
        else {
            _fieldNames = CNJsonUtils.getPropertyNames(_obj);
        }
        return _fieldNames;
    }

    @Override
    public boolean isValueType() {
        return false;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public String toString() {
        if(_obj == null) {
            return null;
        }
        return _obj.toString();
    }
}
