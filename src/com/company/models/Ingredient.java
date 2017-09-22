package com.company.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
    public JsonNode[][] effects;

    @JsonIgnore
    public String filePath;

    @JsonIgnore
    public String patchFile;

    public Ingredient() { }

    public Ingredient(String name) {
        this(name, 0.0, 0.0, null);
    }

    public Ingredient(String name, Double price, Double foodValue, JsonNode[][] effects) {
        this.itemName = name;
        this.objectName = name;
        this.projectileName = name;
        this.name = name;
        this.price = price;
        this.foodValue = foodValue;
        this.effects = effects;
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

    public boolean effectsAreEqual(Ingredient otherIngredient) {
        if(effects == null || otherIngredient.effects == null) {
            return false;
        }
        if(effects.length != otherIngredient.effects.length) {
            return false;
        }
        return effectsAreEqual(otherIngredient.effects);
    }

    public boolean effectsAreEqual(JsonNode[][] otherIngEffects) {
        if(otherIngEffects.length != effects.length) {
            return false;
        }
        boolean isSame = true;
        for(int i = 0; i < effects.length; i++) {
            JsonNode[] selfEffects = effects[i];
            JsonNode[] otherEffects = otherIngEffects[i];
            for(int j = 0; j < selfEffects.length; j++) {
                JsonNode selfEffect = selfEffects[j];
                JsonNode otherEffect = otherEffects[j];
                if(isValueType(otherEffect) || isValueType(selfEffect)) {
                    if(!otherEffect.asText().equals(selfEffect.asText())) {
                        isSame = false;
                    }
                }
                else {
                    if(selfEffect.has("effect")) {
                        if (!otherEffect.has("effect")) {
                            isSame = false;
                        }
                        else if(!selfEffect.get("effect").asText().equals(otherEffect.get("effect").asText())) {
                            isSame = false;
                        }
                    }
                    if(selfEffect.has("duration")) {
                        if(!otherEffect.has("duration")) {
                            isSame = false;
                        }
                        else if(selfEffect.get("duration").asDouble() != otherEffect.get("duration").asDouble()) {
                            isSame = false;
                        }
                    }
                }

                if(!isSame) {
                    j = effects[i].length;
                }
            }
            if(!isSame) {
                i = effects.length;
            }
        }
        return isSame;
    }

    private boolean isValueType(JsonNode node) {
        return node.isDouble()
                || node.isInt()
                || node.isBoolean()
                || node.isTextual();
    }
}
