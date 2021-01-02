package com.fileDB.MemoryManagement;

import com.fileDB.Utils.DataStoreConstants;

public class MemoryPool {

    private Block startBlock;
    private Block endBlock;
    private int size;

    MemoryPool() {
        this.startBlock = this.endBlock = new Block(0, DataStoreConstants.STORAGE_LIMIT, null, null);
    }

    void addFirst(Block e) {
        assert e != null;

        if (startBlock == null) {
            startBlock = endBlock = e;
        } else {
            startBlock.setPrev(e);
            e.setNext(startBlock);
            startBlock = e;
        }
        size++;
    }

    void addLast(Block e) {
        assert e != null;

        if (endBlock == null) {
            startBlock = endBlock = e;
        } else {
            endBlock.setNext(e);
            e.setPrev(endBlock);
            endBlock = e;
        }
        size++;
    }

    void addAfter(Block e, Block node) {
        assert e != null;
        if (e.getNext() == null) addLast(node);
        else {
            node.setPrev(e);
            node.setNext(e.getNext());
            e.setNext(node);
            size++;
        }
    }

    void remove(Block e) {
        if (e.getPrev() == null) removeStartBlock();
        else if (e.getNext() == null) removeEndBlock();
        else {
            e.getPrev().setNext(e.getNext());
            e.getNext().setPrev(e.getPrev());
            size--;
        }
    }

    void removeStartBlock() {
        assert startBlock != null;
        startBlock = startBlock.getNext();
        startBlock.setPrev(null);
        size--;
    }

    void removeEndBlock() {
        assert endBlock != null;
        endBlock = endBlock.getPrev();
        endBlock.setNext(null);
        size--;
    }

    Block getStartBlock() {
        return startBlock;
    }

    int size() {
        return size;
    }
}
