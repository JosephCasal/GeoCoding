package com.example.joseph.geocoding;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Location currentLocation;

    int currentmaptype = 0;

    Marker currentMarker;

    int markerswitch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currentLocation = getIntent().getParcelableExtra("location");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        currentMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Marker in current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
    }

    public void changeMapType(View view) {

        switch (currentmaptype){
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                currentmaptype = 1;
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                currentmaptype = 2;
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                currentmaptype = 0;
                break;
        }

    }

    public void addCustomMarker(View view) {

        currentMarker.remove();

        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if(markerswitch == 0) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Marker in current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.sharingan_small)));
            markerswitch = 1;
        }else{
            currentMarker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Marker in current location"));
            markerswitch = 0;

        }

    }
}
