package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import com.swt.smartrss.wear.R;
import com.swt.smartrss.wear.adapters.ListAdapter;

public class MainActivity extends Activity implements WearableListView.ClickListener {

    private TextView mTextView;
    private WearableListView listView;
    private String testStrings[] = {"Affe", "Banane", "Chameleon", "Drogen", "Was passiert eigentlich, wenn ich hier einen laengeren String eintrage?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) findViewById(R.id.list);
                listView.setClickListener(new WearableListView.ClickListener() {
                    @Override
                    public void onClick(WearableListView.ViewHolder viewHolder) {

                    }

                    @Override
                    public void onTopEmptyRegionClick() {

                    }
                });

                listView.setAdapter(new ListAdapter(getApplicationContext(), testStrings));
            }
        });
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
