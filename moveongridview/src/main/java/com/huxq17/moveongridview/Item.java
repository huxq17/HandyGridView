package com.huxq17.moveongridview;

import android.view.View;


public class Item {
    public int position;
    public View view;

    public Item(View view) {
        this.view = view;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Item) {
            Item item = (Item) obj;
            if (this.view == item.view) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
