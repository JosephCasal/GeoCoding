package com.example.joseph.geocoding;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.joseph.geocoding.model.GeoCodingResult;
import com.example.joseph.geocoding.model.Result;
import com.example.joseph.geocoding.remote.GeoCodingData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final String address = "1600 Amphitheatre Parkway, Mountain View, CA";
    public static final Double lat = 40.714224;
    public static final Double lng = -73.961452;
    public static final String latlng = "40.714224,-73.961452";
    private static final String TAG = "MainActivityTag";
    List<Result> resultList = new ArrayList<>();
    List<Result> reverseList = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView infoText;

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        infoText = (TextView) findViewById(R.id.infoText);

        //using google geocode api
        //geocode = address -> latlng
        GeoCodingData.searchGeoCode(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<GeoCodingResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(@NonNull GeoCodingResult geoCodingResult) {
                        resultList = geoCodingResult.getResults();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                        Log.d(TAG, "onComplete: Geocoding: formatted address: " + resultList.get(0).getFormattedAddress());

                        Log.d(TAG, "onComplete: Geocoding: lat: " + resultList.get(0).getGeometry().getLocation().getLat() + ", lng: " + resultList.get(0).getGeometry().getLocation().getLng());

                    }
                });


        //using google geocode api
        //reverse geocode = latlng -> address
        GeoCodingData.reverseGeoCode(latlng)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<GeoCodingResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(@NonNull GeoCodingResult geoCodingResult) {
                        reverseList = geoCodingResult.getResults();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Reverse Geocoding: lat: " + reverseList.get(0).getGeometry().getLocation().getLat() + ", lng: " + reverseList.get(0).getGeometry().getLocation().getLng());
                        Log.d(TAG, "onComplete: Reverse Geocoding: formatted address: " + reverseList.get(0).getFormattedAddress());
                    }
                });



        //Using Geocode android class
        AddressResultReceiver addressResultReceiver = new AddressResultReceiver(null);

        Intent intent = new Intent(this, GeoCodeIntentService.class);
        intent.putExtra("RECEIVER", addressResultReceiver);
        //fetchType = 1 = using address
        //fetchType = 2 = using location
        int fetchType = 1;
        intent.putExtra("FETCH_TYPE_EXTRA", fetchType);
        if(fetchType == 1) {
//            if(addressEdit.getText().length() == 0) {
//                Toast.makeText(this, "Please enter an address name", Toast.LENGTH_LONG).show();
//                return;
//            }
            intent.putExtra("LOCATION_NAME_DATA_EXTRA", address);
        }
        else {
//            if(latitudeEdit.getText().length() == 0 || longitudeEdit.getText().length() == 0) {
//                Toast.makeText(this,
//                        "Please enter latitude/longitude values",
//                        Toast.LENGTH_LONG).show();
//                return;
//            }
            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lng);
            intent.putExtra("LOCATION_DATA_EXTRA", location);
        }
        progressBar.setVisibility(View.VISIBLE);
        Log.e(TAG, "Starting Service");
        startService(intent);


        //getting user's current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermission();


    }

    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: not granted");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Log.d(TAG, "checkPermission: shouldshowrequestpermissionrationale");

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Please allow GeoCode App to access your location");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                /*Intent intent = new Intent(Settings.ACTION_SETTINGS) ;
                this.startActivity(intent);
                 */
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);


                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

//                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);


            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

                Log.d(TAG, "checkPermission: requestpermissions");
            }
        }else{
            Log.d(TAG, "onCreate: already granted");
            updateLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: ");
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "onRequestPermissionsResult: granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    updateLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void updateLocation() {
        Log.d(TAG, "updateLocation: ");
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        currentLocation = location;
                        if(location != null)
                            Log.d(TAG, "onSuccess: " + location.toString());
                        else
                            Log.d(TAG, "onSuccess: location null");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@android.support.annotation.NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000, 100, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + location.toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "onStatusChanged: " + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "onProviderEnabled: " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "onProviderDisabled: " + s);
    }

    public void goToMaps(View view) {

        if(currentLocation != null){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("location", currentLocation);
            startActivity(intent);
        }else{
            Log.d(TAG, "goToMaps: location null");
        }

    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == 1) {
                final Address address = resultData.getParcelable("RESULT_ADDRESS");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        infoText.setText("Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" +
                                "Address: " + resultData.getString("RESULT_DATA_KEY"));

                        Log.d(TAG, "run: " + "Latitude: " + address.getLatitude() + "\n" +
                                "Longitude: " + address.getLongitude() + "\n" +
                                "Address: " + resultData.getString("RESULT_DATA_KEY"));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        infoText.setText(resultData.getString("RESULT_DATA_KEY"));
                    }
                });
            }
        }

    }
}
