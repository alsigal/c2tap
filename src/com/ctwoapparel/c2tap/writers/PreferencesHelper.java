package com.ctwoapparel.c2tap.writers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Helper class to store and retrieve user preferences for UI text fields.
 */
public class PreferencesHelper {

    private SharedPreferences m_sharedPreferences;
    private SharedPreferences.Editor m_editor;

    /**
     * @param context
     */
    public PreferencesHelper(Context context) {
        this.m_sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.m_editor = m_sharedPreferences.edit();
    }

    /**
     * @param key
     * @return the value of the key, or the empty string if one doesn't exist
     */
    public String getPreferences(String key) {
        return m_sharedPreferences.getString(key, "");
    }

    /**
     * @param key String
     * @param value String
     */
    public void savePreferences(String key, String value) {
        m_editor.putString(key, value);
        m_editor.commit();
    }
}
