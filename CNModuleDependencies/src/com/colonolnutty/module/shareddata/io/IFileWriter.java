package com.colonolnutty.module.shareddata.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:29 AM
 */
public interface IFileWriter {
    String writeValueAsString(Object obj) throws JsonProcessingException;
    JsonNode valueToTree(Object obj);
    <T> T treeToValue(TreeNode modNode, Class<T> valueType) throws JsonProcessingException;

    File createFile(String filePath) throws IOException;
    void writeData(String filePath, String data) throws IOException;
    void writeData(File file, String data) throws IOException;
}
