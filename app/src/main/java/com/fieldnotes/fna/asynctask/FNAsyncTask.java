package com.fieldnotes.fna.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Abstract Class for an Asynchronous Task to FNP
 */
public abstract class FNAsyncTask extends AsyncTask<String, String, String> {
    private static Context mContext;
    protected ProgressDialog mProgressDialog;

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

    /**
     * Describes what the async class will do in the background
     */
    @Override
    protected abstract String doInBackground(String... args);

    /**
     * Dismiss the progress bar, and display response message as a toast
     **/
    protected void onPostExecute(String message) {
        super.onPostExecute(message);

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        // display response message
        if (message != null) {
            Toast toast = Toast.makeText(mContext.getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
