package com.BHATT.scrollme;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.HashMap;

/**
 * MainActivity.java
 *
 * Main Activity of scrollME
 *
 * @author Amar Bhatt
 */
public class MainActivity extends Activity{

//////////////////////////////////////////////////VARIABLES/////////////////////////////////////////
    //Dictionaries and Values
    /**
     * Dictionary and Values for social media
     */
    public HashMap <String,String> socialMediaDictionary;
    public HashMap <String, Integer> mediaValueDictionary;
    public HashMap <String, Integer> colorValueDictionary;
    public int[] socialMediaIcons;
    //Webview url and social media
    public String viewPrimaryCurrent;
    public String viewSecondaryCurrent;
    public int currentMediaSelectedValueView1;
    public int currentMediaSelectedValueView2;
    public int navButton;
    public boolean navOptions;
    //Settings
    public boolean fullscreen;
    public boolean urlview;
    public boolean new_web_page;
    public boolean help_alert;
    //Full Screen Video
    private MyChromeClient mWebChromeClient = new MyChromeClient();
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    //Picture Upload
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE=1;

///////////////////////////////////////////ACTIVITY STARTUP/////////////////////////////////////////

    /**
     * This is for uploading pictures
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if(requestCode==FILECHOOSER_RESULTCODE){
            if (null == mUploadMessage) return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }//end if
    }//end onActivityResult

    /**
     * Creates main activity
     * @param savedInstanceState
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set Social Media Dictionaries and icons
        setSocialMediaDictionary();
        iconValues();
        colorValues();
        //Load Settings
        setSettings(0);
        if (savedInstanceState == null) {//First Start
            setSettings(1);
            if (help_alert){ //help dialog
                helpPopUp();
            }//end if
        }//end if
        //Show MainActivity
        if (fullscreen){ //One View
            setContentView(R.layout.fullscreen_option);
        }//end if
        else { //Two Views
            setContentView(R.layout.activity_main);
        }//end else
        //Set Background Color
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Resources r = getResources();
        String[] color = r.getStringArray(R.array.color_toggle);
        findViewById(R.id.activity_main).setBackgroundColor(
                colorValueDictionary.get(pref.getString("color_view", color[0]))); //Default Black
        //Set Initial Views
        WebView primary = (WebView) findViewById(R.id.View1);
        primary.setWebViewClient(new MyWebViewClient());
        primary.setWebChromeClient(new MyChromeClient());
        WebSettings webSettingsPrimary = primary.getSettings();
        webSettingsPrimary.setJavaScriptEnabled(true);
        webSettingsPrimary.setDomStorageEnabled(true);
        webSettingsPrimary.setPluginState(WebSettings.PluginState.ON);
        webSettingsPrimary.setBuiltInZoomControls(true);
        webSettingsPrimary.setGeolocationEnabled(true);
        webSettingsPrimary.setAllowFileAccess(true);
        webSettingsPrimary.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //Load URL
        primary.loadUrl(viewPrimaryCurrent);
        if (!fullscreen){
            WebView secondary = (WebView) findViewById(R.id.View2);
            secondary.setWebViewClient(new MyWebViewClient());
            secondary.setWebChromeClient(new MyChromeClient());
            WebSettings webSettingsSecondary = secondary.getSettings();
            webSettingsSecondary.setJavaScriptEnabled(true);
            webSettingsSecondary.setDomStorageEnabled(true);
            webSettingsSecondary.setPluginState(WebSettings.PluginState.ON);
            webSettingsSecondary.setBuiltInZoomControls(true);
            webSettingsPrimary.setGeolocationEnabled(true);
            webSettingsPrimary.setAllowFileAccess(true);
            webSettingsSecondary.setCacheMode(WebSettings.LOAD_NO_CACHE);
            //Load URL
            secondary.loadUrl(socialMediaDictionary.get(viewSecondaryCurrent));
        }//end if
        //Set Social Media Selectors (Spinners)
        createMediaSelector();
    }//end onCreate



////////////////////////////////////////////////UI SET-UP///////////////////////////////////////////

    /**
     * Sets Social Media Spinners
     */
    public void createMediaSelector (){
        //First View
        Spinner spinner1 = (Spinner) findViewById(R.id.media1);
        spinner1.setOnItemSelectedListener(new SocialMediaSelection());
        spinner1.setSelection(currentMediaSelectedValueView1);
        if (!fullscreen){
            //Second View
            Spinner spinner2 = (Spinner) findViewById(R.id.media2);
            spinner2.setOnItemSelectedListener(new SocialMediaSelection());
            spinner2.setSelection(currentMediaSelectedValueView2);
        }//end if
    }//end createMediaSelector

