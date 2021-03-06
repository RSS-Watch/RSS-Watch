package com.swt.smartrss.wear.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.swt.smartrss.wear.R;
import com.swt.smartrss.wear.spritz.SpritzSectionsScrollView;
import com.swt.smartrss.wear.spritz.SpritzerTextView;

import java.util.ArrayList;

/**
 * Activity to !spritz! a given String with a special SpritzTextView on the screen
 */
public class SpritzerActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();
    private static SpritzerTextView spritzTV;
    String articleId;
    private boolean isPlaying;
    private Intent intent;

    private void openArticleOnPhone() {
        Bundle bundle = new Bundle();
        bundle.putString("id", articleId);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spritzer);

        /**
         * Getting the intent for current activity
         */
        intent = getIntent();
        articleId = intent.getStringExtra(ReaderActivity.ID);

        /**
         * final Spritztextview for setting onClickListener
         */
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

                SpritzSectionsScrollView spritzScrollView = (SpritzSectionsScrollView) stub.findViewById(R.id.spritzScrollView);
                spritzScrollView.setHorizontalScrollBarEnabled(false);
                /**
                 * Getting display size for
                 */
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                /**
                 * Adding layouts to scrollview
                 */
                ArrayList aList = new ArrayList();
                aList.add("spritzerLayout");
                aList.add("spritzerExtrasLayout");

                LinearLayout spritzExtrasLayout = (LinearLayout) View.inflate(getApplicationContext(), R.layout.spritz_extras_layout, null);
                spritzExtrasLayout.setMinimumWidth(width);
                spritzExtrasLayout.setMinimumHeight(height);
                Button btnOpenArticle = (Button) spritzExtrasLayout.findViewById(R.id.btnOpenArticle);
                btnOpenArticle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openArticleOnPhone();
                        Log.d(TAG, "open article");
                    }
                });

                spritzScrollView.setFeatureItems(aList, width, height, spritzExtrasLayout);

                /**
                 * Setting up click listener events to spritzTextView
                 */
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

                /**
                 * Getting configuration via intent for spritzTextView
                 */
                spritzTV.setSpritzText(intent.getStringExtra(ReaderActivity.TEXT));
                spritzTV.setWpm(intent.getIntExtra(ReaderActivity.WPM, 350));
            }
        });
    }

    /**
     * onResume: called on resuming activity, starting to spritz the text
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (spritzTV != null) {
            spritzTV.play();
            isPlaying = true;
        }
    }

    /**
     * onPause: called pausing the activity, stopping to spritz the text
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (spritzTV != null) {
            spritzTV.pause();
            isPlaying = false;
        }
    }

    /**
     * onWindowFocusChanged: workaround for inflated views: called when everything is finished,
     * guarantees finished views and starting to spritz
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            spritzTV.play();
        }
    }
}
