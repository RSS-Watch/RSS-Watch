package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.swt.smartrss.wear.R;

public class ReaderActivity extends Activity {

    private TextView mTextView;
    private Button mButton;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        final LayoutInflater inflater = getLayoutInflater();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub_reader);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                //inflater.inflate(R.layout.round_activity_reader,stub);
                mTextView = (TextView) stub.findViewById(R.id.textView4);
                mTextView.setText("Beispieltext");
                mScrollView = (ScrollView) stub.findViewById(R.id.scrollView);
                mButton = (Button) stub.findViewById(R.id.button4);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mScrollView.scrollTo(0, mScrollView.getHeight());
                    }
                });
            }
        });
    }
}
