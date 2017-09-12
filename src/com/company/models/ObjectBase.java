package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 11:24 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjectBase {
    public String objectName;
    public Double price;

    @JsonIgnore
    public Double foodValue;
}
