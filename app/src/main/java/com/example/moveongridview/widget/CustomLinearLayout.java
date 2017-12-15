package com.example.moveongridview.widget;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.huxq17.moveongridview.MoveOnGridView;

public class CustomLinearLayout extends LinearLayout {
    public CustomLinearLayout(Context context) {
        super(context);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int mGridViewIndex = -1;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof MoveOnGridView) {
                MoveOnGridView moveOnGridView = (MoveOnGridView) child;
                if (moveOnGridView.isLongPressMode() || moveOnGridView.isTouchMode()) {
                    setChildrenDrawingOrderEnabled(true);
                    mGridViewIndex = i;
                }
                return;
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int index = i;
        if (mGridViewIndex != -1) {
            if (i == mGridViewIndex) {
                index = childCount - 1;
            } else if (i == childCount - 1) {
                index = mGridViewIndex;
            }
        }
        return index;
    }
}
