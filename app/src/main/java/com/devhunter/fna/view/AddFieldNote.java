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
import android.util.Log;
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
import com.devhunter.fna.model.FieldNote;
import com.devhunter.fna.parser.JSONParser;
import com.devhunter.fna.validation.FNValidate;
import com.devhunter.fna.view.adapters.HintAdapter;
import com.devhunter.fna.view.datetime.SelectDateFragment;
import com.devhunter.fna.view.datetime.SelectTimeFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.devhunter.fna.constants.FNAConstants.BILLING_CODE_ARRAY;
import static com.devhunter.fna.constants.FNAConstants.LOCATION_ARRAY;
import static com.devhunter.fna.constants.FNAConstants.PREFS_NAME;
import static com.devhunter.fna.constants.FNAConstants.PREF_CUSTOMER_KEY;
import static com.devhunter.fna.constants.FNConstants.ADD_NOTE_URL;
import static com.devhunter.fna.constants.FNConstants.BILLING_TAG;
import static com.devhunter.fna.constants.FNConstants.DATE_END_TAG;
import static com.devhunter.fna.constants.FNConstants.DATE_START_TAG;
import static com.devhunter.fna.constants.FNConstants.DESCRIPTION_TAG;
import static com.devhunter.fna.constants.FNConstants.GPS_TAG;
import static com.devhunter.fna.constants.FNConstants.HTTP_REQUEST_METHOD_POST;
import static com.devhunter.fna.constants.FNConstants.LOCATION_TAG;
import static com.devhunter.fna.constants.FNConstants.MILEAGE_END_TAG;
import static com.devhunter.fna.constants.FNConstants.MILEAGE_START_TAG;
import static com.devhunter.fna.constants.FNConstants.PRODUCT_KEY_TAG;
import static com.devhunter.fna.constants.FNConstants.PROJECT_NUMBER_TAG;
import static com.devhunter.fna.constants.FNConstants.RESPONSE_MESSAGE_TAG;
import static com.devhunter.fna.constants.FNConstants.RESPONSE_STATUS_SUCCESS;
import static com.devhunter.fna.constants.FNConstants.RESPONSE_STATUS_TAG;
import static com.devhunter.fna.constants.FNConstants.TIME_END_TAG;
import static com.devhunter.fna.constants.FNConstants.TIME_START_TAG;
import static com.devhunter.fna.constants.FNConstants.USERNAME_TAG;
import static com.devhunter.fna.constants.FNConstants.WELLNAME_TAG;

/**
 * Created by DevHunter on 5/3/2018.
 */

public class AddFieldNote extends Fragment {

    private ProgressDialog mProgressDialog;
    private JSONParser mJsonParser;

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

    public AddFieldNote() {
        mJsonParser = new JSONParser();
    }

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
        final HintAdapter hintAdapter = new HintAdapter(getActivity(), R.layout.layout_spinner_item, LOCATION_ARRAY);
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
        final HintAdapter hintAdapter2 = new HintAdapter(getActivity(), R.layout.layout_spinner_item, BILLING_CODE_ARRAY);
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

        private String LOG_TAG = "FieldNoteAdd";

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
            FieldNote fieldNote = null;

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

            try {
                //build FieldNote
                fieldNote = new FieldNote.FieldNoteBuilder()
                        .setCreator(loggedInUser)
                        .setProject(FNValidate.validate(project))
                        .setWellname(FNValidate.validate(wellName))
                        .setLocation(FNValidate.validateSpinner(location))
                        .setBilling(FNValidate.validateSpinner(billingCode))
                        .setDateStart(FNValidate.validateDateTime(dateStart))
                        .setDateEnd(FNValidate.validateDateTime(dateEnd))
                        .setTimeStart(FNValidate.validateDateTime(timeStart))
                        .setTimeEnd(FNValidate.validateDateTime(timeEnd))
                        .setMileageStart(FNValidate.validateInt(mileageStart))
                        .setMileageEnd(FNValidate.validateInt(mileageEnd))
                        .setDescription(FNValidate.validate(description))
                        .build();

            } catch (final Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //TODO: convert the FieldNote object to JSON and sent that instead of this
            if (fieldNote != null) {

                //get customer key from preferences
                SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                String customerKey = prefs.getString(PREF_CUSTOMER_KEY, "");

                //load params/value pairs into List
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(USERNAME_TAG, fieldNote.getCreator()));
                params.add(new BasicNameValuePair(WELLNAME_TAG, fieldNote.getWellName()));
                params.add(new BasicNameValuePair(DATE_START_TAG, fieldNote.getStartDate()));
                params.add(new BasicNameValuePair(TIME_START_TAG, fieldNote.getStartTime()));
                params.add(new BasicNameValuePair(MILEAGE_START_TAG, fieldNote.getMileageStart()));
                params.add(new BasicNameValuePair(DESCRIPTION_TAG, fieldNote.getDescription()));
                params.add(new BasicNameValuePair(MILEAGE_END_TAG, fieldNote.getMileageEnd()));
                params.add(new BasicNameValuePair(DATE_END_TAG, fieldNote.getEndDate()));
                params.add(new BasicNameValuePair(TIME_END_TAG, fieldNote.getEndTime()));
                params.add(new BasicNameValuePair(PROJECT_NUMBER_TAG, fieldNote.getProject()));
                params.add(new BasicNameValuePair(BILLING_TAG, fieldNote.getBilling()));
                params.add(new BasicNameValuePair(LOCATION_TAG, fieldNote.getLocation()));
                if (mGpsCheckbox.isChecked()) {
                    mCurrentLocation = SelfLocator.getCurrentLocation();
                }
                params.add(new BasicNameValuePair(GPS_TAG, mCurrentLocation));
                params.add(new BasicNameValuePair(PRODUCT_KEY_TAG, customerKey));

                try {
                    //send params and get JSONObject response
                    JSONObject json = mJsonParser.createHttpRequest(ADD_NOTE_URL, HTTP_REQUEST_METHOD_POST, params);
                    String status = json.getString(RESPONSE_STATUS_TAG);
                    if (status.equals(RESPONSE_STATUS_SUCCESS)) {
                        // return to default activity
                        Intent ii = new Intent(getActivity(), Welcome.class);
                        startActivity(ii);
                        getActivity().finish();
                        return json.getString(RESPONSE_MESSAGE_TAG);
                    } else {
                        //add failure
                        return json.getString(RESPONSE_MESSAGE_TAG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.v(LOG_TAG, "JSONException -> check FieldNotes webservice");
                }
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