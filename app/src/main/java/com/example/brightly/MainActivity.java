package com.example.brightly;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.example.brightly.databinding.BrightlyLayoutBinding;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private BrightlyLayoutBinding binding;
    private GoogleMap mMap;
    private CurrentLocation currentLocation;
    private ButtonOfCurrent buttonOfCurrent;
    private LimitedBoundary limitedBoundary;
    private SaveMarker saveMarker;
    private TextView tailTextView; // Tail 영역에 대한 참조
    private Marker selectedMarker; // 현재 선택된 마커에 대한 참조
    private Button deleteMarkerButton; // 마커 삭제 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BrightlyLayoutBinding binding = BrightlyLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 지도 프래그먼트 설정
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // SaveMarker 인스턴스 생성
        saveMarker = new SaveMarker(this);

        // 현재 위치 버튼 설정
        Button currentLocationButton = findViewById(R.id.buttonCurrentLocation);
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLocation != null) {
                    buttonOfCurrent.addMarkerAtCurrentLocation();
                }
            }
        });

        Button saveMarkerButton = findViewById(R.id.buttonCurrentLocation);

        // Tail 영역의 TextView 찾기
        tailTextView = findViewById(R.id.tail_text_view);

        // 마커 삭제 버튼 초기화 및 이벤트 리스너 설정
        deleteMarkerButton = findViewById(R.id.delete_marker_button);
        deleteMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMarker != null) {
                    LatLng markerPosition = selectedMarker.getPosition();
                    selectedMarker.remove(); // 선택된 마커 삭제
                    saveMarker.removeMarkerPosition(markerPosition); // SharedPreferences에서 마커 정보 삭제
                    selectedMarker = null; // 참조 초기화
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // CreateMap 클래스를 사용하여 지도 초기화
        com.example.brightly.CreateMap createMap = new com.example.brightly.CreateMap(mMap);

        // 현재 위치를 추적하는 객체 초기화
        currentLocation = new CurrentLocation(this, mMap);
        currentLocation.initializeLocationListener();

        // 현재 위치에 마커를 추가하는 버튼 객체 초기화
        buttonOfCurrent = new ButtonOfCurrent(currentLocation, mMap, saveMarker);

        // 저장된 마커 위치 불러오기 및 표시
        loadSavedMarkers();

        // 지도가 완전히 로드된 후에 경계 설정
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds polygonBounds = createMap.getPolygonBounds();
                limitedBoundary = new LimitedBoundary(mMap, polygonBounds);
            }
        });

        // 마커 클릭 이벤트 처리
        mMap.setOnMarkerClickListener(marker -> {
            selectedMarker = marker; // 클릭된 마커 저장
            LatLng latLng = marker.getPosition();
            tailTextView.setText("Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
            return false;
        });
    }

    // 저장된 마커 위치를 불러와서 마커 생성
    private void loadSavedMarkers() {
        Set<LatLng> savedMarkerLatLngs = saveMarker.loadAllMarkerPositions();
        if (savedMarkerLatLngs != null) {
            for (LatLng latLng : savedMarkerLatLngs) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("저장된 위치"));
                Log.d("MainActivity", "Adding saved marker: " + latLng.toString()); // 로그 출력
            }
        }
    }
}
