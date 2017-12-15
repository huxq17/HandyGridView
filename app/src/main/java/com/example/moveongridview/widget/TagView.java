package com.example.moveongridview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.example.moveongridview.R;


public class TagView extends android.support.v7.widget.AppCompatTextView {
    private Drawable deleteIcon;
    private int iconWidth;
    private int iconHeight;
    private boolean showIcon = true;

    public TagView(Context context) {
        super(context);
//        int id = context.getResources().getIdentifier("ic_delete", "drawable", context.getPackageName());
        deleteIcon = context.getResources().getDrawable(R.drawable.ic_delete);
        Rect drawableRect = new Rect(0, 0, deleteIcon.getIntrinsicWidth(), deleteIcon.getIntrinsicHeight());
        iconWidth = drawableRect.width();
        iconHeight = drawableRect.height();
        deleteIcon.setBounds(drawableRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showIcon) {
            canvas.translate(-iconWidth / 2, -iconHeight / 2);
            deleteIcon.draw(canvas);
        }
    }

    public void showDeleteIcon(boolean show) {
        showIcon = show;
        invalidate();
    }
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        int widthSize = 100;
//        int heightSize = DensityUtil.dip2px(getContext(),40);
////
////        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
////        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.e("onmea","width="+getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec)+";height="+getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec));
//    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

    }
}
