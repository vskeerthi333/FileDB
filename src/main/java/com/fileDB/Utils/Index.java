package com.fileDB.Utils;

import com.fileDB.MemoryManagement.StorageBlock;

public class Index {

    private final String key;
    private final long expiresAt;
    private final StorageBlock storageBlock;

    public Index(String key, long expiresAt, StorageBlock storageBlock) {
        this.key = key;
        this.expiresAt = expiresAt;
        this.storageBlock = storageBlock;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }

    public String getKey() {
        return key;
    }

    public StorageBlock getStorageBlock() {
        return storageBlock;
    }

    public long expiresAt() {
        return expiresAt;
    }
}
