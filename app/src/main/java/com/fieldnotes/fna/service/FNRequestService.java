package com.fieldnotes.fna.service;

import com.fieldnotes.fna.model.FNReponseType;
import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.parser.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Service for sending requests to FNP
 */

public class FNRequestService {
    private static final String LOGIN_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_login.php";
    private static final String ADD_NOTE_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_addNote.php";
    private static final String UPDATE_NOTE_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_updateNote.php";
    private static final String SEARCH_NOTE_URL = "http://www.fieldnotesfn.com/FN_PROCESSOR/FN_searchNotes.php";
    private static final String HTTP_METHOD_POST = "POST";

    private static final String TAG_STATUS = "status";
    private static final String TAG_MESSAGE = "message";

    public static FNResponse sendRequest(FNRequest request) throws JSONException {
        JSONParser mJsonParser = new JSONParser();
        JSONObject json;

        switch (request.getRequestType()) {
            case LOGIN:
                json = mJsonParser.createHttpRequest(LOGIN_URL, HTTP_METHOD_POST, request.getRequestParams());

                return FNResponse.newBuilder()
                        .setStatustype(convertResponseType(json.getString(TAG_STATUS)))
                        .setMessage(json.getString(TAG_MESSAGE))
                        .build();
            case ADD:
                json = mJsonParser.createHttpRequest(ADD_NOTE_URL, HTTP_METHOD_POST, request.getRequestParams());

                return FNResponse.newBuilder()
                        .setStatustype(convertResponseType(json.getString(TAG_STATUS)))
                        .setMessage(json.getString(TAG_MESSAGE))
                        .build();
            case UPDATE:
                json = mJsonParser.createHttpRequest(UPDATE_NOTE_URL, HTTP_METHOD_POST, request.getRequestParams());

                return FNResponse.newBuilder()
                        .setStatustype(convertResponseType(json.getString(TAG_STATUS)))
                        .setMessage(json.getString(TAG_MESSAGE))
                        .build();

            case SEARCH:
                json = mJsonParser.createHttpRequest(SEARCH_NOTE_URL, HTTP_METHOD_POST, request.getRequestParams());

                FNResponse.Builder responseBuilder = FNResponse.newBuilder();
                responseBuilder.setStatustype(convertResponseType(json.getString(TAG_STATUS)));
                if (json.getString(TAG_STATUS).equals("success")) {
                    //get Json object that is inside of the 'message'
                    JSONArray tickets = new JSONArray(json.getString(TAG_MESSAGE));

                    if (tickets.length() > 0) {
                        ArrayList<HashMap<String, String>> allSearchResults = new ArrayList<>();
                        //assign the strings to values
                        for (int i = 0; i < tickets.length(); i++) {
                            //create new HashMap on each loop, so the same keys can be re-used
                            HashMap<String, String> singleResult = new HashMap<>();
                            //get result
                            JSONObject result = tickets.getJSONObject(i);
                            // map the result
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
                            allSearchResults.add(singleResult);
                        }
                        responseBuilder.setMessage("Search Complete");
                        responseBuilder.setResultList(allSearchResults);
                    } else {
                        responseBuilder.setMessage("No Results Found");
                    }
                } else {
                    responseBuilder.setMessage("Search Failed");
                }

                return responseBuilder.build();
            default:
                return FNResponse.newBuilder()
                        .setStatustype(FNReponseType.FAILURE)
                        .setMessage("Could not set Request Type")
                        .build();
        }
    }

    private static FNReponseType convertResponseType(String status) {
        switch (status) {
            case "success":
                return FNReponseType.SUCCESS;
            case "failure":
            default:
                return FNReponseType.FAILURE;
        }
    }
}
