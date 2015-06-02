package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import com.swt.smartrss.wear.R;
import com.swt.smartrss.wear.spritz.SpritzerTextView;

public class ReaderActivity extends Activity {

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

                Button spritzerButton = (Button) stub.findViewById(R.id.spritzerButton);

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

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.spritz_test);
        dialog.setTitle("Spritz-Test");

        SpritzerTextView text = (SpritzerTextView) dialog.findViewById(R.id.spritzTV);
        text.setSpritzText("This is just a test and an example text to test the spitz textview with the spritz text split");
        text.setWpm(250);
        text.play();

        Button dialogButton = (Button) dialog.findViewById(R.id.buttonClose);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
