package com.swt.smartrss.app.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import com.swt.smartrss.app.R;

/**
 * This activity shows headers of preferences, each of which is associated with a PreferenceFragment to display the preferences of that header.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //replace an existing fragment that was added to a container
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    /**
     * Shows a hierarchy of Preference objects as lists
     */
    public static class PreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //inflates the given XML resource and adds the preference hierarchy to the current preference hierarchy
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
