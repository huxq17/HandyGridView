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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

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
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View firstChild = getChildAt(0);
                if (firstChild != null) {
                    mFirstTop = firstChild.getTop();
                    mFirstLeft = firstChild.getLeft();
                    mColumnsNum = getNumColumns();
                    int rowLine = firstVisibleItem / mColumnsNum;
                    mScrollY = mFirstTop - rowLine * (mVerticalSpacing + mRowHeight);
                    mVisibleItemCount = visibleItemCount;
                }
            }

        });
    }

    private int mScrollY;
    private int mFirstTop, mFirstLeft;
    private int mVisibleItemCount;

    @Override
    public void onTouchModeChanged(boolean isInTouchMode) {
        super.onTouchModeChanged(isInTouchMode);
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
        return mDragPosition;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

        log("ontouch currDraggedPosition=" + currDraggedPosition);
        mColumnWidth = getChildAt(0).getWidth();
        mRowHeight = getChildAt(0).getHeight();
        mHorizontalSpacing = getHorizontalSpacing();
        mVerticalSpacing = getVerticalSpacing();
        Rect dragFrame = new Rect();
        getChildAt(0).getHitRect(dragFrame);
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
                    log("dragPosition=" + dragPosition + ";currDraggedPosition=" + currDraggedPosition);
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
                        mAdapter.setViewForPosition(currDraggedPosition, mDragedView);
                        mDragPosition = currDraggedPosition;
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
        View fromView = mAdapter.getViewForPosition(from);
        int[] froms = getLeftAndTopForPosition(from);
        int[] tos = getLeftAndTopForPosition(to);
        int offsetX = tos[0] - froms[0];
        int offsetY = tos[1] - froms[1];
        log("translateItem from=" + from + ";offsetX=" + offsetX + ";offsetY=" + offsetY);
        fromView.offsetLeftAndRight(offsetX);
        fromView.offsetTopAndBottom(offsetY);
        TextView textView = (TextView) fromView;
//        textView.setText("position="+from);
        mAdapter.setViewForPosition(to, fromView);
    }

    public int[] getLeftAndTopForPosition(int position) {
        int[] lt = new int[2];
        int m = position % mColumnsNum;
        int n = position / mColumnsNum;
        int left = mFirstLeft + m * (mColumnWidth + mHorizontalSpacing);
        int top = mFirstTop + n * (mRowHeight + mVerticalSpacing);
        lt[0] = left;
        lt[1] = top;
        return lt;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            
        }
    }

    private Rect mTouchFrame;
    private Rect mRect = new Rect();

    @Override
    public int pointToPosition(int x, int y) {
        if (mTouchFrame == null) {
            mTouchFrame = new Rect();
        }
        try {
            int m = (x - mFirstLeft) / (mColumnWidth + mHorizontalSpacing);
            int n = (y - mFirstTop) / (mRowHeight + mVerticalSpacing);
            int right = mFirstLeft + m * (mColumnWidth + mHorizontalSpacing) + mColumnWidth;
            int bottom = mFirstTop + n * (mRowHeight + mVerticalSpacing) + mRowHeight;
            if (x > right || y > bottom) {
                return INVALID_POSITION;
            } else {
                int result = n * mColumnsNum + m + getFirstVisiblePosition();
                if (result <= getLastVisiblePosition()) {
                    return n * mColumnsNum + m + getFirstVisiblePosition();
                } else {
                    return INVALID_POSITION;
                }
            }
        } catch (Exception e) {

        }
//        for (int i = 0; i < mVisibleItemCount; i++) {
//            final int firstTop = mFirstTop;
//            final int firstLeft = mFirstLeft;
//            int row = i / mColumnsNum;
//            int column = i % mColumnsNum;
//            int left = firstLeft + column * (mColumnWidth + mHorizontalSpacing);
//            int top = firstTop + row * (mRowHeight + mVerticalSpacing);
//            mTouchFrame.set(left, top, left + mColumnWidth, top + mRowHeight);
//            if (mTouchFrame.contains(x, y)) {
//                log("pointToPosition position=" + (i + getFirstVisiblePosition()) );
//                return i + getFirstVisiblePosition();
//            }
//        }
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
    private int mDragPosition;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        view.getGlobalVisibleRect(mDragedRect);
        int top = mDragedRect.top;
        int left = mDragedRect.left;
//        int[] l1 = new int[2];
//        view.getLocationOnScreen(l1);
        mDragPosition = position;
        log("onItemLongClick mDragPosition=" + mDragPosition);
        view.setScaleX(1.2f);
        view.setScaleY(1.2f);
        view.setAlpha(0.6f);
        mDragedView = view;
        mDragIndex = indexOfChild(view);

        return true;
    }

    private int totalScroll;

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