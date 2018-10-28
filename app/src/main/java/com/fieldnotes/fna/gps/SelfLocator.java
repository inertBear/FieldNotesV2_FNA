package com.fieldnotes.fna.gps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;


/**
 * This class requests location permissions and finds the users location when the
 * "save GPS coords" checkbox is selected while adding a new FieldNote or editing and existing FieldNote
 *
 * Created by DevHunter on 7/10/2018.
 */

public class SelfLocator extends AsyncTask<String, String, String> {
    private static String TAG = "SelfLocator";
    private static String mCurrentLocation = "0,0";
    private final WeakReference<Context> mWeakContext;
    private ProgressDialog mProgressDialog;
    private double latitude = 0.0;
    private double longitude = 0.0;

    public SelfLocator(Context context) {
        this.mWeakContext = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mWeakContext.get());
        mProgressDialog.setMessage("Getting Location...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        Location location;
        LocationManager locationManager = (LocationManager) mWeakContext.get().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v(TAG, "GPS location updated: " + location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.v(TAG, "GPS status changed: " + status);
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.v(TAG, "Status Changed: Out of Service");
                        Toast.makeText(mWeakContext.get(), "Status Changed: Out of Service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.v(TAG, "Status Changed: Temporarily Unavailable");
                        Toast.makeText(mWeakContext.get(), "Status Changed: Temporarily Unavailable",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.v(TAG, "Status Changed: Available");
                        Toast.makeText(mWeakContext.get(), "Status Changed: Available",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.v(TAG, "GPS provider enabled: " + provider);
                Toast.makeText(mWeakContext.get(), "GPS provider has been enabled",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.v(TAG, "GPS provider disabled: " + provider);
                Toast.makeText(mWeakContext.get(), "GPS provider has been disabled",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mWeakContext.get().startActivity(intent);
            }
        };

        // get last-known location
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mWeakContext.get(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mWeakContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                try {
                    ActivityCompat.requestPermissions((Activity) mWeakContext.get(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } catch (SecurityException e) {
                    Log.d(TAG, "Requesting Location Permissions");
                }
                return null;
            }
            locationManager.requestLocationUpdates("gps", 500, 0, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        mCurrentLocation = String.valueOf(latitude) + ", " + String.valueOf(longitude);
        return mCurrentLocation;
    }

    protected void onPostExecute(String message) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        //update user ui
        if (message != null) {
            Toast.makeText(mWeakContext.get(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public static String getCurrentLocation(){
        return mCurrentLocation;
    }
}
