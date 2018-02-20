package com.colonolnutty.module.shareddata.prettyprinters;

import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 12/31/2017
 * Time: 12:35 PM
 */
public abstract class BasePrettyPrinter implements IPrettyPrinter {
    protected String[] _propertiesInOrder;
    public static final String NEW_LINE = "\r\n";
    public static final int INDENT_SIZE = 2;

    protected BasePrettyPrinter() {
        _propertiesInOrder = new String[0];
    }

    @Override
    public void setPropertyOrder(String[] propertyOrder) {
        _propertiesInOrder = propertyOrder;
    }

    public ArrayList<String> sortProperties(String[] propertyNamesInOrder, ArrayList<String> properties) {
        ArrayList<String> sortedProperties = new ArrayList<String>();
        if(propertyNamesInOrder != null) {
            for (String propertyName : propertyNamesInOrder) {
                if (properties.contains(propertyName)
                        && !sortedProperties.contains(propertyName)) {
                    sortedProperties.add(propertyName);
                }
            }
        }
        for(String propertyName : properties) {
            if(!sortedProperties.contains(propertyName)) {
                sortedProperties.add(propertyName);
            }
        }
        return sortedProperties;
    }
}
