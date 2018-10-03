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
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.devhunter.fna.view.Login.PREFS_NAME;
import static com.devhunter.fna.view.Login.PREF_CUSTOMER_KEY;

/**
 * Created by DevHunter on 5/8/2018.
 */

public class UpdateFieldNote extends Fragment {

    private static final String UPDATE_NOTE_URL = "http://www.fieldnotesfn.com/FNA_test/FNA_updateNote.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    // there is no way to implement a "spinner hint" with using an Android resource array
    private final String[] locationArray = new String[]{"Field", "Office", "Shop", "N/A", "Location"};
    private final String[] billingCodeArray = new String[]{"Billable", "Not Billable", "Turn-key", "N/A", "Billing"};

    private ProgressDialog mProgressDialog;
    private JSONParser mJsonParser;

    private View mFocusView;
    private EditText mProjectName;
    private EditText mWellName;
    private EditText mDescription;
    private Spinner mBillingCode;
    private EditText mDateStart;
    private EditText mDateEnd;
    private EditText mTimeStart;
    private EditText mTimeEnd;
    private Spinner mLocation;
    private EditText mMileageStart;
    private EditText mMileageEnd;
    private CheckBox mGpsCheckBox;
    private FloatingActionButton mUpdateButton;

    private HashMap<String, String> mOldData;

    private String mCurrentLocation = "0,0";

    public UpdateFieldNote() {
        mJsonParser = new JSONParser();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // custom Action Bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(R.drawable.fn_icon);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        mOldData = (HashMap<String, String>) bundle.getSerializable("oldData");
        return inflater.inflate(R.layout.fragment_update_tab_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // define views
        mFocusView = getView().findViewById(R.id.edit_focus_view);
        mFocusView.requestFocus();
        mProjectName = (EditText) getView().findViewById(R.id.projectName);
        mWellName = (EditText) getView().findViewById(R.id.wellName);
        mDescription = (EditText) getView().findViewById(R.id.description);
        mDateStart = (EditText) getView().findViewById(R.id.dateStart);
        mDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new SelectDateFragment(mDateStart);
                dateFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        mTimeStart = (EditText) getView().findViewById(R.id.timeStart);
        mTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new SelectTimeFragment(mTimeStart);
                timeFragment.show(getFragmentManager(), "TimePicker");
            }
        });
        mDateEnd = (EditText) getView().findViewById(R.id.dateEnd);
        mDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateFragment = new SelectDateFragment(mDateEnd);
                dateFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        mTimeEnd = (EditText) getView().findViewById(R.id.timeEnd);
        mTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timeFragment = new SelectTimeFragment(mTimeEnd);
                timeFragment.show(getFragmentManager(), "TimePicker");
            }
        });
        mMileageStart = (EditText) getView().findViewById(R.id.mileageStart);
        mMileageEnd = (EditText) getView().findViewById(R.id.mileageEnd);

        mLocation = (Spinner) getView().findViewById(R.id.update_location);
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

        mBillingCode = (Spinner) getView().findViewById(R.id.update_billingCode);
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

        mGpsCheckBox = (CheckBox) getView().findViewById(R.id.gpsCheckbox);
        mGpsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //if location services are not granted to FieldNotes
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mGpsCheckBox.setChecked(false);
                    }
                    new SelfLocator(getActivity()).execute();
                }
            }
        });

        mUpdateButton = (FloatingActionButton) getView().findViewById(R.id.addButton);


        //TODO: encapsulate this to a SpinnerHint Class
        // in-app conversions (billing)
        String bill = mOldData.get("bill");
        int bill_position;
        switch (bill) {
            case "Billable":
                bill_position = 0;
                break;
            case "Not Billable":
                bill_position = 1;
                break;
            case "Turn-key":
                bill_position = 2;
                break;
            default:
                bill_position = 3;
                break;
        }
        // in-app conversions (location)
        String location = mOldData.get("location");
        int location_position;
        switch (location) {
            case "Field":
                location_position = 0;
                break;
            case "Office":
                location_position = 1;
                break;
            case "Shop":
                location_position = 2;
                break;
            default:
                location_position = 3;
                break;
        }

        //set the original values of edit fields to retrieved values
        mProjectName.setText(mOldData.get("project"));
        mWellName.setText(mOldData.get("well"));
        mDescription.setText(mOldData.get("description"));
        mBillingCode.setSelection(bill_position);
        mDateStart.setText(mOldData.get("sDate"));
        mTimeStart.setText(mOldData.get("sTime"));
        mDateEnd.setText(mOldData.get("eDate"));
        mTimeEnd.setText(mOldData.get("eTime"));
        mLocation.setSelection(location_position);
        mMileageStart.setText(mOldData.get("sMile"));
        mMileageEnd.setText(mOldData.get("eMile"));

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FieldNoteUpdate().execute();
            }
        });
    }

    /**
     * This class updates a FieldNote in a background thread. Works the same way as an FieldNoteAdd,
     * but follows a different workflow
     */

    //TODO: duplicate code here and in FieldNoteUpdate. Extract these two locations to a single "AddUpdateFieldNote" class

    class FieldNoteUpdate extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create progress bar
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Updating FieldNote...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            int success;
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

            //get customer key from preferences
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String customerKey = prefs.getString(PREF_CUSTOMER_KEY, "");

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
                //load params/values into List
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("userName", Login.getLoggedInUser()));
                params.add(new BasicNameValuePair("wellName", FNValidate.validate(mWellName.getText().toString())));
                params.add(new BasicNameValuePair("dateStart", mDateStart.getText().toString()));
                params.add(new BasicNameValuePair("timeStart", mTimeStart.getText().toString()));
                params.add(new BasicNameValuePair("mileageStart", mMileageStart.getText().toString()));
                params.add(new BasicNameValuePair("description", FNValidate.validate(mDescription.getText().toString())));
                params.add(new BasicNameValuePair("mileageEnd", mMileageEnd.getText().toString()));
                params.add(new BasicNameValuePair("dateEnd", mDateEnd.getText().toString()));
                params.add(new BasicNameValuePair("timeEnd", mTimeEnd.getText().toString()));
                params.add(new BasicNameValuePair("projectNumber", FNValidate.validate(mProjectName.getText().toString())));
                params.add(new BasicNameValuePair("billing", mBillingCode.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("location", mLocation.getSelectedItem().toString()));
                if (mGpsCheckBox.isChecked()) {
                    mCurrentLocation = SelfLocator.getCurrentLocation();
                }
                params.add(new BasicNameValuePair("gps", mCurrentLocation));
                params.add(new BasicNameValuePair("ticketNumber", mOldData.get("ticket")));

                params.add(new BasicNameValuePair("customerKey", customerKey));

                try {
                    //send params and get JSONObject response
                    JSONObject json = mJsonParser.createHttpRequest(UPDATE_NOTE_URL, "POST", params);
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // return to default activity
                        Intent ii = new Intent(getActivity(), Welcome.class);
                        startActivity(ii);
                        getActivity().finish();
                        return json.getString(TAG_MESSAGE);
                    } else {
                        // not successful
                        return json.getString(TAG_MESSAGE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // should not reach this unless the PHP is wrong
                }
            }
            return null;
        }

        /**
         * Once the background process mInputStream done we need to Dismiss the progress
         * dialog before leaving activity
         **/
        protected void onPostExecute(String message) {
            // dismiss progress bar
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            //update user ui
            if (message != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }
}
