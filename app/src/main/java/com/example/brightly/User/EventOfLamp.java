package com.example.brightly.User;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
    private MainActivity mainActivity;

    public EventOfLamp(Context context, LampManager lampManager) {
        if (context instanceof MainActivity) {
            this.mainActivity = (MainActivity) context;
        }
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
            DataFetcher.Streetlight selectedLight = (DataFetcher.Streetlight) marker.getTag();

            // 고장 및 신고 상태에 따른 텍스트와 색상 설정
            String statusText = selectedLight.getIsFaulty() ? "고장" : "정상";
            int statusColor = selectedLight.getIsFaulty() ? Color.RED : Color.GREEN;
            String reportText = selectedLight.getIsReport() ? "신고" : "미신고";
            int reportColor = selectedLight.getIsReport() ? Color.RED : Color.GREEN;

            // 텍스트뷰에 상태 표시
            TextView statusTextView = mainActivity.findViewById(R.id.status_text_view);
            statusTextView.setText("상태: " + statusText);
            statusTextView.setTextColor(statusColor);

            // 텍스트뷰에 신고 여부 표시
            TextView reportTextView = mainActivity.findViewById(R.id.report_text_view);
            reportTextView.setText("신고 여부: " + reportText);
            reportTextView.setTextColor(reportColor);


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

            // 가로등 마커 클릭 시 UI 요소 표시
            showLayout();

            return true;
        } else {
            // 가로등 마커가 아닐 경우

            // 선택 해제 처리
            if (lastSelectedMarker != null) {
                resetMarkerIcon(lastSelectedMarker);
                lastSelectedMarker = null;
            }

            // UI 요소 숨기기
            hideLayout();

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
        // 마커 선택 해제
        if (lastSelectedMarker != null) {
            resetMarkerIcon(lastSelectedMarker);
            lastSelectedMarker = null;
        }

        // UI 요소 숨기기
        hideLayout();
    }


    // 레이아웃을 표시하는 메소드
    // 레이아웃을 표시하는 메소드
    private void showLayout() {
        if (mainActivity != null) {
            mainActivity.findViewById(R.id.status_text_view).setVisibility(View.VISIBLE);
            mainActivity.findViewById(R.id.report_text_view).setVisibility(View.VISIBLE);
            mainActivity.findViewById(R.id.report_button).setVisibility(View.VISIBLE);
        }
    }

    // 레이아웃을 숨기는 메소드
    private void hideLayout() {
        if (mainActivity != null) {
            mainActivity.findViewById(R.id.status_text_view).setVisibility(View.GONE);
            mainActivity.findViewById(R.id.report_text_view).setVisibility(View.GONE);
            mainActivity.findViewById(R.id.report_button).setVisibility(View.GONE);
        }
    }
}