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
    private GoogleMap mMap;
    private DataFetcher dataFetcher;

    public BuildingManager(GoogleMap map) {
        this.mMap = map;
        this.dataFetcher = DataFetcher.getInstance();
        // 건물 데이터 로딩
        dataFetcher.loadBuildingData();
    }

    public void showBuildings() {
        Map<String, DataFetcher.Building> buildings = dataFetcher.getBuildings();
        for (Map.Entry<String, DataFetcher.Building> entry : buildings.entrySet()) {
            DataFetcher.Building building = entry.getValue();
            LatLng position = new LatLng(building.getLatitude(), building.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(building.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
            marker.setTag(building); // Set the building object as a tag
        }
    }
}