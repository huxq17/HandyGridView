package com.huxq17.moveongridview;

import java.util.LinkedList;

public class FixedQueue<E> extends LinkedList<E> {
    private int mLimit;

    public FixedQueue() {
    }

    public void setLimit(int limit) {
        mLimit = limit;
    }

    public boolean add(E e) {
        if (mLimit > 0 && size() >= mLimit) {
            super.poll();
        }
        return super.add(e);
    }

    public void addFirst(E e) {
        if (mLimit > 0 && size() >= mLimit) {
            super.pollLast();
        }
        super.addFirst(e);
    }
}
