package com.fieldnotes.fna.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.fieldnotes.fna.R;
import com.fieldnotes.fna.asynctask.FNAsyncTask;
import com.fieldnotes.fna.gps.SelfLocator;
import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNRequestType;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.model.FNResponseType;
import com.fieldnotes.fna.service.FNRequestService;
import com.fieldnotes.fna.validation.FNValidate;
import com.fieldnotes.fna.view.adapters.HintAdapter;
import com.fieldnotes.fna.view.datetime.SelectDateFragment;
import com.fieldnotes.fna.view.datetime.SelectTimeFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.fieldnotes.fna.constants.FNAConstants.BILLING_CODE_ARRAY;
import static com.fieldnotes.fna.constants.FNAConstants.LOCATION_ARRAY;
import static com.fieldnotes.fna.constants.FNAConstants.PREFS_NAME;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_TOKEN;
import static com.fieldnotes.fna.constants.FNAConstants.PREF_USERNAME;
import static com.fieldnotes.fna.constants.FNConstants.BILLING_TAG;
import static com.fieldnotes.fna.constants.FNConstants.DATE_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.DATE_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.DESCRIPTION_TAG;
import static com.fieldnotes.fna.constants.FNConstants.GPS_TAG;
import static com.fieldnotes.fna.constants.FNConstants.LOCATION_TAG;
import static com.fieldnotes.fna.constants.FNConstants.MILEAGE_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.MILEAGE_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.PROJECT_NUMBER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TIME_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TIME_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TOKEN_TAG;
import static com.fieldnotes.fna.constants.FNConstants.USER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.WELLNAME_TAG;

public class AddFieldNote extends Fragment {
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

    private String mCurrentLocation = "LOCATION NOT SET";

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
        return inflater.inflate(R.layout.fragment_add_tab_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // define views
        View focusView = view.findViewById(R.id.focus_view);
        focusView.requestFocus();

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
        final HintAdapter locationHintAdapter = new HintAdapter(getActivity(), R.layout.layout_spinner_item, LOCATION_ARRAY);
        mLocation.setAdapter(locationHintAdapter);
        mLocation.setSelection(locationHintAdapter.getCount());
        mLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < locationHintAdapter.getCount()) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBillingCode = view.findViewById(R.id.billingCode);
        final HintAdapter billingHintAdapter = new HintAdapter(getActivity(), R.layout.layout_spinner_item, BILLING_CODE_ARRAY);
        mBillingCode.setAdapter(billingHintAdapter);
        mBillingCode.setSelection(billingHintAdapter.getCount());
        mBillingCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < billingHintAdapter.getCount()) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mMileageStart = view.findViewById(R.id.mileageStart);
        mMileageEnd = view.findViewById(R.id.mileageEnd);
        FloatingActionButton addButton = view.findViewById(R.id.addButton);

        mGpsCheckbox = view.findViewById(R.id.gpsCheckbox);
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddNoteAsyncTask(getContext()).execute();
            }
        });
    }

    /**
     * Asynchronous AddNote with progress bar
     */
    class AddNoteAsyncTask extends FNAsyncTask {

        AddNoteAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            //get values from view
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

            //get values from preferences
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String username = prefs.getString(PREF_USERNAME, "");
            String token = prefs.getString(PREF_TOKEN, "");

            try {
                // convert to list of params
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(USER_TAG, username));
                params.add(new BasicNameValuePair(WELLNAME_TAG, FNValidate.validate(wellName)));
                params.add(new BasicNameValuePair(DATE_START_TAG, FNValidate.validateDateTime(dateStart)));
                params.add(new BasicNameValuePair(TIME_START_TAG, FNValidate.validateDateTime(timeStart)));
                params.add(new BasicNameValuePair(MILEAGE_START_TAG, FNValidate.validateInt(mileageStart)));
                params.add(new BasicNameValuePair(DESCRIPTION_TAG, FNValidate.validate(description)));
                params.add(new BasicNameValuePair(MILEAGE_END_TAG, FNValidate.validateInt(mileageEnd)));
                params.add(new BasicNameValuePair(DATE_END_TAG, FNValidate.validateDateTime(dateEnd)));
                params.add(new BasicNameValuePair(TIME_END_TAG, FNValidate.validateDateTime(timeEnd)));
                params.add(new BasicNameValuePair(PROJECT_NUMBER_TAG, FNValidate.validate(project)));
                params.add(new BasicNameValuePair(BILLING_TAG, FNValidate.validateSpinner(billingCode)));
                params.add(new BasicNameValuePair(LOCATION_TAG, FNValidate.validateSpinner(location)));
                if (mGpsCheckbox.isChecked()) {
                    mCurrentLocation = SelfLocator.getCurrentLocation();
                }
                params.add(new BasicNameValuePair(GPS_TAG, mCurrentLocation));
                params.add(new BasicNameValuePair(TOKEN_TAG, token));

                // build FNRequest
                FNRequest request = FNRequest.newBuilder()
                        .setRequestType(FNRequestType.ADD)
                        .setRequestParams(params)
                        .build();

                // use request service to send request to FNP
                FNResponse response = FNRequestService.sendRequest(request);

                if (response.getResponseType().equals(FNResponseType.SUCCESS)) {
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
    }

    @Override
    public void onPause() {
        super.onPause();
//        // prevent leaks
//        if (mProgressDialog != null) {
//            if (mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        // prevent leaks
//        if (mProgressDialog != null) {
//            if (mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//            }
//        }
    }
}