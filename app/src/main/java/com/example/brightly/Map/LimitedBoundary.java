package com.example.brightly.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class LimitedBoundary {
    private GoogleMap mMap; // Google Maps 객체
    private LatLngBounds bounds; // 제한할 지역의 경계

    // 생성자: GoogleMap 객체와 제한할 경계 설정
    public LimitedBoundary(GoogleMap googleMap, LatLngBounds bounds) {
        this.mMap = googleMap;
        this.bounds = bounds;

        // 지도의 카메라 이동 리스너 설정
        setCameraMoveListener();
    }

    // 지도 카메라 이동 리스너 설정 메서드
    private void setCameraMoveListener() {
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                // 카메라의 현재 위치가 설정된 경계를 벗어나는 경우 확인
                if (!bounds.contains(mMap.getCameraPosition().target)) {
                    // 카메라를 경계의 중앙으로 재설정
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
                }
            }
        });
    }
}