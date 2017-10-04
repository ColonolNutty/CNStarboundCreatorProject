package com.company.balancer;

/**
 * User: Jack's Computer
 * Date: 09/11/2017
 * Time: 12:14 PM
 */
public class ItemDescriptor {
    public String item;
    public Double count;

    public ItemDescriptor() {}

    public ItemDescriptor(String item, Double count) {
        this.item = item;
        this.count = count;
    }
}
