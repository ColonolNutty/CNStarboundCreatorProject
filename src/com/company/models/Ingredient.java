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
    public String name;
    public String itemName;
    public String objectName;
    public String projectileName;
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
        return itemName != null || objectName != null || name != null || projectileName != null;
    }

    public String getName() {
        if(itemName != null) {
            return itemName;
        }
        if(name != null) {
            return name;
        }
        if(projectileName != null) {
            return projectileName;
        }
        return objectName;
    }

    public boolean priceEquals(Ingredient otherIngredient) {
        return valuesEqual(price, otherIngredient.price);
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Ingredient))return false;
        Ingredient otherIngredient = (Ingredient)other;
        if(getName() == null || !getName().equals(otherIngredient.getName())) {
            return false;
        }
        return valuesEqual(price, otherIngredient.price)
                && valuesEqual(foodValue, otherIngredient.foodValue);
    }

    private boolean valuesEqual(Double one, Double two) {
        if(one == null) {
            one = 0.0;
        }
        if(two == null) {
            two = 0.0;
        }
        if(one == 0.0 && two == 0.0) {
            return true;
        }
        if(one == 0.0 || two == 0.0) {
            return false;
        }
        return one.equals(two);
    }
}
