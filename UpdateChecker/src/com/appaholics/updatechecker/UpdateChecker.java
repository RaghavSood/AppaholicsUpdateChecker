/*
 * @author: Raghav Sood © 2012
 */

package com.appaholics.updatechecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class UpdateChecker {

	private String TAG = "UpdateChecker";
	private boolean updateAvailable = false;
	private Context mContext;

	private boolean haveValidContext = false;

	public UpdateChecker(Context context) {
		mContext = context;
		if (mContext != null) {
			haveValidContext = true;
		}
	}

	public void checkForUpdateByVersionCode(String url) {
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
	}

	public int getVersionCode() {
		int code;
		try {
			code = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode;
			return code; // Found the code!
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Version Code not available"); // There was a problem
														// with the code
														// retrieval.
		}

		return -1; // There was a problem.
	}

	public void downloadAndInstall(String apkUrl)
	{
		DownloadManager downloadManager = new DownloadManager(mContext);
		downloadManager.execute(apkUrl);
	}
	
	public boolean isUpdateAvailable() {
		return updateAvailable;
	}
	
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
