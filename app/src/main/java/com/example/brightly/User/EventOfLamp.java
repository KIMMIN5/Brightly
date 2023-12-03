package com.example.brightly.User;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.brightly.Admin.DataFetcher;
import com.example.brightly.Admin.LampManager;
import com.example.brightly.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

public class EventOfLamp implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener {
    private static final float MIN_ZOOM_LEVEL_FOR_MARKERS = 17.0f;
    private GoogleMap mMap;
    private Context context;
    private LampManager lampManager;

    public EventOfLamp(Context context, LampManager lampManager) {
        this.context = context;
        this.lampManager = lampManager;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Object tag = marker.getTag();
        if (tag instanceof DataFetcher.Streetlight) {
            DataFetcher.Streetlight selectedLight = (DataFetcher.Streetlight) tag;
            String status = selectedLight.getIsFaulty() ? "고장" : "작동중";

            TextView tailTextView = ((Activity)context).findViewById(R.id.tail_text_view);
            tailTextView.setText("가로등: " + status);

            Button reportButton = ((Activity)context).findViewById(R.id.report_button);
            reportButton.setVisibility(View.VISIBLE);
            reportButton.setOnClickListener(v -> {
                // 고장 신고 로직 구현
            });
        }
        return true;
    }

    @Override
    public void onCameraIdle() {
        float zoomLevel = mMap.getCameraPosition().zoom;
        boolean shouldMarkersBeVisible = zoomLevel >= MIN_ZOOM_LEVEL_FOR_MARKERS;
        for (Marker marker : lampManager.getExistingMarkers().values()) {
            marker.setVisible(shouldMarkersBeVisible);
        }
    }
}