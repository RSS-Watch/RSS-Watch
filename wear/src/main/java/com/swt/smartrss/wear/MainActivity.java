package com.swt.smartrss.wear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

public class MainActivity extends Activity implements WearableListView.ClickListener {

    private TextView mTextView;
    private WearableListView listView;
    private String testStrings[] = {"Affe","Banane","Chameleon","Drogen","Was passiert eigentlich, wenn ich hier einen längeren String eintrage?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        listView = (WearableListView) findViewById(R.id.list);
        listView.setClickListener(this);

        listView.setAdapter(new ListAdapter(this, testStrings));

        /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });*/
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

    }

    @Override
    public void onTopEmptyRegionClick() {

    }
}