    /**
     * Handles spinner option selection
     */
    private class SocialMediaSelection implements AdapterView.OnItemSelectedListener {
       public void onItemSelected(AdapterView<?> parent, View view,
                                  int pos, long id) {
           int selected = parent.getId();
           String item = (String)parent.getItemAtPosition(pos);
           String targetSite = socialMediaDictionary.get(item);
           if (selected == findViewById(R.id.media1).getId()){
               currentMediaSelectedValueView1 = mediaValueDictionary.get(item);
               setWebViews(1,targetSite);
           }//end if
           else {
               currentMediaSelectedValueView2 = mediaValueDictionary.get(item);
               setWebViews(2,targetSite);
           }//end else
       }//end onItemSelected
       public void onNothingSelected(AdapterView<?> parent) {
           //Do Nothing
       }//end onNothingSelected
   }//end SocialMediaSelection

    /**
     * Sets Webview(s)
     * @param selector view being altered
     * @param targetSite social media selected
     */
    public void setWebViews (int selector, String targetSite) {
        if (selector == 1){
            WebView primary = (WebView) findViewById(R.id.View1);
            primary.clearView(); //reset webview
            primary.loadUrl(targetSite);
            ImageButton current = (ImageButton)findViewById(R.id.viewNav1);
            //Load icon
            current.setBackgroundResource(socialMediaIcons[currentMediaSelectedValueView1]);
            primary.clearHistory();
        }//end if
        else { //view 2
            WebView secondary = (WebView) findViewById(R.id.View2);
            secondary.clearView(); //reset webview
            secondary.loadUrl(targetSite);
            ImageButton current = (ImageButton)findViewById(R.id.viewNav2);
            //Load icon
            current.setBackgroundResource(socialMediaIcons[currentMediaSelectedValueView2]);
            secondary.clearHistory();
        }//end else
    }//end setWebViews

    /**
     * Loads Settings into appropriate variables
     * @param i 1 if application starts cold
     */
    public void setSettings(int i){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        fullscreen = pref.getBoolean("full_screen_view", false); //Default two views
        urlview = pref.getBoolean("url_view", false); //Default do not show
        new_web_page = pref.getBoolean("new_page_view", false); //Default show
        help_alert = pref.getBoolean("help_alert", true); //Default show
        if (i == 1){
            String primary = pref.getString("primary_view_default", "Facebook");//Default Facebook
            String secondary = pref.getString("secondary_view_default", "Twitter");//Default Twitter
            viewPrimaryCurrent = socialMediaDictionary.get(primary);
            currentMediaSelectedValueView1 = mediaValueDictionary.get(primary);
            viewSecondaryCurrent = socialMediaDictionary.get(secondary);
            currentMediaSelectedValueView2 = mediaValueDictionary.get(secondary);
        }//end if
    }//end setSettings

///////////////////////////////////////////////WEBPAGE MANAGEMENT///////////////////////////////////

