/**
 * @author: Raghav Sood
 * @date: Jul 5, 2012
 */
package com.appaholics.updatechecker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class DownloadManager extends AsyncTask<String, Integer, String>
{
    private ProgressDialog progressDialog;


    private Context mContext;
    
    public DownloadManager(Context context)
    {
        mContext = context;
    }

    public boolean isOnline()
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

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/appupdate.apk");

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;
        }
 // end of class DownloadManager()

    @Override
    protected void onProgressUpdate(Integer... changed)
    {
        progressDialog.setProgress(changed[0]);
    }

    @Override
    protected void onPreExecute()
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Downloading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result)
    {
        progressDialog.dismiss();
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/appupdate.apk";
        Uri fileLoc = Uri.fromFile(new File(filepath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileLoc, "application/vnd.android.package-archive");
        mContext.startActivity(intent);

    }
}
