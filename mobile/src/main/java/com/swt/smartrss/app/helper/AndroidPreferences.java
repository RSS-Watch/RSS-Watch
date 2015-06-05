package com.swt.smartrss.app.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.swt.smartrss.app.R;

/**
 * Created by Dropsoft on 01.06.2015.
 */
public class AndroidPreferences {
    private static String keyPrefsAccountString;
    private static String keyPrefsFeedlyToken;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    public AndroidPreferences(Context context) {
        keyPrefsAccountString = context.getString(R.string.preference_edit_account);
        keyPrefsFeedlyToken = "preference_token";
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.prefsEditor = sharedPreferences.edit();
    }

    public String getAccountName() {
        return sharedPreferences.getString(keyPrefsAccountString, "");
    }

    public void setAccountName(String accountName) {
        prefsEditor.putString(keyPrefsAccountString, accountName);
        prefsEditor.commit();
    }

    public String getFeedlyToken() {
        return sharedPreferences.getString(keyPrefsFeedlyToken, "");
    }

    public void setFeedlyToken(String feedlyToken) {
        prefsEditor.putString(keyPrefsFeedlyToken, feedlyToken);
        prefsEditor.commit();
    }
}

