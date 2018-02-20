package com.colonolnutty.module.shareddata.models;

import com.colonolnutty.module.shareddata.utils.CNJsonUtils;
import com.colonolnutty.module.shareddata.utils.CNStringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.Hashtable;

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
    public String category;
    public String description;
    public String shortdescription;
    public String inventoryIcon;
    public Object interactData;
    public Object breakDropOptions;
    public ArrayNode effects;
    public Boolean printable;

    @JsonIgnore
    public Hashtable<IngredientProperty, ArrayList<Object>> _ingredientUpdates;

    @JsonIgnore
    public static int DefaultEffectDuration = 60;

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
        if(hasEffects(effects)) {
            this.effects = effects;
        }
    }

    public boolean hasName() {
        return itemName != null || objectName != null || name != null || projectileName != null;
    }

    public boolean hasPrice() {
        Double latestPrice = getPrice();
        return latestPrice != null && latestPrice > 0.0;
    }

    public boolean hasFoodValue() {
        Double latestFoodValue = getFoodValue();
        return latestFoodValue != null && latestFoodValue > 0.0;
    }

    public boolean hasDescription() {
        String latestDescription = getDescription();
        return !CNStringUtils.isNullOrWhitespace(latestDescription);
    }

    public boolean hasEffects() {
        return hasEffects(getEffects());
    }

    public boolean hasEffects(JsonNode eff) {
        return effectsNotEmpty(eff) && effectsNotEmpty(eff.get(0));
    }

    public void setName(String name) {
        if(name == null) {
            return;
        }
        String currentName = getName();
        if(currentName == null) {
            this.name = name;
            this.itemName = name;
            this.projectileName = name;
            this.objectName = name;
            return;
        }
        if(this.name != null) {
            this.name = name;
        }
        if(this.itemName != null) {
            this.itemName = name;
        }
        if(this.projectileName != null) {
            this.projectileName = name;
        }
        if(this.objectName != null) {
            this.objectName = name;
        }
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

    public String getIdentifier() {
        String name = getName();
        if(name != null) {
            return name;
        }
        if(patchFile != null) {
            return patchFile;
        }
        return filePath;
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
        return valuesEqual(getPrice(), otherIngredient.getPrice())
                && valuesEqual(getFoodValue(), otherIngredient.getFoodValue());
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

    public boolean effectsAreEqual(JsonNode otherEffects) {
        boolean selfHasEffects = hasEffects();
        boolean otherHasEffects = hasEffects(otherEffects);
        if(!selfHasEffects && !otherHasEffects) {
            return true;
        }
        if(selfHasEffects != otherHasEffects) {
            return false;
        }
        ArrayNode selfEffects = getEffects();
        if(selfEffects.size() != otherEffects.size()) {
            return false;
        }
        boolean isSame = true;
        for(int i = 0; i < selfEffects.size(); i++) {
            if(!effectsAreEqual(selfEffects.get(i), otherEffects.get(i))) {
                isSame = false;
                i = selfEffects.size();
            }
        }
        return isSame;
    }

    private boolean effectsAreEqual(JsonNode effectsOne, JsonNode effectsTwo) {
        boolean selfHasEffects = effectsNotEmpty(effectsOne);
        boolean otherHasEffects = effectsNotEmpty(effectsTwo);
        if(!selfHasEffects && !otherHasEffects) {
            return true;
        }
        if(selfHasEffects != otherHasEffects) {
            return false;
        }
        if(effectsOne.isArray() != effectsTwo.isArray()) {
            return false;
        }
        if(!effectsOne.isArray()) {
            boolean effectsOneIsValueType = CNJsonUtils.isValueType(effectsOne);
            boolean effectsTwoIsValueType = CNJsonUtils.isValueType(effectsTwo);
            if(effectsOneIsValueType && effectsTwoIsValueType) {
                return effectsOne.asText().equals(effectsTwo.asText());
            }
            if(effectsOneIsValueType || effectsTwoIsValueType) {
                return false;
            }
            return false;
        }
        if(effectsOne.size() != effectsTwo.size()) {
            return false;
        }

        boolean isSame = true;
        for(int i = 0; i < effectsOne.size(); i++) {
            JsonNode selfEffect = effectsOne.get(i);
            JsonNode otherEffect = effectsTwo.get(i);
            if(selfEffect.isArray() && otherEffect.isArray()) {
                if(!effectsAreEqual(selfEffect, otherEffect)) {
                    isSame = false;
                    i = effectsOne.size();
                }
                continue;
            }
            boolean selfIsValueType = CNJsonUtils.isValueType(selfEffect);
            boolean otherIsValueType = CNJsonUtils.isValueType(otherEffect);
            if(selfIsValueType && otherIsValueType) {
                if(!selfEffect.asText().equals(otherEffect.asText())) {
                    isSame = false;
                }
            }
            else if(selfIsValueType || otherIsValueType) {
                isSame = false;
            }
            else {
                if(!fieldIsSame(selfEffect, otherEffect, "effect")) {
                    isSame = false;
                }
                else if(!fieldIsSame(selfEffect, otherEffect, "duration")) {
                    isSame = false;
                }
            }
            if(!isSame) {
                i = effectsOne.size();
            }
        }
        return isSame;
    }

    private boolean fieldIsSame(JsonNode selfEffect, JsonNode otherEffect, String fieldName) {
        if(selfEffect.has(fieldName) && otherEffect.has(fieldName)) {
            if(!selfEffect.get(fieldName).asText().equals(otherEffect.get(fieldName).asText())) {
                return false;
            }
        }
        else if(selfEffect.has(fieldName) || otherEffect.has(fieldName)) {
            return false;
        }
        return true;
    }

    public boolean effectsNotEmpty(JsonNode eff) {
        return eff != null && eff.isArray() && eff.size() > 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Ingredient copy() {
        Ingredient copyIngred = new Ingredient();
        copyIngred.name = this.name;
        copyIngred.itemName = this.itemName;
        copyIngred.objectName = this.objectName;
        copyIngred.projectileName = this.projectileName;
        copyIngred.price = this.price;
        copyIngred.foodValue = this.foodValue;
        copyIngred.category = this.category;
        copyIngred.description = this.description;
        copyIngred.shortdescription = this.shortdescription;
        copyIngred.inventoryIcon = this.inventoryIcon;
        copyIngred.interactData = this.interactData;
        copyIngred.breakDropOptions = this.breakDropOptions;
        copyIngred.effects = this.effects;
        copyIngred.printable = this.printable;
        copyIngred._ingredientUpdates = this._ingredientUpdates;
        return copyIngred;
    }

    public void update(IngredientProperty property, Object newValue) {
        if(_ingredientUpdates == null) {
            _ingredientUpdates = new Hashtable<>();
        }
        if(!_ingredientUpdates.containsKey(property)) {
            _ingredientUpdates.put(property, new ArrayList<Object>());
        }
        _ingredientUpdates.get(property).add(newValue);
    }

    public <T> T getLatestOrDefault(IngredientProperty property, T defaultValue) {
        if(_ingredientUpdates == null || !_ingredientUpdates.containsKey(property)) {
            return defaultValue;
        }
        return (T) _ingredientUpdates.get(property).get(_ingredientUpdates.get(property).size() - 1);
    }

    public void applyUpdates() {
        price = getPrice();
        foodValue = getFoodValue();
        description = getDescription();
        effects = getEffects();
    }

    public Double getPrice() {
        return getLatestOrDefault(IngredientProperty.Price, price);
    }

    public Double getFoodValue() {
        return getLatestOrDefault(IngredientProperty.FoodValue, foodValue);
    }

    public String getDescription() {
        return getLatestOrDefault(IngredientProperty.Description, description);
    }

    public ArrayNode getEffects() {
        return getLatestOrDefault(IngredientProperty.Effects, effects);
    }
}
