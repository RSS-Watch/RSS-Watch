package com.swt.smartrss.wear.spritz;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.swt.smartrss.wear.R;

/**
 * Customized by Andrej Sch�fer
 */
public class SpritzerTextView extends TextView implements View.OnClickListener {
    public static final String TAG = SpritzerTextView.class.getName();
    public static final int PAINT_WIDTH_DP = 4;          // thickness of spritz guide bars in dp
    private Spritzer mSpritzer;
    // For optimal drawing should be an even number
    private Paint mPaintGuides;
    private float mPaintWidthPx;
    private String mTestString;
    private boolean mDefaultClickListener = false;
    private int mAdditonalPadding;
    private OnClickControlListener mClickControlListener;

    public SpritzerTextView(Context context) {
        super(context);
        init();
    }

    public SpritzerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public SpritzerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setAdditionalPadding(attrs);
        final TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SpritzerTextView, 0, 0);
        try {
            mDefaultClickListener = a.getBoolean(R.styleable.SpritzerTextView_clickControls, false);
        } finally {
            a.recycle();
        }
        init();

    }

    private void setAdditionalPadding(AttributeSet attrs) {
        //check padding attributes
        int[] attributes = new int[]{android.R.attr.padding, android.R.attr.paddingTop,
                android.R.attr.paddingBottom};

        final TypedArray paddingArray = getContext().obtainStyledAttributes(attrs, attributes);
        try {
            final int padding = paddingArray.getDimensionPixelOffset(0, 0);
            final int paddingTop = paddingArray.getDimensionPixelOffset(1, 0);
            final int paddingBottom = paddingArray.getDimensionPixelOffset(2, 0);
            mAdditonalPadding = Math.max(padding, Math.max(paddingTop, paddingBottom));
            Log.w(TAG, "Additional Padding " + mAdditonalPadding);
        } finally {
            paddingArray.recycle();
        }
    }

    private void init() {
        int pivotPadding = getPivotPadding();
        setPadding(getPaddingLeft(), pivotPadding, getPaddingRight(), pivotPadding);
        mPaintWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PAINT_WIDTH_DP, getResources().getDisplayMetrics());
        mSpritzer = new Spritzer(this);
        mPaintGuides = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGuides.setColor(getCurrentTextColor());
        mPaintGuides.setStrokeWidth(mPaintWidthPx);
        mPaintGuides.setAlpha(128);
        if (mDefaultClickListener) {
            this.setOnClickListener(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Measurements for top & bottom guide line
        int beginTopX = 0;
        int endTopX = getMeasuredWidth();
        int topY = 0;

        int beginBottomX = 0;
        int endBottomX = getMeasuredWidth();
        int bottomY = getMeasuredHeight();
        // Paint the top guide and bottom guide bars
        canvas.drawLine(beginTopX, topY, endTopX, topY, mPaintGuides);
        canvas.drawLine(beginBottomX, bottomY, endBottomX, bottomY, mPaintGuides);

        // Measurements for pivot indicator
        float centerX = calculatePivotXOffset() + getPaddingLeft();
        final int pivotIndicatorLength = getPivotIndicatorLength();

        // Paint the pivot indicator
        canvas.drawLine(centerX, topY + (mPaintWidthPx / 2), centerX, topY + (mPaintWidthPx / 2) + pivotIndicatorLength, mPaintGuides); //line through center of circle
        canvas.drawLine(centerX, bottomY - (mPaintWidthPx / 2), centerX, bottomY - (mPaintWidthPx / 2) - pivotIndicatorLength, mPaintGuides);
    }

    private int getPivotPadding() {
        return getPivotIndicatorLength() * 2 + mAdditonalPadding;
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        int pivotPadding = getPivotPadding();
        setPadding(getPaddingLeft(), pivotPadding, getPaddingRight(), pivotPadding);

    }

    private int getPivotIndicatorLength() {

        return getPaint().getFontMetricsInt().bottom;
    }

    private float calculatePivotXOffset() {
        // Craft a test String of precise length
        // to reach pivot character
        if (mTestString == null) {
            // Spritzer requires monospace font so character is irrelevant
            mTestString = "a";
        }
        // Measure the rendered distance of CHARS_LEFT_OF_PIVOT chars
        // plus half the pivot character
        return (getPaint().measureText(mTestString, 0, 1) * (Spritzer.CHARS_LEFT_OF_PIVOT + .50f));
    }

    /**
     * Pass input text to spritzer object
     *
     * @param input
     */
    public void setSpritzText(String input) {
        mSpritzer.setText(input);
    }

    /**
     * Will play the spritz text that was set in setSpritzText
     */
    public void play() {
        mSpritzer.start();
    }

    public void pause() {
        mSpritzer.pause();
    }

    /**
     * This determines the words per minute the sprizter will read at
     *
     * @param wpm the number of words per minute
     */
    public void setWpm(int wpm) {
        mSpritzer.setWpm(wpm);
    }

    @Override
    public void onClick(View v) {
        if (mSpritzer.isPlaying()) {
            if (mClickControlListener != null) {
                mClickControlListener.onPause();
            }
            pause();
        } else {
            if (mClickControlListener != null) {
                mClickControlListener.onPlay();
            }
            play();
        }

    }

    /**
     * Interface definition for a callback to be invoked when the
     * clickControls are enabled and the view is clicked
     */
    public interface OnClickControlListener {
        /**
         * Called when the spritzer pauses upon click
         */
        void onPause();

        /**
         * Called when the spritzer plays upon clicked
         */
        void onPlay();
    }
}