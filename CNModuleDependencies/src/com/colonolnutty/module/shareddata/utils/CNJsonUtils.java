package com.colonolnutty.module.shareddata.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 09/17/2017
 * Time: 11:37 AM
 */
public abstract class CNJsonUtils {

    public static boolean isValueType(JsonNode node) {
        return node.isDouble()
                || node.isInt()
                || node.isBoolean()
                || node.isTextual();
    }

    public static boolean hasTestNode(JsonNode node) {
        if(!node.isArray()) {
            return node.has("op")
                    && node.get("op").asText().equals("test");
        }
        boolean foundTestNode = false;
        for(int i = 0; i < node.size(); i++) {
            if(hasTestNode(node.get(i))) {
                foundTestNode = true;
                i = node.size();
            }
        }
        return foundTestNode;
    }

    public static boolean hasPathName(JsonNode node, String pathName) {
        return node.has("path")
                && node.get("path").asText().equals("/" + pathName);
    }
}
