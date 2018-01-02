package com.colonolnutty.module.shareddata.models.json;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:17 PM
 */
public interface IJsonObjectWrapper extends IJsonWrapper {
    IJsonWrapper get(String fieldName);
    ArrayList<String> fieldNames();
}
