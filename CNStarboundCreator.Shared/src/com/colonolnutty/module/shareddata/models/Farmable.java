package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * User: Jack's Computer
 * Date: 10/09/2017
 * Time: 11:28 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Farmable extends Ingredient {
    public ArrayNode orientations;
    public ArrayNode stages;

    public boolean isSeed() {
        return category != null && category.equals("seed");
    }

    public Farmable copy() {
        Farmable farm = (Farmable)super.copy();
        farm.orientations = this.orientations;
        farm.stages = this.stages;
        return farm;
    }
}
