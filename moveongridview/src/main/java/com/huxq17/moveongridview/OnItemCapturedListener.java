package com.huxq17.moveongridview;

import android.view.View;

public interface OnItemCapturedListener {
    /**
     * Called when user selected a view to drag.
     *
     * @param v
     */
    void onItemCaptured(View v);

    /**
     * Called when user released the drag view.
     *
     * @param v
     */
    void onItemReleased(View v);
}
