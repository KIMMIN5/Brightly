package com.example.brightly.Map;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class CreateMap {
    private GoogleMap googleMap;
    private LatLngBounds polygonBounds; // 폴리곤 경계를 저장 할 변수

    public CreateMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
        initializeMap();
    }

    public void initializeMap() {
        // 지도 기본 설정
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);

        // 폴리곤 설정 및 추가
        List<LatLng> coordinates = coordinates();
        PolygonOptions polygonOptions = new PolygonOptions()
                .addAll(coordinates)
                .fillColor(Color.argb(30, 100, 100, 255)) // 투명도 수정
                .strokeColor(Color.RED)
                .strokeWidth(5f);
        googleMap.addPolygon(polygonOptions);

        // 카메라 위치 및 줌 설정
        LatLngBounds bounds = getLatLngBounds(coordinates);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private List<LatLng> coordinates() {
        List<LatLng> coordinates = new ArrayList<>();
        // 계명대학교 경계 좌표 반환
        coordinates.add(new LatLng(35.8538333, 128.4791287));
        coordinates.add(new LatLng(35.8537571, 128.4792443));
        coordinates.add(new LatLng(35.8536474, 128.4792963));
        coordinates.add(new LatLng(35.8532547, 128.4794462));
        coordinates.add(new LatLng(35.8531763, 128.4799756));
        coordinates.add(new LatLng(35.8531821, 128.4801556));
        coordinates.add(new LatLng(35.8530011, 128.4812317));
        coordinates.add(new LatLng(35.8528309, 128.4823434));
        coordinates.add(new LatLng(35.8526526, 128.4839648));
        coordinates.add(new LatLng(35.8526244, 128.484748));
        coordinates.add(new LatLng(35.8526287, 128.4854937));
        coordinates.add(new LatLng(35.8526026, 128.485904));
        coordinates.add(new LatLng(35.8525526, 128.4862018));
        coordinates.add(new LatLng(35.8524961, 128.4865102));
        coordinates.add(new LatLng(35.8522284, 128.4877409));
        coordinates.add(new LatLng(35.8521479, 128.4884794));
        coordinates.add(new LatLng(35.8518351, 128.4913882));
        coordinates.add(new LatLng(35.8521396, 128.4918056));
        coordinates.add(new LatLng(35.8543614, 128.4917968));
        coordinates.add(new LatLng(35.8561005, 128.4917781));
        coordinates.add(new LatLng(35.8569396, 128.4917848));
        coordinates.add(new LatLng(35.8569648, 128.490578));
        coordinates.add(new LatLng(35.8580582, 128.4898963));
        coordinates.add(new LatLng(35.8592346, 128.4888941));
        coordinates.add(new LatLng(35.859459, 128.4891235));
        coordinates.add(new LatLng(35.8599656, 128.4887794));
        coordinates.add(new LatLng(35.8608841, 128.4874325));
        coordinates.add(new LatLng(35.8597711, 128.4858232));
        coordinates.add(new LatLng(35.8588668, 128.4853297));
        coordinates.add(new LatLng(35.8591103, 128.4838491));
        coordinates.add(new LatLng(35.8591103, 128.4838491));
        coordinates.add(new LatLng(35.8605189, 128.4833985));
        coordinates.add(new LatLng(35.8614928, 128.4832697));
        coordinates.add(new LatLng(35.8607276, 128.4816389));
        coordinates.add(new LatLng(35.8607798, 128.4802227));
        coordinates.add(new LatLng(35.8598059, 128.4800296));
        coordinates.add(new LatLng(35.8596842, 128.4816389));
        coordinates.add(new LatLng(35.8592842, 128.4817891));
        coordinates.add(new LatLng(35.8579527, 128.4794265));
        coordinates.add(new LatLng(35.857493, 128.4781843));
        coordinates.add(new LatLng(35.8570408, 128.4779911));
        coordinates.add(new LatLng(35.8564364, 128.4781929));
        coordinates.add(new LatLng(35.8564872, 128.4785556));
        coordinates.add(new LatLng(35.8563356, 128.4788027));
        coordinates.add(new LatLng(35.8559113, 128.4792376));
        coordinates.add(new LatLng(35.8553532, 128.4790358));
        coordinates.add(new LatLng(35.854795, 128.4789927));
        coordinates.add(new LatLng(35.8545258, 128.4790754));
        coordinates.add(new LatLng(35.8538333, 128.4791287));

        return coordinates;
    }

    private LatLngBounds getLatLngBounds(List<LatLng> coordinates) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng coord : coordinates) {
            builder.include(coord);
        }
        return builder.build();
    }

    // 폴리곤 경계 반환 메서드
    public LatLngBounds getPolygonBounds() {
        if (polygonBounds == null) {
            List<LatLng> coordinates = coordinates();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng coord : coordinates) {
                builder.include(coord);
            }
            polygonBounds = builder.build();
        }
        return polygonBounds;
    }
}
