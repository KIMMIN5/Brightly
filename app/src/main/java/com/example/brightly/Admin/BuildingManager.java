package com.example.brightly.Admin;

import android.util.Log;

import com.example.brightly.User.EventOfBuilding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class BuildingManager {
    private GoogleMap mMap; // Google Maps 객체
    private DataFetcher dataFetcher; // 건물 데이터를 가져오는 DataFetcher 인스턴스

    // 생성자: GoogleMap 객체 초기화 및 DataFetcher 인스턴스 설정, 건물 데이터 로드
    public BuildingManager(GoogleMap map) {
        this.mMap = map;
        this.dataFetcher = DataFetcher.getInstance();
        dataFetcher.loadBuildingData(); // 건물 데이터 로딩 시작
    }

    // 지도에 건물 마커를 표시하는 메서드
    public void showBuildings() {
        Map<String, DataFetcher.Building> buildings = dataFetcher.getBuildings(); // 건물 데이터 가져오기
        for (Map.Entry<String, DataFetcher.Building> entry : buildings.entrySet()) {
            DataFetcher.Building building = entry.getValue();
            LatLng position = new LatLng(building.getLatitude(), building.getLongitude()); // 건물의 위치

            // 마커 옵션 설정 및 지도에 마커 추가
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(building.getName()) // 건물명을 타이틀로 설정
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))); // 마커 색상 설정

            marker.setTag(building); // 마커에 건물 객체 태그 설정
        }
    }
}