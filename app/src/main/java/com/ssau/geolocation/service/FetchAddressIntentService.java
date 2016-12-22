package com.ssau.geolocation.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Илья on 22.12.2016.
 */

public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(null);
    }

    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        int requestCode = intent.getIntExtra(Constants.REQUEST_CODE_EXTRA, -1);
        if (requestCode == Constants.REQUEST_LOCATION) {
            getLocationFromAddress(this, intent.getStringExtra(Constants.REQUSTED_STRING_LOCATION));
        } else {
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                //   errorMessage = getString(R.string.service_not_available);
                //  Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                // errorMessage = getString(R.string.invalid_lat_long_used);
                //Log.e(TAG, errorMessage + ". " +
                //       "Latitude = " + location.getLatitude() +
                //       ", Longitude = " +
                //       location.getLongitude(), illegalArgumentException);
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    //  errorMessage = getString(R.string.no_address_found);
                    //  Log.e(TAG, errorMessage);
                }
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                addressFragments.add(address.getThoroughfare() != null ? address.getThoroughfare() : address.getLocality() != null ? address.getLocality() : address.getAdminArea());
                deliverResultToReceiver(requestCode,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments));
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

    private void deliverResultToReceiver(int resultCode, LatLng location) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.LOCATION_DATA_EXTRA, location);
        mReceiver.send(resultCode, bundle);
    }

    public void getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                deliverResultToReceiver(Constants.SUCCESS_RESULT, new LatLng(0, 0));
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        deliverResultToReceiver(Constants.REQUEST_LOCATION, p1);

    }
}
