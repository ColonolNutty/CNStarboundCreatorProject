package com.colonolnutty.module.shareddata.models;

import com.colonolnutty.module.shareddata.CNUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

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
        return price != null && price > 0.0;
    }

    public boolean hasFoodValue() {
        return foodValue != null && foodValue > 0.0;
    }

    public boolean hasEffects() {
        return hasEffects(effects);
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

    public boolean effectsAreEqual(JsonNode otherEffects) {
        boolean selfHasEffects = hasEffects();
        boolean otherHasEffects = hasEffects(otherEffects);
        if(!selfHasEffects && !otherHasEffects) {
            return true;
        }
        if(selfHasEffects != otherHasEffects) {
            return false;
        }
        if(effects.size() != otherEffects.size()) {
            return false;
        }
        boolean isSame = true;
        for(int i = 0; i < effects.size(); i++) {
            if(!effectsAreEqual(effects.get(i), otherEffects.get(i))) {
                isSame = false;
                i = effects.size();
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
            boolean effectsOneIsValueType = CNUtils.isValueType(effectsOne);
            boolean effectsTwoIsValueType = CNUtils.isValueType(effectsTwo);
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
            boolean selfIsValueType = CNUtils.isValueType(selfEffect);
            boolean otherIsValueType = CNUtils.isValueType(otherEffect);
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

    public boolean hasEffects(JsonNode eff) {
        return effectsNotEmpty(eff) && effectsNotEmpty(eff.get(0));
    }

    public boolean effectsNotEmpty(JsonNode eff) {
        return eff != null && eff.isArray() && eff.size() > 0;
    }

    public boolean hasPatchFile() {
        return patchFile != null;
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
        copyIngred.description = this.description;
        copyIngred.shortdescription = this.shortdescription;
        copyIngred.inventoryIcon = this.inventoryIcon;
        copyIngred.stages = this.stages;
        copyIngred.interactData = this.interactData;
        copyIngred.breakDropOptions = this.breakDropOptions;
        copyIngred.effects = this.effects;
        return copyIngred;
    }
}
