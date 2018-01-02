package com.colonolnutty.module.shareddata.models.json;

/**
 * User: Jack's Computer
 * Date: 01/01/2018
 * Time: 2:18 PM
 */
public interface IJsonArrayWrapper extends IJsonWrapper {
    IJsonWrapper get(int index);
    boolean firstItemIsValueType();
    int size();
}
