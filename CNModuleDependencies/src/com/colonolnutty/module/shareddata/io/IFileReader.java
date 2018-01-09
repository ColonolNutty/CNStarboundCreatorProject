package com.colonolnutty.module.shareddata.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:59 AM
 */
public interface IFileReader {
    <T> T read(String filePath, Class<T> classOfT) throws IOException;
    String readFile(String filePath) throws IOException;
    List<String> readAllLines(File file) throws IOException;
}
