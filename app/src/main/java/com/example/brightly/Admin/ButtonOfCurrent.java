package com.example.brightly.Admin;

import android.util.Log;

import com.example.brightly.Map.CurrentLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
        LatLng currentLatLng = currentLocation.getCurrentLatLng();
        if (currentLatLng != null) {
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("현재 위치"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            saveMarker.saveMarkerPosition(currentLatLng);
        } else {
            Log.d("ButtonOfCurrent", "Current location is null");
        }
    }
}
