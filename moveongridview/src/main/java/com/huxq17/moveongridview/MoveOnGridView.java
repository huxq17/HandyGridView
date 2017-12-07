package com.huxq17.moveongridview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.huxq17.moveongridview.scrollrunner.ICarrier;
import com.huxq17.moveongridview.scrollrunner.ScrollRunner;
import com.huxq17.moveongridview.utils.ReflectUtil;
import com.huxq17.moveongridview.utils.SdkVerUtils;

public class MoveOnGridView extends GridView implements AdapterView.OnItemLongClickListener, ICarrier {
    private int mScrollY;
    private int mFirstTop, mFirstLeft;
    private int mFirstVisibleFirstItem = -1;
    private float mLastX, mLastY;
    private Children mChildren;
    private int mTouchSlop;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnScrollListener mOnScrollListener;
    private ScrollRunner mScrollRunner;
    private int mScrollSpeed = 500;

    public MoveOnGridView(Context context) {
        this(context, null);
    }

    public MoveOnGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    private void init(Context context) {
        mScrollRunner = new ScrollRunner(this);
        setChildrenDrawingOrderEnabled(true);
        super.setOnItemLongClickListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mChildren = new Children(this);
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount != 0) {
                    if (mFirstVisibleFirstItem == -1) {
                        mFirstVisibleFirstItem = firstVisibleItem;
                        onGridViewVisible();
                    } else {
                        int rowLine = firstVisibleItem / mColumnsNum;
                        View firstChild = getChildAt(0);
                        mFirstLeft = getListPaddingLeft();
                        mFirstTop = firstChild.getTop();
                        mColumnWidth = firstChild.getWidth();
                        mRowHeight = firstChild.getHeight();
                        int scrollY = mFirstTop - rowLine * (mVerticalSpacing + mRowHeight);
                        mScrollY = scrollY;
                        mFirstVisibleFirstItem = firstVisibleItem;
                    }
                    if (mOnScrollListener != null) {
                        mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                    }
                }
            }
        });
    }

    public void onGridViewVisible() {
//        mColumnsNum = getNumColumns();
    }

    private ListAdapter mAdapter;
    private OnItemMovedListener mItemMovedListener;

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        mHorizontalSpacing = horizontalSpacing;
    }

    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mColumnsNum = numColumns;
    }

    @Override
    public void setVerticalSpacing(int verticalSpacing) {
        super.setVerticalSpacing(verticalSpacing);
        mVerticalSpacing = verticalSpacing;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
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
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }

    private void refreshChildren() {
        int childCount = getChildCount();
        clearAllChildren();
        for (int i = 0; i < childCount; i++) {
            addChild(i, getChildAt(i));
        }
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {
        return super.dispatchDragEvent(event);
    }

    @Override
    protected void detachViewsFromParent(int start, int count) {
        super.detachViewsFromParent(start, count);
        if (start == 0) {
            for (int i = start; i < start + count; i++) {
                removeChild(0);
            }
        } else {
            start = mChildren.size() - 1;
            for (int i = start; i > start - count; i--) {
                removeChild(i);
            }
        }
    }

    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
        addChild(index, child);
    }

    @Override
    protected void detachAllViewsFromParent() {
        super.detachAllViewsFromParent();
        clearAllChildren();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        int index = indexOfChild(child);
        addChild(index, child);
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        removeChild(child);
    }

    private void clearAllChildren() {
        mChildren.clear();
    }

    private void addChild(int index, View view) {

        if (index < 0) {
            index = mChildren.size();
        }
        mChildren.add(index, view);
    }

    private boolean removeChild(View view) {
        boolean result = false;
        int childSize = mChildren.size();
        for (int i = 0; i < childSize; i++) {
            Child child = mChildren.get(i);
            if (child.view == view) {
                result = mChildren.remove(child);
                break;
            }
        }
        return result;
    }

    private void removeChild(int index) {
        mChildren.remove(index);
    }

    private View getChildFrom(int index) {
        Child child = mChildren.get(index);
        return child == null ? null : child.view;
    }

    private Rect mGridViewVisibleRect;
    private MotionEvent mCurrMotionEvent;
    private int mDeltaX, mDeltaY;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        mCurrMotionEvent = ev;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                log("down");
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                mDeltaX = (int) (ev.getRawX() - ev.getX());
                mDeltaY = (int) (ev.getRawY() - ev.getY());
