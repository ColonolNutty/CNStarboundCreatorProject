package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * User: Jack's Computer
 * Date: 10/10/2017
 * Time: 3:19 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectFramesGrid {
    public ArrayNode size;
    public ArrayNode dimensions;
    public ArrayNode names;
}