    /**
     * Handles non-UI management of webviews
     */
    private class MyWebViewClient extends WebViewClient {
        /**
         * Url Manager
         * @param view webview being altered
         * @param url website requesting to be opened
         * @return if url is overridden
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (urlview){ //shows url being loaded if checked in settings
                Toast.makeText(getBaseContext(), url, Toast.LENGTH_SHORT).show();
            }//end if
            if (R.id.View1 == view.getId()){
                if(!checkUrlSupplement(currentMediaSelectedValueView1, url)){ //checks url
                    viewPrimaryCurrent = url;
                    return false;
                }//end if
                //Sends webpage back if the url is not to be loaded
                else {
                    pressedRefreshButton(1);
                }//end else
            }//end if
            else {// View 2
                if (!checkUrlSupplement(currentMediaSelectedValueView2, url)){ //checks url
                    viewSecondaryCurrent = url;
                    return false;
                }//end if
                //Sends webpage back if the url is not to be loaded
                else {
                    pressedRefreshButton(2);
                }//end else
            }//end else
            //Open URL in new browser
            //If hide dialog is checked in settings, leave activity without notice
            if (new_web_page){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }//end if
            else {// ask user to leave activity or stay
                newWebPagePopUp(url);
                return true;
            }//end else
        }//end shouldOverrideUrlLoading

        @Override
        public void onReceivedError(
                WebView view, int errorCode, String description, String failingUrl) {
            webpageFailPopUp(); //Notifies user of a webpage load failure
        }//end onReceivedError
    }//end MyWebViewClient

    /**
     * Handles UI of webviews
     */
    private class MyChromeClient extends WebChromeClient {
        //FrameLayout.LayoutParams LayoutParameters =
        // new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            //    FrameLayout.LayoutParams.MATCH_PARENT);

        /**
         * onShowCustomView and onHideCustomView manages full screen video playback
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            FrameLayout.LayoutParams LayoutParameters =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            // if a full screen view exists then end it
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }//end if
            //Set up full screen view
            mContentView = (RelativeLayout) findViewById(R.id.activity_main);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(MainActivity.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            //Show full screen view
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
        }//end onShowCustomView

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }//end if
            else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show main activity
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
                clearWebViews();
            }//end else
        }//end onHideCustomView

        /**
         * Allows for social media to pinpoint location if user desires
         */
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // callback.invoke(String origin, boolean allow, boolean remember);
            callback.invoke(origin, true, false);
        }//end onGeolocationPermissionShowPrompt

