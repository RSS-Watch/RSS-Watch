package com.swt.smartrss.wear.spritz;

/**
 * Created by andrewgiang on 3/19/14.
 */
public interface DelayStrategy {

    int getStartDelay();

    /**
     * A delay strategy that will determine how long
     * the Thread sleeps after a word is being processed.
     *
     * @param word the word to be checked for a possible delay multiplier
     * @return int multiplier
     * @see {@link DefaultDelayStrategy}
     */
    int delayMultiplier(String word);

}
