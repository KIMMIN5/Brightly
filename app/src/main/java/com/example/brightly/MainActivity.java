package com.example.brightly;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import android.Manifest;

import com.example.brightly.Admin.ButtonOfCurrent;
import com.example.brightly.Map.CreateMap;
import com.example.brightly.Map.CurrentLocation;
import com.example.brightly.Map.DayAndNight;
import com.example.brightly.Map.LimitedBoundary;
import com.example.brightly.User.Permissions;
import com.example.brightly.R;
import com.example.brightly.Admin.SaveMarker;
import com.example.brightly.Admin.SharedPreferencesExporter;
import com.example.brightly.databinding.BrightlyLayoutBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, Permissions.LocationPermissionHandler {

    private BrightlyLayoutBinding binding;
    private GoogleMap mMap;
    private CurrentLocation currentLocation;
    private ButtonOfCurrent buttonOfCurrent;
    private LimitedBoundary limitedBoundary;
    private SaveMarker saveMarker;
    private TextView tailTextView; // Tail 영역에 대한 참조
    private Marker selectedMarker; // 현재 선택된 마커에 대한 참조
    private Button deleteMarkerButton; // 마커 삭제 버튼
    private Button exportButton; // 내보내기 버튼 추가


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
                Log.d("MainActivity", "Current location button clicked"); // 로그 추가
                buttonOfCurrent.addMarkerAtCurrentLocation();
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

        // 내보내기 버튼 초기화 및 리스너 설정
        exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportSharedPreferences();
            }
        });

        // 위치 관련 기능 초기화
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeLocationRelatedStuff();
        }

        // 위치 권한 요청
        Permissions.checkLocationPermission(this);
    }

    @Override
    public void initializeLocationRelatedStuff() {
        if (mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentLocation = new CurrentLocation(this, mMap);
            currentLocation.initializeLocationListener();
        }

        // 현재 위치에 마커를 추가하는 버튼 객체 초기화
        buttonOfCurrent = new ButtonOfCurrent(currentLocation, mMap, saveMarker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 주야간 모드에 따른 지도 스타일 설정
        DayAndNight.setMapStyleBasedOnTime(mMap, this);

        // CreateMap 클래스를 사용하여 지도 초기화
        CreateMap createMap = new CreateMap(mMap);

        // 현재 위치 추적 객체 초기화
        currentLocation = new CurrentLocation(this, mMap);
        currentLocation.initializeLocationListener();

        // 앱이 처음 시작될 때 사용자의 현재 위치를 가져오는 로직
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                LatLng userLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 30));
            }
        }

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

        // 지도가 준비된 후 위치 관련 기능 초기화 (권한이 이미 부여된 경우에만)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeLocationRelatedStuff();
        }

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

    // SharedPreferences 데이터를 내보내는 메소드
    private void exportSharedPreferences() {
        SharedPreferencesExporter.exportSharedPreferences(this, "MarkerPref");
    }
}

