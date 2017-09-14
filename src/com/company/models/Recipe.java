package com.company.models;

import com.company.ItemDescriptor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:09 PM
 */
public class Recipe {
    public ItemDescriptor[] input;
    public ItemDescriptor output;
    public String[] groups;
}
