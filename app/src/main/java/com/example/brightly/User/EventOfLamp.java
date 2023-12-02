package com.example.brightly.User;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.example.brightly.Admin.DataFetcher;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventOfLamp implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, DataFetcher.DataChangeListener {
    private static final float MIN_ZOOM_LEVEL_FOR_MARKERS = 15.0f;
    private GoogleMap mMap;
    private Context context;
    private HashMap<LatLng, Marker> markerMap = new HashMap<>();

    public EventOfLamp(Context context) {
        this.context = context;
        DataFetcher.getInstance().addDataChangeListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        // Data loading is now handled in onDataLoadComplete.
    }

    @Override
    public void onDataChanged(List<DataFetcher.Streetlight> streetlights) {
        // Update or create markers based on the new data
        for (DataFetcher.Streetlight light : streetlights) {
            LatLng position = new LatLng(light.getLatitude(), light.getLongitude());
            Marker marker = markerMap.get(position);

            if (marker == null) {
                // Create a new marker if it doesn't exist
                MarkerOptions markerOptions = new MarkerOptions().position(position).title("Street Light");
                marker = mMap.addMarker(markerOptions);
                marker.setTag(light);
                markerMap.put(position, marker);
            } else {
                // Update existing marker's tag
                marker.setTag(light);
            }
        }

        // Remove markers if they are not in the new data
        ArrayList<LatLng> keysToRemove = new ArrayList<>();
        for (LatLng key : markerMap.keySet()) {
            boolean found = false;
            for (DataFetcher.Streetlight light : streetlights) {
                if (new LatLng(light.getLatitude(), light.getLongitude()).equals(key)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                keysToRemove.add(key);
            }
        }

        for (LatLng key : keysToRemove) {
            Marker marker = markerMap.get(key);
            if (marker != null) {
                marker.remove();
                markerMap.remove(key);
            }
        }
    }

    @Override
    public void onDataLoadComplete() {
        loadStreetlightData();
    }

    private void loadStreetlightData() {
        // Method is now simplified, as marker management is handled in onDataChanged
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof DataFetcher.Streetlight) {
            DataFetcher.Streetlight selectedLight = (DataFetcher.Streetlight) tag;
            String status = selectedLight.getIsFaulty() == 1 ? "Faulty" : "Operational";
            Toast.makeText(context, "Street Light Status: " + status, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("EventOfLamp", "Marker clicked but tag is not Streetlight or is null.");
        }
        return true;
    }

    @Override
    public void onCameraIdle() {
        updateMarkerVisibility();
    }

    private void updateMarkerVisibility() {
        float zoomLevel = mMap.getCameraPosition().zoom;
        for (Marker marker : markerMap.values()) {
            marker.setVisible(zoomLevel >= MIN_ZOOM_LEVEL_FOR_MARKERS);
        }
    }
}