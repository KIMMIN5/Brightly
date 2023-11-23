package com.example.brightly;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.maps.model.LatLng;
import java.util.HashSet;
import java.util.Set;

public class SaveMarker {
    private SharedPreferences sharedPreferences;

    public SaveMarker(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MarkerPref", Context.MODE_PRIVATE);
    }

    public void saveMarkerPosition(LatLng latLng) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> currentMarkers = sharedPreferences.getStringSet("markers", new HashSet<>());
        Set<String> updatedMarkers = new HashSet<>(currentMarkers); // 새로운 Set 인스턴스 생성
        String latLngString = latLng.latitude + "," + latLng.longitude;
        updatedMarkers.add(latLngString); // 새로운 Set에 마커 위치 추가
        editor.putStringSet("markers", updatedMarkers); // 새로운 Set 저장
        editor.apply();
    }

    public void removeMarkerPosition(LatLng latLngToRemove) {
        Set<String> currentMarkers = sharedPreferences.getStringSet("markers", new HashSet<>());
        Set<String> updatedMarkers = new HashSet<>(currentMarkers); // 새로운 Set 인스턴스 생성
        String latLngToRemoveString = latLngToRemove.latitude + "," + latLngToRemove.longitude;

        if (updatedMarkers.contains(latLngToRemoveString)) {
            updatedMarkers.remove(latLngToRemoveString); // 새로운 Set에서 마커 위치 제거
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("markers", updatedMarkers); // 새로운 Set 저장
            editor.apply();
        }
    }

    public Set<LatLng> loadAllMarkerPositions() {
        Set<String> markersStringSet = sharedPreferences.getStringSet("markers", new HashSet<>());
        Set<LatLng> latLngs = new HashSet<>();

        for (String markerString : markersStringSet) {
            String[] parts = markerString.split(",");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            latLngs.add(new LatLng(latitude, longitude));
        }
        return latLngs;
    }
}
