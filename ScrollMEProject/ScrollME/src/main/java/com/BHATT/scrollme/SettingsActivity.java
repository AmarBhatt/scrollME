package com.BHATT.scrollme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * SettingsActivity.java
 *
 * Manages Settings
 *
 * @author Amar Bhatt
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PRIMARY_VIEW = "primary_view_default";
    public static final String KEY_SECONDARY_VIEW = "secondary_view_default";
    public static final String KEY_FULLSCREEN_VIEW = "full_screen_view";
    public static final String KEY_NEW_PAGE_VIEW = "new_page_view";
    public static final String KEY_COLOR_VIEW = "color_view";
    public static final String KEY_URL_VIEW = "url_view";
    public static final String KEY_HELP_VIEW = "help_alert";
    private ListPreference PrimaryViewPreference;
    private ListPreference SecondaryViewPreference;
    private ListPreference ColorViewPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        PrimaryViewPreference = (ListPreference) getPreferenceScreen()
                .findPreference(KEY_PRIMARY_VIEW);
        SecondaryViewPreference = (ListPreference) getPreferenceScreen()
                .findPreference(KEY_SECONDARY_VIEW);
        ColorViewPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_COLOR_VIEW);
    }//end onCreate

    /**
     * Handles when a preference is changed
     * @param sharedPreferences preference
     * @param key changed preference
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PRIMARY_VIEW)) { //View 1
            Preference primaryView = findPreference(key);
            // Set summary to be the user-description for the selected value
            setDefaults(key, sharedPreferences.getString(key,""),getApplicationContext());
            Toast.makeText(
                    getBaseContext(), PrimaryViewPreference.getValue(), Toast.LENGTH_SHORT).show();
            primaryView.setSummary(sharedPreferences.getString(key, ""));
        }//end if
        else if (key.equals(KEY_SECONDARY_VIEW)) { //View 2
            Preference secondaryView = findPreference(key);
            // Set summary to be the user-description for the selected value
            setDefaults(key, sharedPreferences.getString(key,""),getApplicationContext());
            Toast.makeText(
                   getBaseContext(), SecondaryViewPreference.getValue(), Toast.LENGTH_SHORT).show();
            secondaryView.setSummary(sharedPreferences.getString(key, ""));
        }//end else if
        else if (key.equals(KEY_FULLSCREEN_VIEW)){ //Full screen
            Preference fullView = findPreference(key);
            if (sharedPreferences.getBoolean(KEY_FULLSCREEN_VIEW, true)){
                // Set summary to be the user-description for the selected value
                setDefaults(key, true, getApplicationContext());
                Toast.makeText(getBaseContext(),
                        this.getText(R.string.fullscreen_enabled).toString(),
                        Toast.LENGTH_SHORT).show();
                fullView.setSummary(this.getText(R.string.fullscreen_summaryOne).toString());

            }//end if
            else {
                // Set summary to be the user-description for the selected value
                setDefaults(key, false, getApplicationContext());
                Toast.makeText(getBaseContext(),
                        this.getText(R.string.fullscreen_disabled).toString(),
                        Toast.LENGTH_SHORT).show();
                fullView.setSummary(this.getText(R.string.fullscreen_summaryOne).toString());
            }//end else
        }//end else if
        else if (key.equals(KEY_COLOR_VIEW)) { //Background Color
            Preference colorView = findPreference(key);
            // Set summary to be the user-description for the selected value
            setDefaults(key, sharedPreferences.getString(key,""),getApplicationContext());
            Toast.makeText(
                    getBaseContext(), ColorViewPreference.getValue(), Toast.LENGTH_SHORT).show();
            colorView.setSummary(sharedPreferences.getString(key, ""));
        }//end else if
        else if (key.equals(KEY_URL_VIEW)){// URL show/hide
            Preference urlView = findPreference(key);
            if (sharedPreferences.getBoolean(KEY_URL_VIEW, false)){ //show
                setDefaults(key, true, getApplicationContext());
                Toast.makeText(getBaseContext(),
                        this.getText(R.string.url_view_enabled).toString(),
                        Toast.LENGTH_SHORT).show();
            }//end if
            else { //hide
                setDefaults(key, false, getApplicationContext());
                Toast.makeText(getBaseContext(),
                        this.getText(R.string.url_view_disabled).toString(),
                        Toast.LENGTH_SHORT).show();
            }//end else
        }//end else if
        else if (key.equals(KEY_NEW_PAGE_VIEW)){ //URL Load show/hide
            if (sharedPreferences.getBoolean(KEY_NEW_PAGE_VIEW, false)){ //hide
                // Set summary to be the user-description for the selected value
                setDefaults(key, true, getApplicationContext());
                Toast.makeText(getBaseContext(), this.getText(R.string.new_page_hidden).toString(),
                        Toast.LENGTH_SHORT).show();
            }//end if
            else { //show
                // Set summary to be the user-description for the selected value
                setDefaults(key, false, getApplicationContext());
                Toast.makeText(getBaseContext(), this.getText(R.string.new_page_shown).toString(),
                        Toast.LENGTH_SHORT).show();
            }//end else
        }//end else if
        else if (key.equals(KEY_HELP_VIEW)){ //URL Load show/hide
            if (sharedPreferences.getBoolean(KEY_HELP_VIEW, false)){ //hide
                // Set summary to be the user-description for the selected value
                setDefaults(key, true, getApplicationContext());
                Toast.makeText(getBaseContext(), this.getText(
                        R.string.help_alert_enable).toString(), Toast.LENGTH_SHORT).show();
            }//end if
            else { //show
                // Set summary to be the user-description for the selected value
                setDefaults(key, false, getApplicationContext());
                Toast.makeText(getBaseContext(), this.getText(
                        R.string.help_alert_disable).toString(), Toast.LENGTH_SHORT).show();
            }//end else
        }//end else if
    }//end onSharedPreferenceChanged

    /**
     * Set Defaults for Strings
     * @param key preference
     * @param value new value of preference (string)
     * @param context context of application
     */
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }//end setDefaults

    /**
     * Set Defaults for Strings
     * @param key preference
     * @param value new value of preference (boolean)
     * @param context context of application
     */
    public static void setDefaults(String key, Boolean value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }//end setDefaults

/////////////////////////////////////////ACTIVITY LIFE CYCLE////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        //Set Current Preferences
        PrimaryViewPreference.setSummary(PrimaryViewPreference.getEntry());
        SecondaryViewPreference.setSummary(SecondaryViewPreference.getEntry());
        ColorViewPreference.setSummary(ColorViewPreference.getEntry());
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }//end onResume

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }//end onPause

    @Override
    public void onBackPressed() {
        Toast.makeText(
                getApplicationContext(),
                this.getText(R.string.onBackPressed_settings).toString(),
                Toast.LENGTH_SHORT).show();
    }//end onBackPressed
}//end SettingsActivity