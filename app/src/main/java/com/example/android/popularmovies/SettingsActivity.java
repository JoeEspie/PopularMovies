package com.example.android.popularmovies;

import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * Created by jespie on 9/29/2017.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }
}
