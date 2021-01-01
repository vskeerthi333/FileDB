package com.fileDB;

public interface DataStore {

    /**
     *
     * @param key
     * @param value
     * @param ttl
     * @return
     */
    boolean create(String key, String value, int ttl);

    /**
     *
     * @param key
     * @param value
     * @return
     */
    boolean create(String key, String value);

    /**
     *
     * @param key
     * @return
     */
    boolean delete(String key);

    /**
     *
     * @param key
     * @return
     */
    String read(String key);
}
