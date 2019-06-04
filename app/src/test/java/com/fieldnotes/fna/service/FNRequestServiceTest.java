package com.fieldnotes.fna.service;

import com.fieldnotes.fna.model.FNRequest;
import com.fieldnotes.fna.model.FNRequestType;
import com.fieldnotes.fna.model.FNResponse;
import com.fieldnotes.fna.model.FNResponseType;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for communicating with FNP
 */
public class FNRequestServiceTest {
    private String mRequestingUser = "User";
    private String mProductKey = "1159616266";

    private String mUser = "userName";
    private String mWell = "wellName";
    private String mSDate = "dateStart";
    private String mEDate = "dateEnd";
    private String mSTime = "timeStart";
    private String mETime = "timeEnd";
    private String mSMile = "mileageStart";
    private String mEMile = "mileageEnd";
    private String mProj = "projectNumber";
    private String mDesc = "description";
    private String mBill = "billing";
    private String mLoc = "location";

    @Before
    public void setup() {

    }

    @After
    public void teardown() {

    }

    @Test
    @Ignore
    public void requestServiceTest() throws JSONException {
        // ADD
        List<NameValuePair> addParams = new ArrayList<>();
        addParams.add(new BasicNameValuePair(mUser, mUser));
        addParams.add(new BasicNameValuePair(mWell, mWell));
        addParams.add(new BasicNameValuePair(mSDate, mSDate));
        addParams.add(new BasicNameValuePair(mSTime, mSTime));
        addParams.add(new BasicNameValuePair(mSMile, mSMile));
        addParams.add(new BasicNameValuePair(mDesc, mDesc));
        addParams.add(new BasicNameValuePair(mEMile, mEMile));
        addParams.add(new BasicNameValuePair(mEDate, mEDate));
        addParams.add(new BasicNameValuePair(mETime, mETime));
        addParams.add(new BasicNameValuePair(mProj, mProj));
        addParams.add(new BasicNameValuePair(mBill, mBill));
        addParams.add(new BasicNameValuePair(mLoc, mLoc));

        FNRequest addRequest = FNRequest.newBuilder()
                .setRequestType(FNRequestType.ADD)
                .setProductKey(mProductKey)
                .setRequestingUser(mRequestingUser)
                .setRequestParams(addParams)
                .setTimestamp(Date.from(Instant.now()))
                .setMetadata(null)
                .build();

        FNResponse addResponse = FNRequestService.sendRequest(addRequest);

        assertEquals(FNResponseType.SUCCESS, addResponse.getResponseType());
        assertEquals("FieldNote Added to Database", addResponse.getMessage());

        // send request through the service to the db
        // assert FNResponse
        // do a search request for that record
        // assert FNResponse
        // do an update for that record
        // assert FNResponse
    }
}