package com.fileDB.MemoryManagement;

public class StorageBlock {

    private long filePointer;
    private long blockSize;

    public StorageBlock(long filePointer, long blockSize) {
        this.filePointer = filePointer;
        this.blockSize = blockSize;
    }

    public long getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(long filePointer) {
        this.filePointer = filePointer;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
    }

    public void extendBlock(long noOfBytes) {
        filePointer += noOfBytes;
        blockSize += noOfBytes;
    }

    public void shrinkBlock(long noOfBytes) {
        filePointer -= noOfBytes;
        blockSize -= noOfBytes;
    }
}
