package com.example.brightly.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class CurrentLocation {

    private static final String TAG = "CurrentLocation";
    private Context context;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LatLng currentLatLng;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 30;
    private boolean isFirstLocationUpdate = true;

    public CurrentLocation(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    // 위치 업데이트 리스너 인터페이스 정의
    public interface LocationUpdateListener {
        void onLocationUpdated(LatLng newLocation);
    }

    private LocationUpdateListener locationUpdateListener;

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.locationUpdateListener = listener;
    }

    public void initializeLocationListener() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (googleMap != null) {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.d(TAG, "Location updated: " + currentLatLng.toString());
                    // 지도 카메라 이동
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 25)); // 또는 원하는 줌 레벨 설정
                    // 최초 위치 변경 이벤트에서 지도의 카메라 업데이트 (단, 한 번만 실행)
                    if (isFirstLocationUpdate) {
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 25));
                        isFirstLocationUpdate = false;
                    }
                } else {
                    Log.d(TAG, "GoogleMap is null");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onProviderDisabled(String provider) { }
        };

        // 위치 권한 확인 및 위치 업데이트 요청
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