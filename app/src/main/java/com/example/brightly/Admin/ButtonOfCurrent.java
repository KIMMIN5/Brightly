package com.example.brightly.Admin;

import android.util.Log;

import com.example.brightly.Map.CurrentLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ButtonOfCurrent {
    private CurrentLocation currentLocation;
    private GoogleMap mMap;
    private SaveMarker saveMarker;

    public ButtonOfCurrent(CurrentLocation currentLocation, GoogleMap googleMap, SaveMarker saveMarker) {
        this.currentLocation = currentLocation;
        this.mMap = googleMap;
        this.saveMarker = saveMarker;
    }

    public void addMarkerAtCurrentLocation() {
        if (mMap != null && currentLocation != null) {
            LatLng currentLatLng = currentLocation.getCurrentLatLng();
            if (currentLatLng != null) {
                Marker marker = mMap.addMarker(new MarkerOptions().position(currentLatLng).title("현재 위치"));
                marker.setTag("currentLocation"); // 현재 위치 마커 구별을 위한 태그 설정

                saveMarker.saveMarkerPosition(currentLatLng);

                float currentZoom = mMap.getCameraPosition().zoom;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, currentZoom));
            }
        }
    }
}