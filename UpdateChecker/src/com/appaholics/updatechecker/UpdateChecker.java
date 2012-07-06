package com.appaholics.updatechecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Raghav Sood
 * @version API 2
 * @since API 1
 */

public class UpdateChecker {

	private String TAG = "UpdateChecker";
	private boolean updateAvailable = false;
	private Context mContext;
	private boolean haveValidContext = false;
	private boolean useToasts = false;
	private DownloadManager downloadManager;

	/**
	 * Constructor that only takes the Activity context.
	 * 
	 * This constructor sets the toast notification functionality to false. Example call:
	 * 		UpdateChecker uupdateChecker = new UpdateChecker(this);
	 * 
	 * @param context An instance of your Activity's context
	 * @since API 1
	 */
	public UpdateChecker(Context context)
	{
		this(context, false);
	}
	
	/**
	 * Constructor for UpdateChecker
	 * 
	 * Example call:
	 * 		UpdateChecker uupdateChecker = new UpdateChecker(this, false);
	 * 
	 * @param context An instance of your Activity's context
	 * @param toasts True if you want toast notifications, false by default
	 * @since API 2
	 */
	public UpdateChecker(Context context, boolean toasts) {
		mContext = context;
		if (mContext != null) {
			haveValidContext = true;
			useToasts = toasts;
		}
	}

	/**
	 * Checks for app update by version code.
	 * 
	 * Example call:
	 * 		updateChecker.checkForUpdateByVersionCode("http://www.example.com/version.txt");
	 * 
	 * @param url URL at which the text file containing your latest version code is located.
	 * @since API 1
	 */
	public void checkForUpdateByVersionCode(String url) {
		if(isOnline())
		{
			if (haveValidContext) {
				int versionCode = getVersionCode();
				int readCode = 0;
				if (versionCode >= 0) {
					try {
						readCode = Integer.parseInt(readFile(url));
						// Check if update is available.
						if (readCode > versionCode) {
							updateAvailable = true; //We have an update available
						}
					} catch (NumberFormatException e) {
						Log.e(TAG, "Invalid number online"); //Something wrong with the file content
					}
				
				} else {
					Log.e(TAG, "Invalid version code in app"); //Invalid version code
				}
			} else {
				Log.e(TAG, "Context is null"); //Context was null
			}
		} else {
			if(useToasts)
			{
				makeToastFromString("App update check failed. No internet connection available").show();
			}
		}
	}

	/**
	 * Get's the version code of your app by the context passed in the constructor
	 * 
	 * @return The version code if successful, -1 if not
	 * @since API 1
	 */
	public int getVersionCode() {
		int code;
		try {
			code = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode;
			return code; // Found the code!
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Version Code not available"); // There was a problem with the code retrieval.
		} catch (NullPointerException e) {
			Log.e(TAG, "Context is null");
		}

		return -1; // There was a problem.
	}

	/**
	 * Downloads and installs the update apk from the URL
	 * 
	 * @param apkUrl URL at which the update is located
	 * @since API 1
	 */
	public void downloadAndInstall(String apkUrl)
	{
		if(isOnline())
		{
			downloadManager = new DownloadManager(mContext, true);
			downloadManager.execute(apkUrl);
		}
		else {
			if(useToasts)
			{
				makeToastFromString("App update failed. No internet connection available").show();
			}
		}
	}
	
	/**
	 * Must be called only after download(). 
	 * @since API 2
	 * @throws NullPointerException Thrown when download() hasn't been called.
	 */
	public void install()
	{
		downloadManager.install();
	}
	
	/**
	 * Downloads the update apk, but does not install it
	 * 
	 * @param apkUrl URL at which the update is located.
	 * @since API 2
	 */
	public void download(String apkUrl)
	{
		if(isOnline())
		{
		downloadManager = new DownloadManager(mContext, false);
		downloadManager.execute(apkUrl);
		} else {
			if(useToasts)
			{
				makeToastFromString("App update failed. No internet connection available").show();
			}
		}
	}
	
	/**
	 * Should be called after checkForUpdateByVersionCode()
	 * @return Returns true if an update is available, false if not.
	 * @since API 1
	 */
	public boolean isUpdateAvailable() {
		return updateAvailable;
	}
	
	/**
	 * Checks to see if an Internet connection is available
	 * 
	 * @return True if connected or connecting, false otherwise
	 * @since API 2
	 */
    public boolean isOnline()
    {
    	if(haveValidContext)
    	{
    		try
        	{
            	ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            	return cm.getActiveNetworkInfo().isConnectedOrConnecting(); 
        	}
        	catch (Exception e)
        	{
            	return false;
        	}
    	}
    	return false;
    }
	
    /**
     * Launches your app's page on Google Play if it exists.
     * 
     * @since API 2
     */
    public void launchMarketDetails()
    {
    	if(haveValidContext)
    	{
    		if(hasGooglePlayInstalled())
    		{
    			String marketPage = "market://details?id=" + mContext.getPackageName();
    			Intent intent = new Intent(Intent.ACTION_VIEW);
    			intent.setData(Uri.parse(marketPage));
    			mContext.startActivity(intent);
    		} else {
    			if(useToasts)
    			{
    				makeToastFromString("Google Play isn't installed on your device.").show();
    			}
    		}
    	}
    }
    
    /**
     * Checks to use if the user's device has Google Play installed
     * @return true if Google Play is installed, otherwise false
     * @since API 2
     */
    public boolean hasGooglePlayInstalled() {
        Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=dummy"));
        PackageManager manager = mContext.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(market, 0);

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).activityInfo.packageName.startsWith("com.android.vending") == true) {
                    return true;
                }
            }
         }
        return false;
    }
    
    /**
     * Makes a toast message with a short duration from the given text.
     * @param text The text to be displayed by the toast
     * @return The toast object.
     * @since API 2
     */
    @SuppressLint("ShowToast")
	public Toast makeToastFromString(String text)
    {
    	Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
    	return toast;
    }
    
    /**
     * Reads a file at the given URL
     * 
     * @param url The URL at which the file is located
     * @return Returns the content of the file if successful
     * @since API 1
     */
	public String readFile(String url)
	{
		String result;
		InputStream inputStream;
		try {
			inputStream = new URL(url).openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			result = bufferedReader.readLine();
			return result;
		} catch (MalformedURLException e) {
			Log.e(TAG, "Invalid URL");
		} catch (IOException e) {
			Log.e(TAG, "There was an IO exception");
		}
		
		Log.e(TAG, "There was an error reading the file");
		return "Problem reading the file";
	}
}
