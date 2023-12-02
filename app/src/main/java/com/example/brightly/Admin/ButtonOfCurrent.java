package com.example.brightly.Admin;

import android.util.Log;

import com.example.brightly.Map.CurrentLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
        if (mMap != null && currentLocation != null) {
            LatLng currentLatLng = currentLocation.getCurrentLatLng();
            if (currentLatLng != null) {
                // 현재 위치에 마커 추가
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("현재 위치")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                // SaveMarker 클래스를 사용하여 마커 위치 저장
                saveMarker.saveMarkerPosition(currentLatLng);

                // 현재 카메라 줌 레벨을 가져옵니다.
                float currentZoom = mMap.getCameraPosition().zoom;

                // 현재 위치로 카메라를 이동하되, 줌 레벨은 유지합니다.
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, currentZoom));
            }
        }
    }
}
