package com.company;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * User: Jack's Computer
 * Date: 09/17/2017
 * Time: 11:37 AM
 */
public abstract class CNUtils {
    public static boolean fileEndsWith(String filePath, ArrayList<String> values) {
        boolean hasExtension = false;
        for(int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if(filePath.endsWith(value)) {
                hasExtension = true;
                i = values.size();
            }
        }
        return hasExtension;
    }

    public static JsonNode[] toArray(JsonNode node) {
        ArrayList<JsonNode> nodes = new ArrayList<JsonNode>();
        if(node.isArray()) {
            return toArray(nodes);
        }
        Iterator<JsonNode> elements = node.elements();
        while(elements.hasNext()) {
            nodes.add(elements.next());
        }
        return toArray(nodes);
    }

    public static JsonNode[][] toDoubleArray(JsonNode node) {
        ArrayList<JsonNode[]> nodes = new ArrayList<JsonNode[]>();
        if(node.isArray()) {
            return toDoubleArray(nodes);
        }
        Iterator<JsonNode> elements = node.elements();
        while(elements.hasNext()) {
            JsonNode jsonNode = elements.next();
            if(jsonNode.isArray()) {
                nodes.add(toArray(jsonNode));
            }
        }
        return toDoubleArray(nodes);
    }

    public static JsonNode[] toArray(ArrayList<JsonNode> list) {
        JsonNode[] array = new JsonNode[list.size()];
        for(int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static JsonNode[][] toDoubleArray(ArrayList<JsonNode[]> list) {
        JsonNode[][] array = new JsonNode[list.size()][];
        for(int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
