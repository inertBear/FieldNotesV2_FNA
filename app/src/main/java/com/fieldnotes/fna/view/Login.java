package com.fieldnotes.fna.view;

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
import com.fieldnotes.fna.model.FNResponseType;
import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNRequestType;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.service.FNRequestService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.fieldnotes.fna.constants.FNAConstants.PREFS_NAME;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_AUTOLOG;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_PASSWORD;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_TOKEN;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_USERNAME;
import static com.fieldnotes.fna.constants.FNConstants.USERNAME_TAG;
import static com.fieldnotes.fna.constants.FNConstants.USER_PASSWORD_TAG;

public class Login extends AppCompatActivity {
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
    class LoginAsyncTask extends FNAsyncTask {

        LoginAsyncTask(Context context) {
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
            params.add(new BasicNameValuePair(USERNAME_TAG, username));
            params.add(new BasicNameValuePair(USER_PASSWORD_TAG, password));

            // build request
            FNRequest request = FNRequest.newBuilder()
                    .setRequestType(FNRequestType.LOGIN)
                    .setRequestParams(params)
                    .build();
            try {
                // send request to FNP
                FNResponse response = FNRequestService.sendRequest(request);

                if (response.getResponseType().equals(FNResponseType.SUCCESS)) {
                    // save login preferences
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                            .putString(PREF_USERNAME, username)
                            .putString(PREF_PASSWORD, password)
                            .putString(PREF_TOKEN, response.getToken())
                            .apply();

                    // navigate to Welcome View
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
     * automatically fill the username and password if stored in preferences
     */
    private void checkAutoLogin() {
        // check for auto-login
        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean autolog = pref.getBoolean(PREF_AUTOLOG, false);
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

