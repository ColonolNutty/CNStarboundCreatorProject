package com.colonolnutty.module.shareddata.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/25/2017
 * Time: 10:00 AM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusEffect {
    public String name;
    public int defaultDuration;
    public String blockingStat;
    public String label;

    @JsonIgnore
    public String filePath;

    @JsonIgnore
    public String patchFilePath;

    public StatusEffect() {}
    public StatusEffect(String name, int duration) {
        this.name = name;
        this.defaultDuration = duration;
    }
}
