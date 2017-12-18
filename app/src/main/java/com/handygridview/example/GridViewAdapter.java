package com.handygridview.example;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.handygridview.example.widget.TagView;
import com.huxq17.handygridview.scrollrunner.OnItemMovedListener;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter implements OnItemMovedListener, TagView.OnTagDeleteListener {
    private Context context;
    private List<String> mDatas = new ArrayList<>();

    public GridViewAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.mDatas.addAll(dataList);
    }

    private boolean inEditMode = false;

    public void setData(List<String> dataList) {
        this.mDatas.clear();
        this.mDatas.addAll(dataList);
        notifyDataSetChanged();
    }

    public void setInEditMode(boolean inEditMode) {
        this.inEditMode = inEditMode;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public String getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       TagView textView;
        if (convertView == null) {
            textView = new TagView(context);
            convertView = textView;
            textView.setMaxLines(1);
            textView.setHeight(DensityUtil.dip2px(context, 40));
            int id = context.getResources().getIdentifier("s_grid_item", "drawable", context.getPackageName());
            Drawable drawable = context.getResources().getDrawable(id);
            textView.setBackgroundDrawable(drawable);
            textView.setGravity(Gravity.CENTER);
        } else {
            textView = (TagView) convertView;
        }
        if (!isFixed(position)) {
            textView.showDeleteIcon(inEditMode);
        } else {
            textView.showDeleteIcon(false);
        }
        textView.setText(getItem(position));
        textView.setOnTagDeleteListener(position, this);
        return convertView;
    }

    @Override
    public void onItemMoved(int from, int to) {
        String s = mDatas.remove(from);
        mDatas.add(to, s);
    }

    @Override
    public boolean isFixed(int position) {
        //此时0位置为固定的，不可拖动
        if (position == 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onDelete(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }
}