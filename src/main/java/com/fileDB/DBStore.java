package com.fileDB;

import com.fileDB.Exception.*;
import com.fileDB.MemoryManagement.MemoryManager;
import com.fileDB.MemoryManagement.StorageBlock;
import com.fileDB.Utils.DataStoreConstants;
import com.fileDB.Utils.Index;
import com.google.common.base.Utf8;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DBStore implements DataStore {

    private RandomAccessFile DSFile;
    private MemoryManager memoryManager;
    private final FileLock lock;
    private final Map<String, Index> keyToIndex = new ConcurrentHashMap<>();
    private final SortedSet<Index> expiredKeys = Collections.synchronizedSortedSet(new TreeSet<>(new CompareExpiryOfKey()));

    public DBStore(String filePath) throws IOException {
        if (filePath == null) filePath = DataStoreConstants.DEFAULT_FILE_PATH + hashCode();
        RandomAccessFile file = new RandomAccessFile(new File(filePath), "rwd");
        lock =  file.getChannel().lock();
        this.DSFile = file;
        this.memoryManager = MemoryManager.getInstance(file);
    }

    public DBStore() throws IOException {
        this(null);
    }

    @Override
    public synchronized void create(String key, String value) throws IOException, DataStoreException {
        create(key, value, Long.MAX_VALUE);
    }

    @Override
    public synchronized void delete(String key) throws DataStoreException {
        Index index = keyToIndex.remove(key);
        if (index == null) throw new KeyNotFoundException("Key not found to delete");
        memoryManager.free(index.getStorageBlock());
        keyToIndex.remove(index);
    }

    @Override
    public synchronized String read(String key) throws DataStoreException, IOException {
        Index index = keyToIndex.get(key);
        if (index == null) throw new KeyNotFoundException("Key doesn't exists");
        if (index.isExpired()) {
            delete(key);
            throw new KeyNotFoundException("Key doesn't exists");
        }
        return readValue(index.getStorageBlock().getFilePointer());
    }

    @Override
    public synchronized void create(String key, String value, long ttl) throws DataStoreException, IOException {
        validateConstraints(key, value);
        StorageBlock block = memoryManager.allocate(Utf8.encodedLength(value) + 2);
        long expiresAt = getTimeStamp(ttl);
        Index index = new Index(key, expiresAt, block);
        keyToIndex.put(key, index);
        writeValue(block.getFilePointer(), value);
        expiredKeys.add(index);
    }

    private long getTimeStamp(long ttl) {
        if (ttl != Long.MAX_VALUE) {
            return System.currentTimeMillis() + ttl * 1000;
        }
        return ttl;
    }


    private String readValue(long filePointer) throws IOException {
        DSFile.seek(filePointer);
        return DSFile.readUTF();
    }

    private void writeValue(long filePointer, String value) throws IOException {
        DSFile.seek(filePointer);
        DSFile.writeUTF(value);
    }

    private void validateConstraints(String key, String value) throws DataStoreException {
        if (key.length() > DataStoreConstants.KEY_SIZE) {
            throw new ConstrainViolationException(String.format("Key Length is more than %d", DataStoreConstants.KEY_SIZE));
        }
        if (Utf8.encodedLength(value) > DataStoreConstants.VALUE_SIZE) {
            throw new ConstrainViolationException(String.format("Value is more than %d KB", DataStoreConstants.VALUE_SIZE));
        }
        deleteExpiredKeys();
        if (keyToIndex.containsKey(key)) {
            throw new KeyAlreadyExistsException(String.format("Key - %s already exists", key));
        }
    }

    private void deleteExpiredKeys() throws DataStoreException {
        Index currIndex;
        while (!expiredKeys.isEmpty() && (currIndex = expiredKeys.first()).isExpired()) {
            delete(currIndex.getKey());
            expiredKeys.remove(currIndex);
        }
    }
    private static class CompareExpiryOfKey implements Comparator<Index> {
        @Override
        public int compare(Index o1, Index o2) {
            return (int) (o1.expiresAt() - o2.expiresAt());
        }
    }

    @Override
    public void close() throws IOException {
        lock.release();
    }
}
