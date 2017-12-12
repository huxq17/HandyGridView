package com.huxq17.moveongridview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridViewAdapter extends BaseAdapter implements OnItemMovedListener {
    private Context context;
    private List<String> mDatas;

    public GridViewAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.mDatas = dataList;
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
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            convertView = textView;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            textView.setPadding(20, 100, 20, 100);
//            textView.setBackgroundColor(0x33ff00ff);
            Drawable drawable = context.getResources().getDrawable(R.drawable.s_grid_item);
            textView.setBackgroundDrawable(drawable);
            textView.setGravity(Gravity.CENTER);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(getItem(position));
        return convertView;
    }

    @Override
    public void onItemMoved(int from, int to) {
        String s = mDatas.remove(from);
        mDatas.add(to, s);
    }

    @Override
    public boolean isFixed(int position) {
        if (position == 0) {
            return true;
        }
        return false;
    }
}