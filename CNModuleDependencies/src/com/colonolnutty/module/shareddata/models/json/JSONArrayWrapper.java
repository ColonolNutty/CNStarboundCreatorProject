package com.colonolnutty.module.shareddata.models.json;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:14 PM
 */
public class JSONArrayWrapper implements IJsonArrayWrapper {
    private JSONArray _jsonArr;

    public JSONArrayWrapper(JSONArray jsonArr) {
        _jsonArr = jsonArr;
    }

    @Override
    public IJsonWrapper get(int index) {
        if(size() == 0 || index < 0 || index > (size() - 1) || _jsonArr.isNull(index)) {
            return null;
        }
        Object obj = _jsonArr.get(index);
        if(obj == null) {
            return null;
        }
        if(obj instanceof JSONObject) {
            return new JSONObjectWrapper((JSONObject) obj);
        }
        if(obj instanceof JSONArray) {
            return new JSONArrayWrapper((JSONArray) obj);
        }
        return new ValueTypeWrapper(obj);
    }

    @Override
    public boolean firstItemIsValueType() {
        Object first = get(0);
        if(first == null) {
            return false;
        }
        return !(first instanceof IJsonObjectWrapper) && !(first instanceof IJsonArrayWrapper);
    }

    @Override
    public int size() {
        if(_jsonArr == null) {
            return 0;
        }
        return _jsonArr.length();
    }

    @Override
    public boolean isValueType() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public String toString() {
        if(_jsonArr == null) {
            return null;
        }
        return _jsonArr.toString();
    }
}
