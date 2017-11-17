package com.huxq17.moveongridview;

public interface OnItemMovedListener {
    /**
     * Called when user moved the item of gridview.
     *
     * @param from item's original position
     * @param to   item's destination poisition
     */
    void onItemMoved(int from, int to);
}
