package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/12/2017
 * Time: 10:25 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumableBase {
    public String itemName;
    public Double price;
    public Double foodValue;
    public String shortdescription;

    @JsonIgnore
    public String filePath;
}
