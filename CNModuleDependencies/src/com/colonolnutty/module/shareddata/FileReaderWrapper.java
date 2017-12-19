package com.colonolnutty.module.shareddata;

import java.io.*;

/**
 * User: Jack's Computer
 * Date: 12/16/2017
 * Time: 11:59 AM
 */
public class FileReaderWrapper extends MapperWrapper implements IFileReader {

    @Override
    public <T> T read(String filePath, Class<T> classOfT) throws IOException {
        File file = new File(filePath);
        if(!file.exists()) {
            return null;
        }
        Reader reader = new FileReader(filePath);
        T value = _mapper.readValue(reader, classOfT);
        reader.close();
        return value;
    }

    @Override
    public String readFile(String filePath) throws IOException {
        String fileData = "";
        File file = new File(filePath);
        if(!file.exists() || file.isDirectory()) {
            return null;
        }
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] texts = line.split("//");
            if(texts.length != 0 && !texts[0].trim().equals("")) {
                fileData += texts[0];
            }
        }
        br.close();
        return fileData;
    }
}
