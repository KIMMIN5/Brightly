package com.example.brightly;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;

import com.example.brightly.Admin.BuildingManager;
import com.example.brightly.Admin.ButtonOfCurrent;
import com.example.brightly.Admin.DataFetcher;
import com.example.brightly.Admin.LampManager;
import com.example.brightly.Map.CreateMap;
import com.example.brightly.Map.CurrentLocation;
import com.example.brightly.Map.DayAndNight;
import com.example.brightly.Map.LimitedBoundary;
import com.example.brightly.User.ButtonOfReport;
import com.example.brightly.User.EventOfBuilding;
import com.example.brightly.User.EventOfLamp;
import com.example.brightly.User.Permissions;
import com.example.brightly.Admin.SaveMarker;
import com.example.brightly.Admin.SharedPreferencesExporter;
import com.example.brightly.databinding.BrightlyLayoutBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        Permissions.LocationPermissionHandler, CurrentLocation.LocationUpdateListener {

    private BrightlyLayoutBinding binding;
    private GoogleMap mMap;
    private CurrentLocation currentLocation;
    private ButtonOfCurrent buttonOfCurrent;
    private LimitedBoundary limitedBoundary;
    private TextView tailTextView;
    private SaveMarker saveMarker;
    private Marker selectedMarker;
    private Button deleteMarkerButton;
    private Button exportButton;
    private Button reportButton;
    private LampManager lampManager;
    private EventOfLamp eventOfLamp;
    private BuildingManager buildingManager;
    private EventOfBuilding eventOfBuilding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BrightlyLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        saveMarker = new SaveMarker(this);

        // 긴급 신고 버튼 초기화
        reportButton = findViewById(R.id.buttonEmergencyReport);
        ButtonOfReport buttonOfReport = new ButtonOfReport(this, reportButton);
        // 현재위치마커찍기 버튼 초기화
        Button currentLocationButton = findViewById(R.id.buttonCurrentLocation);
        currentLocationButton.setOnClickListener(v -> buttonOfCurrent.addMarkerAtCurrentLocation());

        deleteMarkerButton = findViewById(R.id.delete_marker_button);
        deleteMarkerButton.setOnClickListener(v -> {
            if (selectedMarker != null) {
                LatLng markerPosition = selectedMarker.getPosition();
                selectedMarker.remove();
                saveMarker.removeMarkerPosition(markerPosition);
                Log.d("MainActivity", "Marker deleted: " + markerPosition);
                selectedMarker = null;
            }
        });
        exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener(v -> exportSharedPreferences());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeLocationRelatedStuff();
        }

        // 위치 권한 확인
        Permissions.checkLocationPermission(this);

        // 전화 권한 확인
        Permissions.checkCallPhonePermission(this);
    }

    public void initializeLocationRelatedStuff() {
        if (mMap != null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = new CurrentLocation(this, mMap);
            currentLocation.initializeLocationListener();
            currentLocation.setLocationUpdateListener(this);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
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
        DayAndNight.setMapStyleBasedOnTime(mMap, this);

        CreateMap createMap = new CreateMap(mMap);

        // 이 부분에 로그 추가
        Log.d("MainActivity", "Map is ready");

        // 현재 위치 및 버튼 관련 설정
        buttonOfCurrent = new ButtonOfCurrent(currentLocation, mMap, saveMarker);
        loadSavedMarkers();

        // 지도가 로드되면, 제한된 경계 설정
        mMap.setOnMapLoadedCallback(() -> {
            LatLngBounds polygonBounds = createMap.getPolygonBounds();
            limitedBoundary = new LimitedBoundary(mMap, polygonBounds);
        });

        // 위치 관련 권한이 승인된 경우 위치 관련 기능 초기화
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            initializeLocationRelatedStuff();
        }

        lampManager = new LampManager(mMap);
        eventOfLamp = new EventOfLamp(this, lampManager);

        // BuildingManager 인스턴스 생성 및 건물 마커 표시
        buildingManager = new BuildingManager(mMap);
        eventOfBuilding = new EventOfBuilding(this, mMap);

        mMap.setOnCameraIdleListener(eventOfLamp);
        eventOfLamp.onMapReady(mMap);
        buildingManager.showBuildings();

        DataFetcher dataFetcher = DataFetcher.getInstance();
        dataFetcher.setBuildingDataLoadListener(() -> runOnUiThread(() -> buildingManager.showBuildings()));


        mMap.setOnCameraIdleListener(eventOfLamp);
        eventOfLamp.onMapReady(mMap);
        buildingManager.showBuildings();

        // 마커 클릭 이벤트 처리를 위한 중앙 핸들러 설정
        mMap.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            Log.d("MainActivity", "Marker clicked with tag: " + tag);

            if (tag instanceof DataFetcher.Streetlight) {
                Log.d("MainActivity", "Marker clicked with tag: " + tag);
                return eventOfLamp.onMarkerClick(marker);
            } else if (tag instanceof DataFetcher.Building) {
                Log.d("MainActivity", "Marker clicked with tag: " + tag);
                return eventOfBuilding.onMarkerClick(marker);
            } else if ("currentLocation".equals(tag)) {
                Log.d("MainActivity", "Marker clicked with tag: " + tag);
                return handleCurrentLocationMarkerClick(marker);
            }

            Log.d("MainActivity", "Unknown marker clicked");
            return false;
        });
    }

    private boolean handleCurrentLocationMarkerClick(Marker marker) {
        setSelectedMarker(marker);
        Log.d("MainActivity", "Current location marker clicked");
        showMarkerActionButtons(); // 현재 위치 마커 클릭 시 액션 버튼 표시
        return true;
    }

    private void showMarkerActionButtons() {
        deleteMarkerButton.setVisibility(View.VISIBLE);
        exportButton.setVisibility(View.VISIBLE);
    }

    private void hideMarkerActionButtons() {
        deleteMarkerButton.setVisibility(View.GONE);
        exportButton.setVisibility(View.GONE);
    }

    @Override
    public void onLocationUpdated(LatLng newLocation) {
        tailTextView.setText("현재 위치: " + newLocation.latitude + ", " +   newLocation.longitude);
    }

    private void loadSavedMarkers() {
        Set<LatLng> savedMarkerLatLngs = saveMarker.loadAllMarkerPositions();
        if (savedMarkerLatLngs != null) {
            for (LatLng latLng : savedMarkerLatLngs) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("저장된 위치"));
                Log.d("MainActivity", "Adding saved marker: " + latLng.toString());
            }
        }
    }

    private void exportSharedPreferences() {
        SharedPreferencesExporter.exportSharedPreferences(this, "MarkerPref");
    }

    public void setSelectedMarker(Marker marker) {
        this.selectedMarker = marker;
    }
}