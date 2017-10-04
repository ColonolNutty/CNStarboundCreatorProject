package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User: Jack's Computer
 * Date: 10/04/2017
 * Time: 10:13 AM
 */
public abstract class BaseSettings {

    @JsonIgnore
    public String configLocation;
}
