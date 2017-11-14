package com.huxq17.moveongridview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MoveOnGridView extends GridView implements AdapterView.OnItemLongClickListener {
    public MoveOnGridView(Context context) {
        this(context, null);
    }

    public MoveOnGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setChildrenDrawingOrderEnabled(true);
        super.setOnItemLongClickListener(this);
    }

    private float mLastX, mLastY;

    @Override
    public void invalidate() {
        super.invalidate();
//        log("invalidate");
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
        log("ontouch currDraggedPosition=" + currDraggedPosition);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (ev.getRawX() - mLastX);
                int deltaY = (int) (ev.getRawY() - mLastY);
                if (mDragedView != null) {
                    mDragedView.offsetLeftAndRight(deltaX);
                    mDragedView.offsetTopAndBottom(deltaY);
                }
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void log(String msg) {
        Log.e("moveongridview", msg);
    }

    private View mDragedView;
    private int mDragPosition = -1, mDragIndex = -1;
private Rect mDragedRect = new Rect();
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        log("onItemLongClick");


        view.setScaleX(1.2f);
        view.setScaleY(1.2f);
        view.setAlpha(0.6f);
        mDragedView = view;
        mDragPosition = position;
        mDragIndex = indexOfChild(view);

        view.getGlobalVisibleRect(mDragedRect);
        int top = mDragedRect.top;
        int left = mDragedRect.left;
        int[] l1 = new int[2];
        view.getLocationOnScreen(l1);
        log("left=" + left + ";top=" + top+"\n;"+"left1="+l1[0]+";top1="+l1[1]);

        return true;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mDragedView != null) {
            if (i == mDragIndex) {
                return childCount - 1;
            } else if (i == childCount - 1) {
                return mDragIndex;
            }
        }
        return i;
    }
}
