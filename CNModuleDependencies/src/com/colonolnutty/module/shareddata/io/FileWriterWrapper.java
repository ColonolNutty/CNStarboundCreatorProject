package com.colonolnutty.module.shareddata.io;

import com.colonolnutty.module.shareddata.MapperWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:28 AM
 */
public class FileWriterWrapper extends MapperWrapper implements IFileWriter {

    public String writeValueAsString(Object obj) throws JsonProcessingException {
        return _mapper.writeValueAsString(obj);
    }
    public JsonNode valueToTree(Object obj) {
        return _mapper.valueToTree(obj);
    }

    @Override
    public <T> T treeToValue(TreeNode modNode, Class<T> valueType) throws JsonProcessingException {
        return _mapper.treeToValue(modNode, valueType);
    }

    @Override
    public File createFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    @Override
    public void writeData(String filePath, String data) throws IOException {
        writeData(new File(filePath), data);
    }

    @Override
    public void writeData(File file, String data) throws IOException {
        Writer writer = new FileWriter(file);
        writer.write(data);
        writer.close();
    }
}
