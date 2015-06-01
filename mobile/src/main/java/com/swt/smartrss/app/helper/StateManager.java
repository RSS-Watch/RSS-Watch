package com.swt.smartrss.app.helper;

import android.content.Context;

/**
 * Created by Dropsoft on 01.06.2015.
 */
public class StateManager {
    private Context mContext;
    private AndroidPreferences androidPreferences;

    public StateManager(Context context) {
        this.mContext = context;
        androidPreferences = new AndroidPreferences(mContext);
    }

    public AndroidPreferences getAndroidPreferences() {
        return androidPreferences;
    }
}