package com.huxq17.moveongridview;

import android.view.View;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Children {
    private LinkedHashMap<View, Child> container = new LinkedHashMap<>();
    private LinkedList<Child> mChild = new LinkedList<>();
    private MoveOnGridView parent;

    public Children(MoveOnGridView parent) {
        this.parent = parent;
    }

    public void add(int index, View view) {
        Child child = container.get(view);
        if (child == null) {
            child = new Child(view);
            child.setParent(parent);
            container.put(view, child);
        }
        mChild.add(index, child);
    }

    public boolean remove(Child child) {
        return mChild.remove(child);
    }

    public void remove(int index) {
        mChild.remove(index);
    }

    public Child get(int index) {
        return mChild.get(index);
    }

    public int size() {
        return mChild.size();
    }

    public void clear() {
        mChild.clear();
    }
}
