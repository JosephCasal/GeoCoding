package com.example.joseph.geocoding;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoCodeIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.joseph.geocoding.action.FOO";
    private static final String ACTION_BAZ = "com.example.joseph.geocoding.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.joseph.geocoding.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.joseph.geocoding.extra.PARAM2";


    protected ResultReceiver resultReceiver;
    private static final String TAG = "FetchAddyIntentService";


    public GeoCodeIntentService() {
        super("GeoCodeIntentService");
    }

//    /**
//     * Starts this service to perform action Foo with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    // TODO: Customize helper method
//    public static void startActionFoo(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, GeoCodeIntentService.class);
//        intent.setAction(ACTION_FOO);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }
//
//    /**
//     * Starts this service to perform action Baz with the given parameters. If
//     * the service is already performing a task this action will be queued.
//     *
//     * @see IntentService
//     */
//    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, GeoCodeIntentService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
//        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        resultReceiver = intent.getParcelableExtra("RECEIVER");
        int fetchType = intent.getIntExtra("FETCH_TYPE_EXTRA", 0);

        String errorMessage = "";

        //using address
        if(fetchType == 1) {
            String name = intent.getStringExtra("LOCATION_NAME_DATA_EXTRA");
            try {
                addresses = geocoder.getFromLocationName(name, 1);
            } catch (IOException e) {
                Log.e(TAG, "Service not available", e);
            }
        }
        //using location
        else if(fetchType == 2) {
            Location location = intent.getParcelableExtra(
                    "LOCATION_DATA_EXTRA");

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException ioException) {
                Log.e(TAG, "Service Not Available", ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e(TAG, "Invalid Latitude or Longitude Used" + ". " +
                        "Latitude = " + location.getLatitude() + ", Longitude = " +
                        location.getLongitude(), illegalArgumentException);
            }
        }
        else {
            errorMessage = "Unknown Type";
        }

        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Not Found";
            }
            //FAILURE_RESULT = 0
            //SUCCESS_RESULT = 1
            deliverResultToReceiver(0, errorMessage, null);
        } else {
            for(Address address : addresses) {
                String outputAddress = "";
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    outputAddress += " --- " + address.getAddressLine(i);
                }
            }
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
//            (R.string.address_found));
            //FAILURE_RESULT = 0
            //SUCCESS_RESULT = 1
            deliverResultToReceiver(1,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments), address);
        }

    }

    //FAILURE_RESULT = 0
    //SUCCESS_RESULT = 1
    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("RESULT_ADDRESS", address);
        bundle.putString("RESULT_DATA_KEY", message);
        resultReceiver.send(resultCode, bundle);
    }

//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionFoo(String param1, String param2) {
//        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