        /**
         * openFileChooser is a hidden method, this handles uploading pictures in this activity
         * For multiple APIs
         */
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);
        }//end openFileChooser
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i,"File Chooser"), FILECHOOSER_RESULTCODE);
        }//end openFileChooser
    }//end MyChromeClient

    /**
     * Checks to see if destination url is apart of the current social media selected
     * @param selection social media value
     * @param url destination url
     * @return boolean False if it is apart of social media, true if it is not
     */
    public boolean checkUrlSupplement(int selection, String url){
        switch (selection){
            case 0: //Facebook
                if ((Uri.parse(url).getHost().equals("touch.facebook.com")) ||
                        (Uri.parse(url).getHost().equals("m.facebook.com")))  {
                    return false;
                }//end if
                else {
                    return true;
        }//end else
            case 1: //Twitter
                if ((Uri.parse(url).getHost().equals("mobile.twitter.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 2: //LinkedIn
                if ((Uri.parse(url).getHost().equals("touch.www.linkedin.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 3: //Instagram
                if ((Uri.parse(url).getHost().equals("instagram.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 4: //Youtube
                if ((Uri.parse(url).getHost().equals("www.youtube.com")) ||
                        (Uri.parse(url).getHost().equals("m.youtube.com"))||
                        (Uri.parse(url).getHost().equals("accounts.google.com")) ||
                        (Uri.parse(url).getHost().equals("accounts.youtube.com")))  {
                    /**
                     Uri uri = Uri.parse(url);
                     String vid = uri.getQueryParameter("v");
                     if (vid != null){
                     startYoutube(vid);
                     }//end if
                     **/
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 5: //Google+
                if ((Uri.parse(url).getHost().equals("plus.google.com")) ||
                        (Uri.parse(url).getHost().equals("accounts.google.com")) ||
                        (Uri.parse(url).getHost().equals("accounts.youtube.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 6: //Tumblr
                if ((Uri.parse(url).getHost().equals("www.tumblr.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else

            case 7: //Reddit
                if ((Uri.parse(url).getHost().equals("www.reddit.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 8: //Pinterest
                if ((Uri.parse(url).getHost().equals("m.pinterest.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            case 9: //ESPN
                if ((Uri.parse(url).getHost().equals("m.espn.com")) ||
                        (Uri.parse(url).getHost().equals("m.espn.go.com")) ||
                        (Uri.parse(url).getHost().equals("scores.espn.go.com")) ||
                        (Uri.parse(url).getHost().equals("espn.go.com")) ||
                        (Uri.parse(url).getHost().equals("insider.espn.go.com")))  {
                    return false;
                }//end if
                else {
                    return true;
                }//end else
            default:
                return true;
        }//end switch
    }//end checkUrlSupplement

    /**
     * Clears Webview(s), resets them
     */
    public void clearWebViews (){
        WebView view1 = ((WebView)findViewById(R.id.View1));
        view1.setWebChromeClient(new WebChromeClient());
        view1.clearCache(true);
        if (!fullscreen){
            WebView view2 = ((WebView)findViewById(R.id.View2));
            view2.setWebChromeClient(new WebChromeClient());
        }//end if
    }//end clearWebViews

//////////////////////////////////////////////DICTIONARIES AND VALUES///////////////////////////////

    /**
     * Sets socialMediaDictionary and mediaValueDictionary
     */
    public void setSocialMediaDictionary (){
        HashMap<String,String> social = new HashMap<String,String>();
        social.put("Facebook", "https://touch.facebook.com");
        social.put("Twitter", "https://m.twitter.com");
        social.put("LinkedIn", "https://touch.www.linkedin.com/");
        social.put("Instagram", "https://instagram.com/accounts/login/#");
        social.put("Youtube", "https://m.youtube.com/"); //?nomobile=1
        //social.put("Google+", "https://accounts.google.com/ServiceLogin?service=oz&
        // passive=1209600&continue=https://plus.google.com/app/basic/stream?source%3
        // Dapppromo%26gpsrc%3Dgpmobile:android%26partnerid%3Dgpmobile:android&btmpl=
        // mobile_tier2&ltmpl=tier2gplus");
        social.put("Google+","https://plus.google.com/app/basic/stream");
        social.put("Tumblr", "https://www.tumblr.com");
        social.put("Reddit", "http://www.reddit.com");
        social.put("Pinterest", "https://m.pinterest.com");
        social.put("ESPN", "https://m.espn.go.com/wireless/");
        socialMediaDictionary = social;
        HashMap<String, Integer> value = new HashMap<String, Integer>();
        value.put("Facebook", 0);
        value.put("Twitter", 1);
        value.put("LinkedIn", 2);
        value.put("Instagram", 3);
        value.put("Youtube", 4);
        value.put("Google+", 5);
        value.put("Tumblr", 6);
        value.put("Reddit", 7);
        value.put("Pinterest", 8);
        value.put("ESPN", 9);
        mediaValueDictionary = value;
    }//end setSocialMediaDictionary

    /**
     * Sets colorValue dictionary for Settings
     * @return dictionary of colorValue
     */
    private void colorValues(){
        HashMap <String, Integer> colors = new HashMap<String, Integer>();
        Resources r = getResources();
        String[] color = r.getStringArray(R.array.color_toggle);
        colors.put(color[0], Color.BLACK);
        colors.put(color[1], Color.BLUE);
        colors.put(color[2], Color.RED);
        colors.put(color[3], Color.GREEN);
        colors.put(color[4], Color.YELLOW);
        colors.put(color[5], Color.MAGENTA);
        colors.put(color[6], Color.WHITE);
        colors.put(color[7],Color.LTGRAY);
        colors.put(color[8], Color.DKGRAY);
        colors.put(color[9], Color.CYAN);
        colorValueDictionary = colors;
    }//end themeValues

    /**
     * Sets Icons
     */
    public void iconValues(){
        Resources r = getResources();
        socialMediaIcons = r.getIntArray(R.array.social_media_icons);
    }//end iconValues

///////////////////////////////////////////OPTIONS& SETTINGS///////////////////////////////////////

    /**
     * Opens proper Menu depending on button pressed
     * @param menu
     * @return
     */
    @Override
   public boolean onPrepareOptionsMenu(Menu menu){
       menu.clear();
       MenuInflater inflater = getMenuInflater();
       //Navigation button pressed
        if (navOptions){
           navOptions = false;
           inflater.inflate(R.menu.navigation, menu); //Open Navigation Menu
       }//end if
       //Regular menu hardware or software button pressed
       else {
           inflater.inflate(R.menu.main, menu); //Open Traditional Menu
       }//end else
      return super .onPrepareOptionsMenu(menu);
   }//end onPrepareOptionsMenu

    /**
     * This is called when a Navigation Button is pressed
     * @param view the navigation button pressed
     */
   public void navigationMenu(View view){
       navOptions = true; //navigation options will be opened
       //Find which navigation button was pressed
       if(view.getId() == R.id.viewNav1){
           navButton = 1; //view 1
       }//end if
       else{
           navButton = 2; //view 2
       }//end else
       openOptionsMenu(); //open navigation menu
   }//end navigationMenu

    /**
     * This is called when software Menu Button is pressed
     * @param view the toggle button
     */
    public void ShowOptionsMenu(View view){
        openOptionsMenu();
    }//end ShowOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Settings();
                return true;
            case R.id.action_refresh:
                Refresh();
                return true;
            case R.id.action_about:
                aboutScreen();
                return true;
            case R.id.nav_back:
                pressedBackButton(navButton);
                return true;
            case R.id.nav_home:
                pressedHomeButton(navButton);
                return true;
            case R.id.nav_forward:
                pressedForwardButton(navButton);
                return true;
            case R.id.nav_refresh:
                pressedRefreshButton(navButton);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }//end switch
    }//end onOptionsItemSelected

    /**
     * Make webpage go backward in history
     * @param view view being altered
     */
    public void pressedBackButton(int view){
        if (view == 1){
            WebView current = (WebView)findViewById(R.id.View1);
            if (current.canGoBack()){
                current.goBack();
                //Makes webview only go back within its specified social media
                if (!checkUrlSupplement(currentMediaSelectedValueView1, current.getUrl())){
                    viewPrimaryCurrent = current.getUrl();
                }//end if
                else{
                    current.goForward();
                }//end else
            }//end if
        }//end if
        else{
            WebView current = (WebView)findViewById(R.id.View2);
            if (current.canGoBack()){
                current.goBack();
                //Makes webview only go back within its specified social media
                if (!checkUrlSupplement(currentMediaSelectedValueView2, current.getUrl())){
                    viewSecondaryCurrent = current.getUrl();
                }//end if
                else{
                    current.goForward();
                }//end else
            }//end if
        }//end else
    }//end pressedBackButton

    /**
     * Make webpage go forward in history
     * @param view view being altered
     */
    public void pressedForwardButton(int view){
        if (view == 1){
            WebView current = (WebView)findViewById(R.id.View1);
            if (current.canGoForward()){
                current.goForward();
                viewPrimaryCurrent = current.getUrl();
            }//end if
        }//end if
        else{
            WebView current = (WebView)findViewById(R.id.View2);
            if (current.canGoForward()){
                current.goForward();
                viewSecondaryCurrent = current.getUrl();
            }//end if
        }//end else
    }//end pressedForwardButton

    /**
     * Make webpage go home (home page of social site)
     * @param view view being altered
     */
    public void pressedHomeButton(int view){
        //Note: https:// lets users open this in most social media blocked networks
        if (view == 1){
            WebView current = (WebView)findViewById(R.id.View1);
            if (currentMediaSelectedValueView1 == 7){ //if Reddit
                current.loadUrl("http://" + Uri.parse(current.getUrl()).getHost());
                viewPrimaryCurrent = current.getUrl();
            }//end if
            else{
                current.loadUrl("https://" + Uri.parse(current.getUrl()).getHost());
                viewPrimaryCurrent = current.getUrl();
            }//end else
        }//end if
        else{
            WebView current = (WebView)findViewById(R.id.View2);
            if (currentMediaSelectedValueView2 == 7){ //if Reddit
                current.loadUrl("http://" + Uri.parse(current.getUrl()).getHost());
                viewPrimaryCurrent = current.getUrl();
            }//end if
            else{
                current.loadUrl("https://" + Uri.parse(current.getUrl()).getHost());
                viewSecondaryCurrent = current.getUrl();
            }//end else
        }//end else
    }//end pressedHomeButton

    /**
     * Refreshes specific view
     * @param view view being altered
     */
    public void pressedRefreshButton(int view){
        if (view == 1){
            WebView current = (WebView)findViewById(R.id.View1);
            current.reload();
        }//end if
        else{
            WebView current = (WebView)findViewById(R.id.View2);
            current.reload();
        }//end else
    }//end pressedRefreshedButton

    /**
     * Opens settings activity
     */
    public void Settings() {
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }//end Settings

    /**
     * Refreshes all views in main activity
     */
    public void Refresh(){
        ((WebView)findViewById(R.id.View1)).reload();
        if (!fullscreen) {
            ((WebView)findViewById(R.id.View2)).reload();
        }//end if
    }//end Refresh

    /**
     * Opens about screen activity
     */
    public void aboutScreen() {
        Intent about = new Intent(this,AboutScreen.class);
        startActivity(about);
    }//end aboutScreen
    /**
    private void startYoutube(String videoID) { // default youtube app
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
            List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                                                              i, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() == 0) {
            // default youtube app not present or doesn't conform to the standard we know
            // use our own activity
                //i = new Intent(getApplicationContext(), YouTube.class);
                //i.putExtra("VIDEO_ID", videoID);
            }
            startActivity(i);
    }//end startVideo
    **/
    /////////////////////////////////////////////SAVE STATES///////////////////////////////////////


    /**
     * Currently not used
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("primaryCurrent", viewPrimaryCurrent);
        outState.putInt("selectionPrimaryCurrent", currentMediaSelectedValueView1);
        if (!fullscreen){
            outState.putString("secondaryCurrent", viewSecondaryCurrent);
            outState.putInt("selectionSecondaryCurrent", currentMediaSelectedValueView2);
        }//end if
        super.onSaveInstanceState(outState);
    }//end onSaveInstanceState

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPrimaryCurrent = savedInstanceState.getString("primaryCurrent");
        currentMediaSelectedValueView1 = savedInstanceState.getInt("selectionPrimaryCurrent");
        if(!fullscreen){
            currentMediaSelectedValueView2 = savedInstanceState.getInt("selectionSecondaryCurrent");
            viewSecondaryCurrent = savedInstanceState.getString("secondaryCurrent");
        }//end if
    }//end onRestoreInstanceState

    ////////////////////////////////////////ACTIVITY LIFE CYCLE////////////////////////////////////

    @Override
    public void onBackPressed() {
        if (mCustomViewContainer != null){ //ends full screen video
            mWebChromeClient.onHideCustomView();
        }//end if
        else { //exits application
            super.onBackPressed();
        }//end else
    }//end onBackPressed

    @Override
    protected void onPause() {
        super.onPause();
        //Save Current Pages
        viewPrimaryCurrent = ((WebView)findViewById(R.id.View1)).getUrl();
        if (!fullscreen){
            viewSecondaryCurrent = ((WebView)findViewById(R.id.View2)).getUrl();
        }//end if
    }//end onPause

    @Override
    protected void onResume() {
        super.onResume();
        //Check Network
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = false; //No connection
        if (activeNetwork != null) {
            isConnected = activeNetwork.isConnectedOrConnecting();
        }//end if
        //If no Connection open dialog
        if (!isConnected){
            noConnectionPopUp();
        }//end if
        //Reload Web Pages, currently not in use
        /**
         SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
         fullscreen = pref.getBoolean("full_screen_view", true);
         ((WebView)findViewById(R.id.View1)).loadUrl(viewPrimaryCurrent);
         if(!fullscreen){
         ((WebView)findViewById(R.id.View2)).loadUrl(viewSecondaryCurrent);
         }//end if
         **/
    }//end onResume

    @Override
    protected void onStop(){
        super.onStop();
        //If video is playing full screen, end it when the app closes
        if (mCustomView != null)
        {
            if (mCustomViewCallback != null)
                mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
        }
    }//end onStop();

    //////////////////////////////////////POP UP DIALOGS////////////////////////////////////////////

    /**
     * Pop up for failed network connection
     */
    private void noConnectionPopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(this.getText(R.string.noConnectionPopUp_title).toString());
        TextView message = new TextView(this);
        message.setText(this.getText(R.string.noConnectionPopUp_message).toString());
        message.setGravity(Gravity.CENTER);
        helpBuilder.setView(message);
        //Ok button exits the app
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }//end onClick
                });
        //Make the dialog visible to the user
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }//end noConnectionPopUp

    /**
     * Webpage failure (No Connection, Site Down, Wrong URL)
     */
    private void webpageFailPopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(this.getText(R.string.webpageFailPopUp_title).toString());
        TextView message = new TextView(this);
        message.setText(this.getText(R.string.noConnectionPopUp_message).toString());
        message.setGravity(Gravity.CENTER);
        helpBuilder.setView(message);
        //Ok button closes dialog
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing
                    }//end onClick
                });
        // Make dialog visible to user
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }//end webpageFailPopUp

    /**
     * Notifies user if a link will open outside of the main application
     * gives them the option to choose whether to leave the app or not open the link
     *
     * @param u URL of the external site
     */
    private void newWebPagePopUp(String u) {
        final String url = u; //URL of external site
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        TextView message = new TextView(this);
        message.setText(this.getText(R.string.newWebPagePopUp_message1).toString() + " "+
                Uri.parse(u).getHost()+ " " + this.getText(
                R.string.newWebPagePopUp_message2).toString());
        message.setGravity(Gravity.CENTER);
        helpBuilder.setView(message);
        helpBuilder.setTitle(this.getText(R.string.newWebPagePopUp_title).toString());
        //Ok button opens external link and leaves main application
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(
                                getBaseContext(),
                               MainActivity.this.getText(R.string.newWebPagePopUp_toast).toString(),
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }//end onClick
                });
        //Go Back button returns user to main application and does not open external link
        helpBuilder.setNegativeButton(this.getText(R.string.newWebPagePopUp_goback).toString(),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do Nothing
            }//end onClick
        });
        // Make dialog visible to user
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }//end newWebPagePopUp

    /**
     * Pop up for first use help
     */
    private void helpPopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle(this.getText(R.string.helpPopUp_title).toString());
        TextView message = new TextView(this);
        message.setText(this.getText(R.string.helpPopUp_message).toString());
        message.setGravity(Gravity.CENTER);
        helpBuilder.setView(message);
        //Ok button closes dialog
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing
                    }//end onClick
                });
        //Make the dialog visible to the user
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }//end noConnectionPopUp
}//end MainActivity
