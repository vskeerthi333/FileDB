package com.fileDB.MemoryManagement;

import com.fileDB.Exception.StorageNotAvailableException;
import com.fileDB.Utils.DataStoreConstants;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MemoryManager {

    private final RandomAccessFile file;
    private final MemoryPool pool;
    private int USED_STORAGE = 0;

    public MemoryManager(RandomAccessFile file) {
        this.file = file;
        pool = new MemoryPool();
    }

    public static MemoryManager getInstance(RandomAccessFile file) {
        return new MemoryManager(file);
    }

    public Block allocate(long noOfBytes) throws StorageNotAvailableException, IOException {
        if (noOfBytes > getAvailableStorage()) {
            throw new StorageNotAvailableException("Insufficient storage");
        }
        Block block = getBestFitBlock(noOfBytes);
        if (block == null)  {
            performCompaction();
            block = getBestFitBlock(noOfBytes);
        }
        USED_STORAGE += noOfBytes;
        return block;
    }

    public void free(StorageBlock block) {
        Block blockToBeDeleted = (Block) block;
        USED_STORAGE -= blockToBeDeleted.getBlockSize();
        blockToBeDeleted.setAllocated(false);
        mergeAdjacentEmptyBlocks(blockToBeDeleted, true);
        mergeAdjacentEmptyBlocks(blockToBeDeleted, false);

    }

    private void mergeAdjacentEmptyBlocks(Block curr, boolean isPrevious) {
        Block blockToBeMerged = null;
        if (isPrevious) blockToBeMerged = curr.getPrev();
        else blockToBeMerged = curr.getNext();

        if (blockToBeMerged != null && !blockToBeMerged.isAllocated()) {
            if (isPrevious) curr.setFilePointer(blockToBeMerged.getFilePointer());
            curr.setBlockSize(blockToBeMerged.getBlockSize() + curr.getBlockSize());
            pool.remove(blockToBeMerged);
        }
    }

    private void performCompaction() throws IOException {
        Block curr = pool.getStartBlock();
        while (curr != null && curr.getNext() != null) {
            Block next = curr.getNext();
            // Bring next forward
            if (!curr.isAllocated() && next.isAllocated()) {
                file.seek(next.getFilePointer());
                String contents = file.readUTF();
                file.seek(curr.getFilePointer());
                file.writeUTF(contents);

                long totalSize = curr.getBlockSize() + next.getBlockSize();
                curr.setBlockSize(next.getBlockSize());
                next.setBlockSize(totalSize - curr.getBlockSize());

                next.setFilePointer(curr.getFilePointer() + curr.getBlockSize());

                curr.setAllocated(true);
                next.setAllocated(false);
                curr = next;
            } else if (!curr.isAllocated() && !next.isAllocated()) {
                mergeAdjacentEmptyBlocks(curr, false);
            } else curr = curr.getNext();
        }
    }

    private Block getBestFitBlock(long noOfBytes) {
        Block curr = pool.getStartBlock();
        Block bestFitBlock = null;

        while (curr != null) {
            if (curr.isAllocated()) {
                curr = curr.getNext();
                continue;
            }
            if (curr.getBlockSize() >= noOfBytes) {
                if (bestFitBlock == null) bestFitBlock = curr;
                else if (bestFitBlock.getBlockSize() > curr.getBlockSize()) bestFitBlock = curr;
            }
            curr = curr.getNext();
        }

        spiltExtraSpace(bestFitBlock, noOfBytes);
        return bestFitBlock;
    }

    private void spiltExtraSpace(Block block, long requiredBytes) {
        if (block == null) return;

        long spaceRemained = block.getBlockSize() - requiredBytes;
        if (spaceRemained > 0) {
            Block extraBlock = new Block(block.getFilePointer() + requiredBytes, spaceRemained, null, null);
            block.setBlockSize(requiredBytes);
            pool.addAfter(block, extraBlock);
        }

        block.setAllocated(true);
    }

    public int getAvailableStorage() {
        return DataStoreConstants.STORAGE_LIMIT - USED_STORAGE;
    }
}
