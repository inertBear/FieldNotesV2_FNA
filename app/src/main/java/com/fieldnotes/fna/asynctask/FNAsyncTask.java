package com.fieldnotes.fna.asynctask;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Abstract Class for an Asynchronous Task to FNP
 */
public class FNAsyncTask extends AsyncTask<String, String, String> {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private ProgressDialog mProgressDialog;

    public FNAsyncTask(Context context) {
        mContext = context;
    }

    /**
     * Show Progress Dialog
     **/
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Working...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... args) {
        return null;
    }


    /**
     * Dismiss the progress bar
     **/
    protected void onPostExecute(String message) {
        super.onPostExecute(message);

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        // display response message
        if (message != null) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }
}
