package com.BHATT.scrollme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TextView;

/**
 * LoadingScreen.java
 *
 * First time app start up
 *
 * @author Amar Bhatt
 */
public class LoadingScreen extends Activity {

    private long SPLASH_TIME = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        //Check Internet Connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //activeNetwork may throw NullPointerException
        boolean isConnected = false;
        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnectedOrConnecting();
        }//end if
        if (!isConnected){
            noConnectionPopUp(); //Alert user that there is no network
        }//end if
        //We have a connection, start loading MainActivity
        //NOTE: in AndroidManifest, android:noHistory = "true", ends the splash screen, cannot go
        //back to it (VERY GOOD)
        else { //Start a thread to start main activity and slowly fade it in
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent main = new Intent(LoadingScreen.this,MainActivity.class);
                    startActivity(main);
                    overridePendingTransition(R.layout.splashin,R.layout.splashout);
                }//end run
            }, SPLASH_TIME);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, SPLASH_TIME);
        }//end else
    }//end onCreate

    /**
     * Alerts user that there is no network found
     */
    private void noConnectionPopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(this.getText(R.string.noConnectionPopUp_title).toString());
        TextView message = new TextView(this);
        message.setText(this.getText(R.string.noConnectionPopUp_message).toString());
        message.setGravity(Gravity.CENTER);
        helpBuilder.setView(message);
        //Ok closes application
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                    }//end onClick
                });
        //Make dialog visible to user
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }//end noConnectionPopUp

}//end LoadingScreen
