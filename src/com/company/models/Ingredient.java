package com.company.models;

import com.company.CNUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

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
    public Object breakDropOptions;
    public ArrayNode effects;

    @JsonIgnore
    public String filePath;

    @JsonIgnore
    public String patchFile;

    public Ingredient() { }

    public Ingredient(String name) {
        this(name, 0.0, 0.0, null);
    }

    public Ingredient(String name, Double price, Double foodValue, ArrayNode effects) {
        this.itemName = name;
        this.objectName = name;
        this.projectileName = name;
        this.name = name;
        this.price = price;
        this.foodValue = foodValue;
        this.effects = effects;
        if(!hasEffects()) {
            this.effects = null;
        }
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

    public boolean effectsAreEqual(JsonNode otherIngEffects) {
        if(effects == null && otherIngEffects == null) {
            return true;
        }
        if(effects == null || otherIngEffects == null) {
            return false;
        }
        if(effects.isArray() != otherIngEffects.isArray()) {
            return false;
        }
        if(effects.size() != otherIngEffects.size()) {
            return false;
        }
        boolean isSame = true;
        Iterator<JsonNode> selfEffects = effects.elements();
        Iterator<JsonNode> otherEffects = otherIngEffects.elements();
        while(selfEffects.hasNext()) {
            if(!otherEffects.hasNext()) {
                isSame = false;
                break;
            }
            JsonNode selfEffect = selfEffects.next();
            JsonNode otherEffect = otherEffects.next();
            if(!effectsAreEqual(selfEffect, otherEffect)) {
                isSame = false;
                break;
            }
        }
        return isSame;
    }

    private boolean effectsAreEqual(JsonNode effectsOne, JsonNode effectsTwo) {
        if(effectsOne == null && effectsTwo == null) {
            return true;
        }
        if(effectsOne == null || effectsTwo == null) {
            return false;
        }
        if(effectsOne.isArray() != effectsTwo.isArray()) {
            return false;
        }
        if(!effectsOne.isArray()) {
            boolean effectsOneIsValueType = CNUtils.isValueType(effectsOne);
            if(effectsOneIsValueType != CNUtils.isValueType(effectsTwo)) {
                return false;
            }
            if(effectsOneIsValueType) {
                if(!effectsOne.asText().equals(effectsTwo.asText())) {
                    return false;
                }
            }
        }
        if(effectsOne.size() != effectsTwo.size()) {
            return false;
        }
        boolean isSame = true;
        Iterator<JsonNode> selfEffects = effectsOne.elements();
        Iterator<JsonNode> otherEffects = effectsTwo.elements();
        while(selfEffects.hasNext()) {
            if(!otherEffects.hasNext()) {
                isSame = false;
                break;
            }
            JsonNode selfEffect = selfEffects.next();
            JsonNode otherEffect = otherEffects.next();
            if(selfEffect.isArray() && otherEffect.isArray()) {
                if(!effectsAreEqual(selfEffect, otherEffect)) {
                    isSame = false;
                    break;
                }
                continue;
            }
            boolean selfIsValueType = CNUtils.isValueType(selfEffect);
            boolean otherIsValueType = CNUtils.isValueType(otherEffect);
            if(selfIsValueType && otherIsValueType) {
                if(!selfEffect.asText().equals(otherEffect.asText())) {
                    isSame = false;
                    break;
                }
            }
            else if(selfIsValueType || otherIsValueType) {
                isSame = false;
                break;
            }
            else {
                if(!fieldIsSame(selfEffect, otherEffect, "effect")) {
                    isSame = false;
                    break;
                }
                else if(!fieldIsSame(selfEffect, otherEffect, "duration")) {
                    isSame = false;
                    break;
                }
            }
        }
        return isSame;
    }

    private boolean fieldIsSame(JsonNode selfEffect, JsonNode otherEffect, String fieldName) {
        boolean isSame = false;
        if(selfEffect.has(fieldName)) {
            if (!otherEffect.has(fieldName)) {
                isSame = false;
            }
            else if(!selfEffect.get(fieldName).asText().equals(otherEffect.get(fieldName).asText())) {
                isSame = false;
            }
        }
        return isSame;
    }

    public boolean hasEffects() {
        return effects != null && effects.isArray() && effects.size() > 0 && effects.get(0).isArray() && effects.get(0).size() > 0;
    }
}
