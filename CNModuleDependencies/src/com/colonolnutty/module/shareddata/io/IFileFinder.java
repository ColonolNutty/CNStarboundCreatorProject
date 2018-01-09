package com.colonolnutty.module.shareddata.io;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Jack's Computer
 * Date: 01/06/2018
 * Time: 3:30 PM
 */
public interface IFileFinder {
    ArrayList<String> findFiles(String root);
    ArrayList<String> findFiles(File root);
}
