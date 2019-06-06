package com.fieldnotes.fna.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fieldnotes.fna.R;
import com.fieldnotes.fna.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * UNUSED, but saved - registration has been temporarily removed from the working app
 */
public class RegisterProduct extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private JSONParser mJsonParser;

    private TextView mRegistrationCodeTv;
    private Button mSubmitBtn;

    private static final String REGISTER_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_register.php";
    public static final String PREFS_NAME = "fnPrefFile";
    public static final String PREF_CUSTOMER_KEY = "customerkey";
    private static final String TAG_STATUS = "status";
    private static final String TAG_MESSAGE = "message";

    public RegisterProduct() {
        mJsonParser = new JSONParser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_product);
        // customize action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.fn_icon);
        getSupportActionBar().setTitle("");

        mRegistrationCodeTv = findViewById(R.id.customer_key_tv);
        mSubmitBtn = findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.submit_btn:
                        new AttemptRegister().execute();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    /**
     * Asynchornous register with a progress bar running on the UI thread to show the user that
     * the register is "working"
     */
    class AttemptRegister extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display progress bar
            mProgressDialog = new ProgressDialog(RegisterProduct.this);
            mProgressDialog.setMessage("Registering...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        /**
         * Run the register in an AsyncTask background thread
         *
         * @param args
         * @return string
         */
        @Override
        protected String doInBackground(String... args) {
            String status;
            // get login strings
            String customerKey = mRegistrationCodeTv.getText().toString();
            customerKey = customerKey.replace("-", "");

            try {
                // convert to List of params
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("customerKey", customerKey));
                // make HTTP connection
                JSONObject json = mJsonParser.createHttpRequest(REGISTER_URL, "POST", params);
                status = json.getString(TAG_STATUS);
                // check return value from PHP
                if (status.equals("success")) {
                    //save customer code to preferences
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(PREF_CUSTOMER_KEY, customerKey)
                            .apply();
                    // successful Register, open Login Activity
                    Intent ii = new Intent(RegisterProduct.this, Login.class);
                    startActivity(ii);
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    // failed register - bad code entered
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                // JSON exception - should never be reached
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Once the background process mInputStream done we need to Dismiss the progress
         * dialog before leaving activity
         **/
        protected void onPostExecute(String message) {
            // remove progress bar
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            // display log in success/failure to user
            if (message != null) {
                Toast.makeText(RegisterProduct.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
