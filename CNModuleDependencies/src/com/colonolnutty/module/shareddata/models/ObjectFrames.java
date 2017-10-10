package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 10/10/2017
 * Time: 3:19 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectFrames {
    public ObjectFramesGrid frameGrid;
}
