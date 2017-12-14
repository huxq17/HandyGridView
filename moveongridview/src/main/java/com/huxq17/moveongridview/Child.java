package com.huxq17.moveongridview;

import android.content.Context;
import android.view.View;

import com.huxq17.moveongridview.scrollrunner.ICarrier;
import com.huxq17.moveongridview.scrollrunner.ScrollRunner;


public class Child implements ICarrier {
    public int position;
    public View view;
    private ScrollRunner mRunner;
    private int from, to;
    private boolean hasNext = false;
    private MoveOnGridView parent;

    public Child(View view) {
        this.view = view;
        mRunner = new ScrollRunner(this);
    }

    public void setParent(MoveOnGridView parent) {
        this.parent = parent;
    }

    private void move(final int offsetX, final int offsetY) {
        mRunner.start(offsetX, offsetY);
    }

    public void moveTo(int from, int to) {
        this.from = from;
        this.to = to;
        int[] froms = parent.getLeftAndTopForPosition(from);
        int[] tos = parent.getLeftAndTopForPosition(to);
        if (!mRunner.isRunning()) {
            int offsetX = tos[0] - froms[0];
            int offsetY = tos[1] - froms[1];
            move(offsetX, offsetY);
        } else {
            hasNext = true;
        }
    }

    @Override
    public void onDone() {
        int[] froms = new int[]{view.getLeft(), view.getTop()};
        from = parent.pointToPosition(froms[0], froms[1]);

        int[] tos = parent.getLeftAndTopForPosition(to);
        if (hasNext) {
            if (from != to) {
                int offsetX = tos[0] - froms[0];
                int offsetY = tos[1] - froms[1];
                move(offsetX, offsetY);
            }
            hasNext = false;
        }
    }

    @Override
    public void onMove(int lastX, int lastY, int curX, int curY) {
        int deltaX = curX - lastX;
        int deltaY = curY - lastY;
        view.offsetLeftAndRight(deltaX);
        view.offsetTopAndBottom(deltaY);
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
    public Context getContext() {
        return view.getContext();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Child) {
            Child child = (Child) obj;
            if (this.view == child.view) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
