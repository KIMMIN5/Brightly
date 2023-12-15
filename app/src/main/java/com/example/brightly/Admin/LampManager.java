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
    private GoogleMap mMap; // Google Maps 객체
    private DataFetcher dataFetcher; // 가로등 데이터를 가져오는 DataFetcher 인스턴스
    private HashMap<String, Marker> existingMarkers = new HashMap<>(); // 이미 추가된 마커를 관리하는 맵

    // 생성자: GoogleMap 객체와 DataFetcher 인스턴스 초기화, 가로등 데이터 로드 시작
    public LampManager(GoogleMap map) {
        this.mMap = map;
        this.dataFetcher = DataFetcher.getInstance();
        dataFetcher.addDataChangeListener(this);
        dataFetcher.loadStreetlightData();
    }

    // DataFetcher로부터 업데이트된 가로등 데이터를 받았을 때 호출되는 메서드
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

    // 데이터 로드가 완료되었을 때 호출될 메서드
    @Override
    public void onDataLoadComplete() {
        Log.i("LampManager", "Data load complete.");
    }

    // 기존 마커의 색상과 태그 업데이트 메서드
    private void updateMarkerColorAndTag(Marker marker, Streetlight light) {
        // 고장난 가로등, 신고된 가로등 등에 따라 마커 색상 변경
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

    // 새로운 마커를 지도에 추가하는 메서드
    private void addMarkerToMap(String id, LatLng latLng, Streetlight light) {
        if (existingMarkers.containsKey(id)) {
            return;
        }
        // 마커 옵션 설정 및 지도에 추가
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

    // 기존에 추가된 마커들을 반환하는 메서드
    public HashMap<String, Marker> getExistingMarkers() {
        return existingMarkers;
    }
}