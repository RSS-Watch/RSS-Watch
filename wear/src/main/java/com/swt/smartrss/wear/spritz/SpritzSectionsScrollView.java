package com.swt.smartrss.wear.spritz;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.swt.smartrss.wear.R;

import java.util.ArrayList;

/**
 * Created by Andrej on 09.06.2015.
 */
public class SpritzSectionsScrollView extends ScrollView {
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 300;

    private ArrayList mItems = null;
    private GestureDetector mGestureDetector;
    private int mActiveFeature = 0;

    public SpritzSectionsScrollView(Context context) {
        super(context);
    }

    public SpritzSectionsScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpritzSectionsScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFeatureItems(ArrayList pItems, int width, int height) {

        this.mItems = pItems;

        LinearLayout internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        internalWrapper.setOrientation(LinearLayout.VERTICAL);
        addView(internalWrapper);

        LinearLayout spritzLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.spritz_layout, null);
        LinearLayout spritzExtrasLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.spritz_extras_layout, null);

        SpritzerTextView stv = (SpritzerTextView) spritzLayout.findViewById(R.id.spritzTV);

        spritzLayout.setMinimumWidth(width);
        spritzLayout.setMinimumHeight(height);

        spritzExtrasLayout.setMinimumWidth(width);
        spritzExtrasLayout.setMinimumHeight(height);

        internalWrapper.addView(spritzLayout);
        internalWrapper.addView(spritzExtrasLayout);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //If the user swipes
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int scrollY = getScrollY();
                    int featureHeight = v.getMeasuredHeight();
                    mActiveFeature = ((scrollY + (featureHeight / 2)) / featureHeight);
                    int scrollTo = mActiveFeature * featureHeight;

                    smoothScrollTo(0, scrollTo);
                    return true;
                } else {
                    return false;
                }
            }
        });
        mGestureDetector = new GestureDetector(new MyGestureDetector());
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //up to down
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureHeight = getMeasuredHeight();
                    mActiveFeature = (mActiveFeature < (mItems.size() - 1)) ? mActiveFeature + 1 : mItems.size() - 1;
                    smoothScrollTo(0, mActiveFeature * featureHeight);
                    return true;

                }
                //down to up
                else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureHeight = getMeasuredHeight();
                    mActiveFeature = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
                    smoothScrollTo(0, mActiveFeature * featureHeight);
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }
}

