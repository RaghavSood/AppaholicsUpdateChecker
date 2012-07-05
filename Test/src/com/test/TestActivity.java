/**
 * @author: Raghav Sood
 * @date: Jul 5, 2012
 */
package com.test;

import com.appaholics.updatechecker.UpdateChecker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends Activity {
	
	String TAG = "UpdateCheckerTest";
	
	Context mContext;
	
	@Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.help);
		
		mContext = this;
		Log.d(TAG, String.valueOf(mContext));
		
		UpdateChecker checker = new UpdateChecker(mContext);
		
		checker.checkForUpdateByVersionCode("URL with http:// to the text file with the version code");
		
		boolean update = checker.isUpdateAvailable();
		Log.d(TAG, String.valueOf(update));
		
		if(update)
		{
			checker.downloadAndInstall("URL with http:// to the text file with the update apk");
		}
	}

}
