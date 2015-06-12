package com.swt.smartrss.wear.spritz;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.swt.smartrss.wear.R;

import java.util.ArrayList;

/**
 * Class SpritzSectionsScrollView creates a ScrollView to seperate the Spritz text view and extras buttons
 * Created by Andrej on 09.06.2015.
 */
public class SpritzSectionsScrollView extends ScrollView {
    /**
     * Minimum swipe distance and swipe kspeed/velocity
     */
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

    /**
     * setFeatureItems inflates spritz views and implement them into the scrollview.
     * Views gets paramters for height and width to fit the whole display size.
     *
     * @param pItems ArrayList - typeless - list of items to inflate
     * @param width  width of the display
     * @param height height of the display
     */
    public void setFeatureItems(ArrayList pItems, int width, int height) {

        this.mItems = pItems;

        /**
         * Creating a linear layout to wrap inflated views
         */
        LinearLayout internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        internalWrapper.setOrientation(LinearLayout.VERTICAL);
        addView(internalWrapper);

        /**
         * Inlfating layouts and setting up sizes
         */
        LinearLayout spritzLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.spritz_layout, null);
        LinearLayout spritzExtrasLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.spritz_extras_layout, null);

        spritzLayout.setMinimumWidth(width);
        spritzLayout.setMinimumHeight(height);
        spritzExtrasLayout.setMinimumWidth(width);
        spritzExtrasLayout.setMinimumHeight(height);
        internalWrapper.addView(spritzLayout);
        internalWrapper.addView(spritzExtrasLayout);

        /**
         * final spritzerTextview to send pause function on swipe
         */
        final SpritzerTextView stv = (SpritzerTextView) spritzLayout.findViewById(R.id.spritzTV);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int feature = mActiveFeature;
                //If the user swipes
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int scrollY = getScrollY(); //Get scroll difference
                    int featureHeight = v.getMeasuredHeight(); //Get height of the view
                    /**
                     * scrollY gets an additive maximum of display size, featureHeight also gets a maximum of display size
                     * The sum of scrollY and (featureHeight / 2) is always between 2 layouts.
                     * Dividing the sum by featureHeight and rounding it mercantily returns the indexed active feature.
                     */
                    mActiveFeature = ((scrollY + (featureHeight / 2)) / featureHeight);
                    if (mActiveFeature > 0) {//Active feature is not the spritz text view (=stv)
                        stv.pause();
                    } else if ((mActiveFeature == 0) && (feature == 0)) { // active feature was stv and still is stv
                        stv.play();
                    }
                    feature = mActiveFeature;
                    int scrollTo = mActiveFeature * featureHeight; //Y-position to indexed feature

                    smoothScrollTo(0, scrollTo); //Smooth scrolling to determined feature
                    return true;
                } else {
                    if (feature == 0) { //Motion event changed nothing - just pressed the screen
                        stv.pause();
                    }
                    return false;
                }
            }
        });
        mGestureDetector = new GestureDetector(new SpritzGestureDetector(stv));
    }

    class SpritzGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private SpritzerTextView mSpritzTextView;

        public SpritzGestureDetector(SpritzerTextView view) {
            mSpritzTextView = view;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                /**
                 * Flinging / swiping fast from up to down
                 */
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE //Swipe distance is greater than determined minimum distance
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) { //Swipe speed is greater than determined velocity
                    int featureHeight = getMeasuredHeight();
                    mActiveFeature = (mActiveFeature < (mItems.size() - 1)) ? mActiveFeature + 1 : mItems.size() - 1;
                    smoothScrollTo(0, mActiveFeature * featureHeight);
                    mSpritzTextView.pause();
                    return true;

                }
                /**
                 * Flinging / swiping fast from down to up
                 */
                else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE //Same as up to down
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) { //Same as up to down
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

