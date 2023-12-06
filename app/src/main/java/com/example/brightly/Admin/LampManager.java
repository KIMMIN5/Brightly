package com.example.brightly.Admin;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.example.brightly.Admin.DataFetcher;
import com.example.brightly.Admin.DataFetcher.Streetlight;
import java.util.HashMap;
import java.util.Map;

public class LampManager implements DataFetcher.DataChangeListener {
    private GoogleMap mMap;
    private DataFetcher dataFetcher;
    private HashMap<String, Marker> existingMarkers = new HashMap<>();

    public LampManager(GoogleMap map) {
        this.mMap = map;
        this.dataFetcher = DataFetcher.getInstance();
        dataFetcher.addDataChangeListener(this);
        dataFetcher.loadStreetlightData();
    }

    @Override
    public void onDataChanged(Map<String, Streetlight> updatedLights) {
        for (Map.Entry<String, Streetlight> entry : updatedLights.entrySet()) {
            String id = entry.getKey();
            Streetlight light = entry.getValue();
            LatLng position = new LatLng(light.getLatitude(), light.getLongitude());
            Marker marker = existingMarkers.get(id);

            if (marker != null) {
                updateMarkerColorAndTag(marker, light);
            } else {
                addMarkerToMap(id, position, light);
            }
        }
    }

    @Override
    public void onDataLoadComplete() {

    }

    private void updateMarkerColorAndTag(Marker marker, Streetlight light) {
        if (light.getIsFaulty()) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        else if(light.getIsReport()){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        }
        else {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        marker.setTag(light);
    }

    private void addMarkerToMap(String id, LatLng latLng, Streetlight light) {
        if (existingMarkers.containsKey(id)) {
            return;
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(light.getIsFaulty() ? "Faulty Street Light" : "Street Light");
        if (light.getIsFaulty()) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        Marker marker = mMap.addMarker(markerOptions);
        existingMarkers.put(id, marker);
    }

    public HashMap<String, Marker> getExistingMarkers() {
        return existingMarkers;
    }
}