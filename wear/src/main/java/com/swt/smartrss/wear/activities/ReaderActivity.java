package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.swt.smartrss.wear.R;

public class ReaderActivity extends Activity {
    public final static String TEXT = "com.swt.smartrss.wear.activities.TEXT";
    public final static String WPM = "com.swt.smartrss.wear.activities.WPM";
    public final static String ID = "com.swt.smartrss.wear.activities.ID";

    private TextView mTextView;
    private Button mButton;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        final LayoutInflater inflater = getLayoutInflater();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

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

    @Override
    protected void onStart() {
        super.onStart();

        final LayoutInflater inflater = getLayoutInflater();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                Button spritzerButton = (Button) stub.findViewById(R.id.spritz_test);

                spritzerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spritzerTest(ReaderActivity.this);
                    }
                });
            }
        });
    }

    protected void spritzerTest(Context context) {
        String text = "This is just a test and an example with a large woooooooooooooooooooooord text to test the spitz textview with the spritz text split function";
        int wpm = 300;

        Intent intent = new Intent(this, SpritzerActivity.class);
        intent.putExtra(TEXT, text);
        intent.putExtra(WPM, wpm);
        startActivity(intent);
    }
}
