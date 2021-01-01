package com.fileDB;

import java.util.HashMap;

public class DBStore implements DataStore {

    public DBStore() {
    }

    public DBStore(String filePath) {
    }

    @Override
    public boolean create(String key, String value, int ttl) {
        return false;
    }

    @Override
    public boolean create(String key, String value) {
        return false;
    }

    @Override
    public boolean delete(String key) {
        return false;
    }

    @Override
    public String read(String key) {
        return null;
    }
}
