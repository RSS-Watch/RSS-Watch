package com.swt.smartrss.wear.spritz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
public class SpritzSectionsScrollView extends HorizontalScrollView {
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

    public void setFeatureItems(ArrayList pItems) {

        this.mItems = pItems;

        LinearLayout internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        internalWrapper.setOrientation(LinearLayout.VERTICAL);
        addView(internalWrapper);

        LinearLayout spritzLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.spritz_layout, null);
        LinearLayout spritzExtrasLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.spritz_extras_layout, null);

        internalWrapper.addView(spritzLayout);
        internalWrapper.addView(spritzExtrasLayout);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //If the user swipes
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int scrollX = getScrollX();
                    int featureWidth = v.getMeasuredWidth();
                        mActiveFeature = ((scrollX + (featureWidth / 10)) / featureWidth);
                    int scrollTo = mActiveFeature * featureWidth;

                    smoothScrollTo(scrollTo, 0);
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
                //right to left
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = getMeasuredWidth();
                    mActiveFeature = (mActiveFeature < (mItems.size() - 1)) ? mActiveFeature + 1 : mItems.size() - 1;
                    smoothScrollTo(mActiveFeature * featureWidth, 0);
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int featureWidth = getMeasuredWidth();
                    mActiveFeature = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
                    smoothScrollTo(mActiveFeature * featureWidth, 0);
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }
}

