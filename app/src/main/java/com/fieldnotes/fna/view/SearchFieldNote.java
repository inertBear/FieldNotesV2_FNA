package com.fieldnotes.fna.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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

import com.fieldnotes.fna.R;
import com.fieldnotes.fna.asynctask.FNAsyncTask;
import com.fieldnotes.fna.model.FNResponseType;
import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNRequestType;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.service.FNRequestService;
import com.fieldnotes.fna.validation.FNValidate;
import com.fieldnotes.fna.view.datetime.SelectDateFragment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
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
import static com.fieldnotes.fna.constants.FNConstants.TICKET_NUMBER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TIME_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TIME_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TOKEN_TAG;
import static com.fieldnotes.fna.constants.FNConstants.USER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.WELLNAME_TAG;

public class SearchFieldNote extends Fragment {
    private TextView mDateStart;
    private TextView mDateEnd;
    private ListView mListView;
    private ArrayList<HashMap<String, String>> mAllSearchResults;

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
        mListView = rootView.findViewById(android.R.id.list);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // detail views
        mDateStart = view.findViewById((R.id.SearchDateStart));
        mDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(mDateStart);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        mDateEnd = view.findViewById(R.id.SearchDateEnd);
        mDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(mDateEnd);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SearchNoteAsyncTask(getContext()).execute();
            }
        });
    }

    /**
     * Asynchronous SearchNote with progress bar
     * <p>
     * This class runs a background thread to search for field notes created by the logged in user
     * by a date range. Results are listed as fully qualified results. These results can be selected
     * by the user to edit the values within each note
     */
    class SearchNoteAsyncTask extends FNAsyncTask {

        SearchNoteAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackground(String... strings) {
            //get values from preferences
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String username = prefs.getString(PREF_USERNAME, "");
            String token = prefs.getString(PREF_TOKEN, "");

            try {
                // create search params
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(USER_TAG, username));
                params.add(new BasicNameValuePair(DATE_START_TAG, FNValidate.validateDateTime(mDateStart.getText().toString())));
                params.add(new BasicNameValuePair(DATE_END_TAG, FNValidate.validateDateTime(mDateEnd.getText().toString())));
                params.add(new BasicNameValuePair(TOKEN_TAG, token));

                // build FNRequest
                FNRequest request = FNRequest.newBuilder()
                        .setRequestType(FNRequestType.SEARCH)
                        .setRequestParams(params)
                        .build();

                // use request service to send request to FNP
                FNResponse response = FNRequestService.sendRequest(request);

                if (response.getResponseType().equals(FNResponseType.SUCCESS)) {
                    mAllSearchResults = response.getResultList();
                }

                return response.getMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException ex) {
                return "Please enter date range";
            }
            return null;
        }

        /**
         * after the search results have been received, set list adapter with search results and set
         * onClickListener to Edit Note Fragment
         **/
        @Override
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
                        R.layout.layout_search_list_item, new String[]{TICKET_NUMBER_TAG, USER_TAG,
                        PROJECT_NUMBER_TAG, WELLNAME_TAG, DESCRIPTION_TAG, BILLING_TAG, DATE_START_TAG, DATE_END_TAG, TIME_START_TAG, TIME_END_TAG,
                        LOCATION_TAG, MILEAGE_START_TAG, MILEAGE_END_TAG, GPS_TAG}, new int[]{R.id.resultTicket, R.id.resultUser,
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
                        Toast.makeText(getActivity(), "Edit ticket " + selectedValue.get(TICKET_NUMBER_TAG), Toast.LENGTH_SHORT).show();
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
