package com.fileDB.Exception;

import com.fileDB.Utils.DataStoreConstants;

public abstract class DataStoreException extends Exception {
    public DataStoreException(String message) {
        super(message);
    }
}
