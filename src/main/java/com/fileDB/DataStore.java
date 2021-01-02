package com.fileDB;

import com.fileDB.Exception.*;

import java.io.IOException;

public interface DataStore {

    /**
     * Inserts a Key-Value pair into data store along with Time-to-live property
     * @param key
     * @param value
     * @param ttl
     * @throws IOException
     * @throws DataStoreException
     */
    void create(String key, String value, long ttl) throws IOException, DataStoreException;

    /**
     * Inserts a Key-Value pair into data store.
     * @param key
     * @param value
     * @throws IOException
     * @throws DataStoreException
     */
    void create(String key, String value) throws IOException, DataStoreException;

    /**
     * Delete's a Key-Value pair from data store permanently.
     * @param key
     * @throws DataStoreException
     */
    void delete(String key) throws DataStoreException;

    /**
     * Retrieves corresponding value of a key
     * @param key
     * @return
     * @throws DataStoreException
     * @throws IOException
     */
    String read(String key) throws DataStoreException, IOException;

    /**
     * releases lock on datastore
     * @throws IOException
     */
    void close() throws IOException;
}
