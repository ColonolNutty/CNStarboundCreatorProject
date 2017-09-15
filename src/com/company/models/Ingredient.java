package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 2:24 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {
    public String itemName;
    public String objectName;
    public Double price;
    public Double foodValue;
    public String description;
    public String shortdescription;
    public String inventoryIcon;
    public Object stages;
    public Object interactData;

    @JsonIgnore
    public String filePath;

    @JsonIgnore
    public String patchFile;

    public Ingredient() { }

    public Ingredient(String itemName) {
        this(itemName, 0.0, 0.0);
    }

    public Ingredient(String itemName, Double price, Double foodValue) {
        this.itemName = itemName;
        this.objectName = itemName;
        this.price = price;
        this.foodValue = foodValue;
    }

    public boolean hasName() {
        return itemName != null || objectName != null;
    }

    public String getName() {
        if(itemName != null) {
            return itemName;
        }
        return objectName;
    }
}
