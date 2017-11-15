package com.huxq17.moveongridview;


import android.annotation.TargetApi;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.HashMap;

public class WrappedApdater extends BaseAdapter {
    private BaseAdapter mRealApdater;
    private HashMap<View, Integer> mItems;

    WrappedApdater(BaseAdapter realApdater) {
        mRealApdater = realApdater;
        mItems = new HashMap<>();
    }

    @Override
    public int getCount() {
        return mRealApdater.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mRealApdater.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mRealApdater.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mRealApdater.getView(position, convertView, parent);
        mItems.put(view, position);
        return view;
    }

    public int getPositionForView(View view) {
        Integer position = mItems.get(view);
        if(position==null){
            Log.e("tag","getPositionForView view="+view);
        }
        return position == null ? AdapterView.INVALID_POSITION : position;
    }

    public void setPositionForView(View view, int position) {
        mItems.put(view, position);
    }

    @Override
    public boolean hasStableIds() {
        return mRealApdater.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mRealApdater.getItemViewType(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mRealApdater.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mRealApdater.unregisterDataSetObserver(observer);
    }

    @Override
    public void notifyDataSetInvalidated() {
        mRealApdater.notifyDataSetInvalidated();
    }

    @Override
    public void notifyDataSetChanged() {
        mRealApdater.notifyDataSetChanged();
    }

    @Override
    public boolean isEnabled(int position) {
        return mRealApdater.isEnabled(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mRealApdater.areAllItemsEnabled();
    }

    @Override
    public int getViewTypeCount() {
        return mRealApdater.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mRealApdater.isEmpty();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return mRealApdater.getDropDownView(position, convertView, parent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public CharSequence[] getAutofillOptions() {
        return mRealApdater.getAutofillOptions();
    }
}
