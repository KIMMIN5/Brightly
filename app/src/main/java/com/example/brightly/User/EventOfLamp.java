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
import java.util.List;

public class EventOfLamp implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {
    private GoogleMap mMap;
    private Context context; // Context 추가
    private List<Marker> streetlightMarkers = new ArrayList<>();
    private static final float MIN_ZOOM_LEVEL_FOR_MARKERS = 15.0f;

    // Context를 포함한 생성자
    public EventOfLamp(Context context) {
        this.context = context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        loadStreetlightData();
    }

    private void loadStreetlightData() {
        DataFetcher dataFetcher = DataFetcher.getInstance(); // 싱글턴 인스턴스 사용
        List<DataFetcher.Streetlight> streetlights = dataFetcher.getStreetlights();

        for (DataFetcher.Streetlight light : streetlights) {
            LatLng position = new LatLng(light.getLatitude(), light.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(position).title("Street Light");
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(light);
            streetlightMarkers.add(marker);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Streetlight selectedLight = (Streetlight) marker.getTag();
        if (selectedLight != null) {
            String status = selectedLight.isFaulty() ? "Faulty" : "Operational";
            Toast.makeText(context, "Street Light Status: " + status, Toast.LENGTH_SHORT).show();
            Log.d("EventOfLamp", "Marker clicked: " + marker.getPosition().toString() + ", Status: " + status);
        } else {
            Log.d("EventOfLamp", "Marker clicked but no data found.");
        }
        return true;
    }

    @Override
    public void onCameraIdle() {
        updateMarkerVisibility();
    }

    private void updateMarkerVisibility() {
        float zoomLevel = mMap.getCameraPosition().zoom;
        for (Marker marker : streetlightMarkers) {
            marker.setVisible(zoomLevel >= MIN_ZOOM_LEVEL_FOR_MARKERS);
        }
    }

    public class Streetlight {
        private int isFaulty;
        private int isReport;
        private double latitude;
        private double longitude;

        // 생성자, getter, setter 추가
        public Streetlight(int isFaulty, int isReport, double latitude, double longitude) {
            this.isFaulty = isFaulty;
            this.isReport = isReport;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public boolean isFaulty() {
            return isFaulty == 1;
        }

        public boolean isReport() {
            return isReport == 1;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        // 추가 필요한 메소드
    }
}