package com.ssau.geolocation.service;

/**
 * Created by Илья on 22.12.2016.
 */

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int REQUEST_START = 1;
    public static final int REQUEST_END = 2;
    public static final int REQUEST_LOCATION = 3;
    public static final int REQUEST_POINT = 4;
    public static final int FAILURE_RESULT = 5;
    public static final String PACKAGE_NAME =
            "com.ssay.geocoder";
    public static final String REQUEST_CODE_EXTRA = "requestCode";
    public static final String REQUSTED_STRING_LOCATION ="stringLocation";

    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
}
