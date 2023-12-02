package com.example.brightly.Admin;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import com.example.brightly.Admin.DataFetcher;
import com.example.brightly.Admin.DataFetcher.Streetlight;

import java.util.List;

public class LampManager implements DataFetcher.DataChangeListener {
    private GoogleMap mMap;
    private DataFetcher dataFetcher;

    public LampManager(GoogleMap map) {
        this.mMap = map;
        this.dataFetcher = DataFetcher.getInstance();
        dataFetcher.addDataChangeListener(this);
        dataFetcher.loadStreetlightData(); // 데이터 로딩을 여기에서 초기화합니다.
    }

    @Override
    public void onDataChanged(List<Streetlight> streetlights) {
        // 데이터가 변경될 때 실행되는 콜백
        for (Streetlight light : streetlights) {
            addMarkerToMap(new LatLng(light.getLatitude(), light.getLongitude()), light.getIsFaulty() == 1);
        }
    }

    // DataFetcher.DataChangeListener 인터페이스의 메소드 구현
    @Override
    public void onDataLoadComplete() {
        // 데이터 로딩이 완료된 후 필요한 작업을 여기에 구현합니다.
        Log.d("LampManager", "Data loading is complete.");
    }

    private void addMarkerToMap(LatLng latLng, boolean isFaulty) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(isFaulty ? "Faulty Street Light" : "Street Light");

        if (mMap != null) {
            mMap.addMarker(markerOptions);
        }
    }

    // 필요한 추가 메소드 ...
}