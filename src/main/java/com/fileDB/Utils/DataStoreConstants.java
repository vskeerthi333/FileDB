package com.fileDB.Utils;

public class DataStoreConstants {

    /**
     * Storage is specified in bytes
     * Storage = 1GB
     */
    public static final int STORAGE_LIMIT = 1024 * 1024 * 1024;

    /**
     * Key size is specified in characters
     * Key is of at most of 32 characters i.e keyLength <= 32
     */
    public static final int KEY_SIZE = 32;

    /**
     * Value size is specified in bytes;
     * value <= 16KB
     */
    public static final int VALUE_SIZE = 16 * 1024;

    /**
     * If file path is not specified it will store in current directory
     */
    public static String DEFAULT_FILE_PATH = "./DSFile";

}
