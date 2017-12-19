package com.colonolnutty.module.shareddata;

import java.io.IOException;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:59 AM
 */
public interface IFileReader {
    <T> T read(String filePath, Class<T> classOfT) throws IOException;
    String readFile(String filePath) throws IOException;
}
