package com.example.brightly.User;

<<<<<<< HEAD
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.brightly.Admin.DataFetcher;
import com.example.brightly.Admin.LampManager;
import com.example.brightly.MainActivity;
import com.example.brightly.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class EventOfLamp implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener {
    private static final float MIN_ZOOM_LEVEL_FOR_MARKERS = 17.0f;
    private GoogleMap mMap;
    private Context context;
    private LampManager lampManager;
    private Marker lastSelectedMarker = null; // 마지막으로 선택된 마커 추적

    public EventOfLamp(Context context, LampManager lampManager) {
        this.context = context;
        this.lampManager = lampManager;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMapClickListener(this); // 지도 클릭 리스너 설정
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTag() instanceof DataFetcher.Streetlight) {
            // 마커 선택 처리
            selectMarker(marker);

            // MainActivity의 selectedMarker 업데이트
            if (context instanceof MainActivity) {
                ((MainActivity) context).setSelectedMarker(marker);
            }

            // 이전 마커의 아이콘을 초기 상태로 복원
            if (lastSelectedMarker != null && !lastSelectedMarker.equals(marker)) {
                resetMarkerIcon(lastSelectedMarker);
            }

            // 선택된 마커의 아이콘 변경
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            lastSelectedMarker = marker;

            Object tag = marker.getTag();
            if (tag instanceof DataFetcher.Streetlight) {
                DataFetcher.Streetlight selectedLight = (DataFetcher.Streetlight) tag;
                String status = selectedLight.getIsFaulty() ? "고장" : "작동중";

                TextView tailTextView = ((Activity) context).findViewById(R.id.tail_text_view);
                tailTextView.setText("가로등: " + status);

                Button reportButton = ((Activity) context).findViewById(R.id.report_button);
                reportButton.setVisibility(View.VISIBLE);
                reportButton.setOnClickListener(v -> {
                    // 고장 신고 로직 구현
                });
            }

            return true;
        }
        else {
            // 가로등 마커가 아닐 경우

            // 선택 해제 처리
            if (lastSelectedMarker != null) {
                resetMarkerIcon(lastSelectedMarker);
                lastSelectedMarker = null;
            }

            // UI 요소 숨기기
            TextView tailTextView = ((Activity)context).findViewById(R.id.tail_text_view);
            tailTextView.setText("");

            Button reportButton = ((Activity)context).findViewById(R.id.report_button);
            reportButton.setVisibility(View.GONE);

            return false; // 이벤트 미처리 (기본 동작 수행)
        }
    }

    @Override
    public void onCameraIdle() {
        float zoomLevel = mMap.getCameraPosition().zoom;
        boolean shouldMarkersBeVisible = zoomLevel >= MIN_ZOOM_LEVEL_FOR_MARKERS;
        for (Marker marker : lampManager.getExistingMarkers().values()) {
            marker.setVisible(shouldMarkersBeVisible);
        }
    }

    // 마커 아이콘을 초기 상태로 복원하는 메소드
    private void resetMarkerIcon(Marker marker) {
        if (marker.getTag() instanceof DataFetcher.Streetlight) {
            DataFetcher.Streetlight light = (DataFetcher.Streetlight) marker.getTag();
            if (light.getIsFaulty()) {
                // 고장난 가로등 색상 설정
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                // 정상 가로등 색상 설정
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
        }
    }

    // 마커 선택 시 아이콘 변경
    private void selectMarker(Marker marker) {
        if (lastSelectedMarker != null && !lastSelectedMarker.equals(marker)) {
            resetMarkerIcon(lastSelectedMarker);
        }
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        lastSelectedMarker = marker;
    }


    @Override
    public void onMapClick(LatLng latLng) {
        // UI 요소 숨기기
        if (lastSelectedMarker != null) {
            resetMarkerIcon(lastSelectedMarker);
            lastSelectedMarker = null;
        }

        TextView tailTextView = ((Activity)context).findViewById(R.id.tail_text_view);
        tailTextView.setText("");

        Button reportButton = ((Activity)context).findViewById(R.id.report_button);
        reportButton.setVisibility(View.GONE);
    }
}
=======
public class EventOfLamp {
}
>>>>>>> 11275e6 (마커 색깔 변경)
