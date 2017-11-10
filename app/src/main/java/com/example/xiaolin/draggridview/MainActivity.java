package com.example.xiaolin.draggridview;

import android.os.Bundle;
import android.widget.GridView;
import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private List<String> strList;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        gridView = (GridView)findViewById(R.id.drag_grid_view);
        GridViewAdapter adapter = new GridViewAdapter(this, strList);
        gridView.setAdapter(adapter);
    }
}
