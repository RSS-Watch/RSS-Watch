package com.swt.smartrss.wear.spritz;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.speedreading.api.SpeedReadingAPI;
import org.speedreading.api.WordORP;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;


/**
 * Spritzer parses a String into a Queue
 * of words, and displays them one-by-one
 * onto a TextView at a given WPM.
 * TODO REMOVE UNUSED METHODS!
 */
public class Spritzer {
    protected static final String TAG = "Spritzer";
    protected static final boolean VERBOSE = false;

    protected static final int MSG_PRINT_WORD = 1;

    protected static final int CHARS_LEFT_OF_PIVOT = 3;
    protected static final ForegroundColorSpan spanRed = new ForegroundColorSpan(Color.RED);
    protected ArrayDeque<WordORP> mWordQueue;        // The queue of words from mWordArrayList yet to be displayed
    protected SpeedReadingAPI speedReadingAPI;
    protected TextView mTarget;
    protected int mWPM;
    protected int mWordCount;
    protected String mInput;
    protected Handler mSpritzHandler;
    protected Object mPlayingSync = new Object();
    protected boolean mPlaying;
    protected boolean mFirstWordAfterStart;
    protected boolean mPlayingRequested;
    protected boolean mSpritzThreadStarted;

    protected int mCurWordIdx;
    private ProgressBar mProgressBar;
    private DelayStrategy mDelayStrategy;
    private OnCompletionListener mOnCompletionListener;

    public Spritzer(TextView target) {
        init();
        mTarget = target;
        mTarget.setTypeface(Typeface.MONOSPACE);
        mSpritzHandler = new SpritzHandler(this);
        speedReadingAPI = new SpeedReadingAPI();
    }

//    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
//        mOnCompletionListener = onCompletionListener;
//    }

    /**
     * Prepare to Spritz the given String input
     * <p/>
     * Call {@link #start()} to begin display
     *
     * @param input
     */
    public void setText(String input) {
        mInput = input;
        refillWordQueue();
        mWordCount = mWordQueue.size();
        setMaxProgress();
    }

    private void setMaxProgress() {
        if (mProgressBar != null) {
            mProgressBar.setMax(mWordCount);
        }
    }

    protected void init() {

        mDelayStrategy = new DefaultDelayStrategy();
        mWPM = 500;
        mPlaying = false;
        mPlayingRequested = false;
        mSpritzThreadStarted = false;
        mCurWordIdx = 0;
    }

//    public int getMinutesRemainingInQueue() {
//        if (mWordQueue.size() == 0) {
//            return 0;
//        }
//        return mWordQueue.size() / mWPM;
//    }

//    public int getWpm() {
//        return mWPM;
//    }

    /**
     * Set the target Word Per Minute rate.
     * Effective immediately.
     *
     * @param wpm
     */
    public void setWpm(int wpm) {
        mWPM = wpm;
    }

//    /**
//     * Swap the target TextView. Call this if your
//     * host Activity is Destroyed and Re-Created.
//     * Effective immediately.
//     *
//     * @param target
//     */
//    public void swapTextView(TextView target) {
//        mTarget = target;
//        if (!mPlaying) {
//            printLastWord();
//        }
//    }

    /**
     * Start displaying the String input
     * fed to {@link #setText(String)}
     */
    public void start() {
        if (mPlaying) {
            return;
        }
        if (mWordQueue.isEmpty()) {
            refillWordQueue();
        }

        mPlayingRequested = true;
        mFirstWordAfterStart = true;
        startTimerThread();
    }

    private int getInterWordDelay() {
        return 60000 / mWPM;
    }

    private void refillWordQueue() {
        updateProgress();
        mCurWordIdx = 0;
        mWordQueue = speedReadingAPI.convertToSpeedReadingText(mInput);
    }

    private void updateProgress() {
        if (mProgressBar != null) {
            mProgressBar.setProgress(mCurWordIdx);
        }
    }

