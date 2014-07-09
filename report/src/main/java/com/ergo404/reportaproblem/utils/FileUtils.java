package com.ergo404.reportaproblem.utils;

import java.io.File;

/**
 * Created by pierre.rossines on 09/07/2014.
 */
public class FileUtils {
    /**
     * Non optimized code to delete a file or a folder recursively
     * @param file the File to delete
     * @return true if the file/folder was deleted
     */
    public static boolean delete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }
        return file.delete();
    }
}
