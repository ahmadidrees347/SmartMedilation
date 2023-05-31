package com.smart.medilation.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smart.medilation.R;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Set up the map and other configurations
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        googleMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(LAT, LONG);

        drawPathToLocation(LAT, LONG);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void drawPathToLocation(double destinationLat, double destinationLng) {
        // Get the user's current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            double currentLat = location.getLatitude();
            double currentLng = location.getLongitude();

            // Create a LatLng object for the current and destination location
            LatLng originLatLng = new LatLng(currentLat, currentLng);
            LatLng destinationLatLng = new LatLng(destinationLat, destinationLng);

            // Define the API request URL
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                    currentLat + "," + currentLng +
                    "&destination=" + destinationLat + "," + destinationLng +
                    "&key=YOUR_API_KEY";

            // Make an API request using Volley, Retrofit, or any other networking library
            // Parse the response to get the encoded polyline
            // Decode the polyline to get a list of LatLng points

            // Draw the polyline on the map
//            PolylineOptions polylineOptions = new PolylineOptions()
//                    .addAll(decodedPolylinePoints)
//                    .width(5f)
//                    .color(Color.RED);
//            mMap.addPolyline(polylineOptions);

            // Zoom the map to fit the polyline and the current location
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(originLatLng);
            builder.include(destinationLatLng);
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

}