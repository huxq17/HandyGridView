package com.example.xiaolin.draggridview;

import android.app.Activity;
import android.os.Bundle;

import com.example.xiaolin.draggridview.sortgridview.DragSortGridView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity2 extends Activity {
    private List<String> strList;
    private DragSortGridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initData();
        initView();
    }

    public void initData(){
        strList = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            strList.add("Channel " + i);
        }
    }

    private void initView() {
        gridView = (DragSortGridView)findViewById(R.id.drag_sort_grid_view);
        gridView.setDragModel(DragSortGridView.DRAG_BY_LONG_CLICK);
        GridViewAdapter2 adapter  = new GridViewAdapter2(this, strList);
        gridView.setAdapter(adapter);
    }
}
