package com.colonolnutty.module.shareddata.ui;

/**
 * User: Jack's Computer
 * Date: 11/19/2017
 * Time: 1:21 PM
 */
public abstract class ProgressController {
    public abstract void setMaximum(int total);

    public abstract void add(int amount);

    public abstract void reset();
}