    /**
     * Read the current head of mWordQueue and
     * submit the appropriate Messages to mSpritzHandler.
     * <p/>
     * Split long words y submitting the first segment of a word
     * and placing the second at the head of mWordQueue for processing
     * during the next cycle.
     * <p/>
     * Must be called on a background thread, as this method uses
     * {@link Thread#sleep(long)} to time pauses in display.
     *
     * @throws InterruptedException
     */
    protected void processNextWord() throws InterruptedException {
        if (!mWordQueue.isEmpty()) {
            WordORP wop = mWordQueue.remove();
            mCurWordIdx += 1;

            boolean firstWordInQueue = (mWordQueue.size() == mWordCount - 1);

            if (mFirstWordAfterStart && !firstWordInQueue) {
                mFirstWordAfterStart = false;
                final int delayMultiplier = mDelayStrategy.getStartDelay();
                final int startDelay = getInterWordDelay() * (mDelayStrategy != null ? delayMultiplier < 1 ? 1 : delayMultiplier : 1);
                Thread.sleep(startDelay);
            }

            mSpritzHandler.sendMessage(mSpritzHandler.obtainMessage(MSG_PRINT_WORD, wop.getWord()));

            // Removes spaces at the beginning of a word
            wop.setWord(wop.getWord().substring(3 - wop.getOrp()));

            final int delayMultiplier;

            if (firstWordInQueue)
                delayMultiplier = mDelayStrategy.getStartDelay();
            else
                delayMultiplier = mDelayStrategy.delayMultiplier(wop.getWord());

            //Do not allow multiplier that is less than 1
            final int wordDelay = getInterWordDelay() * (mDelayStrategy != null ? delayMultiplier < 1 ? 1 : delayMultiplier : 1);
            Thread.sleep(wordDelay);

        }
        updateProgress();
    }

//    private void printLastWord() {
//        if (mWordQueue != null) {
//            printWord(mWordQueue.getLast().getWord());
//        }
//    }

    /**
     * Applies the given String to this Spritzer's TextView,
     * padding the beginning if necessary to align the pivot character.
     * Styles the pivot character.
     *
     * @param word
     */
    private void printWord(String word) {

        Spannable spanRange = new SpannableString(word);
        spanRange.setSpan(spanRed, 3, 4, 0);
        mTarget.setText(spanRange, TextView.BufferType.SPANNABLE);

    }

    public void pause() {
        mPlayingRequested = false;
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    /**
     * Begin the background timer thread
     */
    private void startTimerThread() {
        synchronized (mPlayingSync) {
            if (!mSpritzThreadStarted) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (VERBOSE) {
                            Log.i(TAG, "Starting spritzThread with queue length " + mWordQueue.size());
                        }
                        mPlaying = true;
                        mSpritzThreadStarted = true;
                        while (mPlayingRequested) {
                            try {
                                processNextWord();
                                if (mWordQueue.isEmpty()) {
                                    if (VERBOSE) {
                                        Log.i(TAG, "Queue is empty after processNextWord. Pausing");
                                    }
                                    mTarget.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mOnCompletionListener != null) {
                                                mOnCompletionListener.onComplete();
                                            }
                                        }
                                    });
                                    mPlayingRequested = false;

                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                        if (VERBOSE)
                            Log.i(TAG, "Stopping spritzThread");
                        mPlaying = false;
                        mSpritzThreadStarted = false;

                    }
                }).start();
            }
        }
    }

//    public void attachProgressBar(ProgressBar bar) {
//        if (bar != null) {
//            mProgressBar = bar;
//        }
//    }

//    /**
//     * @param strategy @see{@link DelayStrategy#delayMultiplier(String) }
////     */
//    public void setDelayStrategy(DelayStrategy strategy) {
//        mDelayStrategy = strategy;
//
//    }


    public interface OnCompletionListener {

        void onComplete();

    }

    /**
     * A Handler intended for creation on the Main thread.
     * Messages are intended to be passed from a background
     * timing thread. This Handler communicates timing
     * thread events to the Main thread for UI update.
     */
    protected static class SpritzHandler extends Handler {
        private WeakReference<Spritzer> mWeakSpritzer;

        public SpritzHandler(Spritzer muxer) {
            mWeakSpritzer = new WeakReference<Spritzer>(muxer);
        }

        @Override
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;

            Spritzer spritzer = mWeakSpritzer.get();
            if (spritzer == null) {
                return;
            }

            switch (what) {
                case MSG_PRINT_WORD:
                    spritzer.printWord((String) obj);
                    break;
                default:
                    throw new RuntimeException("Unexpected msg what=" + what);
            }
        }

    }


}