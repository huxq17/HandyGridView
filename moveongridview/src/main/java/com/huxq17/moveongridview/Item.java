package com.huxq17.moveongridview;

import android.content.Context;
import android.view.View;

import com.huxq17.moveongridview.scrollrunner.ICarrier;
import com.huxq17.moveongridview.scrollrunner.ScrollRunner;


public class Item implements ICarrier {
    public int position;
    public View view;
    private ScrollRunner mRunner;
    private int mDuration = 200;

    public Item(View view) {
        this.view = view;
        mRunner = new ScrollRunner(this);
    }

    public void move(int offsetX, int offsetY) {
        mRunner.cancel();
        mRunner.start(offsetX, offsetY, mDuration);
    }

    @Override
    public Context getContext() {
        return view.getContext();
    }

    @Override
    public void onMove(int lastX, int lastY, int curX, int curY) {
        int deltaX = curX - lastX;
        int deltaY = curY - lastY;
        view.offsetLeftAndRight(deltaX);
        view.offsetTopAndBottom(deltaY);
    }

    @Override
    public void onDone() {

    }

    @Override
    public boolean post(Runnable runnable) {
        return view.post(runnable);
    }

    @Override
    public boolean removeCallbacks(Runnable action) {
        return view.removeCallbacks(action);
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
