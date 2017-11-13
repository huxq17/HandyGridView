package com.huxq17.moveongridview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridView;

public class MoveOnGridView extends GridView {
    public MoveOnGridView(Context context) {
        super(context);
    }

    public MoveOnGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int currDraggedPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
        Log.i("onTouch", "currDraggedPosition=" + currDraggedPosition);
        return super.onTouchEvent(ev);
    }
}
