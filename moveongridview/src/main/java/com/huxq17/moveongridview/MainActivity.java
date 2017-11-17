package com.huxq17.moveongridview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MoveOnGridView mGridView;
    private List<String> strList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();

    }

    public void initData() {
        strList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            strList.add("ITEM " + i);
        }
    }

    private void initView() {
        mGridView = (MoveOnGridView) findViewById(R.id.id_gridview);
        GridViewAdapter adapter = new GridViewAdapter(this, strList);
        mGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mGridView.setOnItemCapturedListener(new OnItemCapturedListener() {
            @Override
            public void onItemCaptured(View v) {
                v.setScaleX(1.2f);
                v.setScaleY(1.2f);
                v.setAlpha(0.6f);
            }

            @Override
            public void onItemReleased(View v) {
                v.setScaleX(1f);
                v.setScaleY(1f);
                v.setAlpha(1f);
            }
        });
    }
}
