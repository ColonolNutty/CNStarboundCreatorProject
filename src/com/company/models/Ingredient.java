package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:24 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {
    public String itemName;
    public Double price;
    public Double foodValue;

    public Ingredient() {

    }

    public Ingredient(String itemName) {
        this(itemName, 0.0, 0.0);
    }

    public Ingredient(String itemName, Double price, Double foodValue) {
        this.itemName = itemName;
        this.price = price;
        this.foodValue = foodValue;
    }
}
