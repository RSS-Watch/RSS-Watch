package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.swt.smartrss.wear.R;
import com.swt.smartrss.wear.spritz.SpritzSectionsScrollView;
import com.swt.smartrss.wear.spritz.SpritzerTextView;

import java.util.ArrayList;

/**
 * Activity to !spritz! a given String with a special SpritzTextView on the screen
 */
public class SpritzerActivity extends Activity {

    private static SpritzerTextView spritzTV;
    private LinearLayout linearLO;
    private LinearLayout internalWrapper;
    private LinearLayout spritzLayout;
    private LinearLayout spritzExtrasLayout;
    private boolean isPlaying;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spritzer);

        intent = getIntent();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                SpritzSectionsScrollView view = (SpritzSectionsScrollView) stub.findViewById(R.id.spritzScrollView);
                ArrayList aList = new ArrayList();
                aList.add("spritzerLayout");
                aList.add("spritzerExtrasLayout");

                view.setFeatureItems(aList);

                spritzTV = (SpritzerTextView) stub.findViewById(R.id.spritzTV);
                spritzTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isPlaying) {
                            spritzTV.pause();
                            isPlaying = false;
                        } else {
                            spritzTV.play();
                            isPlaying = true;
                        }
                    }
                });

                spritzTV.setSpritzText(intent.getStringExtra(ReaderActivity.TEXT));
                spritzTV.setWpm(intent.getIntExtra(ReaderActivity.WPM, 350));

                linearLO = (LinearLayout) stub.findViewById(R.id.spritzLinearLO);
                if (linearLO != null) {
                    linearLO.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            if (isPlaying) {
                                spritzTV.pause();
                                isPlaying = false;
                            } else {
                                spritzTV.play();
                                isPlaying = true;
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (spritzTV != null) {
            spritzTV.play();
            isPlaying = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (spritzTV != null) {
            spritzTV.pause();
            isPlaying = false;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            spritzTV.play();
        }
    }
}
