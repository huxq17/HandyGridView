package com.huxq17.moveongridview.scrollrunner;


import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

public class ScrollRunner implements Runnable {
    private Scroller mScroller;
    private ICarrier mCarrier;
    private int mDuration = 250;
    private int lastX, lastY;

    public ScrollRunner(ICarrier carrier) {
        mCarrier = carrier;
        mScroller = new Scroller(carrier.getContext(), new LinearInterpolator());
    }

    public void setCarrier(ICarrier carrier) {
        mCarrier = carrier;
    }

    public void start(int dx, int dy) {
        start(dx, dy, mDuration);
    }

    public void start(int dx, int dy, int duration) {
        start(0, 0, dx, dy, duration);
    }

    public void start(int startX, int startY, int dx, int dy) {
        start(startX, startY, dx, dy, mDuration);
    }

    public void start(int startX, int startY, int dx, int dy, int duration) {
        this.mDuration = duration;
        mScroller.startScroll(startX, startY, dx, dy, duration);
        mCarrier.removeCallbacks(this);
        mCarrier.post(this);
        lastX = startX;
        lastY = startY;
    }

    public void cancel() {
        if (!mScroller.isFinished()) {
            mCarrier.removeCallbacks(this);
            mScroller.forceFinished(true);
        }
    }

    public int getCurX() {
        return mScroller.getCurrX();
    }

    public int getCurY() {
        return mScroller.getCurrY();
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    public boolean isRunning() {
        return !mScroller.isFinished();
    }

    @Override
    public void run() {
        if (mScroller.computeScrollOffset()) {
            int currentX = mScroller.getCurrX();
            int currentY = mScroller.getCurrY();
            mCarrier.onMove(lastX, lastY, currentX, currentY);
            lastX = currentX;
            lastY = currentY;
            if (currentX == mScroller.getFinalX() && currentY == mScroller.getFinalY()) {
                mCarrier.removeCallbacks(this);
                mCarrier.onDone();
            } else {
                mCarrier.post(this);
            }
        } else {
            mCarrier.removeCallbacks(this);
            mCarrier.onDone();
        }
    }

}
