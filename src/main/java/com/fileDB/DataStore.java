package com.fileDB;

import com.fileDB.Exception.*;

import java.io.IOException;

public interface DataStore {

    /**
     *
     * @param key
     * @param value
     * @param ttl
     * @return
     */
    void create(String key, String value, long ttl) throws IOException, DataStoreException;

    /**
     *
     * @param key
     * @param value
     * @return
     */
    void create(String key, String value) throws IOException, DataStoreException;

    /**
     *
     * @param key
     * @return
     */
    void delete(String key) throws KeyNotFoundException, DataStoreException;

    /**
     *
     * @param key
     * @return
     */
    String read(String key) throws DataStoreException, IOException;

    /**
     *
     * @throws IOException
     */
    void close() throws IOException;
}
