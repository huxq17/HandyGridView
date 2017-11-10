package com.example.xiaolin.draggridview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.xiaolin.draggridview.sortgridview.DragAdapter;

import java.util.List;


public class GridViewAdapter2 extends DragAdapter {
    private List<String> list;
    private Context context;

    public GridViewAdapter2(Context context, List<String> strList) {
        this.context = context;
        this.list = strList;
    }
    @Override
    public void onDataModelMove(int from, int to) {
        String s = list.remove(from);
        list.add(to, s);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            FrameLayout frameLayout = new FrameLayout(context);
            convertView = frameLayout;
            textView = new TextView(context);
            frameLayout.setPadding(20, 20, 20, 20);
            textView.setPadding(20, 100, 20, 100);
            frameLayout.addView(textView);
            textView.setBackgroundColor(0x33ff00ff);
            textView.setGravity(Gravity.CENTER);
        } else {
            textView = (TextView) ((FrameLayout) convertView).getChildAt(0);
        }
        textView.setText(getItem(position));
        return convertView;
    }
}