//                Debug.startMethodTracing("MoveOnGridView");
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (ev.getRawX() - mLastX);
                int deltaY = (int) (ev.getRawY() - mLastY);
                if (mDragedView != null) {
                    if (Math.abs(deltaX) > mTouchSlop || Math.abs(deltaY) > mTouchSlop) {
                    }
                    mDragedView.offsetLeftAndRight(deltaX);
                    mDragedView.offsetTopAndBottom(deltaY);
                    mLastX = ev.getRawX();
                    mLastY = ev.getRawY();
                    swapItemIfNeed(ev);
                    scrollIfNeeded();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                log("up");
//                Debug.stopMethodTracing();
                if (mDragedView != null) {
                    releaseItem();
                    removeCallbacks(mScrollRunner);
                }
                mDragedView = null;
                mCurrMotionEvent = null;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * set the number of pixels scrolled per second
     *
     * @param scrollSpeed
     */
    public void setScrollSpeed(int scrollSpeed) {
        mScrollSpeed = scrollSpeed;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void scrollIfNeeded() {
        mDragedView.getGlobalVisibleRect(mDragedRect);
        getRect();
        if (mDragedRect.top <= mGridViewVisibleRect.top) {
            boolean canScrollDown = canScrollDown();
            if (canScrollDown && !mScrollRunner.isRunning()) {
                int deltaY = mScrollY;
                final int duration = Math.abs(deltaY) * 1000 / mScrollSpeed;
                mScrollRunner.start(0, deltaY, duration);
            }
        } else if (mDragedRect.bottom >= mGridViewVisibleRect.bottom) {
            boolean canScrollUp = canScrollUp();
            if (canScrollUp && !mScrollRunner.isRunning()) {
                int deltaY = getTotalScrollY() + mScrollY;
                final int duration = Math.abs(deltaY) * 1000 / mScrollSpeed;
                mScrollRunner.start(0, deltaY, duration);
            }
        } else {
            mScrollRunner.cancel();
        }
    }

    private void getRect() {
        if (mGridViewVisibleRect == null) {
            mGridViewVisibleRect = new Rect();
            getGlobalVisibleRect(mGridViewVisibleRect);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void scrollListBy(int deltaY) {
        if (SdkVerUtils.isAbove19()) {
            super.scrollListBy(deltaY);
        } else {
            ReflectUtil.invokeMethod(this, "trackMotionScroll", new Object[]{-deltaY, -deltaY}, new Class[]{int.class, int.class});
        }
    }

    @Override
    public void onMove(int lastX, int lastY, int curX, int curY) {
        int deltaY = curY - lastY;
        mDragedView.offsetTopAndBottom(deltaY);
        scrollListBy(deltaY);
        swapItemIfNeed(mCurrMotionEvent);
    }

    @Override
    public void onDone() {
    }

    /**
     * detact whether the content of gridview can scroll down.
     *
     * @return
     */
    public boolean canScrollDown() {
        if (mScrollY < 0) {
            return true;
        }
        return false;
    }

    /**
     * detact whether the content of gridview can scroll up.
     *
     * @return
     */
    public boolean canScrollUp() {
        if (getTotalScrollY() > -mScrollY) {
            return true;
        }
        return false;
    }

    public int getTotalScrollY() {
        int row = (mAdapter.getCount() - 1) / mColumnsNum + 1;
        int total = row * mRowHeight + (row - 1) * mVerticalSpacing;
        return total - getHeight();
    }

    private void swapItemIfNeed(MotionEvent ev) {
        if (ev == null || mDragedView == null) return;
        final int realX = (int) (ev.getRawX() - mDeltaX);
        final int realY = (int) (ev.getRawY() - mDeltaY);
        int currDraggedPosition = pointToPosition(realX, realY);
        if (currDraggedPosition != INVALID_POSITION) {
            int dragPosition = getDragPosition();
            if (currDraggedPosition != dragPosition) {
                getChildAt(currDraggedPosition - getFirstVisiblePosition()).getGlobalVisibleRect(mDragedRect);
                if (currDraggedPosition < dragPosition) {
                    for (int i = dragPosition - 1; i >= currDraggedPosition; i--) {
                        swapItem(i, i + 1);
                    }
                } else {
                    for (int i = dragPosition + 1; i <= currDraggedPosition; i++) {
                        swapItem(i, i - 1);
                    }
                }
                moveViewToPosition(currDraggedPosition, mDragedView);
                mDragPosition = currDraggedPosition;
            }
        }
    }

    private void swapItem(int from, int to) {
        int fromIndex = from - getFirstVisiblePosition();
        Child fromChild = mChildren.get(fromIndex);
        final View fromView = fromChild.view;
        if (fromChild == null || fromView == null) return;
        fromChild.moveTo(from, to);
        moveViewToPosition(to, fromView);
        dispatchItemMoved(from, to);
        removeViewInLayout(fromView);
        addViewInLayout(fromView, to - getFirstVisiblePosition(), fromView.getLayoutParams(), true);
    }

    private void dispatchItemMoved(int from, int to) {
        if (mItemMovedListener != null) {
            mItemMovedListener.onItemMoved(from, to);
        }
    }

    private void moveViewToPosition(int position, View view) {
        boolean removeResult = removeChild(view);
        addChild(getIndex(position), view);
    }

    private int getIndex(int position) {
        return position - mFirstVisibleFirstItem;
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

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        refreshChildren();
    }

    @Override
    public View getChildAt(int index) {
        int position = index;
        final int childCount = getChildCount();
        if (mDragedView != null) {
            int dragIndex = mDragPosition - mFirstVisibleFirstItem;
            if (dragIndex == 0) {
                if (index == 0) {
                    position = 1;
                } else if (position == 1) {
                    position = 0;
                } else {
//                position = position;
                }
            } else if (dragIndex == childCount - 1) {
                if (childCount % mColumnsNum != 1) {
                    if (index == childCount - 1) {
                        position = index - 1;
                    } else if (index == childCount - 2) {
                        position = childCount - 1;
                    }
                }
            }
        }
        if (position >= getChildCount()) {
            position = getChildCount() - 1;
        }
//        log("getChildAt position=" + position + ";index=" + index + ";getChildCount=" + getChildCount());
        return super.getChildAt(position);
    }

    @Override
    public int getChildCount() {
        return super.getChildCount();
    }


    public int pointToPosition(int x, int y) {
        int m = (x - mFirstLeft) / (mColumnWidth + mHorizontalSpacing);
        int n = (y - mFirstTop) / (mRowHeight + mVerticalSpacing);
        int right = mFirstLeft + m * (mColumnWidth + mHorizontalSpacing) + mColumnWidth;
        int bottom = mFirstTop + n * (mRowHeight + mVerticalSpacing) + mRowHeight;
        if (x > right || y > bottom) {
            return INVALID_POSITION;
        } else {
            int result = n * mColumnsNum + m + mFirstVisibleFirstItem;
            return result <= getLastVisiblePosition() ? result : INVALID_POSITION;
        }
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        view.getGlobalVisibleRect(mDragedRect);
        int top = mDragedRect.top;
        int left = mDragedRect.left;
//        int[] l1 = new int[2];
//        view.getLocationOnScreen(l1);
        mDragPosition = position;
        mDragedView = view;
        mDragIndex = indexOfChild(view);
        dispatchItemCaptured();
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(parent, view, position, id);
        }
        float motionX = mCurrMotionEvent.getRawX();
        float motionY = mCurrMotionEvent.getRawY();
        getRect();
        log("onItemLongClick mDragPosition=" + mDragPosition + ";motionX=" + motionX + ";motionY=" + motionY + ";mRowHeight=" + mRowHeight
                + ";mCurrMotionEvent.getRawY()=" + mCurrMotionEvent.getRawY() +
                ";mGridViewVisibleRect.left=" + mGridViewVisibleRect.left + ";mGridViewVisibleRect.top =" + mGridViewVisibleRect.top);
        int deltax = (int) (motionX - mGridViewVisibleRect.left - (mDragedView.getLeft() + mColumnWidth / 2));
        int deltaY = (int) (motionY - mGridViewVisibleRect.top - (mDragedView.getTop() + mRowHeight / 2));
        mDragedView.offsetLeftAndRight(deltax);
        mDragedView.offsetTopAndBottom(deltaY);
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

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mDragedView != null) {
            mDragIndex = indexOfChild(mDragedView);

            if (i == mDragIndex) {
                return childCount - 1;
            } else if (i == childCount - 1) {
                return mDragIndex;
            } else {
                return i;
            }
        }
        return i;
    }
}