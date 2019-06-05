package com.fieldnotes.fna.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fieldnotes.fna.R;
import com.fieldnotes.fna.asynctask.FNAsyncTask;
import com.fieldnotes.fna.model.FNReponseType;
import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNRequestType;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.service.FNRequestService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    public static final String PREFS_NAME = "FNPrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    public static final String PREF_TOKEN = "productToken";
    public static String mUserName = "";

    // Views
    private EditText mUserNameET;
    private EditText mPasswordET;

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

        // Login button
        Button loginBtn = findViewById(R.id.LoginButton);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoginAsyncTask(Login.this).execute();
            }
        });

        checkAutoLogin();
    }

    /**
     * Asynchronous login with progress bar
     */
    @SuppressLint("StaticFieldLeak")
    class LoginAsyncTask extends FNAsyncTask {

        LoginAsyncTask(Context context){
            super(context);
        }

        /**
         * Run the login in an AsyncTask background thread
         *
         * @param args
         * @return string
         */
        @Override
        protected String doInBackground(String... args) {
            // construct params
            String username = mUserNameET.getText().toString();
            String password = mPasswordET.getText().toString();

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("UserName", username));
            params.add(new BasicNameValuePair("UserPassword", password));

            // build request
            FNRequest request = FNRequest.newBuilder()
                    .setRequestType(FNRequestType.LOGIN)
                    .setRequestParams(params)
                    .build();
            try {
                // send request to FNP
                FNResponse response = FNRequestService.sendRequest(request);

                if (response.getResponseType().equals(FNReponseType.SUCCESS)) {
                    // save username for app functions
                    mUserName = mUserNameET.getText().toString();

                    // save login preferences
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                            .putString(PREF_USERNAME, username)
                            .putString(PREF_PASSWORD, password)
                            .putString(PREF_TOKEN, response.getToken())
                            .apply();

                    // Navigate to Welcome View
                    Intent ii = new Intent(Login.this, Welcome.class);
                    startActivity(ii);
                    finish();
                }
                return response.getMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
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

    /**
     * automatically fill the username and password if stored in preferences
     */
    private void checkAutoLogin(){
        // check for auto-login
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
    }
}

