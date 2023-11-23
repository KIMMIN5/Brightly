package com.example.brightly.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class LimitedBoundary {
    private GoogleMap mMap;
    private LatLngBounds bounds;

    public LimitedBoundary(GoogleMap googleMap, LatLngBounds bounds) {
        this.mMap = googleMap;
        this.bounds = bounds;

        // 지도의 카메라 이동 리스너 설정
        setCameraMoveListener();
    }

    private void setCameraMoveListener() {
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                // 사용자의 카메라 위치가 경계를 벗어나는 경우, 경계 중앙으로 카메라를 재설정
                if (!bounds.contains(mMap.getCameraPosition().target)) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
                }
            }
        });
    }
}
