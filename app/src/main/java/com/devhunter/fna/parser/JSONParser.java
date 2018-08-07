package com.devhunter.fna.parser;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Creates an HTTP request and parses the JSON values coming back from the PHP Server Code. Returns
 * a JSON object to the Fragments to be read
 *
 * Created on 5/2/2018.
 */

public class JSONParser {

    static InputStream mInputStream = null;
    static JSONObject mJsonObj;
    static String mJsonString = "";

    public JSONParser() {
    }

    public JSONObject createHttpRequest(String url, String method,
                                        List<NameValuePair> params) {
        // Make new HTTP request
        mInputStream = null;
        mJsonObj = null;
        mJsonString = "";
        try {
            // checking request method
            if (method.equals("POST")) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                mInputStream = httpEntity.getContent();
            } else if (method.equals("GET")) {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                mInputStream = httpEntity.getContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("POST exception", "An exception in POST");
        }

        try {
            // read response data and build string
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    mInputStream, "iso-8859-1"), 8);
            StringBuilder str = new StringBuilder();
            String strLine = null;
            while ((strLine = reader.readLine()) != null) {
                str.append(strLine + "\n");
            }
            mInputStream.close();
            mJsonString = str.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BufferedReader", "An Exception in building the string");
        }
        // parse the string into JSON object
        try {
            mJsonObj = new JSONObject(mJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            // should only reach this if the PHP is wrong - check server for errors
            Log.e("JSON Parse", "Make sure you check you PHP");
        }
        return mJsonObj;
    }
}