package com.devhunter.fna.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import static com.devhunter.fna.constants.FNAConstants.PREFS_NAME;
import static com.devhunter.fna.constants.FNAConstants.PREF_CUSTOMER_KEY;
import static com.devhunter.fna.constants.FNAConstants.PREF_PASSWORD;
import static com.devhunter.fna.constants.FNAConstants.PREF_REMEMBER_LOGIN;
import static com.devhunter.fna.constants.FNAConstants.PREF_USERNAME;
import static com.devhunter.fna.constants.FNConstants.HTTP_REQUEST_METHOD_POST;
import static com.devhunter.fna.constants.FNConstants.LOGIN_URL;
import static com.devhunter.fna.constants.FNConstants.PRODUCT_KEY_TAG;
import static com.devhunter.fna.constants.FNConstants.RESPONSE_MESSAGE_TAG;
import static com.devhunter.fna.constants.FNConstants.RESPONSE_STATUS_SUCCESS;
import static com.devhunter.fna.constants.FNConstants.RESPONSE_STATUS_TAG;
import static com.devhunter.fna.constants.FNConstants.USER_PASSWORD_TAG;
import static com.devhunter.fna.constants.FNConstants.USER_USERNAME_TAG;

public class Login extends AppCompatActivity {

    public static String mUserName = "";
    private ProgressDialog mProgressDialog;
    private JSONParser mJsonParser = new JSONParser();
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
        mUserNameET = findViewById(R.id.UsernameET);
        mPasswordET = findViewById(R.id.PasswordET);

        // check preferences and auto-fill login form
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean autolog = pref.getBoolean(PREF_REMEMBER_LOGIN, false);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);
        if (autolog) {
            if (username != null && password != null) {
                mUserNameET.setText(username);
                mPasswordET.setText(password);
            }
        }

        // Login button OnClick
        mButtonLogin = findViewById(R.id.LoginButton);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.LoginButton:
                        new AttemptLogin().execute();
                        break;
                    default:
                        break;
                }
            }
        });

        //get customer key from preferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String customerKey = prefs.getString(PREF_CUSTOMER_KEY, "");
        if (customerKey.isEmpty()) {
            Intent openRegisterActivity = new Intent(Login.this, RegisterProduct.class);
            startActivity(openRegisterActivity);
        }
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
            String status;
            // get login strings
            String username = mUserNameET.getText().toString();
            String password = mPasswordET.getText().toString();

            //get customer key from preferences
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String customerKey = prefs.getString(PREF_CUSTOMER_KEY, "");

            try {
                // convert to List of params
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(USER_USERNAME_TAG, username));
                params.add(new BasicNameValuePair(USER_PASSWORD_TAG, password));
                params.add(new BasicNameValuePair(PRODUCT_KEY_TAG, customerKey));
                // make HTTP connection
                JSONObject json = mJsonParser.createHttpRequest(LOGIN_URL, HTTP_REQUEST_METHOD_POST, params);
                status = json.getString(RESPONSE_STATUS_TAG);
                // check return value from PHP
                if (status.equals(RESPONSE_STATUS_SUCCESS)) {
                    // successful Login
                    Intent ii = new Intent(Login.this, Welcome.class);
                    startActivity(ii);
                    finish();
                    return json.getString(RESPONSE_MESSAGE_TAG);
                } else {
                    // failed login - bad username/password
                    return json.getString(RESPONSE_MESSAGE_TAG);
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

