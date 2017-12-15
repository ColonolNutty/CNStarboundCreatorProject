package com.colonolnutty.module.shareddata.utils;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 12/14/2017
 * Time: 12:30 PM
 */
public class CNFileUtils {
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

    public static boolean fileStartsWith(String filePath, String[] values) {
        boolean hasExtension = false;
        for(int i = 0; i < values.length; i++) {
            String value = values[i];
            File valueFilePath = new File(value);

            if(filePath.startsWith(valueFilePath.getAbsolutePath())) {
                hasExtension = true;
                i = values.length;
            }
        }
        return hasExtension;
    }
}
