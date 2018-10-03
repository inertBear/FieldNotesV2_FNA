package com.devhunter.fna.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devhunter.fna.R;
import com.devhunter.fna.parser.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    // static strings
    private static final String LOGIN_URL = "http://www.fieldnotesfn.com/FNA_test/FNA_login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    public static final String PREFS_NAME = "fnPrefFile";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    public static final String PREF_CUSTOMER_KEY = "customerkey";
    private static final String CUSTOMER_KEY = "1234567890";
    public static String mUserName = "";
    // AsyncTask
    private ProgressDialog mProgressDialog;
    private JSONParser mJsonParser = new JSONParser();
    // Views
    private EditText mUserNameET;
    private EditText mPasswordET;
    private Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        //customize actionbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.fn_icon);
        getSupportActionBar().setTitle("");
        // get views
        mUserNameET = (EditText) findViewById(R.id.UsernameET);
        mPasswordET = (EditText) findViewById(R.id.PasswordET);

        // check preferences and auto-fill login form
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean autolog = pref.getBoolean("RememberLogin", false);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);
        if (autolog) {
            if (username != null && password != null) {
                mUserNameET.setText(username);
                mPasswordET.setText(password);
            }
        }

        // Login button OnClick
        mButtonLogin = (Button) findViewById(R.id.LoginButton);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.LoginButton:
                        new AttemptLogin().execute();
                        break;
                    //case R.id.RegisterButton:
                    //new AttemptRegister().execute();
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Asynchornous login with a progress bar running on the UI thread to show the user that
     * the login mInputStream 'working'
     */

    class AttemptLogin extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display progress bar
            mProgressDialog = new ProgressDialog(Login.this);
            mProgressDialog.setMessage("Logging In...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();

            //write user login to preferences
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(PREF_USERNAME, mUserNameET.getText().toString())
                    .putString(PREF_PASSWORD, mPasswordET.getText().toString())
                    .putString(PREF_CUSTOMER_KEY, CUSTOMER_KEY)
                    .apply();
        }

        /**
         * Run the login in an AsyncTask background thread
         *
         * @param args
         * @return string
         */
        @Override
        protected String doInBackground(String... args) {
            int success;
            // get login strings
            String username = mUserNameET.getText().toString();
            String password = mPasswordET.getText().toString();

            //get customer key from preferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String customerKey = prefs.getString(PREF_CUSTOMER_KEY, "");

            try {
                // convert to List of params
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("customerKey", customerKey));
                // make HTTP connection
                JSONObject json = mJsonParser.createHttpRequest(LOGIN_URL, "POST", params);
                success = json.getInt(TAG_SUCCESS);
                // check return value from PHP
                if (success == 1) {
                    // successful Login
                    Intent ii = new Intent(Login.this, Welcome.class);
                    startActivity(ii);
                    finish();
                    return json.getString(TAG_MESSAGE);
                } else {
                    // failed login - bad username/password
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
            // save username for app functions
            mUserName = mUserNameET.getText().toString();
            // display log in success/failure to user
            if (message != null) {
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * retrieves the username of the logged in user
     *
     * @return
     */
    public static String getLoggedInUser() {
        return mUserName;
    }
}

