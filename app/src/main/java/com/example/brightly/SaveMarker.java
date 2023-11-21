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
        Set<String> markers = sharedPreferences.getStringSet("markers", new HashSet<>());
        String latLngString = latLng.latitude + "," + latLng.longitude;
        markers.add(latLngString); // 마커 위치를 String 형태로 추가
        editor.putStringSet("markers", markers); // 마커 위치 Set 저장
        editor.apply();
    }

    public void removeMarkerPosition(LatLng latLngToRemove) {
        Set<String> markers = sharedPreferences.getStringSet("markers", new HashSet<>());
        String latLngToRemoveString = latLngToRemove.latitude + "," + latLngToRemove.longitude;

        if (markers.contains(latLngToRemoveString)) {
            markers.remove(latLngToRemoveString);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("markers", markers);
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
