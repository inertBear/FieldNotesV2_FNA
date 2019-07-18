package com.fieldnotes.fna.service;

import com.fieldnotes.fna.model.FNResponseType;
import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.parser.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.fieldnotes.fna.constants.FNConstants.ADD_NOTE_URL;
import static com.fieldnotes.fna.constants.FNConstants.BILLING_TAG;
import static com.fieldnotes.fna.constants.FNConstants.DATE_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.DATE_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.DESCRIPTION_TAG;
import static com.fieldnotes.fna.constants.FNConstants.GPS_TAG;
import static com.fieldnotes.fna.constants.FNConstants.HTTP_REQUEST_METHOD_POST;
import static com.fieldnotes.fna.constants.FNConstants.LOCATION_TAG;
import static com.fieldnotes.fna.constants.FNConstants.LOGIN_URL;
import static com.fieldnotes.fna.constants.FNConstants.MILEAGE_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.MILEAGE_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.PROJECT_NUMBER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.RESPONSE_MESSAGE_TAG;
import static com.fieldnotes.fna.constants.FNConstants.RESPONSE_STATUS_FAILURE;
import static com.fieldnotes.fna.constants.FNConstants.RESPONSE_STATUS_SUCCESS;
import static com.fieldnotes.fna.constants.FNConstants.RESPONSE_STATUS_TAG;
import static com.fieldnotes.fna.constants.FNConstants.RESPONSE_TOKEN_TAG;
import static com.fieldnotes.fna.constants.FNConstants.SEARCH_NOTES_URL;
import static com.fieldnotes.fna.constants.FNConstants.TICKET_NUMBER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TIME_END_TAG;
import static com.fieldnotes.fna.constants.FNConstants.TIME_START_TAG;
import static com.fieldnotes.fna.constants.FNConstants.UPDATE_NOTE_URL;
import static com.fieldnotes.fna.constants.FNConstants.USER_TAG;
import static com.fieldnotes.fna.constants.FNConstants.WELLNAME_TAG;

/**
 * Service for sending requests to FNP
 */
public class FNRequestService {

    public static FNResponse sendRequest(FNRequest request) throws JSONException {
        JSONParser mJsonParser = new JSONParser();
        JSONObject json;

        switch (request.getRequestType()) {
            case LOGIN:
                json = mJsonParser.createHttpRequest(LOGIN_URL, HTTP_REQUEST_METHOD_POST, request.getRequestParams());

                if (json.getString(RESPONSE_TOKEN_TAG) != null) {
                    return FNResponse.newBuilder()
                            .setStatustype(convertResponseType(json.getString(RESPONSE_STATUS_TAG)))
                            .setMessage(json.getString(RESPONSE_MESSAGE_TAG))
                            .setToken(json.getString(RESPONSE_TOKEN_TAG))
                            .build();
                } else {
                    return FNResponse.newBuilder()
                            .setStatustype(FNResponseType.FAILURE)
                            .setMessage("No Login Token Received")
                            .build();
                }
            case ADD:
                json = mJsonParser.createHttpRequest(ADD_NOTE_URL, HTTP_REQUEST_METHOD_POST, request.getRequestParams());

                return FNResponse.newBuilder()
                        .setStatustype(convertResponseType(json.getString(RESPONSE_STATUS_TAG)))
                        .setMessage(json.getString(RESPONSE_MESSAGE_TAG))
                        .build();
            case UPDATE:
                json = mJsonParser.createHttpRequest(UPDATE_NOTE_URL, HTTP_REQUEST_METHOD_POST, request.getRequestParams());

                return FNResponse.newBuilder()
                        .setStatustype(convertResponseType(json.getString(RESPONSE_STATUS_TAG)))
                        .setMessage(json.getString(RESPONSE_MESSAGE_TAG))
                        .build();

            case SEARCH:
                json = mJsonParser.createHttpRequest(SEARCH_NOTES_URL, HTTP_REQUEST_METHOD_POST, request.getRequestParams());

                FNResponse.Builder responseBuilder = FNResponse.newBuilder();
                responseBuilder.setStatustype(convertResponseType(json.getString(RESPONSE_STATUS_TAG)));
                if (json.getString(RESPONSE_STATUS_TAG).equals(RESPONSE_STATUS_SUCCESS)) {
                    //get Json object that is inside of the 'message'
                    JSONArray tickets = new JSONArray(json.getString(RESPONSE_MESSAGE_TAG));

                    if (tickets.length() > 0) {
                        ArrayList<HashMap<String, String>> allSearchResults = new ArrayList<>();
                        //assign the strings to values
                        for (int i = 0; i < tickets.length(); i++) {
                            //create new HashMap on each loop, so the same keys can be re-used
                            HashMap<String, String> singleResult = new HashMap<>();
                            //get result
                            JSONObject result = tickets.getJSONObject(i);
                            // map the result
                            singleResult.put(TICKET_NUMBER_TAG, result.getString(TICKET_NUMBER_TAG));
                            singleResult.put(USER_TAG, result.getString(USER_TAG));
                            singleResult.put(PROJECT_NUMBER_TAG, result.getString(PROJECT_NUMBER_TAG));
                            singleResult.put(WELLNAME_TAG, result.getString(WELLNAME_TAG));
                            singleResult.put(DESCRIPTION_TAG, result.getString(DESCRIPTION_TAG));
                            singleResult.put(BILLING_TAG, result.getString(BILLING_TAG));
                            singleResult.put(DATE_START_TAG, result.getString(DATE_START_TAG));
                            singleResult.put(DATE_END_TAG, result.getString(DATE_END_TAG));
                            singleResult.put(TIME_START_TAG, result.getString(TIME_START_TAG));
                            singleResult.put(TIME_END_TAG, result.getString(TIME_END_TAG));
                            singleResult.put(LOCATION_TAG, result.getString(LOCATION_TAG));
                            singleResult.put(MILEAGE_START_TAG, result.getString(MILEAGE_START_TAG));
                            singleResult.put(MILEAGE_END_TAG, result.getString(MILEAGE_END_TAG));
                            singleResult.put(GPS_TAG, result.getString(GPS_TAG));
                            //put HashMap into ArrayList
                            allSearchResults.add(singleResult);
                        }
                        responseBuilder.setMessage("Search Complete");
                        responseBuilder.setResultList(allSearchResults);
                    } else {
                        responseBuilder.setMessage(json.getString(RESPONSE_MESSAGE_TAG));
                    }
                } else {
                    responseBuilder.setMessage(json.getString(RESPONSE_MESSAGE_TAG));
                }

                return responseBuilder.build();
            default:
                return FNResponse.newBuilder()
                        .setStatustype(FNResponseType.FAILURE)
                        .setMessage("Could not set Request Type")
                        .build();
        }
    }

    private static FNResponseType convertResponseType(String status) {
        switch (status) {
            case RESPONSE_STATUS_SUCCESS:
                return FNResponseType.SUCCESS;
            case RESPONSE_STATUS_FAILURE:
            default:
                return FNResponseType.FAILURE;
        }
    }
}
