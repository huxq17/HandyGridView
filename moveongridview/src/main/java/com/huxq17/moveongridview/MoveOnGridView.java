package com.huxq17.moveongridview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class MoveOnGridView extends GridView implements AdapterView.OnItemLongClickListener {
    public MoveOnGridView(Context context) {
        this(context, null);
    }

    public MoveOnGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private int mTouchSlop;

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        throw new UnsupportedOperationException("setOnItemLongClickListener(OnItemLongClickListener) is not supported in " + getClass().getSimpleName());
    }

    private void refreshChildren() {
        int childCount = getChildCount();
        log("refreshChildren count = " + childCount);
        mChildren.clear();
        for (int i = 0; i < childCount; i++) {
            mChildren.add(getChildAt(i));
        }
    }

    private void init(Context context) {
        setChildrenDrawingOrderEnabled(true);
        super.setOnItemLongClickListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLastFirstVisibleFirstItem != firstVisibleItem) {
                    mLastFirstVisibleFirstItem = firstVisibleItem;
                    refreshChildren();
                }
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
    private int mLastFirstVisibleFirstItem = -1;
    private float mLastX, mLastY;

    @Override
    public void invalidate() {
        super.invalidate();
//        log("invalidate");
    }

    private WrappedApdater mAdapter;
    private OnItemMovedListener mItemMovedListener;

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = new WrappedApdater((BaseAdapter) adapter);
        if (adapter instanceof OnItemMovedListener) {
            mItemMovedListener = (OnItemMovedListener) adapter;
        } else {
            //TODO should throw a exception here.
            log("Your adapter should implements OnItemMovedListener for listening the change of item's position.");
        }
        super.setAdapter(mAdapter);
    }

    public int getDragPosition() {
        return mDragPosition;
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
//        log("addViewInLayout");
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
//        log("attachViewToParent");
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        int index = indexOfChild(child);
        mAdapter.setViewForPosition(index + getFirstVisiblePosition(), child);
        log("onViewAdd child=" + child.hashCode() + ";position=" + (index + getFirstVisiblePosition()));
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        log("onViewRemoved child=" + child.hashCode());
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {
        return super.dispatchDragEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

//        log("ontouch currDraggedPosition=" + currDraggedPosition + ";mScrollY=" + mScrollY);
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
                    if (Math.abs(deltaX) > mTouchSlop && Math.abs(deltaY) > mTouchSlop) {
                    }
                    mDragedView.offsetLeftAndRight(deltaX);
                    mDragedView.offsetTopAndBottom(deltaY);
                    mLastX = ev.getRawX();
                    mLastY = ev.getRawY();
                    if (currDraggedPosition == INVALID_POSITION) return super.onTouchEvent(ev);
                    int dragPosition = getDragPosition();
//                    log("dragPosition=" + dragPosition + ";currDraggedPosition=" + currDraggedPosition);
                    if (currDraggedPosition != dragPosition) {
                        getChildAt(currDraggedPosition - getFirstVisiblePosition()).getGlobalVisibleRect(mDragedRect);
                        if (currDraggedPosition < dragPosition) {

                            for (int i = dragPosition - 1; i >= currDraggedPosition; i--) {
                                log("swap i=" + i + ";to=" + (i + 1));
                                swapItem(i, i + 1);
                            }
                        } else {
                            for (int i = dragPosition + 1; i <= currDraggedPosition; i++) {
                                swapItem(i, i - 1);
                            }
                        }
                        log("dragview position=" + (currDraggedPosition - getFirstVisiblePosition()) + ";dragindex=" + indexOfChild(mDragedView));
                        moveViewToPosition(currDraggedPosition, mDragedView);
                        mDragPosition = currDraggedPosition;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mDragedView != null) {
                    releaseItem();
                }
                mDragedView = null;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void swapItem(int from, int to) {
        int fromIndex = from - getFirstVisiblePosition();
        int toIndex = to - getFirstVisiblePosition();
        View fromView = mChildren.get(fromIndex);
//        View fromView = mAdapter.getViewForPosition(from);

        int[] froms = getLeftAndTopForPosition(from);
        int[] tos = getLeftAndTopForPosition(to);
        int offsetX = tos[0] - froms[0];
        int offsetY = tos[1] - froms[1];
        fromView.offsetLeftAndRight(offsetX);
        fromView.offsetTopAndBottom(offsetY);
        moveViewToPosition(to, fromView);
        dispatchItemMoved(from, to);

        removeViewInLayout(fromView);
        addViewInLayout(fromView, toIndex, fromView.getLayoutParams(), true);
    }

    private void dispatchItemMoved(int from, int to) {
        if (mItemMovedListener != null) {
            mItemMovedListener.onItemMoved(from, to);
        }
    }

    private void moveViewToPosition(int position, View view) {
        mAdapter.setViewForPosition(position, view);
        mChildren.remove(view);
        mChildren.add(getIndex(position), view);
    }

    private int getIndex(int position) {
        return position - mLastFirstVisibleFirstItem;
    }

    public int[] getLeftAndTopForPosition(int position) {
        int[] lt = new int[2];
        int m = position % mColumnsNum;
        int n = position / mColumnsNum;
        int left = mFirstLeft + m * (mColumnWidth + mHorizontalSpacing);
        int top = mScrollY + n * (mRowHeight + mVerticalSpacing);
        lt[0] = left;
        lt[1] = top;
        return lt;
    }

    private ArrayList<View> mChildren = new ArrayList<>();

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        refreshChildren();
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
        mDragedView = view;
        mDragIndex = indexOfChild(view);
        dispatchItemCaptured();
        return true;
    }

    private void dispatchItemCaptured() {
        if (mItemCapturedListener != null) {
            mItemCapturedListener.onItemCaptured(mDragedView);
        }
    }

    private void releaseItem() {
        int[] destation = getLeftAndTopForPosition(mDragPosition);
        int offsetX = destation[0] - mDragedView.getLeft();
        int offsetY = destation[1] - mDragedView.getTop();
        mDragedView.offsetLeftAndRight(offsetX);
        mDragedView.offsetTopAndBottom(offsetY);
        dispatchItemReleased();
    }

    private void dispatchItemReleased() {
        if (mItemCapturedListener != null) {
            mItemCapturedListener.onItemReleased(mDragedView);
        }
    }

    private OnItemCapturedListener mItemCapturedListener;

    public void setOnItemCapturedListener(OnItemCapturedListener listener) {
        mItemCapturedListener = listener;
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