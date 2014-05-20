package com.BHATT.scrollme;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

/**
 * AboutScreen.java
 *
 * Displays the Help/About section of Settings
 *
 * @author Amar Bhatt
 */

public class AboutScreen extends Activity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }//end if
    }//end onCreate

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Go back to main Activity
                onBackPressed();
                return true;
        }//end switch
        return super.onOptionsItemSelected(item);
    }//end onOptionsItemSelected
}//end AboutScreen
