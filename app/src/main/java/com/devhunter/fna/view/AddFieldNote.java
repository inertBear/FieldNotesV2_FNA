package com.devhunter.fna.view;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devhunter.fna.R;
import com.devhunter.fna.gps.SelfLocator;
import com.devhunter.fna.model.FNReponseType;
import com.devhunter.fna.model.FNRequest;
import com.devhunter.fna.model.FNRequestType;
import com.devhunter.fna.model.FNResponse;
import com.devhunter.fna.model.FieldNote;
import com.devhunter.fna.parser.JSONParser;
import com.devhunter.fna.service.FNRequestService;
import com.devhunter.fna.validation.FNValidate;
import com.devhunter.fna.view.adapters.HintAdapter;
import com.devhunter.fna.view.datetime.SelectDateFragment;
import com.devhunter.fna.view.datetime.SelectTimeFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.devhunter.fna.view.Login.PREFS_NAME;
import static com.devhunter.fna.view.Login.PREF_CUSTOMER_KEY;

/**
 * Created by DevHunter on 5/3/2018.
 */

public class AddFieldNote extends Fragment {
    // there is no way to implement a "spinner hint" with using an Android resource array
    private static final String[] locationArray = new String[]{"Field", "Office", "Shop", "N/A", "Location"};
    private static final String[] billingCodeArray = new String[]{"Billable", "Not Billable", "Turn-key", "N/A", "Billing"};
    // AsyncTask
    private ProgressDialog mProgressDialog;
    // Views
    private View mFocusView;
    private EditText mProjectName;
    private EditText mWellName;
    private EditText mDescription;
    private EditText mDateStart;
    private EditText mDateEnd;
    private EditText mTimeStart;
    private EditText mTimeEnd;
    private EditText mMileageStart;
    private EditText mMileageEnd;
    private Spinner mLocation;
    private Spinner mBillingCode;
    private CheckBox mGpsCheckbox;
    private FloatingActionButton mAddButton;

    private String mCurrentLocation = "0,0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // customize action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.fn_icon);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_tab_view, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // define views
        mFocusView = view.findViewById(R.id.focus_view);
        mFocusView.requestFocus();

        mProjectName = view.findViewById(R.id.projectName);
        mWellName = view.findViewById(R.id.wellName);
        mDescription = view.findViewById(R.id.description);
        mDateStart = view.findViewById(R.id.dateStart);
        mDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new SelectDateFragment(mDateStart);
                dateFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        mTimeStart = view.findViewById(R.id.timeStart);
        mTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new SelectTimeFragment(mTimeStart);
                timeFragment.show(getFragmentManager(), "TimePicker");
            }
        });
        mDateEnd = view.findViewById(R.id.dateEnd);
        mDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new SelectDateFragment(mDateEnd);
                dateFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        mTimeEnd = view.findViewById(R.id.timeEnd);
        mTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new SelectTimeFragment(mTimeEnd);
                timeFragment.show(getFragmentManager(), "TimePicker");
            }
        });
        mLocation = view.findViewById(R.id.location);
        final HintAdapter hintAdapter = new HintAdapter(getActivity(), R.layout.layout_spinner_item, locationArray);
        mLocation.setAdapter(hintAdapter);
        mLocation.setSelection(hintAdapter.getCount());
        mLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < hintAdapter.getCount()) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBillingCode = getView().findViewById(R.id.billingCode);
        final HintAdapter hintAdapter2 = new HintAdapter(getActivity(), R.layout.layout_spinner_item, billingCodeArray);
        mBillingCode.setAdapter(hintAdapter2);
        mBillingCode.setSelection(hintAdapter2.getCount());
        mBillingCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < hintAdapter2.getCount()) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mMileageStart = getView().findViewById(R.id.mileageStart);
        mMileageEnd = getView().findViewById(R.id.mileageEnd);
        mAddButton = getView().findViewById(R.id.addButton);

        mGpsCheckbox = getView().findViewById(R.id.gpsCheckbox);
        mGpsCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //if location services are not granted to FieldNotes
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mGpsCheckbox.setChecked(false);
                    }
                    new SelfLocator(getActivity()).execute();
                }
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FieldNoteAdd().execute();
            }
        });
    }

    /**
     * This class Adds a FieldNote in a background thread
     */

    class FieldNoteAdd extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Adding FieldNote...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //get values from view
            String loggedInUser = Login.getLoggedInUser();
            String wellName = mWellName.getText().toString();
            String dateStart = mDateStart.getText().toString();
            String timeStart = mTimeStart.getText().toString();
            String mileageStart = mMileageStart.getText().toString();
            String description = mDescription.getText().toString();
            String mileageEnd = mMileageEnd.getText().toString();
            String dateEnd = mDateEnd.getText().toString();
            String timeEnd = mTimeEnd.getText().toString();
            String project = mProjectName.getText().toString();
            String billingCode = mBillingCode.getSelectedItem().toString();
            String location = mLocation.getSelectedItem().toString();

            //get customer key from preferences
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String productKey = prefs.getString(PREF_CUSTOMER_KEY, "");

            try {
                // convert to list of params
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userName", loggedInUser));
                params.add(new BasicNameValuePair("wellName", FNValidate.validate(wellName)));
                params.add(new BasicNameValuePair("dateStart", FNValidate.validateDateTime(dateStart)));
                params.add(new BasicNameValuePair("timeStart", FNValidate.validateDateTime(timeStart)));
                params.add(new BasicNameValuePair("mileageStart", FNValidate.validateInt(mileageStart)));
                params.add(new BasicNameValuePair("description", FNValidate.validate(description)));
                params.add(new BasicNameValuePair("mileageEnd", FNValidate.validateInt(mileageEnd)));
                params.add(new BasicNameValuePair("dateEnd", FNValidate.validateDateTime(dateEnd)));
                params.add(new BasicNameValuePair("timeEnd", FNValidate.validateDateTime(timeEnd)));
                params.add(new BasicNameValuePair("projectNumber", FNValidate.validate(project)));
                params.add(new BasicNameValuePair("billing", FNValidate.validateSpinner(billingCode)));
                params.add(new BasicNameValuePair("location", FNValidate.validateSpinner(location)));
                if (mGpsCheckbox.isChecked()) {
                    mCurrentLocation = SelfLocator.getCurrentLocation();
                }
                params.add(new BasicNameValuePair("gps", mCurrentLocation));
                params.add(new BasicNameValuePair("customerKey", productKey));

                // build FNRequest
                FNRequest request = FNRequest.newBuilder()
                        .setRequestType(FNRequestType.ADD)
                        .setRequestingUser(loggedInUser)
                        .setProductKey(productKey)
                        .setRequestParams(params)
                        .build();

                // use request service to send request to FNP
                FNResponse response = FNRequestService.sendRequest(request);

                if (response.getResponseType().equals(FNReponseType.SUCCESS)) {
                    // return to default activity
                    Intent ii = new Intent(getActivity(), Welcome.class);
                    startActivity(ii);
                    getActivity().finish();
                }
                return response.getMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Once the background process mInputStream done we need to Dismiss the progress
         * dialog before leaving activity
         **/
        protected void onPostExecute(String message) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            // update user
            if (message != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        // prevent leaks
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // prevent leaks
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }
}