package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:24 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientValues {
    public String itemName;
    public Double price;
    public Double foodValue;
}
