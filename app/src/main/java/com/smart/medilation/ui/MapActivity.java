package com.smart.medilation.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.smart.medilation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;

    private ImageView imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(v -> onBackPressed());

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Check location permission
        if (isLocationPermissionGranted()) {
            getCurrentLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void getCurrentLocation() {
        if (isLocationPermissionGranted()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    drawPolyline(currentLatLng, destinationLatLng);

                    // Create a LatLng object for the current and destination location
                    LatLng destinationLatLng = new LatLng(LAT, LONG);

                    requestDirections(getDirectionsUrl(currentLatLng.latitude, currentLatLng.longitude,
                            destinationLatLng.latitude, destinationLatLng.longitude));

                }
            });
        }
    }

    private void requestDirections(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Handle the directions response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray routesArray = jsonResponse.getJSONArray("routes");

                        // Assuming a single route for simplicity
                        if (routesArray.length() > 0) {
                            JSONObject routeObject = routesArray.getJSONObject(0);
                            JSONArray legsArray = routeObject.getJSONArray("legs");

                            // Assuming a single leg for simplicity
                            if (legsArray.length() > 0) {
                                JSONObject legObject = legsArray.getJSONObject(0);
                                JSONArray stepsArray = legObject.getJSONArray("steps");

                                // Extract and decode the polyline
                                List<LatLng> points = new ArrayList<>();
                                for (int i = 0; i < stepsArray.length(); i++) {
                                    JSONObject stepObject = stepsArray.getJSONObject(i);
                                    JSONObject polylineObject = stepObject.getJSONObject("polyline");
                                    String encodedPolyline = polylineObject.getString("points");

                                    List<LatLng> decodedPoints = decodePolyline(encodedPolyline);
                                    points.addAll(decodedPoints);
                                }

                                PolylineOptions polylineOptions = new PolylineOptions()
                                        .addAll(points)
                                        .color(Color.RED)
                                        .width(5);
                                googleMap.addPolyline(polylineOptions);

                                if (!points.isEmpty())
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 12));
                                else
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT, LONG), 12));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle the error
                    Toast.makeText(this, "Error retrieving directions", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }


    private String getDirectionsUrl(double originLat, double originLng, double destLat, double destLng) {
        String apiUrl = "https://maps.googleapis.com/maps/api/directions/json?";
        String origin = "origin=" + originLat + "," + originLng;
        String destination = "destination=" + destLat + "," + destLng;
        String apiKey = getString(R.string.google_maps_api_key);

        return apiUrl + origin + "&" + destination + "&key=" + apiKey;
    }


    private List<LatLng> decodePolyline(String encodedPolyline) {
        List<LatLng> latLngList = new ArrayList<>();
        int index = 0;
        int len = encodedPolyline.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;

            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng latLng = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            latLngList.add(latLng);
        }

        return latLngList;
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }
}