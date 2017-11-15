package com.huxq17.moveongridview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

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

    private WrappedApdater mAdapter;

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = new WrappedApdater((BaseAdapter) adapter);
        super.setAdapter(mAdapter);
    }

    public int getDragPosition() {
        return mAdapter.getPositionForView(mDragedView);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

        mColumnWidth = getChildAt(0).getWidth();
        mRowHeight = getChildAt(0).getHeight();
        mHorizontalSpacing = getHorizontalSpacing();
        mVerticalSpacing = getVerticalSpacing();
        mColumnsNum = getNumColumns();
        Rect dragFrame = new Rect();
        getChildAt(0).getHitRect(dragFrame);
        log("ontouch currDraggedPosition=" + currDraggedPosition + "; top=" + dragFrame.top);
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
                    if (currDraggedPosition == INVALID_POSITION) return super.onTouchEvent(ev);
                    int dragPosition = getDragPosition();
                    if (currDraggedPosition != dragPosition) {
                        getChildAt(currDraggedPosition - getFirstVisiblePosition()).getGlobalVisibleRect(mDragedRect);
                        if (currDraggedPosition < dragPosition) {
                            for (int i = currDraggedPosition; i < dragPosition; i++) {
                                translateItem(i, i + 1);
                            }
                        } else {
                            for (int i = dragPosition + 1; i <= currDraggedPosition; i++) {
                                translateItem(i, i - 1);
                            }
                        }
                        mAdapter.setPositionForView(mDragedView, currDraggedPosition);
                    }
                }
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDragedView = null;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void translateItem(int from, int to) {
        int fromIndex = from - getFirstVisiblePosition();
        int toIndex = to - getFirstVisiblePosition();
        View fromView = getChildAt(fromIndex);
        View toView = getChildAt(toIndex);
        int horizontalSpacing = getHorizontalSpacing();
        int verticalSpacing = getVerticalSpacing();
        int columnWidth = getColumnWidth();
        int fromX = fromView.getLeft();
        int fromY = fromView.getTop();
        int offsetX = (to - from) % mColumnsNum * mColumnWidth;
        int offsetY = (to - from) / mColumnsNum * mRowHeight;
        int toX = offsetX + fromX;
        int toY = offsetY + fromY;
        fromView.offsetLeftAndRight(offsetX);
        fromView.offsetTopAndBottom(offsetY);
        mAdapter.setPositionForView(fromView, to);
    }

    private Rect mTouchFrame;
    private Rect mRect = new Rect();

    @Override
    public int pointToPosition(int x, int y) {
        int firstPosition = getFirstVisiblePosition();
        if (mTouchFrame == null) {
            mTouchFrame = new Rect();
        }
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.VISIBLE && child != mDragedView) {
                child.getHitRect(mTouchFrame);
                if (mTouchFrame.contains(x, y)) {
                    return mAdapter.getPositionForView(child);
                }
            }
        }
        getGlobalVisibleRect(mRect);
        log("mDragedView != null mDragedRect=" + mDragedRect.toShortString() + ";x + getLeft()=" + (x + getLeft()) + ";y=" + (y + mRect.top));
        if (mDragedView != null && mDragedRect.contains(x + getLeft(), y + mRect.top)) {
            return mAdapter.getPositionForView(mDragedView);
        }
        return INVALID_POSITION;
    }

    private void log(String msg) {
        Log.e("moveongridview", msg);
    }

    private View mDragedView;
    private int mDragIndex = -1;
    private Rect mDragedRect = new Rect();
    private int mColumnWidth, mRowHeight, mColumnsNum;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        log("onItemLongClick");
        view.getGlobalVisibleRect(mDragedRect);
        int top = mDragedRect.top;
        int left = mDragedRect.left;
//        int[] l1 = new int[2];
//        view.getLocationOnScreen(l1);
        log("left=" + left + ";top=" + top + "\n;" + "mColumnWidth=" + mColumnWidth + ";mRowHeight=" + mRowHeight);

        view.setScaleX(1.2f);
        view.setScaleY(1.2f);
        view.setAlpha(0.6f);
        mDragedView = view;
        mDragIndex = indexOfChild(view);

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