package com.devhunter.fna.view;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devhunter.fna.R;
import com.devhunter.fna.parser.JSONParser;
import com.devhunter.fna.validation.FNValidate;
import com.devhunter.fna.view.datetime.SelectDateFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.devhunter.fna.view.Login.PREFS_NAME;
import static com.devhunter.fna.view.Login.PREF_CUSTOMER_KEY;

/**
 * Created on 5/3/2018.
 */

public class SearchFieldNote extends Fragment {
    // static Strings
    private static final String SEARCH_NOTE_URL = "http://www.fieldnotesfn.com/FNA_test/FNA_searchNote.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    // JSON parser
    private ProgressDialog mProgressDialog;
    private JSONParser mJsonParser;
    // Views
    private TextView mDateStart;
    private TextView mDateEnd;
    private ListView mListView;
    private Button mSearchButton;

    private ArrayList<HashMap<String, String>> mAllSearchResults;

    public SearchFieldNote() {
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_tab_view, container, false);
        mListView = (ListView) rootView.findViewById(android.R.id.list);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // detail views
        mDateStart = (TextView) view.findViewById((R.id.SearchDateStart));
        mDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(mDateStart);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        mDateEnd = (TextView) view.findViewById(R.id.SearchDateEnd);
        mDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(mDateEnd);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        mSearchButton = (Button) view.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FieldNotesSearch().execute();
            }
        });
    }

    /**
     * This class runs a background thread to search for field notes created by the logged in user
     * by a date range. Results are listed as fully qualified results. These results can be selected
     * by the user to edit the values within each note
     */

    class FieldNotesSearch extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create progress bar
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Searching FieldNotes...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String searchResultMessage = "";
            List<NameValuePair> params = null;

            //get customer key from preferences
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String customerKey = prefs.getString(PREF_CUSTOMER_KEY, "");

            try {
                //create and add search params
                params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("userName", Login.getLoggedInUser()));
                params.add(new BasicNameValuePair("dateStart", FNValidate.validateDateTime(mDateStart.getText().toString())));
                params.add(new BasicNameValuePair("dateEnd", FNValidate.validateDateTime(mDateEnd.getText().toString())));

                params.add(new BasicNameValuePair("customerKey", customerKey));
            } catch (final Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }

            if(params != null) {
                if (params.size() == 3) {
                    try {
                        //array of search values
                        JSONObject json = mJsonParser.createHttpRequest(SEARCH_NOTE_URL, "POST", params);
                        int success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            //get Json object that is inside of the 'message'
                            JSONArray tickets = new JSONArray(json.getString(TAG_MESSAGE));
                            if (tickets.length() > 0) {
                                mAllSearchResults = new ArrayList<>();
                                //assign the strings to values
                                for (int i = 0; i < tickets.length(); i++) {
                                    //create new HashMap on each loop, so the same keys can be re-used
                                    HashMap<String, String> singleResult = new HashMap<>();
                                    //get result
                                    JSONObject result = tickets.getJSONObject(i);
                                    //put result into HashMap
                                    singleResult.put("ticket", result.getString("ticketNumber"));
                                    singleResult.put("user", result.getString("userName"));
                                    singleResult.put("project", result.getString("projectNumber"));
                                    singleResult.put("well", result.getString("wellName"));
                                    singleResult.put("description", result.getString("description"));
                                    singleResult.put("bill", result.getString("billing"));
                                    singleResult.put("sDate", result.getString("dateStart"));
                                    singleResult.put("eDate", result.getString("dateEnd"));
                                    singleResult.put("sTime", result.getString("timeStart"));
                                    singleResult.put("eTime", result.getString("timeEnd"));
                                    singleResult.put("location", result.getString("location"));
                                    singleResult.put("sMile", result.getString("mileageStart"));
                                    singleResult.put("eMile", result.getString("mileageEnd"));
                                    singleResult.put("gps", result.getString("gps"));
                                    //put HashMap into ArrayList
                                    mAllSearchResults.add(singleResult);
                                    // mark success
                                    searchResultMessage = "Search Complete";
                                }
                            } else {
                                // mark no results
                                searchResultMessage = "No Results Found";
                            }
                        } else {
                            // mark no results
                            searchResultMessage = "No Results Found";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //shouldnt reach this, something's wrong wth the php
                        Log.e("JSON Exeption", "getting JSON array from josn object");
                    }
                }
            }
            return searchResultMessage;
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
            // manually layout search result into List adapter
            if (message.equals("Search Complete")) {
                //clear the listview
                mListView.setAdapter(null);
                final ListAdapter searchAdapter = new SimpleAdapter(getActivity(), mAllSearchResults,
                        R.layout.layout_search_list_item, new String[]{"ticket", "user",
                        "project", "well", "description", "bill", "sDate", "eDate", "sTime", "eTime",
                        "location", "sMile", "eMile", "gps"}, new int[]{R.id.resultTicket, R.id.resultUser,
                        R.id.resultProject, R.id.resultWell, R.id.resultDescription, R.id.resultBilling,
                        R.id.resultDateStart, R.id.resultDateEnd, R.id.resultTimeStart, R.id.resultTimeEnd,
                        R.id.resultLocation, R.id.resultMileStart, R.id.resultMileEnd, R.id.resultGps});
                //update UI
                mListView.setAdapter(searchAdapter);
                //clicking an individual search result will move the user to the edit screen
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //user motion to edit data
                        HashMap<String, String> selectedValue = (HashMap) searchAdapter.getItem(position);
                        Toast.makeText(getActivity(), "Edit ticket " + selectedValue.get("ticket"), Toast.LENGTH_SHORT).show();
                        // set extra args for update fragment
                        UpdateFieldNote updateFragment = new UpdateFieldNote();
                        Bundle args = new Bundle();
                        args.putSerializable("oldData", selectedValue);
                        updateFragment.setArguments(args);
                        // go to Edit fragment
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .add(android.R.id.content, updateFragment, "updateFragment")
                                .addToBackStack("updateFragment")
                                .commit();
                    }
                });
            }
            // update user ui
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }
}
