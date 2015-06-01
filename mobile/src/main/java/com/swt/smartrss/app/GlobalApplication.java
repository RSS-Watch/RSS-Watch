package com.swt.smartrss.app;

import android.app.Application;
import android.content.Context;
import com.swt.smartrss.app.helper.StateManager;

/**
 * Created by Dropsoft on 01.06.2015.
 */
public class GlobalApplication extends Application {
    private Context mContext;
    private StateManager mStateManager;

    public StateManager getStateManager() {
        return mStateManager;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        mStateManager = new StateManager(this.getApplicationContext());
        super.onCreate();
    }
}
