package com.example.brightly;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
        Set<String> currentMarkers = new HashSet<>(sharedPreferences.getStringSet("markers", new HashSet<>()));
        String latLngString = latLng.latitude + "," + latLng.longitude;
        currentMarkers.add(latLngString);
        editor.putStringSet("markers", currentMarkers);
        editor.apply();
    }

    public void removeMarkerPosition(LatLng latLngToRemove) {
        Set<String> currentMarkers = new HashSet<>(sharedPreferences.getStringSet("markers", new HashSet<>()));
        String latLngToRemoveString = latLngToRemove.latitude + "," + latLngToRemove.longitude;
        currentMarkers.remove(latLngToRemoveString);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("markers", currentMarkers);
        editor.apply();
    }

    public Set<LatLng> loadAllMarkerPositions() {
        Set<String> markersStringSet = sharedPreferences.getStringSet("markers", new HashSet<>());
        Set<LatLng> latLngs = new HashSet<>();

        for (String markerString : markersStringSet) {
            String[] parts = markerString.split(",");
            try {
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);
                latLngs.add(new LatLng(latitude, longitude));
            } catch (NumberFormatException e) {
                Log.e("SaveMarker", "Error parsing marker position: " + markerString, e);
            }
        }
        return latLngs;
    }
}
