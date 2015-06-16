package com.swt.smartrss.wear.spritz;

/**
 * Created by andrewgiang on 3/19/14.
 */
public class DefaultDelayStrategy implements DelayStrategy {

    @Override
    public int getStartDelay() {
        return 4;
    }

    @Override
    public int delayMultiplier(String word) {
        if (word.contains(":") || word.contains(";") || word.contains(".") || word.contains("?") || word.contains("!") || word.contains("\""))
            return 4;
        if (word.contains(","))
            return 3;
        if (word.length() >= 10)
            return 1;

        return 1;
    }
}
