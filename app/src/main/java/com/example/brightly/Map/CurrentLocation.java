package com.example.brightly.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentLocation {

    private static final String TAG = "CurrentLocation";
    private Context context;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LatLng currentLatLng;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 30;
    private Marker currentLocationMarker;

    public CurrentLocation(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    // 이 메소드는 위치 권한이 부여된 후에 호출되어야 합니다.
    public void initializeLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 여기에서 googleMap이 null인지 확인
                if (googleMap != null) {
                    if (location != null) {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "Location updated: " + currentLatLng.toString());
                        if (currentLocationMarker == null) {
                            currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("현재 위치")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        } else {
                            currentLocationMarker.setPosition(currentLatLng);
                        }

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                } else {
                    Log.d(TAG, "GoogleMap is null");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged: Provider " + provider + " status: " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled: Provider " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: Provider " + provider);
            }
        };

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } else {
            Log.d(TAG, "Location permission not granted");
        }
    }

    public LatLng getCurrentLatLng() {
        return currentLatLng;
    }
}
